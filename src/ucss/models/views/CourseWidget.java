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
public class CourseWidget extends VBox implements CMTableActions {
    
    private ArrayList<CourseModel> subList = new ArrayList<>();

    
    private final CMTableView courseTable = new CMTableView(
        (CMTableActions)this,
        new CMTableColumn("Department", 60, true),
        new CMTableColumn("Course Number", 64, true),
        new CMTableColumn("Title", 200, true),
        new CMTableColumn("Credits", 40, true),
        new CMTableColumn("Whiteboard", 36, true),
        new CMTableColumn("Chalkboard", 36, true),
        new CMTableColumn("Projector", 36, true),
        new CMTableColumn("Tiered Seats", 36, true),
        new CMTableColumn("Moveable Seats", 36, true),
        new CMTableColumn("Windows", 36, true),
        new CMTableColumn("Lab Type", 100),
        new CMTableColumn("Description", 1.0),
        new CMTableColumn("Meetings/Week", 40, true),
        new CMTableColumn("Labs/Week", 40, true)
            
    ); 
    
    public CourseWidget() {
        VBox.setVgrow(courseTable, Priority.ALWAYS);
        setStyle("-fx-padding:10;");
        getChildren().add(courseTable);
        
        initTable();
        
    }
    
    private void initTable() {
        for(int i = 0; i < 20; i++) {
            courseTable.getTable().add(
                new TextField(),            // department
                new TextField(),            // courseNumber
                new TextField(),            // courseTitle
                new TextField(),            // credits
                new CheckBox(),             // whiteboard
                new CheckBox(),             // chalkboard
                new CheckBox(),             // projector
                new CheckBox(),             // tiered seats
                new CheckBox(),             // moveable seats
                new CheckBox(),             // windows
                new TextField(),            // lab type
                new TextField(),            // description
                new TextField(),            // meets/week
                new TextField()             // labs/week
            );
        }
        
        subList.addAll(Model.getCourses());
        courseTable.setMaxRows(subList.size());
        courseTable.updateView();
    }
   
    private final CourseModel getFromSubList(int p) {
        if (subList.isEmpty() || p < 0 || p > (subList.size()-1))
            return null;
        else
            return subList.get(p);
    }
    
    @Override public void setRowValuesFor(CWTableRow row, int p) {
        CourseModel value = getFromSubList(p);
        
        if (row != null) {
            if (value != null) {
                ((TextField)row.get(0)).setText(value.getDepartmentName());
                ((TextField)row.get(1)).setText(value.getCourseNumber());
                ((TextField)row.get(2)).setText(value.getCourseTitle());
                
                ((TextField)row.get(3)).setText(Integer.toString(value.getCredits()));
                
                ((CheckBox)row.get(4)).setSelected(value.getHasWhiteboard());
                ((CheckBox)row.get(5)).setSelected(value.getHasChalkboard());
                ((CheckBox)row.get(6)).setSelected(value.getHasProjector());
                ((CheckBox)row.get(7)).setSelected(value.getHasTieredSeating());
                ((CheckBox)row.get(8)).setSelected(value.getHasMoveableSeating());
                ((CheckBox)row.get(9)).setSelected(value.getHasWindows());
                
                ((TextField)row.get(10)).setText(value.getLabTypeName());
                ((TextField)row.get(11)).setText(value.getCourseDescription());
                ((TextField)row.get(12)).setText(Integer.toString(value.getMeetingsPerWeekHint()));
                ((TextField)row.get(13)).setText(Integer.toString(value.getMeetingsPerWeekAreLabHints()));
            }
        }
        
    }
    
    @Override public void selectedItem(int p) {
        System.out.println("SELECTED ROOM " + p);
    }
}