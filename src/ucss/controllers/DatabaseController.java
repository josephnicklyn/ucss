/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.controllers;

/**
 *
 * @author John
 */

import ucss.models.database.DBConnect;
import ucss.models.tuples.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joseph Nicklyn JR.
 */
public class DatabaseController extends DBConnect {
    private static DatabaseController instance;
    
    public DatabaseController() {
        initialize();
    }
    
    public DatabaseController(String path) {
        super(path);
        initialize();
    }
    
    @Override public void reset() {
        Model.reset();
        //System.out.println("CLEARING MODELS");
    }

    private void initialize() {
        Model.setOnInitialRequest( (e, o, n) -> { loadDataFor(n); });
        Model.setOnInitialMeetingsRequest( (e, o, n) -> {loadDataForTerm(n);});
    }

    private boolean loadingData = false;
    
    private void loadDataFor(ProjectModels n) {
        //System.out.println("LOADING = " + n);
        if (loadingData)
            return;
        
        loadingData = true;
        
        if (n == null) 
            return;
        
        if (super.isConnected()) {

            String sql = n.getSelectStatment();
        
            Connection c = null;
            ResultSet rs = null;
            Statement stmt = null;
            
            try {
                c = super.getConnection();
                
                stmt = c.createStatement();
                
                rs = stmt.executeQuery(sql);
                
                switch (n) {
                    case BUILDING_MODEL:
                        while (rs.next()) new BuildingModel(rs);
                        break;
                    case COLLEGE_MODEL:
                        while (rs.next()) new CollegeModel(rs);
                        break;
                    case COURSE_MODEL:
                        while (rs.next()) new CourseModel(rs);
                        break;
                    case DEPARTMENT_MODEL:
                        while (rs.next()) new DepartmentModel(rs);
                        break;
                    case LAB_MODEL:
                        while (rs.next()) new LabModel(rs);
                        break;
                    case MEETING_MODEL:
                        while (rs.next()) new MeetingModel(rs);
                        break;
                    case PROFESSOR_MODEL:
                        while (rs.next()) new ProfessorModel(rs);
                        break;
                    case ROOM_MODEL:
                        while (rs.next()) new RoomModel(rs);
                        break;
                    case TERM_MODEL:
                        while (rs.next()) new TermModel(rs);
                        break;
                }
            } catch (SQLException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                    if (c != null)
                        c.close();
                } catch (Exception ed) {
                    
                }
            }
        }
        loadingData = false;
    }

    private void loadDataForTerm(TermModel n) {
        if (loadingData)
            return;
        
        loadingData = true;
        
        if (n == null) 
            return;
        
        if (super.isConnected()) {

            String sql =  String.format(
                    "SELECT * FROM sectionMeetings WHERE semester = %d ORDER BY course, section;",
                    n.getModelID()
                );
        
            Connection c = null;
            ResultSet rs = null;
            Statement stmt = null;
            
            try {
                c = super.getConnection();
                
                stmt = c.createStatement();
                
                rs = stmt.executeQuery(sql);
                
                while (rs.next()) new MeetingModel(rs);
                
            } catch (SQLException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                    if (stmt != null)
                        stmt.close();
                    if (c != null)
                        c.close();
                } catch (Exception ed) {
                    
                }
            }
        }
        loadingData = false;
    }
    
    public static DatabaseController getInstance() {
        if (instance == null)
            instance = new DatabaseController();
        return instance;
    }

    boolean hasAChange() {
        for(int ti: Model.getTerms().keySet()) {
            TermModel tm = Model.getTerms().get(ti);
            for(MeetingModel mm: tm.getMeetings()) {
                if (mm.requiresDatabaseAction()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    void saveMeetings() throws Exception {
        Connection c = null;
        for(int ti: Model.getTerms().keySet()) {
            TermModel tm = Model.getTerms().get(ti);
            for(MeetingModel mm: tm.getMeetings()) {
                if (mm.requiresDatabaseAction()) {
                    if (c == null)
                        c = super.getConnection();
                    System.out.println(mm + " -> " + mm.getDatabaseActionString());
                    mm.executeDatabaseAction(c);
                }
            }
        }
        if (c != null)
            c.close();
    }
    
}