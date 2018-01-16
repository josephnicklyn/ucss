/*
 * Copyright (C) 2017 Joseph Nicklyn JR.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ucss.ui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

/**
 * This widget, allows a user to select a single or multiple items from a 
 * collection of items. The widget consists of a a flow pane, which displays
 * the currently selected items and a drop arrow which will display a pop-up
 * window containing a complete list of items to select from; these items
 * can be divided into sub groups.
 * <p>
 * A subset size can be defined:
 * Limits the size of the subset able to be selected.
 * 0 = unlimited, 1 = a single item (when selected, 1 will always be present)
 * > 1 will limit the size of the subset.
 * <p>
 * items can be separated into sub groups for easier reference.
 * <p>
 * the user can deselect items from the bucket or from the subset (if limit is 
 * not set to 1)
 * 
 * @author Joseph F. Nicklyn
 */
public class MultiSelectorWidget<E> extends HBox {
    
    
    private final static PseudoClass EDITABLE = PseudoClass.getPseudoClass("editable");
    
    /** Member variables */
    private final FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 12, 8);
    private final Region region = new Region();
    private final Button btnDropArrow = new Button("", region);
    private final VBox forList = new VBox(8);
    private final ScrollPane sp = new ScrollPane(forList);
    private final Text promptText = new Text("Select items to begin");
    /**
     * Limits the size of the subset able to be selected.
     * 0 = unlimited, 1 = a single item (when selected, 1 will always be present)
     * > 1 will limit the size of the subset
     */
    private final int maxSelectCount;
    
    /** constructors */
    
    /** empty constructor with no limit */
    public MultiSelectorWidget() {
        this(0);
    }
    
    public MultiSelectorWidget(int maxSelect) {
        
        if (maxSelect < 0) maxSelect = 0;
            maxSelectCount = maxSelect;
      
        setFillHeight(true);    
        getStyleClass().add("combo-box-base");
        
        setPrefWidth(180);
        
        flowPane.getStyleClass().add("text-field");
        btnDropArrow.getStyleClass().add("arrow-button");
        region.getStyleClass().add("arrow");
        
        flowPane.setPadding(new Insets(8));
        flowPane.setAlignment(Pos.CENTER_LEFT);
        
        flowPane.setStyle("-fx-min-height:2.5em;");
        forList.setPadding(new Insets(8));
        getChildren().addAll(flowPane, btnDropArrow);
        
        HBox.setHgrow(flowPane, Priority.ALWAYS);
        sp.setFitToWidth(true);
        btnDropArrow.setOnMouseClicked( e-> { toggleShow(); });
        
        forList.heightProperty().addListener((e,o,n) -> {
            double h = n.doubleValue();
            if (h > 420) h = 420;
            
            root.setMinHeight(h);
            root.setPrefHeight(h);
            adjustY();
            
        });
        
        flowPane.heightProperty().addListener( (e,o,n) -> { adjustY(); });
        // get ride of the glow for the scrollpane
        sp.setStyle("-fx-background-color: -fx-box-border, -fx-control-inner-background;-fx-background-insets: 0, 1;");
        flowPane.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                if (!waitAction) {
                    while (c.next()) {
                        if (c.wasPermutated()) {
                            for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            }
                        } else if (c.wasUpdated()) {
                        } else {
                            for (Node n : c.getRemoved()) {
                                triggerNodeListChange(n, ACTION_OUT);
                            }
                            for (Node n : c.getAddedSubList()) {
                                triggerNodeListChange(n, ACTION_IN);
                            }
                        }
                    }
                }
            }
        });
        
        promptText.setFill(Color.GREY);
        testShowMessage();
        
    }
    
    /**
     * Gets the prompt text value.
     * @return String
     */
    public final String getPromptText() {
        return promptText.getText();
    }
    
    /**
     * Sets the prompt text value.
     * @param value String
     */
    public final void setPromptText(String value) {
        promptText.setText(value);
    }
    
    /**
     * Adjusts the popup windows position to always be below. This does not 
     * adjust the position when its new position would cause the popup to
     * be off the screen (where the popup will automatically adjust its 
     * position.
     */
    private void adjustY() {
        if (popWindow != null) {
            if (popWindow.isShowing()) {
                Bounds b = this.localToScreen(this.getBoundsInLocal());
                popWindow.setY(b.getMaxY());
            }
        }
    }
    
    /** 
     * Sets the pseudo class "editable" for this widget. currently only changes
     * the appearance of the subset list.
     * @param value boolean
     */
    public final void setEditable(boolean value) {
        this.pseudoClassStateChanged(EDITABLE, value);
    }
    
    /**
     * The popup window allows the bucket to appear only when needed
     */
    private static Popup popWindow = null;
    private static final StackPane root = new StackPane();
    
    /**
     * In response to clicking the arrow shows or hides the popup. 
     */
    private void toggleShow() {
        
        if (popWindow == null) {
            popWindow = new Popup();
            popWindow.getScene().setRoot(root);
            popWindow.setAutoHide(true);
        }

        if (popWindow.isShowing()) {
            popWindow.hide();
            return;
        }
        
        root.getChildren().setAll(sp);
        
        Bounds b = this.localToScreen(this.getBoundsInLocal());
        root.setMinWidth(b.getWidth());
        root.setPrefWidth(b.getWidth());
        
        root.setPrefHeight(180);
        root.setMaxHeight(420);
        
        popWindow.show(this, b.getMinX(), b.getMaxY());
        popWindow.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
        
    }

    /**
     * Gets the current maximum of the subset  
     * @return integer: 0 = no limit, 1 = a single item when item is selected
     * the item can only be changed, but not removed, greater than 1 limits 
     * the subset to that value.
     */
    public final int getMaxSelectCount() {
        return maxSelectCount;
    }
    
    /**
     * this hash map allows items to be grouped.
     */
    private HashMap<String, VBox> items = new HashMap<>();
    private final List<CheckBoxObject> checkBoxObjects = new ArrayList<>();
    /**
     * adds a new element of type E into the bucket. in the group parentKey
     * @param e type of E. 
     * @param parentKey String, this should be descriptive
     */
    public final void add(E e, String parentKey) {
        
        VBox vb = items.get(parentKey);
        
        FlowPane fp = null;
        
        if (vb == null) {
            
            fp = new FlowPane(Orientation.HORIZONTAL, 16, 12);
            fp.setPadding(new Insets(8));
            fp.setAlignment(Pos.CENTER_LEFT);
        
            fp.setStyle("-fx-min-height:2em;-fx-padding:1em;");
            Label lab = new Label(parentKey);
            lab.setStyle("-fx-font-weight:bold;-fx-font-size:1.333em;");
            vb = new VBox(lab, fp);
            vb.setFillWidth(true);
            forList.getChildren().add(vb);
            items.put(parentKey, vb);
            
        } else {
            fp = (FlowPane)vb.getChildren().get(vb.getChildren().size()-1);
        }
        CheckBoxObject cb = new CheckBoxObject(e);
        checkBoxObjects.add(cb);
        fp.getChildren().add(cb);
        
    }
    
    
    /**
     * gets a list of the E objects which have been selected.
     * @return 
     */
    public final List<E> getSelectedItems() {
        List<E> r = new ArrayList();
        for(Object n: flowPane.getChildren()) {
            if (n.getClass().isAssignableFrom(CheckBoxObject.class)) {
                CheckBoxObject c = (CheckBoxObject)n;
                r.add(c.object);
            }
        }
        return r;
    }
    
    private boolean waitAction = false;
    /**
     * sets the selected items in the selected subset to e.  the selected
     * items will be constrained by the value of maxSelectCount.
     * @param e type E
     */
    public final void setSelectedItems(List<E> e) {
        waitAction = true;
        flowPane.getChildren().clear();
        
        for(CheckBoxObject cb :checkBoxObjects) {
            cb.setSelected(e.contains(cb.object)); 
        }
        
        waitAction = false;
        testShowMessage();
    }
    
    /**
     * Clears all selected items.
     */
    public final void clear() {
        waitAction = true;
        flowPane.getChildren().clear();
        for(CheckBoxObject cb :checkBoxObjects) {
            cb.setSelected(false);
        }
        
        waitAction = false;
        testShowMessage();
    }
    
    /**
     * Selects elements from a list (if in the bucket)
     * @param e E
     */
    public final void select(E e) {
        waitAction = true;
        flowPane.getChildren().clear();
        for(CheckBoxObject cb :checkBoxObjects) {
            if (cb.object == e) {
                cb.setSelected(true);
                break;
            }
        }
        waitAction = false;
        testShowMessage();
    }
    
    /**
     * The previously selected check box object, this is used to assist with 
     * a maxSelectCount of 1.
     */
    CheckBoxObject prev = null;
    
    private class XBox extends CheckBox {
        final E object;
        public XBox(E e) {
            object = e;
            setText(e.toString());
        }
    }
    
    private void testShowMessage() {
        waitAction = true;
        
        if (flowPane.getChildren().isEmpty())
            flowPane.getChildren().add(promptText);
        else if (flowPane.getChildren().size() > 1 && flowPane.getChildren().contains(promptText))
            flowPane.getChildren().remove(promptText);
        
        waitAction = false;
    }
    
    /**
     * This class manages 2 check box objects for the element E.  
     * 1 box will be maintained in the bucket, the other will appear in the
     * selected list when marked. 
     */
    public class CheckBoxObject extends CheckBox {
        
        final XBox forSelected;
        final E object;
        
        /**
         * a single constructor.
         * @param e type E
         */
        CheckBoxObject(E e) {
            
            object = e;
            forSelected = new XBox(e);
            
            
            setText(e.toString());
            setSelected(false);
            // attempt to maintain consistency for columns
            setPrefWidth(80);
            forSelected.setPrefWidth(80);
            
            if (maxSelectCount != 1) {
                this.selectedProperty().addListener( (v, o, n) -> {
                    setContains(forSelected, n);
                    testShowMessage();
                });

                forSelected.setOnAction((v) -> {
                   this.setSelected(forSelected.isSelected());
                });
            } else {
                forSelected.setSelected(true);
                forSelected.setOnAction((v) -> {
                   forSelected.setSelected(true);
                });
                
                this.setOnAction((v) -> {
                   
                   if (prev != null && prev != this) {
                       prev.setSelected(false);
                       flowPane.getChildren().clear();
                   }
                   this.setSelected(true); 
                   
                   if (!flowPane.getChildren().contains(forSelected))
                       flowPane.getChildren().add(forSelected);
                   prev = this;
                   testShowMessage();
                });
            }
        }
        
        /**
         * Select/deselect this check box.
         * @param b CheckBox, the target
         * @param v boolean, the new select value 
         */
        private void setContains(CheckBox b, Boolean v) {
            
            b.setSelected(v);
            
            if (v) {
                if (!flowPane.getChildren().contains(b)) {
                    if (maxSelectCount != 0)
                        if (flowPane.getChildren().size() >= maxSelectCount) {
                            this.setSelected(false);
                            return;
                        }
                    int at = -1;
                    // insert sor 
                    for(int i = 0; i < flowPane.getChildren().size(); i++) {
                        Node q = flowPane.getChildren().get(i);
                        if (q == promptText)
                            continue;
                        CheckBox n = (CheckBox)q;
                        String s = n.getText();
                        if (b.getText().compareTo(s) <= 0) {
                            at = i;
                            break;
                        }
                    }
                    
                    if (at == -1)
                        flowPane.getChildren().add(b);
                    else 
                        flowPane.getChildren().add(at, b);
                    
                }
            } else {
                if (flowPane.getChildren().contains(b))
                    flowPane.getChildren().remove(b);
            }
        }
        /**
         * for sorting.
         * 
         * @return 
         */
        @Override public String toString() {
            return object.toString();
        }
        
    }
    private static final int ACTION_IN      = 1,
                             ACTION_OUT     = 2,
                             ACTION_CLEAR   = 3;
    
    private ObjectProperty<InOutInterface<E>> listChange;
    
    private void triggerNodeListChange(Node n, int action) {
        if (n.getClass().isAssignableFrom(XBox.class)) {
            triggerListChange( ((XBox)n).object, action );
        }
    }
    
    private void triggerListChange(E e, int action) {
        if (listChange != null) {
            switch (action) {
                case ACTION_IN:
                    listChange.get().in(e);
                    break;
                case ACTION_OUT:
                    listChange.get().out(e);
                    break;
                case ACTION_CLEAR:
                    listChange.get().clear();
            }
        }
    }
    
    public final void setListChangeProperty(InOutInterface<E> target) {
        if (listChange == null) 
            listChange = new SimpleObjectProperty();
        listChange.set(target);
    }
    
    public final ObjectProperty<InOutInterface<E>> getListChangeProperty() {
        return listChange;
    }
    
}
