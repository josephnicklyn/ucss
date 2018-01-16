/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views.ribbons;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import ucss.models.tuples.Model;
import ucss.models.tuples.TermModel;
import ucss.ui.widgets.MultiPageToolWindow;
import ucss.ui.widgets.Ribbon;
import ucss.ui.widgets.RibbonAction;
import ucss.ui.widgets.RibbonItem;
import ucss.ui.widgets.ToolWindow;

/**
 *
 * @author John
 */
public class TermRibbon extends Ribbon {
    
    private final ComboBox<TermModel> cmbTerm = new ComboBox();
    private final Button btnNewTerm = new Button("new term");
    
    private final RibbonItem rbTermSelect = new RibbonItem(
        "term select",
        new VBox(),
        new RibbonAction(cmbTerm),
        new RibbonAction(btnNewTerm)
    );
    
    public TermRibbon() {
        super("Term");
        
        getItems().addAll(rbTermSelect);
        
        btnNewTerm.setOnAction( e -> {
            MultiPageToolWindow.show(
                btnNewTerm,    
                "Term Wizard", 
                new Button("First"),
                new Button("Between First and Middle"),
                new Button("Middle"),
                new Button("Between Middle and Last"),
                new Button("LAST")
            );
        });
        
    }
    
    private boolean alreadySet = false;
    @Override public final void onActivate() {
        if (!alreadySet) {
            cmbTerm.getItems().clear();
            
            for(int i: Model.getTerms().keySet()) {
                TermModel t = Model.getTerms().get(i);
                cmbTerm.getItems().add(t);
            }

            alreadySet = true;
        }
    }
    
}
