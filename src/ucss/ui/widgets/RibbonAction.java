/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class RibbonAction extends HBox {
    
    private final Label text = new Label();
    private final boolean horizontal = true;
    private Node nContent = null;
    
   
    public RibbonAction(Node content) {
        this("", content);
    }
    
    public RibbonAction(String title, Node content) {
        
        this.setAlignment(Pos.CENTER_LEFT);
        setSpacing(8);
        HBox.setHgrow(text, Priority.ALWAYS);
        text.setMaxWidth(Double.MAX_VALUE);
        text.setStyle("-fx-font-size:12px;");
        setText(title);
        getStyleClass().add("action-item");

        if (content != null) {
            
            nContent = content;
            if(getText().isEmpty())
                ((Region)content).setMaxWidth(Double.MAX_VALUE);
            getChildren().add(nContent);
            HBox.setHgrow(nContent, Priority.ALWAYS);
            if (nContent instanceof ImageView) {
                getStyleClass().clear();
                getStyleClass().add("action-item-image");
                
                nContent.toBack();
            }
        }
        
    }

    public final Node getContent() {
        return nContent;
    }
    
    public final void setText(String value) {
        text.setText(value);
        
        if (text.getText().isEmpty()) {
            getChildren().remove(text);
        } else if (!getChildren().contains(text)) {
            getChildren().add(text);
        }
        
    }
    
    public final String getText() {
        return text.getText();
    }
   /* 
    public void disabled(boolean value) {
        if (nContent != null) {
            nContent.setDisable(value);
        }
    }
*/
}
