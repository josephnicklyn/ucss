/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.tuples;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import ucss.controllers.GraphController;

/**
 *
 * @author John
 */
public class MeetingModel extends Model {
    
    private int
        room,
        course,
        semester,
        professor,
        section,
        onDay,
        startTime,
        duration;

    private static int autoID = -1;
    
    public MeetingModel(int id) {
        super(id);
        clearFlags();
    }
    
    public MeetingModel(
        int id,
        int room,
        int course,
        int semester,
        int professor,
        int section,
        int onDay,
        int startTime,
        int duration
    ) {
        super(id);
        
        setRoom(room);
        setCourse(course);
        setSemester(semester);
        setProfessor(professor);
        setSection(section);
        setOnDay(onDay);
        setStartTime(startTime);
        setDuration(duration);
        clearFlags();
    }
    
    public MeetingModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("meetingID"));
        setRoom(rs.getInt("room"));
        setCourse(rs.getInt("course"));
        setSemester(rs.getInt("semester"));
        setProfessor(rs.getInt("professor"));
        setSection(rs.getInt("section"));
        setOnDay(rs.getInt("onDay"));
        setStartTime(rs.getInt("startTime"));
        setDuration(rs.getInt("duration"));
        add(MeetingModel.this);
        clearFlags();
    }
    
    @Override public String getDatabaseActionString() { 
        String sql = "getDatabaseActionString";
        if (isFlaggedForRemoval()) {
            sql = "DELETE FROM sectionMeetings WHERE meetingID = " + getModelID();
        } else if (isFlaggedForInsertion()) {
            if (getModelID() < 0) {
                sql = "INSERT INTO sectionMeetings " + 
                         "(room, course, semester, professor, section, onDay, startTime, duration) " + 
                         "values (?, ?, ?, ?, ?, ?, ?, ?);";
            } else {
                sql = "REPLACE INTO sectionMeetings " + 
                         "(room, course, semester, professor, section, onDay, startTime, duration, meetingID) " + 
                         "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
        } else if (isFlaggedForUpdate()) {
            sql = 
                "UPDATE sectionMeetings SET " +
                "room = ?, course = ?, semester = ?, professor = ?, " + 
                "section = ?, onDay = ?, startTime = ?, duration = ? " +
                "WHERE meetingID = " + getModelID();
        } 
        return sql;
    }
    
    @Override public void executeDatabaseAction(Connection conn) throws Exception { 
        if (conn == null)
            throw new NullPointerException();
        
        String sql = getDatabaseActionString();

        if (!sql.isEmpty()) {
            if (isFlaggedForRemoval()) {
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                stmt.close();
                //GraphController.removeMeetingsFor(this);
            } else if (isFlaggedForInsertion()) {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, room);
                ps.setInt(2, course);
                ps.setInt(3, semester);
                ps.setInt(4, professor);
                ps.setInt(5, section);
                ps.setInt(6, onDay);
                ps.setInt(7, startTime);
                ps.setInt(8, duration);
                if (getModelID() > 0) {
                    ps.setInt(9, getModelID());
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        setModelID(rs.getInt(1));
                    }
                } else {
                    ps.executeUpdate();
                }
                
                
                ps.close();
                
            } else if (isFlaggedForUpdate()) {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, room);
                ps.setInt(2, course);
                ps.setInt(3, semester);
                ps.setInt(4, professor);
                ps.setInt(5, section);
                ps.setInt(6, onDay);
                ps.setInt(7, startTime);
                ps.setInt(8, duration);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    setModelID(rs.getInt(1));
                }
                
                ps.close();
                
            }
        } else {
            throw new Exception("SQL statment empty.");
        }
        clearFlags();
    }
    
    
    public final void setRoom(int value) {
        if (value != room) {
            int oldValue = room;
            room = value; 
            triggerEvent(MeetingModelChange.ROOM, oldValue);
        }
    }
    
    public final void setCourse(int value) { 
        if (value != course) {
            int oldValue = course;
            course = value; 
            triggerEvent(MeetingModelChange.COURSE, oldValue);
        }
            
    }
    public final void setSemester(int value) { 
        if (value != semester) {
            int oldValue = semester;
            semester = value; 
            triggerEvent(MeetingModelChange.SEMESTER, oldValue);
        }
    }
    
    public final void setProfessor(int value) { 
        if (value != professor) {
            int oldValue = professor;
            professor = value; 
            triggerEvent(MeetingModelChange.PROFESSOR, oldValue);
        }
    }
    
    public final void setSection(int value) { 
        if (value != section) {
            int oldValue = section;
            section = value; 
            triggerEvent(MeetingModelChange.SECTION, oldValue);
        }
    }
    
    public final void setOnDay(int value) { 
        if (value != onDay) {
            int oldValue = onDay;
            onDay = value; 
            triggerEvent(MeetingModelChange.ONDAY, oldValue);
        }
    }
    
    public final void setStartTime(int value) { 
        if (value != startTime) {
            int oldValue = startTime;
            startTime = value; 
            triggerEvent(MeetingModelChange.STARTTIME, oldValue);
        }
    }
    
    public final void setDuration(int value) { 
        if (value != duration) {
            int oldValue = duration;
            duration = value; 
            triggerEvent(MeetingModelChange.DURATION, oldValue);
        }
    }
    
    public final int getRoom() { return room; }
    public final int getCourse() { return course; }
    public final int getSemester() { return semester; }
    public final int getProfessor() { return professor; }
    public final int getSection() { return section; }
    public final int getOnDay() { return onDay; }
    public final int getStartTime() { return startTime; }
    public final int getDuration() { return duration; }
    
    private String meetingsString = null;
    
    public final CourseModel getCourseModel() {
        return Model.getCourse(course);
    }
    
    public final String getMeetingString() {
        if (meetingsString == null) {
            if (getCourseModel() != null) {
                meetingsString = 
                    String.format("%s - %s", 
                        getCourseModel().getDepartmentModel(),                
                        getCourseModel().getCourseNumber()
                    );
            }
            
        }
        return (meetingsString==null?"[null]":meetingsString);
        
    }
    
    public final String getMeetingStringLong() {
        return getMeetingString() + "/" + getCourseModel().getCourseTitle();
    }

    public final int getDepartmentID() {
        CourseModel fCourse = Model.getCourse(course);
        if (fCourse != null)
            if (Model.getDepartment(fCourse.getDepartment()) != null)
                return Model.getDepartment(fCourse.getDepartment()).modelID;
        
        return -1;
    }
    
    public final String getProfessorName() {
        ProfessorModel pm = Model.getProfessor(professor);
        if (pm != null)
            return pm.toString();
        else
            return "";
    }
    
    public final boolean isSameSection(MeetingModel m) {
        if (m == null)
            return false;
        if (m == this)
            return true;
        return (
                m.getCourse() == getCourse() && 
                m.getSection() == getSection() && 
                m.getSemester() == getSemester());
    }
    
    @Override public void set(Model source) { 
        if (source instanceof MeetingModel) {
            MeetingModel m = (MeetingModel)source;
            if (m.getModelID() == source.getModelID()) {
                room = m.getRoom();
                //course = m.course;
                //semester = m.semester;
                professor = m.getProfessor();
                //section = m.section;
                onDay = m.getOnDay();
                startTime = m.getStartTime();
            }
        }
    }
    
    @Override public Model copy() { 
        return new MeetingModel(
            modelID, 
            room, 
            course, 
            semester, 
            professor, 
            section, 
            onDay, 
            startTime, 
            duration
        );
    }
    
    public final MeetingModel copyAndStore() throws Exception { 
        MeetingModel model = null;
        TermModel term = Model.getTerm(semester);
        if (term != null) {
            model = new MeetingModel(
                getDecrementedID(), 
                room, 
                course, 
                semester, 
                professor, 
                section, 
                onDay, 
                startTime, 
                duration
            );                   
            term.getMeetings().add((MeetingModel)model);
        } else { 
            System.out.println("ERROR");
            throw new NullPointerException("The term with id [" + semester + "] could not be found.");
        }
        return model; 
    }
    
    public String getRoomString() {
        return getMeetingString() + "/" + Model.getRoom(getRoom());
    }
    
    @Override public String getString() {
        return "Meeting Model = [ " +
            "modelID = " + modelID +
            ", room = " + room + 
            ", course = " + course +
            ", semester = " + semester +
            ", professor = " + professor +
            ", section = " + section +
            ", onDay = " + onDay +
            ", startTime = " + startTime +
            ", duration = " + duration;
    }
    
    @Override public String toString() {
        return getMeetingString() + "\n" + getProfessorName();
    }

    private void triggerEvent(MeetingModelChange change, int oldValue) {
        super.flagForUpdate();
        if (getOnChanged() != null) {
            getOnChanged().handle(new MeetingModelChangeEvent(MeetingModel.this, change, oldValue) );
        }
    }
    
    public static final ObjectProperty<EventHandler<MeetingModelChangeEvent>> onChangedProperty() { return onChanged; }
    public static final void setOnChanged(EventHandler<MeetingModelChangeEvent> value) { onChangedProperty().set(value); }
    public static final EventHandler<MeetingModelChangeEvent> getOnChanged() { return onChangedProperty().get(); }
    private static ObjectProperty<EventHandler<MeetingModelChangeEvent>> onChanged = new ObjectPropertyBase<EventHandler<MeetingModelChangeEvent>>() {
       
        @Override
        public Object getBean() {
            return MeetingModel.class;
        }

        @Override
        public String getName() {
            return "onMeetingModelChangeEvent";
        }

       
    };
    
}
