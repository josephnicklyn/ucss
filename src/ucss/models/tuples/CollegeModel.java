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
public class CollegeModel extends Model {
    
    private String collegeName = "";
    
    public CollegeModel(int id) {
        super(id);
    }
    
    public CollegeModel(
        int id,
        String collegeName
    ) {
        setCollegeName(collegeName);
    }
    
    public CollegeModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("collegeID"));
        setCollegeName(rs.getString("collegeName"));
        add(this);
    }
    
    public final void setCollegeName(String value) {
        if (value != null)
            collegeName = value;
        else
            collegeName = "";
    }
    
    public final String getCollegeName() {
        return collegeName;
    }
    
    @Override public String toString() {
        return collegeName;
    }
    
}
