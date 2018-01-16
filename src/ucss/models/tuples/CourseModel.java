/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.tuples;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author John
 */
public class CourseModel extends Model {
    
    private int 
        department,
        credits;

    public String 
        courseNumber,
        courseTitle,
        courseDescription,
        prerequisites;
    
    private int
        meetingsPerWeekHint,
        meetingsPerWeekAreLabHints;
    
    private boolean hasWhiteboard;
    private boolean hasChalkboard;
    private boolean hasProjector;
    private boolean hasTieredSeating;
    private boolean hasMoveableSeating;
    private boolean hasWindows;
    private int labType;
    
    public CourseModel(int id) {
        super(id);
    }
    
    public CourseModel(
        int id,
        int department,
        int credits,
        String courseNumber,
        String courseTitle,
        String courseDescription,
        boolean hasWhiteboard,
        boolean hasChalkboard,
        boolean hasProjector,
        boolean hasTieredSeating,
        boolean hasMoveableSeating,
        boolean hasWindows,
        int labType,
        String prerequisites,
        int meetingsPerWeekHint,
        int meetingsPerWeekArLabHints
    ) {
        super(id);
        setDepartment(department);
        setCredits(credits);
        setCourseNumber(courseNumber);
        setCourseTitle(courseTitle);
        setCourseDescription(courseDescription);
        setHasWhiteboard(hasWhiteboard);
        setHasChalkboard(hasChalkboard);
        setHasProjector(hasProjector);
        setHasTieredSeating(hasTieredSeating);
        setHasMoveableSeating(hasMoveableSeating);
        setHasWindows(hasWindows);
        setLabType(labType);
        setPrerequisites(prerequisites);
        setMeetingsPerWeekHint(meetingsPerWeekHint);
        setMeetingsPerWeekAreLabHints(meetingsPerWeekAreLabHints);
    }
    
    public CourseModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("courseID"));
        
        setDepartment(rs.getInt("department"));
        setCredits(rs.getInt("credits"));
        setCourseNumber(rs.getString("courseNumber"));
        setCourseTitle(rs.getString("courseTitle"));
        setCourseDescription(rs.getString("courseDescription"));
        setHasWhiteboard(rs.getInt("useWhiteBoard") != 0);
        setHasChalkboard(rs.getInt("useChalkBoard") !=  0);
        setHasProjector(rs.getInt("useProjector") !=  0);
        setHasTieredSeating(rs.getInt("useTieredSeating") !=  0);
        setHasMoveableSeating(rs.getInt("useMoveableSeating") !=  0);
        setHasWindows(rs.getInt("useWindows") !=  0);
        setLabType(rs.getInt("labType"));
        setPrerequisites(rs.getString("prerequisites"));
        setMeetingsPerWeekHint(rs.getInt("meetingsPerWeekHint"));
        setMeetingsPerWeekAreLabHints(rs.getInt("meetingsPerWeekAreLabHints"));
        add(this);
    }
    
    public final void setDepartment(int value) { department = value; }
    public final void setCredits(int value) { credits = value; }
    public final void setCourseNumber(String value) {
        if (value != null)
            courseNumber = value;
        else 
            courseNumber = "";
    }
    
    public final void setCourseTitle(String value) {
        if (value != null)
            courseTitle = value;
        else 
            courseTitle = "";
    }
    
    public final void setCourseDescription(String value) {
        if (value != null)
            courseDescription = value;
        else 
            courseDescription = "";
    }
    public final String getCourseDescription() { return courseDescription; }
    
    public final void setPrerequisites(String value) {
        if (value != null)
            prerequisites = value;
        else 
            prerequisites = "";
    }
    public final String getPrerequisites() { return prerequisites; }
    
    public final void setMeetingsPerWeekHint(int value) { meetingsPerWeekHint = value; }
    public final int getMeetingsPerWeekHint() { return meetingsPerWeekHint; }
    
    public final void setMeetingsPerWeekAreLabHints(int value) { meetingsPerWeekAreLabHints = value; }
    public final int getMeetingsPerWeekAreLabHints() { return meetingsPerWeekAreLabHints; }
    
    public final int getDepartment() { return department; }
    public final int getCredits() { return credits; }
    public final String getCourseNumber() { return courseNumber; }
    public final String getCourseTitle() { return courseTitle; }
    
    public final void setHasWhiteboard(boolean value) { hasWhiteboard = value; }
    public final void setHasChalkboard(boolean value) { hasChalkboard = value; }
    public final void setHasProjector(boolean value) { hasProjector = value; }
    public final void setHasTieredSeating(boolean value) { hasTieredSeating = value; }
    public final void setHasMoveableSeating(boolean value) { hasMoveableSeating = value; }
    public final void setHasWindows(boolean value) { hasWindows = value; }
    public final void setLabType(int value) { labType = value; }
    
    public final boolean getHasWhiteboard() { return hasWhiteboard; }
    public final boolean getHasChalkboard() { return hasChalkboard; }
    public final boolean getHasProjector() { return hasProjector; }
    public final boolean getHasTieredSeating() { return hasTieredSeating; }
    public final boolean getHasMoveableSeating() { return hasMoveableSeating; }
    public final boolean getHasWindows() { return hasWindows; }
    public final int getLabType() { return labType; }
    
    public final String getLabTypeName() {
        LabModel lm = Model.getLab(labType);
        return (lm!=null)?lm.getLabName():"";
    }
    
    
    public final String getDepartmentName() {
        DepartmentModel dm = getDepartmentModel();
        return (dm!=null)?dm.getDepartmentName():"";
    }
    
            
    public final DepartmentModel getDepartmentModel() {
        return Model.getDepartment(department);
    }
    
    @Override public String toString() {
        return courseTitle;
    }
}
