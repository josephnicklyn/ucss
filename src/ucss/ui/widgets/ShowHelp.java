/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import ucss.controllers.GraphController;

/**
 *
 * @author John
 */
public class ShowHelp extends Popup {
    
    private final VBox root = new VBox(4);
    private static ShowHelp instance;
    
    private ShowHelp() {
        getScene().setRoot(root);
        root.setStyle("-fx-background-radius:1;-fx-padding:0.25em 0.5em 1em 0.5em;-fx-background-color:#888888, #f0f2f4;-fx-background-insets:0, 1;-fx-opacity:0.9;");
        root.setFillWidth(true);
        this.setAutoHide(true);
        
    }
    
    public static ShowHelp getInstance() {
        if (instance == null)
            instance = new ShowHelp();
        return instance;
    }
    
    public final void setMessage(Node t, MouseEvent e, String message) {
        if (isShowing()) {
            hide();
        }
        Pane into = root;
        root.getChildren().clear();
        String[] parts = message.split("\n");
        for(int i = 0; i < parts.length;i++) {
            Text tx = new Text(parts[i]);
            if (i == 0) {
                Label label = new Label("", tx);
                HBox box = new HBox(label, new ImageView(getHelpImage()));
                tx.getStyleClass().add("help-header");
                label.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(label, Priority.ALWAYS);
                into.getChildren().add(box);
                box.setStyle("-fx-background-color:#113;-fx-padding:0.5em;-fx-background-radius:3;");
                into = new VBox(0);
                root.getChildren().add(into);
                into.setPadding(new Insets(4, 16, 8, 16));
                
            } else {
                
                tx.getStyleClass().add("help-body");
                if (parts[i].contains("\b")) {
                    tx.setFill(Color.rgb(0, 80, 90));
                    tx.setText(parts[i].replaceAll("\b", ""));
                }
                into.getChildren().add(tx);
            }
            
        }
        
        this.show(t.getScene().getWindow(), e.getScreenX(), e.getScreenY());
    }
    
    private static Image helpImage = null;
    
    private static Image getHelpImage() {
        if (helpImage == null) {
            helpImage = new Image(GraphController.class.getResourceAsStream("/resources/help-contents.png"));
        }
        return helpImage;
    }
    
}
