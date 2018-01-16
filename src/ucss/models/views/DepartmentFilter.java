/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.views;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import ucss.models.tuples.CollegeModel;
import ucss.models.tuples.DepartmentModel;
import ucss.models.tuples.Model;
import ucss.ui.widgets.CheckList;
import ucss.ui.widgets.CheckListSelectedEvent;
import ucss.ui.widgets.DropWindow;

/**
 *
 * @author John
 */
public class DepartmentFilter extends DropWindow {
    private final CheckList<DepartmentModel> deptCheck = new CheckList(5, false, true);
    private final CheckList<CollegeModel> collCheck = new CheckList();
    
    private Tab forCollege = new Tab("College", collCheck);
    private Tab forDepartment = new Tab("Departments", deptCheck);
    
    private TabPane tabPane = new TabPane(forCollege, forDepartment);
    
    private final StackPane vBox;
    private final IntegerProperty updateCounter = new SimpleIntegerProperty();

    
    public DepartmentFilter() {
        this("Filter");
    }
    
    public DepartmentFilter(String title) {
        super(title, new StackPane(), 400);
        
        vBox = (StackPane) super.getContent();
        vBox.getChildren().add(tabPane);
        
        forCollege.setClosable(false);
        forDepartment.setClosable(false);
        //vBox.getStyleClass().add("list-view");
        collCheck.setOnSelected(e -> {
            selectColleges((CollegeModel)e.getSelectedObject(), e.isSelected());
        });
        
        deptCheck.setOnSelected(e -> {
            selectDepartments((DepartmentModel)e.getSelectedObject(), e.isSelected());
        });
    }
    
    public final void setOnDepartmentSelected(EventHandler<CheckListSelectedEvent> value) {
        deptCheck.setOnSelected(value);
    }
    
    public final void updateLists() {
        updateCollegeList();
        updateDepartmentList();
    }

    private void updateCollegeList() {
        collCheck.clear();
        for(CollegeModel cm: Model.getColleges()) {
            collCheck.add(cm);
        }
    }
    
    private void updateDepartmentList() {
        deptCheck.clear();
        for(DepartmentModel dm: Model.getDepartments()) {
            deptCheck.add(dm);
        }
    }
    
    private boolean needsUpdate = true;
    
    @Override public void activate() {
        if (needsUpdate) {
            updateLists();
            needsUpdate = false;
        }
    }

    private void selectColleges(CollegeModel collegeModel, boolean selected) {
        
        if (collegeModel == null)
            return;
        collCheck.pauseFire(true);
        
        for(DepartmentModel m: Model.getDepartments()) {
            if (m.getCollege() == collegeModel.getModelID()) {
                deptCheck.setSelected(m, selected);
                m.setSelected(selected);
            }
        }
        updateCounter.set(updateCounter.get() + 1);
        collCheck.pauseFire(false);
    }

    private void selectDepartments(DepartmentModel departmentModel, boolean selected) {
        departmentModel.setSelected(selected);
        updateCounter.set(updateCounter.get() + 1);
    }

    public final void setOnSelectionChanged(ChangeListener<Number> listener) {
        updateCounter.addListener(listener);
    }

}
