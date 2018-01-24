/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views.ribbons;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import ucss.models.tuples.Model;
import ucss.ui.widgets.ActionRequest;
import ucss.ui.widgets.RibbonAction;
import ucss.ui.widgets.RibbonItem;
import ucss.ui.widgets.WidgetHelpers;

/**
 *
 * @author John
 */
public class RibbionActions extends RibbonItem implements ActionRequest {
    private final Button btnUndo;
    private final Button btnRedo;
    
    public RibbionActions() {
        super(
            "actions",
            new VBox(),
            new RibbonAction(new Button("", WidgetHelpers.getImageViewFromResource("edit-undo"))),
            new RibbonAction(new Button("", WidgetHelpers.getImageViewFromResource("edit-redo")))
        );
        
        btnUndo = (Button) getActionItems().get(0).getContent();
        btnRedo = (Button) getActionItems().get(1).getContent();
        
        btnUndo.setPrefWidth(40);
        btnRedo.setPrefWidth(40);
        btnUndo.setDisable(true);
        btnRedo.setDisable(true);
        
        btnUndo.setOnAction( e -> {Model.getMeetingModelMomento().undo();});
        btnRedo.setOnAction( e -> {Model.getMeetingModelMomento().redo();});
        
        Model.getMeetingModelMomento().setOnAction(e -> {
            btnUndo.setDisable(!Model.getMeetingModelMomento().hasUndo());
            btnRedo.setDisable(!Model.getMeetingModelMomento().hasRedo());
        });
        

    }
    
    public final void setOnUndo(EventHandler<ActionEvent> value) {
        btnUndo.setOnAction(value);
    }
    
    public final void setOnRedo(EventHandler<ActionEvent> value) {
        btnRedo.setOnAction(value);
    }
    
    @Override
    public void undoRequest() {
        System.out.println("UNDO");
        btnUndo.fire();
    }

    @Override
    public void redoRequest() {
        System.out.println("REDO");
        btnRedo.fire();
    }

    @Override
    public void onKeyEvent(KeyEvent e) {
        if (e.getCode() == KeyCode.Z) {
            undoRequest();
        } else if (e.getCode() == KeyCode.Y) {
            redoRequest();
        }
    }
    
}
