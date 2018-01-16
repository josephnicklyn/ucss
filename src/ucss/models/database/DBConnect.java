package ucss.models.database;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.sqlite.SQLiteConfig;

/**
 *jf4242nickly
 * @author Joseph Nicklyn JR.
 */
public class DBConnect {
    
    
    public enum DatabaseMessages {
        DBM_NONE,
        
        DBM_CONNECT,
        
        DBM_DISCONNECT,
        
        DBM_CONNECT_TO_LOCAL,
        
        DBM_CONNECT_TO_REMOTE;
    };
    
    private SimpleObjectProperty<DatabaseMessages> dbm = new SimpleObjectProperty(DatabaseMessages.DBM_NONE);
    
    public final void setOnDatabaseMessage(ChangeListener<DatabaseMessages> listener) {
        dbm.addListener(listener);
    }
    
    private Session sshSession;
    
    private static int localPort = 10032;
    
    private boolean hostKeyCheck = false;
    
    private int lPort = localPort;
    
    private String 
        sqlUser,
        sqlDatabase,
        sqlPassword,
    
        sqlURL,
            
        sshUser, 
        sshServer,
        sshPassword, 
            
        useHost;
    
    private int
        sshPort,
        sqlPort;
        
    public DBConnect() { }

    public DBConnect(String file) {
        useLocalFile(file);
    }

    
    public boolean isConnected() {
        if (sshSession != null) 
            return sshSession.isConnected();
        else 
            return useLocalFile;
            
    }
    
    /**
     * Connects to a SSH server, this is required prior to any SQL calls.
     * 
     * @param server String, location of the server
     * @param port Integer, the SSH port number (22)
     * @param user String, the SSH user name
     * @param password String, the password for the SSH server
     * @param hostChecking Boolean
     * @param host String
     * @param database String
     * @param dbUser String
     * @param dbPassword String
     * @param dbPort Integer, the SQL port number (3306)
     * @return Exception - allows for error messaging 
     */
    public Exception sshConnect(
        String server,
        int port,
        String user,
        String password,
        boolean hostChecking,
        String host,
        String database,
        String dbUser,
        String dbPassword,
        int dbPort) {
        
        disconnect();
        useLocalFile = false;
        
        /*
            incase for some reason the SSH connection went bad, 
            we can automatically reconnect later.
        */
        sshServer = server;
        sshUser = user;
        sshPassword = password;
        sshPort = port;
        hostKeyCheck = hostChecking;
        
        sqlDatabase = database;
        sqlUser = dbUser;
        sqlPassword = dbPassword;
        sqlPort = dbPort;
        useHost = host;
        
        JSch jsch = new JSch();
        
        try {
            sshSession = jsch.getSession(
                    user,
                    server,
                    port
            );

            sshSession.setPassword(password);

            if (!hostChecking) {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(config);
            }

            sshSession.connect();

            lPort = localPort++;

            sshSession.setPortForwardingL(
                lPort,
                host,
                dbPort
            );
            
            sqlURL = String.format("jdbc:mysql://localhost:%d/%s", lPort, sqlDatabase);
            
            
            reset();
            dbm.set(DatabaseMessages.DBM_NONE);
            dbm.set(DatabaseMessages.DBM_CONNECT);
            dbm.set(DatabaseMessages.DBM_CONNECT_TO_REMOTE);
            
        } catch (JSchException ex) {
            return ex;
        }
        return null;
    }
    
