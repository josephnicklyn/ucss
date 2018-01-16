/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views.ribbons;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import ucss.controllers.GraphController;
import ucss.models.tuples.*;
import ucss.models.views.DepartmentFilter;
import ucss.ui.widgets.*;
import ucss.views.EventDockPane;

/**
 *
 * @author John
 */
public class EventGraphRibbon extends Ribbon {//implements ActionRequest  {
    
   //private final Button btnUndo = new Button("", WidgetHelpers.getImageViewFromResource("edit-undo")),
   //                      btnRedo = new Button("", WidgetHelpers.getImageViewFromResource("edit-redo"));
    
    private final Button btnShowDepartmentContext = new Button("Departments");
    private final Button btnShowProfessorContext = new Button("Professors");
    
    private final ComboBox<TermModel> cmbTerm = new ComboBox();
    private final ComboBox<BuildingModel> cmbFilter = new ComboBox();
    private final RibbionActions rbItemActions = new RibbionActions();
    //        "actions",
    //        new VBox(),
    //        new RibbonAction(btnUndo),
    //        new RibbonAction(btnRedo)
    //);
    
    private final DepartmentFilter deptFilter = new DepartmentFilter("Filter By ...");
    
    
    private final RibbonItem rbItemSelector = new RibbonItem(
        "graph select",
        new VBox(),
        new RibbonAction("term", cmbTerm),
        new RibbonAction("group", cmbFilter)
    );
    
    private final RibbonItem rbItemFilter = new RibbonItem(
        "filter",
        new VBox(),
        new RibbonAction("", deptFilter)
    );

    private final RibbonItem rbSelect = new RibbonItem(
        "context views",
        new VBox(),
        new RibbonAction("", btnShowDepartmentContext),
        new RibbonAction("", btnShowProfessorContext)
    );
    /*
    private final RibbonItem rbDepartmentEvents = new RibbonItem(
        "department events",
        new VBox(),
        //new RibbonAction("", btnDepartmentView)
        new RibbonAction("", departmentListSelector)
    );
    */
    private boolean alreadySet = false;
    
    private final EventDockPane eventDockPane = new EventDockPane();
    
    public EventGraphRibbon() {
        super("Time Graphs");
        setContent(eventDockPane);
        
        getItems().addAll(
                rbItemActions,
                rbItemSelector,
                rbItemFilter,
                new RibbonItem(),
                rbSelect
        );
        double s = 170;
        cmbFilter.setMinWidth(s);
        cmbTerm.setMinWidth(s);
        cmbFilter.setPrefWidth(s);
        cmbTerm.setPrefWidth(s);
        
        deptFilter.setMinWidth(s);
        
        
        cmbTerm.getSelectionModel().selectedItemProperty().addListener( 
            (e, o, n) -> { selectGraph();}
        );
        
        cmbFilter.getSelectionModel().selectedItemProperty().addListener( 
            (e, o, n) -> { selectGraph();}
        );
        
        deptFilter.setOnSelectionChanged( (e, o, n) -> {
                GraphController.updateMuteFilter();
            }
        );
        
        btnShowDepartmentContext.setOnAction( e-> {
            eventDockPane.activateDepartmentDock();
        });
        
        
        super.setExtraComments("\n\n\bEvent Graph\n\tAllows the user to display and edit\n\tsection meeting times.");

        
    }

    private void selectGraph() {
        TermModel term = cmbTerm.getValue();
        BuildingModel building = cmbFilter.getValue();
        
        if (term != null && building != null) {
            try {
                eventDockPane.loadRoomsGraph(building, term);
            } catch (Exception e) {
                System.out.println("selectGraph " + e.getMessage());
            }
        }
    }

    @Override public final void onActivate() {
        if (!alreadySet) {
            cmbTerm.getItems().clear();
            cmbFilter.getItems().clear();

            for(int i: Model.getTerms().keySet()) {
                TermModel t = Model.getTerms().get(i);
                cmbTerm.getItems().add(t);
            }

            for(BuildingModel b: Model.getBuildings()) {
                cmbFilter.getItems().add(b);
            }
            
            alreadySet = true;
        }
    }
    
}
