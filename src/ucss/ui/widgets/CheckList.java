/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ucss.ui.widgets.CheckList.CheckItem;

/**
 *
 * @author John
 */
public class CheckList<E> extends ScrollPane {

    public static PseudoClass ODD_ITEM = PseudoClass.getPseudoClass("odd");
    public static PseudoClass EVEN_ITEM = PseudoClass.getPseudoClass("even");
    private final ToggleGroup tGroup = new ToggleGroup();
    
    private final Pane content;
    
    private final int prefVisible;
    private final boolean isOptionList;
    private final boolean multiColumn;

    public CheckList() {
        this(5, false, false);
    } 
    
    public CheckList(int visibleSize) {
        this(visibleSize, false, false);
    }
    
    public CheckList(boolean asOptionList) {
        this(5, asOptionList, false);
    }
    
    
    public CheckList(int visibleSize, boolean asOptionList) {
        this(visibleSize, asOptionList, false);
        
    }
    
    public CheckList(int visibleSize, boolean asOptionList, boolean multiColumn) {
        this.multiColumn = multiColumn;
        if (multiColumn) {
            FlowPane fp = new FlowPane();
            content = fp;
            fp.setOrientation(Orientation.VERTICAL);
            setFitToHeight(true);
            fp.setHgap(6.0);
            
        } else {
            content = new VBox();
            setFitToWidth(true);
        }
        
        
        prefVisible = visibleSize;
        isOptionList = asOptionList;
        setContent(content);
        content.getStyleClass().add("check-list");
        
    }
    
    public final void add(E e) {
        new CheckItem<E>(e);
    }
    
    public final void pauseFire(boolean value) {
        pauseFire = value;
    }
    private boolean pauseFire = false;
    private boolean waitFire = false;
    private void setValueSelected(Object source, Boolean n) {
        if (!waitFire && !pauseFire) {
            if (getOnSelected() != null)
                getOnSelected().handle(new CheckListSelectedEvent(source, n));
        }
    }
    
    public final boolean isSelected(Object item) {
        for(Node n: content.getChildren()) {
            CheckItem c = ((CheckItem)n);
            if (c.getSource() == item) 
                return c.isSelected();
        }
        return false;
    }
    
    public final void setSelected(Object item, boolean value) {
        boolean xWait = waitFire;
        waitFire = true;
        for(Node n: content.getChildren()) {
            CheckItem c = ((CheckItem)n);
            if (c.getSource() == item) 
                c.selectValue(value);
        }
        waitFire = xWait;
    }

    public void selectNone() {
        boolean xWait = waitFire;
        waitFire = true;
        for(Node n: content.getChildren()) {
            CheckItem c = ((CheckItem)n);
            c.selectValue(false);
        }
        waitFire = xWait;
 
    }

    
    private double updateHeight() {
        double v = 0.0;
        int p = prefVisible;
        for(Node n: content.getChildren()) {
            if (p-- <= 0) 
                break;
            v+=n.prefHeight(-1);
        }
        v = Math.max(v, 24);
        return v+4;
    }
    
    @Override public void requestLayout() {
        setPrefHeight(updateHeight());
        super.requestLayout();
        
    }
    
    public void clear() {
        content.getChildren().clear();
    }

    class CheckItem<S> extends HBox {
        
        private final S source;
        private final Label label = new Label();
        private final Node box;
        
        public CheckItem(S item) {
            
            setSpacing(8);
            source = item;
            label.setText(source.toString());
            getChildren().add(label);
            content.getChildren().add(this);
            this.setFocusTraversable(true);
            
            this.setOnMousePressed( e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    if (getScene().getFocusOwner() == this) {
                        toggleValue();
                    }
                }
                e.consume();
            });
            
            this.setOnMouseReleased( e -> {
                requestFocus();
                e.consume();
            });
            
            this.focusedProperty().addListener( (e, o, n) -> {
                this.setFocused(n);
            });
            if (!multiColumn) {
                if (content.getChildren().size() % 2 == 0)
                    this.pseudoClassStateChanged(EVEN_ITEM, true);
                else
                    this.pseudoClassStateChanged(ODD_ITEM, true);
            }
            setStyle("-fx-padding:0.5em 0.5em 0.75em 0.5em;-fx-max-width:10000;");
            
            if (isOptionList) {
                RadioButton b = new RadioButton();
                box = b;
                b.setToggleGroup(tGroup);
                b.selectedProperty().addListener( (e, o, n) -> { setValueSelected(source, n); });
            } else {
                CheckBox b = new CheckBox();
                box = b;
                b.selectedProperty().addListener( (e, o, n) -> { setValueSelected(source, n); });
            }
            
            
            getChildren().add(0, box);
            box.setFocusTraversable(false);
                
            box.setOnMousePressed( e -> { 
                this.requestFocus();
            });
            
        }

        public final Object getSource() {
            return source;
        }
        
        private final void toggleValue() {
            if (box instanceof CheckBox) {
                CheckBox c = (CheckBox)box;
                c.setSelected(!c.isSelected());
            } else if (box instanceof RadioButton) {
                RadioButton r = (RadioButton)box;
                r.setSelected(!r.isSelected());
            }
        }
        
        boolean isSelected() {
            if (box instanceof CheckBox) {
                CheckBox c = (CheckBox)box;
                return c.isSelected();
            } else if (box instanceof RadioButton) {
                RadioButton r = (RadioButton)box;
                return r.isSelected();
            }
            return false;
        }
        
        void selectValue(boolean value) {
            if (box instanceof CheckBox) {
                CheckBox c = (CheckBox)box;
                c.setSelected(value);
            } else if (box instanceof RadioButton) {
                RadioButton r = (RadioButton)box;
                r.setSelected(value);
            }
        }
        
        @Override
        public String toString() {
            return source.toString();
        }

    }
    
    public final ObjectProperty<EventHandler<CheckListSelectedEvent>> onSelectedProperty() { return onSelected; }
    public final void setOnSelected(EventHandler<CheckListSelectedEvent> value) { onSelectedProperty().set(value); }
    public final EventHandler<CheckListSelectedEvent> getOnSelected() { return onSelectedProperty().get(); }
    private ObjectProperty<EventHandler<CheckListSelectedEvent>> onSelected = new ObjectPropertyBase<EventHandler<CheckListSelectedEvent>>() {
        @Override protected void invalidated() {
            setEventHandler(CheckListSelectedEvent.SELECTED, get());
        }

        @Override
        public Object getBean() {
            return CheckList.this;
        }

        @Override
        public String getName() {
            return "onSelected";
        }
    };
    
}