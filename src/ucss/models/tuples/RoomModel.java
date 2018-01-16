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
public class RoomModel extends Model {

    private int building;
    private String roomNumber = "";
    private String locationName = "";
    private int capacity;
    private String defaultLayout = "";
    private boolean hasWhiteboard;
    private boolean hasChalkboard;
    private boolean hasProjector;
    private boolean hasTieredSeating;
    private boolean hasMoveableSeating;
    private boolean hasWindows;
    private int labType;
    
    public RoomModel(int id) {
        super(id);
    }
    
    public RoomModel(
       int id,
       int building,
       String roomNumber,
       String locationName,
       int capacity,
       String defaultLayout,
       boolean hasWhiteboard,
       boolean hasChalkboard,
       boolean hasProjector,
       boolean hasTieredSeating,
       boolean hasMoveableSeating,
       boolean hasWindows,
       int labType
       
    ) {
        super(id);
        setBuilding(building);
        setRoomNumber(roomNumber);
        setLocationName(locationName);
        setCapacity(capacity);
        setDefaultLayout(defaultLayout);
        setHasWhiteboard(hasWhiteboard);
        setHasChalkboard(hasChalkboard);
        setHasProjector(hasProjector);
        setHasTieredSeating(hasTieredSeating);
        setHasMoveableSeating(hasMoveableSeating);
        setHasWindows(hasWindows);
        setLabType(labType);
    }
    
    public RoomModel(ResultSet rs) throws SQLException {
        setModelID(rs.getInt("roomID"));
        setBuilding(rs.getInt("building"));
        setRoomNumber(rs.getString("roomNumber"));
        setLocationName(rs.getString("locationName"));
        setCapacity(rs.getInt("capacity"));
        setDefaultLayout(rs.getString("defaultLayout"));
        setHasWhiteboard(rs.getInt("hasWhiteBoard") != 0);
        setHasChalkboard(rs.getInt("hasChalkboard") != 0);
        setHasProjector(rs.getInt("hasProjector") != 0);
        setHasTieredSeating(rs.getInt("hasTieredSeating") != 0);
        setHasMoveableSeating(rs.getInt("hasMoveableSeating") != 0);
        setHasWindows(rs.getInt("hasWindows") != 0);
        setLabType(rs.getInt("labType"));
        add(this);
    }
    
    public final void setBuilding(int value) { building = value; }
    public final void setRoomNumber(String value) { 
        if (value != null)
            roomNumber = value;
        else
            roomNumber = "";
    }
    public final void setLocationName(String value) {
        if (value != null)
            locationName = value;
        else
            locationName = "";
    }
    public final void setCapacity(int value) { capacity = value; }
    public final void setDefaultLayout(String value) {
        if (value != null)
            defaultLayout = value;
        else
            defaultLayout = "";
    }
    public final void setHasWhiteboard(boolean value) { hasWhiteboard = value; }
    public final void setHasChalkboard(boolean value) { hasChalkboard = value; }
    public final void setHasProjector(boolean value) { hasProjector = value; }
    public final void setHasTieredSeating(boolean value) { hasTieredSeating = value; }
    public final void setHasMoveableSeating(boolean value) { hasMoveableSeating = value; }
    public final void setHasWindows(boolean value) { hasWindows = value; }
    public final void setLabType(int value) { labType = value; }
    
    public final int getBuilding() { return building; }
    public final String getRoomNumber() { return roomNumber; }
    public final String getLocationName() { return locationName; }
    public final int getCapacity() { return capacity; }
    public final String getDefaultLayout() { return defaultLayout; }
    public final boolean getHasWhiteboard() { return hasWhiteboard; }
    public final boolean getHasChalkboard() { return hasChalkboard; }
    public final boolean getHasProjector() { return hasProjector; }
    public final boolean getHasTieredSeating() { return hasTieredSeating; }
    public final boolean getHasMoveableSeating() { return hasMoveableSeating; }
    public final boolean getHasWindows() { return hasWindows; }
    public final int getLabType() { return labType; }
    
    public final String getLabTypeName() {
        LabModel lm = Model.getLab(labType);
        return (lm!=null)?Model.getLab(labType).getLabName():"";
    }
    
    public final String getBuildingName() {
        BuildingModel bm = Model.getBuilding(building);
        return (bm!=null)?bm.toString():"no-name";
    }
    
    public final String getLongTitle() {
        String labName = getLabTypeName();
        if (!labName.isEmpty())
            labName = "  \\  " + labName;
        return getBuildingName() + " - " + getRoomNumber() + labName;
    }
    
    @Override public String toString() {
        BuildingModel bm = Model.getBuilding(this.getBuilding());
        if (bm != null)
            return bm.getBuildingName() + " - " + getRoomNumber();
        else
            return  "[null] - " + getRoomNumber();
    }
}
