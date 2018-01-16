/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.tuples;

/**
 *
 * @author John
 */
public enum ProjectModels {
    
    EMPTY(0),
    
    BUILDING_MODEL(1), 
    
    COLLEGE_MODEL(2),
    
    COURSE_MODEL(3),
    
    DEPARTMENT_MODEL(4),
    
    LAB_MODEL(5),
    
    MEETING_MODEL(6),
    
    PROFESSOR_MODEL(7),
    
    ROOM_MODEL(8),
    
    TERM_MODEL(9);
    
    private final int pmIndex;
    
    private final String[] sqlSelects = {
        "",
        "SELECT * FROM buildings ORDER BY buildingCode;",
        "SELECT * FROM college ORDER BY collegeName;",
        "SELECT * FROM courses ORDER BY department, courseNumber;",
        "SELECT * FROM departments ORDER BY departmentName;",
        "SELECT * FROM labs ORDER BY labName;",
        "SELECT * FROM sectionMeetings ORDER BY course, section;",
        "SELECT * FROM professors ORDER BY professorName;",
        "SELECT * FROM rooms ORDER BY building, roomNumber;",
        "SELECT * FROM semester ORDER BY title;"
    };
    
    private ProjectModels(int value) {
        pmIndex = value;
    }
    
    public final String getSelectStatment() {
        return sqlSelects[pmIndex];
    }
}
