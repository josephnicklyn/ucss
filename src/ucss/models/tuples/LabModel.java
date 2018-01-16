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
public class LabModel extends Model {
    private String labName = "";
    
    public LabModel(int id) {
        super(id);
    }
    
    public LabModel(
        int id,
        String labName
    ) {
        super(id);
        setLabName(labName);
    }
    
    public LabModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("labID"));
        setLabName(rs.getString("labName"));
        add(this);
    }
    
    public final void setLabName(String value) {
        if (value != null)
            labName = value;
        else
            labName = "";
    }
    
    public final String getLabName() {
        return labName;
    }
    
    @Override public String toString() {
        return labName;
    }
}
