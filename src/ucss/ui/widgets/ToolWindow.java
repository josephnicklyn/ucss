/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import com.sun.javafx.scene.SceneHelper;
import java.util.HashMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author John
 */
public class ToolWindow extends Stage {
    
    private final StackPane content = new StackPane();
    private final HBox buttonBar = new HBox(6);
    private final VBox root = new VBox(8, content, buttonBar);
    
    public enum ToolWindowButtons {
        TWB_OKAY,
        TWB_CANCEL,
        TWB_NEXT,
        TWB_PREV,
        TWB_HELP;
    }

    private HashMap<ToolWindowButtons, Button> hashButtons = new HashMap();
    
    public ToolWindow(String title) {
        root.setPadding(new Insets(12));
        root.setAlignment(Pos.CENTER);
        final Scene scene = SceneHelper.createPopupScene(root);
        scene.getStylesheets().add("resources/styles.css");
        
        super.setScene(scene);
        super.initStyle(StageStyle.UTILITY);
        super.setResizable(false);
        super.initModality(Modality.WINDOW_MODAL);
        super.setTitle(title);
        VBox.setVgrow(content, Priority.ALWAYS);
        root.setFillWidth(true);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        scene.widthProperty().addListener( (e, o, n) -> { root.setPrefWidth(n.doubleValue());});
        scene.heightProperty().addListener( (e, o, n) -> { root.setPrefHeight(n.doubleValue());});
        
        super.setWidth(600);
        super.setHeight(400);
        
        scene.setOnKeyPressed( e -> { handleKeyEvent(e); }); 
        
        
    }
    
    public final Pane getContent() {
        return content;
    }
    
    public final Pane getRoot() {
        return root;
    }
    
    public final void show(Window owner) {
        super.initOwner(owner);
        super.show();
        
    }
   
    public final void show(Node n) {
        if (n != null && n.getScene() != null) {
            show(n.getScene().getWindow());
        }
    }
    private static final KeyCombination ESCAPE_KEY_COMBINATION =
                KeyCombination.keyCombination("Esc");
    
    private void handleKeyEvent(final KeyEvent event) {
        if (event.isConsumed()) {
            return;
        }
        final Scene scene = getScene();
        if ((event.getEventType() == KeyEvent.KEY_PRESSED)
                && ESCAPE_KEY_COMBINATION.match(event)) {
                close();
                event.consume();
        }
    }
    
    public final void addButton(ToolWindowButtons type, String caption, Node node) {
        Button b = hashButtons.get(type);
        
        if (b == null) {
            b = new Button(caption, node);
            b.getStyleClass().setAll("flat-button");
            b.setStyle("-fx-pref-width:7em");
            switch (type) {
                case TWB_OKAY:
                    b.setOnAction( e -> { 
                        close();
                        onOkay(); 
                    });
                    b.setDefaultButton(true);
                    break;
                case TWB_CANCEL:
                    b.setOnAction( e -> { 
                        close();
                        onCancel(); 
                    });
                    b.setCancelButton(true);
                    break;
                case TWB_NEXT:
                    b.setOnAction( e -> { onNext(); });
                    break;
                case TWB_PREV:
                    b.setOnAction( e -> { onPrevious(); });
                    break;
                case TWB_HELP:
                    b.setOnAction( e -> { onHelp(); });
                    break;
                default:
                    throw new AssertionError(type.name());
                
            }
            
            hashButtons.put(type, b);
            buttonBar.getChildren().add(b);
        }
    }
    
    public final boolean removeButton(ToolWindowButtons type) {
        Button b = hashButtons.get(type);
        
        if (b == null) {
            return false;
        } else {
            buttonBar.getChildren().remove(b);
            hashButtons.remove(type);
            return true;
        }
    }
    
    public final void addButtons(ToolWindowButtons... types) {
        if (types != null)
            for(ToolWindowButtons type: types) {
                String caption = "";
                switch (type) {
                    case TWB_OKAY:
                        caption = "Okay";
                        break;
                    case TWB_CANCEL:
                        caption = "Cancel";
                        break;
                    case TWB_NEXT:
                        caption = "Next >>";
                        break;
                    case TWB_PREV:
                        caption = "<< Back";
                        break;
                    case TWB_HELP:
                        caption = "Help";
                        break;
                }
                addButton(type, caption, null);
            }
    }
    
    public final void setHasNext(boolean value) {
        Button b = hashButtons.get(ToolWindowButtons.TWB_NEXT);
        if (b != null)
            b.setDisable(!value);
    }
    
    public final void setHasPrev(boolean value) {
        Button b = hashButtons.get(ToolWindowButtons.TWB_PREV);
        if (b != null)
            b.setDisable(!value);
    }
    
    public final void setHasOkay(boolean value) {
        Button b = hashButtons.get(ToolWindowButtons.TWB_OKAY);
        if (b != null)
            b.setDisable(!value);
    }
    
    public final void setHasCancel(boolean value) {
        Button b = hashButtons.get(ToolWindowButtons.TWB_CANCEL);
        if (b != null)
            b.setDisable(!value);
    }
    
    public final void setHasHelp(boolean value) {
        Button b = hashButtons.get(ToolWindowButtons.TWB_HELP);
        if (b != null)
            b.setDisable(!value);
    }
    
    public void onCancel() {}
    public void onOkay() {}
    public void onNext() {}
    public void onPrevious() {}
    public void onHelp() {}
    
}
