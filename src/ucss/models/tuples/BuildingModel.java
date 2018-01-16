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
public class BuildingModel extends Model {
 
    private String 
        buildingCode = "",
        buildingName = "";
    
    public BuildingModel(int id) {
        super(id);
    }
    
    public BuildingModel(
        int id,
        String buildingCode,
        String buildingName
    ) {
        super(id);
        setBuildingCode(buildingCode);
        setBuildingName(buildingName);
    }
    
    public BuildingModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("buildingID"));
        setBuildingCode(rs.getString("buildingCode"));
        setBuildingName(rs.getString("buildingName"));
        add(this);
    }
    
    public final void setBuildingCode(String value) {
        if (value != null)
            buildingCode = value;
        else
            buildingCode = "";
    }
    
    public final String getBuildingCode() {
        return buildingCode;
    }
    
    public final void setBuildingName(String value) {
        if (value != null)
            buildingName = value;
        else
            buildingName = "";
    }
    
    public final String getBuildingName() {
        return buildingName;
    }
    
    @Override public String toString() {
        return (buildingName.isEmpty()?buildingCode:buildingName);
    }
    
    
}
