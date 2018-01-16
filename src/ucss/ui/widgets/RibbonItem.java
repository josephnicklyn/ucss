/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author John
 */
public class RibbonItem extends VBox {
    private final Text titleText = new Text();
    private Region userContent = null;
    private final Pane groupAction;
    
    public RibbonItem() {
        titleText.getStyleClass().add("text");
        
        setPrefHeight(20);
        setMaxHeight(220);
        userContent = null;
        groupAction = null;
        setSpacing(4);
        getStyleClass().add("ribbon-item");
        getChildren().addAll(titleText);
        setTitle("text");
        this.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(this, Priority.ALWAYS);
    }
    
    public RibbonItem(String title) {
        this(title, new FlowPane(Orientation.VERTICAL));
        asFlowPane((FlowPane)groupAction);
    }
    
    public RibbonItem(String title, RibbonAction ... items) {
        this(title, new FlowPane(Orientation.VERTICAL));
        asFlowPane((FlowPane)groupAction);
        getActionItems().addAll(items);
    }
    
    public RibbonItem(String title, Pane pane, RibbonAction ... items) {
        this(title, pane);
        getActionItems().addAll(items);
    }
    
    public RibbonItem(String title, Pane pane) {
        groupAction = pane;
        groupAction.setStyle("-fx-font-size:12px;");
        
        titleText.getStyleClass().add("text");
        VBox.setVgrow(groupAction, Priority.ALWAYS);
        
        setPrefHeight(20);
        setMaxHeight(220);
        
        setSpacing(4);
        getStyleClass().add("ribbon-item");
        getChildren().addAll(groupAction, new Separator(), titleText);
        setTitle(title);
        this.setAlignment(Pos.TOP_CENTER);
    }
    
    public final Pane getContentPane() {
        return groupAction;
    }
    
    public final void setTitle(String value) {
        titleText.setText(value);
    }
    
    public final String getTitle() {
        return titleText.getText();
    }
   
    @Override public String toString() {
        return getTitle();
    }
    private final ObservableList<RibbonAction> actionItems = new TrackableObservableList<RibbonAction>() {
        @Override
        protected void onChanged(ListChangeListener.Change<RibbonAction> c) {
            
            if (groupAction == null)
                return;
            
            while(c.next()) {
                
                for (RibbonAction item : c.getAddedSubList()) {
                    if (item != null) {
                        groupAction.getChildren().add(item);
                    }
                }
            }
            groupAction.requestLayout();
        }
    };
    
    public final ObservableList<RibbonAction> getActionItems() { return actionItems; }
    
    public boolean addActionItem(String text) {
        return addActionItem(text, (Node)null);
    }
    
    public boolean addActionItem(String text, Node content) {
        return getActionItems().add(new RibbonAction(text, content));
    }
    
    private void asFlowPane(FlowPane box) {
        box.setStyle("-fx-font-size:12px;");
        box.setVgap(4.0);
        box.setHgap(8.0);
        box.setOrientation(Orientation.VERTICAL);
        
    }
}
