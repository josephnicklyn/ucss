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
public class ProfessorModel extends Model {
    
    public String 
        professorKey = "",
        professorName = "";
    
    public int department, secondaryDepartment;

    public ProfessorModel(int id) {
        super(id);
    }
    
    public ProfessorModel(
        int id,
        String professorKey,
        String professorName,
        int department,
        int secondaryDepartment
    ) {
        setProfessorKey(professorKey);
        setProfessorName(professorName);
        setDepartment(department);
        setSecondaryDepartment(secondaryDepartment);
    }
    
    public ProfessorModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("professorID"));
        setProfessorKey(rs.getString("professorKey"));
        setProfessorName(rs.getString("professorName"));
        setDepartment(rs.getInt("department"));
        setSecondaryDepartment(rs.getInt("secondaryDepartment"));
        add(this);
    }
    
    public final void setProfessorKey(String value) {
        if (value != null)
            professorKey = value;
        else
            professorKey = "";
    }
    
    public final String getProfessorKey() {
        return professorKey;
    }
    
    public final void setProfessorName(String value) {
        if (value != null)
            professorName = value;
        else
            professorName = "";
    }
    
    public final String getProfessorName() {
        return professorName;
    }
    
    public final void setDepartment(int value) {
        department = value;
    }
    
    public final int getDepartment() {
        return department;
    }
    
    public final void setSecondaryDepartment(int value) {
        secondaryDepartment = value;
    }
    
    public final int getSecondaryDepartment() {
        return secondaryDepartment;
    }
    
    public final String getPrimaryDepartmentAsString() {
        DepartmentModel dm = Model.getDepartment(department);
        return (dm!=null)?dm.toString():"";
    }
    
    public final String getSecondaryDepartmentAsString() {
        DepartmentModel dm = Model.getDepartment(secondaryDepartment);
        return (dm!=null)?dm.toString():"";
    }
    
    @Override public String toString() {
        return professorName;
    }
}
