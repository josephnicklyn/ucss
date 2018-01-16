/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author John
 */
public class Ribbon extends Label implements HelperInterface {
    public static PseudoClass SELECTED_STYLE = PseudoClass.getPseudoClass("selected");
    
    protected RibbonWidget owner;
    
    protected final HBox groupHeader = new HBox();
    protected boolean isSelectable = true;
    protected Node content = null;
    
    public Ribbon() {
        this("", null);
    }
    
    public Ribbon(String title) {
        this(title, null);
    }
    
    public Ribbon(String title, RibbonItem... items) {
        /*label.*/getStyleClass().add("ribbon-tab");
        setTitle(title);
        /*label.*/setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY
                    && owner != null) {
                if (!isSelectable) {
                    onSelect();
                }else if (owner.getSelectedRibbon() != this) {
                    owner.setSelectedRibbon(this);
                } else {

                    if (groupHeader.isManaged()) {
                        groupHeader.setManaged(false);
                        groupHeader.setVisible(false);
                    } else {
                        groupHeader.setManaged(true);
                        groupHeader.setVisible(true);
                    }
                }
            }
        });
        if (items != null) {
            getItems().addAll(items);
        }
    }
    
    public void onActivate() {
        
    }
    
    public void onSelect() {}
    
    void setSelected(boolean p) {
        /*label.*/pseudoClassStateChanged(SELECTED_STYLE, p);
        onActivate();
        
    }
    
    void setRibbonWidget(RibbonWidget owner) {
        this.owner = owner;
    }
    
    public final RibbonWidget getRibbonWidget() {
        return owner;
    }
    
    public final void setTitle(String title) {
        /*label.*/setText(title);
    }
    
    public final String getTitle() {
        return /*label.*/getText();
    }
    
    public boolean isSelectable() {
        return isSelectable;
    }
    
    protected void setContent(Node thing) {
        content = thing;
    }
    
    public final Node getContent() {
        return content;
    }
    
    Label getHeaderLabel() {
        return this;//label;
    }
    
    HBox getGroupHeader() {
        return groupHeader;
    }
    
    private final ObservableList<RibbonItem> items = new TrackableObservableList<RibbonItem>() {
        @Override
        protected void onChanged(ListChangeListener.Change<RibbonItem> c) {
            while(c.next()) {
                
                for (RibbonItem item : c.getAddedSubList()) {
                    if (item != null) {
                        groupHeader.getChildren().add(item);
                    }
                }
            }
            groupHeader.requestLayout();
        }
    };
    
    public final ObservableList<RibbonItem> getItems() { return items; }

    @Override
    public String getHelperInfo(MouseEvent e) {
        return "Ribbon Label:\nSelect the ribbon item to activate the ribbon, \n" +
                               "or to hide the ribbon if it is already active." + extraComments;
    }
    private String extraComments = "";
    @Override
    public void setExtraComments(String value) {
        extraComments = value;
    }
    
}
