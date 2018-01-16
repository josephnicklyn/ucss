/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author John
 */
public class LoginView extends GridPane {
    private final TextField 
            sshServer = new TextField("csis.svsu.edu"),
            sshUser = new TextField(""),
            sshPort = new TextField("22"),
            sqlUser = new TextField(""),
            sqlPort = new TextField("3306"),
            sqlDatabase = new TextField(""),
            txtHost = new TextField("localhost");
            
    private final PasswordField 
            sshPassword = new PasswordField(),
            sqlPassword = new PasswordField();

    private final CheckBox hostChecking = new CheckBox("Strict Host Key Checking");
    
    private final Button btnLogin = new Button("Login"),
                         btnCancel = new Button("Cancel"),
                         btnLocal = new Button("Local File");
    private final HBox spacerBox = new HBox(btnLocal);
    private final HBox buttonBar = new HBox(6, spacerBox, btnLogin, btnCancel);

    public LoginView() {
        initialize();
        sshPassword.setText("");
        sqlPassword.setText("");
        
    }

    private void initialize() {
        
        setMaxWidth(500);
        
        HBox.setHgrow(spacerBox, Priority.ALWAYS);
        spacerBox.setAlignment(Pos.CENTER_LEFT);
        
        btnLogin.setDefaultButton(true);
        btnCancel.setCancelButton(true);

        
        setVgap(8);
        setHgap(8);
        buttonBar.setStyle("-fx-padding:8 0;-fx-alignment:CENTER-RIGHT;");
        
        ColumnConstraints c1 = new ColumnConstraints(100);
        ColumnConstraints c2 = new ColumnConstraints();
        ColumnConstraints c3 = new ColumnConstraints(80);
        
        c2.setHgrow(Priority.ALWAYS);
        getStyleClass().add("login-widget");
        getColumnConstraints().addAll(c1, c2, c3);
        Label labTitle = new Label("Connect to Database");
        labTitle.getStyleClass().add("header");
        
        Label labSSH = new Label("SSH Connection");
        Label labSQL = new Label("SQL Connection");
        Label labHost = new Label("Host");
        
        labTitle.setMaxWidth(Double.MAX_VALUE);
       
        labSSH.setStyle("-fx-font-weight:bold;-fx-padding:4 0;");
        labSQL.setStyle("-fx-font-weight:bold;-fx-padding:4 0;");
        labHost.setStyle("-fx-font-weight:bold;-fx-padding:4 0;");
        
        add(labTitle, 0, 0, 3, 1);
        add(labSSH, 0, 1);
        
        add(new Label("Server\\Port"), 0, 2);
        add(sshServer, 1, 2);
        add(sshPort, 2, 2);
        
        add(new Label("User"), 0, 3);
        add(sshUser, 1, 3, 2, 1);
        
        add(new Label("Password"), 0, 4);
        add(sshPassword, 1, 4, 2, 1);
        
        add(labSQL, 0, 5);
        
        add(new Label("Database\\Port"), 0, 6);
        add(sqlDatabase, 1, 6);
        add(sqlPort, 2, 6);
        
        add(new Label("User"), 0, 7);
        add(sqlUser, 1, 7, 2, 1);
        
        add(new Label("Password"), 0, 8);
        add(sqlPassword, 1, 8, 2, 1);
        
        
        add(labHost, 0, 9);
        add(new Label("Host"), 0, 10);
        add(txtHost, 1, 10);
        
        add(hostChecking, 1, 11);

        add(buttonBar, 1, 12, 2, 1);
        
    }
 
    public final void setOnUselLocalFile(EventHandler<ActionEvent> value) {
        btnLocal.setOnAction(value);
    }
    
    public final void setOnCancel(EventHandler<ActionEvent> value) {
        btnCancel.setOnAction(value);
    }

    public final void setOnLogin(EventHandler<ActionEvent> value) {
        btnLogin.setOnAction(value);
    }
    
    public final void setCredentials(Properties credentials) {
        if (credentials != null) {
            sshServer.setText("");
            sshUser.setText("");
            sqlUser.setText("");
            sshPort.setText("");
            sshPort.setText("");
            sqlDatabase.setText("");
            txtHost.setText("");
            sshPassword.setText("");
            sqlPassword.setText("");
            return;
        }
        if (credentials.getProperty("SSH_SERVER") != null)  
            sshServer.setText(credentials.getProperty("SSH_SERVER"));
        else
            sshServer.setText("");
        
        if (credentials.getProperty("SSH_USER") != null)
            sshUser.setText(credentials.getProperty("SSH_USER"));
        else
            sshUser.setText("");
        
        if (credentials.getProperty("SQL_USER") == null)
            sqlUser.setText(credentials.getProperty("SQL_USER"));
        else
            sqlUser.setText("");
        
        if (credentials.getProperty("SSH_PORT") != null)
            sshPort.setText(credentials.getProperty("SSH_PORT"));
        else
            sshPort.setText("");
        
        if (credentials.getProperty("SQL_PORT") != null)
            sqlPort.setText(credentials.getProperty("SQL_PORT"));
        else
            sqlPort.setText("");
        
        if (credentials.getProperty("SQL_DATABASE") != null)
            sqlDatabase.setText(credentials.getProperty("SQL_DATABASE"));
        else
            sqlDatabase.setText("");
        
        if (credentials.getProperty("HOST") != null)
            txtHost.setText(credentials.getProperty("HOST"));
        else
            txtHost.setText("");
        
        if (credentials.getProperty("SSH_PASSWORD") != null)
            sshPassword.setText(credentials.getProperty("SSH_PASSWORD"));
        else
            sshPassword.setText("");
        
        if (credentials.getProperty("SQL_PASSWORD") != null)
            sqlPassword.setText(credentials.getProperty("SQL_PASSWORD"));
        else
            sqlPassword.setText("");

    }
    
    public final Properties getCredentials() {
        Properties props = new Properties();
        
        props.setProperty("SSH_SERVER", sshServer.getText());
        props.setProperty("SSH_USER", sshUser.getText());
        
        props.setProperty("SSH_PORT", sshPort.getText());
        props.setProperty("SQL_PORT", sqlPort.getText());
        props.setProperty("SQL_DATABASE", sqlDatabase.getText());
        props.setProperty("HOST", txtHost.getText());
        props.setProperty("SQL_USER", sqlUser.getText());
        props.setProperty("SSH_PASSWORD", sshPassword.getText());
        props.setProperty("SQL_PASSWORD", sqlPassword.getText());
        
        return props;
    }
    
}
