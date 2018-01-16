/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.views;

import ucss.models.tuples.*;
import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ucss.ui.widgets.CMTableActions;
import ucss.ui.widgets.CMTableColumn;
import ucss.ui.widgets.CMTableView;
import ucss.ui.widgets.CWTableRow;
/**
 *
 * @author John
 */
public class RoomWidget extends VBox implements CMTableActions {
    
    private ArrayList<RoomModel> subList = new ArrayList<>();

    
    private final CMTableView roomTable = new CMTableView(
        (CMTableActions)this,
        new CMTableColumn("Building", 100),
        new CMTableColumn("Room", 100),
        new CMTableColumn("Layout", 0.35),
        new CMTableColumn("Capacity", 60),
        new CMTableColumn("Whiteboard", 36, true),
        new CMTableColumn("Chalkboard", 36, true),
        new CMTableColumn("Projector", 36, true),
        new CMTableColumn("Tiered Seats", 36, true),
        new CMTableColumn("Moveable Seats", 36, true),
        new CMTableColumn("Windows", 36, true),
        new CMTableColumn("Lab Type", 0.65)
            
    ); 
    
    public RoomWidget() {
       VBox.setVgrow(roomTable, Priority.ALWAYS);
        setStyle("-fx-padding:10;");
        getChildren().add(roomTable);
        
        initTable();
        
    }
    
    private void initTable() {
        for(int i = 0; i < 20; i++) {
            roomTable.getTable().add(
                new TextField(),            // building
                new TextField(),            // room
                new TextField(),            // layout
                new TextField(),            // capacity
                new CheckBox(),             // whiteboard
                new CheckBox(),             // chalkboard
                new CheckBox(),             // projector
                new CheckBox(),             // tiered seats
                new CheckBox(),             // moveable seats
                new CheckBox(),             // windows
                new TextField()             // lab type
            );
        }
        
        subList.addAll(Model.getRooms());
        roomTable.setMaxRows(subList.size());
        roomTable.updateView();
    }
   
    private final RoomModel getFromSubList(int p) {
        if (subList.isEmpty() || p < 0 || p > (subList.size()-1))
            return null;
        else
            return subList.get(p);
    }
    
    @Override public void setRowValuesFor(CWTableRow row, int p) {
        RoomModel value = getFromSubList(p);
        
        if (row != null) {
            if (value != null) {
                ((TextField)row.get(0)).setText(value.getBuildingName());
                ((TextField)row.get(1)).setText(value.getRoomNumber());
                ((TextField)row.get(2)).setText(value.getDefaultLayout());
                ((TextField)row.get(3)).setText(Integer.toString(value.getCapacity()));
                
                ((CheckBox)row.get(4)).setSelected(value.getHasWhiteboard());
                ((CheckBox)row.get(5)).setSelected(value.getHasChalkboard());
                ((CheckBox)row.get(6)).setSelected(value.getHasProjector());
                ((CheckBox)row.get(7)).setSelected(value.getHasTieredSeating());
                ((CheckBox)row.get(8)).setSelected(value.getHasMoveableSeating());
                ((CheckBox)row.get(9)).setSelected(value.getHasWindows());
                
                ((TextField)row.get(10)).setText(value.getLabTypeName());
            }
        }
        
    }
    
    @Override public void selectedItem(int p) {
        System.out.println("SELECTED ROOM " + p);
    }

}