    /**
     * disconnects from the SSH server
     */
    public void disconnect() {
        
        boolean sendMessage = false;
        
        if (localConnection == null && sshSession == null) {
            System.out.println("Not connected to database...");
        }
        if (localConnection != null) {
            System.out.println("disconnecting from local database...");
            try {
                localConnection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
            localConnection = null;
            sendMessage = true;
            
        }
        if (sshSession != null) {
            System.out.println("disconnecting ssh...");
            sshSession.disconnect();
            sendMessage = true;
        }
        
        useLocalFile = false;
        sshSession = null;
        localConnection = null;
        sqlUser = "";
        sqlDatabase = "";
        sqlPassword = "";
    
        sqlURL = "";
            
        sshUser = "";
        sshServer = "";
        sshPassword = "";
            
        useHost = "";
        if (sendMessage)
            dbm.set(DatabaseMessages.DBM_DISCONNECT);
        
        reset();
    }
    
    
    private Connection localConnection = null;
    private boolean useLocalFile = false;

    public void useLocalFile(String path) {
        disconnect();
        File f = new File(path);
        
        if (f.exists()) {
            sqlURL = "jdbc:sqlite:" + f.getAbsolutePath();
            sshUser = "Local";
            if (!useLocalFile) {
                dbm.set(DatabaseMessages.DBM_NONE);
                dbm.set(DatabaseMessages.DBM_CONNECT);
                dbm.set(DatabaseMessages.DBM_CONNECT_TO_LOCAL);
            }
        }
        useLocalFile = true;
        
    }
    
    public final Connection getSilentConnection() {
        Connection c = null;
        
        try {
            c = getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return c;
        }
    }
    
    /**
     * Get a connection to the database.
     * 
     * @return java.sql.Connection
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     * @throws Exception
     */
    public final Connection getConnection() 
            throws 
                ClassNotFoundException, 
                SQLException, 
                InstantiationException, 
                IllegalAccessException, 
                Exception {
                
            if (this.useLocalFile) {

                if (localConnection != null) {
                    localConnection.close();
                    localConnection = null;
                }

                Class.forName("org.sqlite.JDBC");

                SQLiteConfig config = new SQLiteConfig();  
                config.enforceForeignKeys(true);  

                String url = sqlURL;//"jdbc:sqlite:" + toFile;

                localConnection = DriverManager.getConnection(url, config.toProperties());
               // dbm.set(DatabaseMessages.DBM_NONE);
               // dbm.set(DatabaseMessages.DBM_CONNECT);
               // dbm.set(DatabaseMessages.DBM_CONNECT_TO_LOCAL);
                return localConnection;
            }
        
            if (sshSession.isConnected() == false) {
                Exception e = sshConnect(
                    sshServer, 
                    sshPort, 
                    sshUser, 
                    sshPassword, 
                    hostKeyCheck, 
                    useHost, 
                    sqlDatabase, 
                    sqlUser, 
                    sqlPassword, 
                    sqlPort
                );  
                if (e == null) {
                    throw e;
                }
            }
        
        Connection conn = null;
        
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection(sqlURL, sqlUser, sqlPassword);
        
        return conn;
    }
    
    /**
     * get the SQL user name.
     * 
     * @return String
     */
    public final String getSQLUser() {
        return sqlUser;
    }
    
    /**
     * get the SSH user name.
     * 
     * @return String
     */
    
    public final String getSSHUser() {
        return sshUser;
    }
    
    public final ExecuteQuery getExecuteQuery(String sql) {
        try {
            return new ExecuteQuery(getConnection(), sql);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public class ExecuteQuery {
        private Connection connection;
        private Statement statement;
        private ResultSet resultSet;
        
        public ExecuteQuery(Connection source, String sql) {
            if (isConnected()) {
                try {
                    connection = source;
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        }
        
        public final ResultSet getResult() {
            return resultSet;
        }
                
        public final void close() {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            resultSet = null;
            statement = null;
            connection = null;
        }

    }
    
    public void reset() {}
    
    public Exception login(Properties credentials) {
        ArrayList<String> message = new ArrayList();
        
        if (credentials == null) 
            message.add("NULL Pointer");
        if (credentials.getProperty("SSH_SERVER") == null)  
            message.add("SSH_SERVER");
        if (credentials.getProperty("SSH_USER") == null)
            message.add("SSH_USER");
        if (credentials.getProperty("SQL_USER") == null)
            message.add("SQL_USER");
        if (credentials.getProperty("SSH_PORT") == null)
            message.add("SSH_PORT");
        if (credentials.getProperty("SQL_PORT") == null)
            message.add("SQL_PORT");
        if (credentials.getProperty("SQL_DATABASE") == null)
            message.add("SQL_DATABASE");
        if (credentials.getProperty("HOST") == null)
            message.add("HOST");
        if (credentials.getProperty("SSH_PASSWORD") == null)
            message.add("SSH_PASSWORD");
        if (credentials.getProperty("SQL_PASSWORD") == null)
            message.add("SQL_PASSWORD");
         
        if (!message.isEmpty()) {
            
            if (sshSession != null) {
                if (
                    !credentials.getProperty("SQL_USER").equalsIgnoreCase(sqlUser) ||
                    !credentials.getProperty("SSH_USER").equalsIgnoreCase(sshUser) ||
                    !credentials.getProperty("SQL_DATABASE").equalsIgnoreCase(sqlDatabase) ||
                    !credentials.getProperty("HOST").equalsIgnoreCase(useHost) 
                ) {
                    return new Exception("Already connected to the same...");
                }
            
            }
            
            StringBuilder msg = new StringBuilder("Error: missing ");
            for(String s: message) {
                msg.append("[").append(s).append("]");
            }
            return new Exception(msg.toString());
        }
        
        return sshConnect(
            credentials.getProperty("SSH_SERVER"),
            Integer.parseInt(credentials.getProperty("SSH_PORT")),
            credentials.getProperty("SSH_USER"),
            credentials.getProperty("SSH_PASSWORD"),
            false,
            credentials.getProperty("HOST"),
            credentials.getProperty("SQL_DATABASE"),
            credentials.getProperty("SQL_USER"),
            credentials.getProperty("SQL_PASSWORD"),
            Integer.parseInt(credentials.getProperty("SQL_PORT"))
        );
    }
    
    public static Properties createCredentials(
        String sshServer,
        String sshUser,
        String sshPort,
        String sqlPort,
        String sqlDatabase,
        String host,
        String sqlUser,
        String sshPassword,
        String sqlPassword
    ) {
        Properties props = new Properties();
        
        props.setProperty("SSH_SERVER", sshServer);
        props.setProperty("SSH_USER", sshUser);
        props.setProperty("SSH_PORT", sshPort);
        props.setProperty("SQL_PORT", sqlPort);
        props.setProperty("SQL_DATABASE", sqlDatabase);
        props.setProperty("HOST", host);
        props.setProperty("SQL_USER", sqlUser);
        props.setProperty("SSH_PASSWORD", sshPassword);
        props.setProperty("SQL_PASSWORD", sqlPassword);
        return props;
    }
    
    public void clearData() { }
}
