/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import javafx.scene.input.KeyCode;
import ucss.models.tuples.BuildingModel;
import ucss.models.tuples.DepartmentModel;
import ucss.models.tuples.TermModel;
import ucss.ui.widgets.DockPane;
import ucss.ui.widgets.DockTab;
import ucss.ui.widgets.DockWindow;

/**
 *
 * @author John
 */
public class EventDockPane extends DockPane {
    
    public EventDockPane() {
    }
    
    public final DockTab loadRoomsGraph(BuildingModel building, TermModel term) throws Error {
        if (building == null || term == null)
            throw new Error("Building nor Term can be null.");
    
        String title = building.toString() + " " + term.getTitle();
        DockTab t = selectTabByTitle(title);
        
        if (t == null) {
            t = new EventDockTab(title, building, term);
            getDockTabs().add(t);
            t.selectTab();
        }
        
        return t;
    }

    private DockTab selectTabByTitle(String title) {
        DockTab t = getTabByTitle(title);
        
        if (t != null) {
            super.selectTab(title);
        }
        
        return t;
    }
    
    

     private void initUndoRedo() {
         
        /*
        Model.getMeetingModelMomento().setOnUndo(e -> { 
            doUndoRedo(e.getMomentoInterfaceTarget());
        });
        
        Model.getMeetingModelMomento().setOnRedo(e -> { 
            System.out.println("REDO .... " );
                    
            doUndoRedo(e.getMomentoInterfaceTarget());
        });
        */
        this.setFocusTraversable(true);
        
        this.setOnMousePressed( e-> {
            //requestFocus();
            this.setFocused(true);
        });
        /*
        this.setOnKeyPressed( e -> {
            //System.out.println(e);
            if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                System.out.println("undo");
                e.consume();
            } else if (e.isControlDown() && e.getCode() == KeyCode.Y) {
                System.out.println("redo");
                e.consume();
            }
        });
        */
    }

    public void activateDepartmentDock() {
        
        DockTab theTab = null;
        
        for(DockTab dt: getDockTabs()) {
            if (dt instanceof DepartmentsDockTab) {
                theTab = dt;
                break;
            }
        }
        
        if (theTab == null) {
            theTab = new DepartmentsDockTab();
            getDockTabs().add(theTab);
        }    
        super.selectTab(theTab);
        
    }

    public void updateDepartmentFilters(DepartmentModel n, boolean b) {
        /*
        if (n == null)
            return;
        for(DockTab dt: getDockTabs()) {
            if (dt instanceof DepartmentsDockTab) {
                DepartmentsDockTab d = (DepartmentsDockTab)dt;
                d.updateDepartmentFilters(n, b);
            }
        }
        
        for(DockTab dt: DockWindow.getInstance().getTabPane().getDockTabs()) {
            if (dt instanceof DepartmentsDockTab) {
                DepartmentsDockTab d = (DepartmentsDockTab)dt;
                d.updateDepartmentFilters(n, b);
            }
        }
        */
    }

}
