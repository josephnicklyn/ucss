/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.views;

import ucss.models.tuples.*;
import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
public class ProfessorWidget extends VBox implements CMTableActions {
    
    private ArrayList<ProfessorModel> subList = new ArrayList<>();
    
    private final CMTableView professorTable = new CMTableView(
            (CMTableActions)this,
            new CMTableColumn("ID", 0.33),
            new CMTableColumn("Name", 0.67),
            new CMTableColumn("Primary", 100),
            new CMTableColumn("Secondary", 100)
    );
    
    private final ComboBox<String> cmbFilter = new ComboBox();
    
    public ProfessorWidget() {
        VBox.setVgrow(professorTable, Priority.ALWAYS);
        setStyle("-fx-padding:10;-fx-background-color:#eaeaef;");
        getChildren().add(professorTable);
        
        initTable();
        
        cmbFilter.setMinWidth(100);
        
        professorTable.getUserContent().getChildren().addAll(
                new Label("Filter  "),
                cmbFilter
        );
        
    }

    private void initTable() {
        for(int i = 0; i < 20; i++) {
            professorTable.getTable().add(new Label(), new Label(), new Label(), new Label());
        }
        
        subList.addAll(Model.getProfessors());
        
        cmbFilter.getItems().add("");
        for(ProfessorModel pm: subList) {
            String p = pm.getPrimaryDepartmentAsString();
            if (!cmbFilter.getItems().contains(p))
                cmbFilter.getItems().add(p);
        }
         int m = subList.size();
        professorTable.setMaxRows(subList.size());
        
        Collections.sort(cmbFilter.getItems());
        
        cmbFilter.getSelectionModel().selectedItemProperty().addListener( (e, o, n) -> { setFilterList(n);});
        professorTable.updateView();
    }

    private final ProfessorModel getFromSubList(int p) {
        if (subList.isEmpty() || p < 0 || p > (subList.size()-1))
            return null;
        else
            return subList.get(p);
    }
    
    @Override public void setRowValuesFor(CWTableRow row, int p) {
        ProfessorModel value = getFromSubList(p);
        
        if (row != null) {
            if (value != null) {
                ((Label)row.get(0)).setText(value.getProfessorKey());
                ((Label)row.get(1)).setText(value.getProfessorName());
                ((Label)row.get(2)).setText(value.getPrimaryDepartmentAsString());
                ((Label)row.get(3)).setText(value.getSecondaryDepartmentAsString());
            } else {
                ((Label)row.get(0)).setText("");
                ((Label)row.get(1)).setText("");
                ((Label)row.get(2)).setText("");
                ((Label)row.get(3)).setText("");
            }
        }
        
    }

    private void setFilterList(String n) {
        subList.clear();
        if (n.length() == 0) {
            subList.addAll(Model.getProfessors());
        } else {
            for(ProfessorModel pm: Model.getProfessors()) {
                if (pm.getPrimaryDepartmentAsString().compareTo(n) == 0) {
                    subList.add(pm);
                } else if (pm.getSecondaryDepartmentAsString().compareTo(n) == 0) {
                    subList.add(pm);
                }
            }
        }
        professorTable.setMaxRows(subList.size());
        
    }
    
    @Override public void selectedItem(int p) {
        System.out.println("SELECTED PROFESSOR " + p);
    }
    
}
