/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ucss.controllers.GraphController;
import ucss.controllers.GraphController.DepartmentGraph;
import ucss.models.tuples.DepartmentModel;
import ucss.models.tuples.Model;
import ucss.models.tuples.TermModel;
import ucss.ui.widgets.DockTab;
import ucss.ui.widgets.EventGroup;
import ucss.ui.widgets.InOutInterface;
import ucss.ui.widgets.MultiSelectorWidget;

/**
 *
 * @author John
 */
public class DepartmentsDockTab extends DockTab {
    private final StackPane forGraph = new StackPane();
    private final VBox content = new VBox();
    private MultiSelectorWidget<DepartmentModel> departmentSelect = new MultiSelectorWidget(5);
    private MultiSelectorWidget<TermModel> termSelect = new MultiSelectorWidget(1);
    private HBox header = new HBox(departmentSelect, termSelect);
    private DepartmentGraph dGraph = null;
    
    public DepartmentsDockTab() {
        
        super("Department Context");
        setContent(content);
        content.setStyle("-fx-background-color:#aabbdd;");
        content.setFillWidth(true);
        content.getChildren().addAll(header, forGraph);
        HBox.setHgrow(departmentSelect, Priority.ALWAYS);
        VBox.setVgrow(forGraph, Priority.ALWAYS);
        fillTerms();
        
        termSelect.setListChangeProperty(termListChange);
        departmentSelect.setListChangeProperty(deptListChange);
        
        departmentSelect.setPromptText("Select a department(s)");
        termSelect.setPromptText("Select a term");
        
        departmentSelect.setDisable(true);
        
        header.setFillHeight(true);        
    }

    InOutInterface<TermModel> termListChange = new InOutInterface<TermModel>() {
        @Override
        public void in(TermModel e) {
            forGraph.getChildren().clear();
            if (e != null) {
                dGraph = GraphController.getGraph(e).getDepartmentGraph();
                forGraph.getChildren().add(dGraph);
                departmentSelect.clear();
                for(EventGroup dx: dGraph.getItems()) {
                    if (dx.getForObject() instanceof DepartmentModel) {
                        departmentSelect.select((DepartmentModel)dx.getForObject());
                    }
                }
            }
            departmentSelect.setDisable(false);
        }

        @Override
        public void out(TermModel e) {
            forGraph.getChildren().clear();
        }

        @Override
        public void clear() {

        }
    
    };

    InOutInterface<DepartmentModel> deptListChange = new InOutInterface<DepartmentModel>() {
        @Override
        public void in(DepartmentModel e) {
            if (dGraph != null)
                dGraph.add(e);
        }

        @Override
        public void out(DepartmentModel e) {
            if (dGraph != null)
                dGraph.remove(e);
        }

        @Override
        public void clear() {

        }
    
    };

    
    private void fillTerms() {
        
        for(int termIndex: Model.getTerms().keySet()) {
            TermModel tm = Model.getTerms().get(termIndex);
            termSelect.add(tm, "Terms");
        }
        
        for(DepartmentModel dm: Model.getDepartments()) {
            departmentSelect.add(dm, dm.getCollegeName());
        }
    }

    
}
