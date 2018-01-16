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
public class DepartmentModel extends Model {
    
    private String 
        departmentName = "",
        description = "";
    
    private int college;
    
    public DepartmentModel(int id) {
        super(id);
    }
    
    public DepartmentModel(
        int id,
        String departmentName,
        String description,
        int college
    ) {
        super(id);
        setDepartmentName(departmentName);
        setDescription(description);
        setCollege(college);
    }
    
    public DepartmentModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("departmentID"));
        setDepartmentName(rs.getString("departmentName"));
        setDescription(rs.getString("description"));
        setCollege(rs.getInt("college"));
        add(this);
    }
    
    
    public final void setDepartmentName(String value) {
        if (value != null)
            departmentName = value;
        else
            departmentName = "";
    }
    
    public final String getDepartmentName() {
        return departmentName;
    }
    
    public final void setDescription(String value) {
        if (value != null)
            description = value;
        else
            description = "";
    }
    
    public final String getDescription() {
        return description;
    }
    
    public final void setCollege(int value) {
        college = value;
    }
    
    public final int getCollege() {
        return college;
    }
    
    @Override public String toString() {
        return departmentName;
    }
    
    private boolean isSelected = false;
    private static int selectCount = 0;
    public final void setSelected(boolean value) {
        isSelected = value;
        if (value) 
            selectCount++;
        else
            selectCount--;
    }
    
    public final boolean isSelected() {
        return isSelected;
    }
    
    public static boolean someSelected() {
        return selectCount != 0;
    }
    
    public final String getCollegeName() {
        for(CollegeModel c: Model.getColleges()) {
            if (c.getModelID() == college)
                return c.toString();
        }
        return "unassocated";
    }
    
}
