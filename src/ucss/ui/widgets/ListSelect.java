/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
/**
 *
 * @author John
 */
public class ListSelect<E> extends VBox {
    
    private final ComboBox<E> cmbBox = new ComboBox();
    private final ListView<CustomListItem> lstBox = new ListView();
    private final Button btnAdd = new Button("+");
    private final HBox hBox = new HBox(cmbBox, btnAdd);
    private int maxDestSize = 5;
    
    private SimpleObjectProperty<E> onAddedItem = new SimpleObjectProperty();
    private SimpleObjectProperty<E> onRemovedItem = new SimpleObjectProperty();
    
    public ListSelect() {
        this(5, null);
    }
    
    public ListSelect(int viewSize) {
        this(viewSize, null);
    }
    
    public ListSelect(E... e) {
        this(5, e);
    }
    
    public ListSelect(int viewSize, E... e) {
        if (viewSize < 1) viewSize = 1;
        maxDestSize = viewSize;
        if (e != null)
            cmbBox.getItems().addAll(e);
        VBox.setVgrow(lstBox, Priority.ALWAYS);
        HBox.setHgrow(cmbBox, Priority.ALWAYS);
        getChildren().addAll(lstBox, hBox);
        this.setFillWidth(true);
        
        cmbBox.setMaxWidth(Double.MAX_VALUE);
        setMinWidth(120);
        setXMinHeight();
        
        btnAdd.setOnAction( v -> { add(); });
        cmbBox.getSelectionModel().selectedItemProperty().addListener(
                (v, o, n) -> {
                    btnAdd.setDisable(n == null);
                }
        );
        btnAdd.setDisable(true);
        btnAdd.setStyle("-fx-background-radius:0px;-fx-background-color:-fx-outer-border, -fx-inner-border, -fx-body-color;-fx-background-insets: 0, 1, 2;");
        cmbBox.setStyle("-fx-background-radius:0px;-fx-background-color:-fx-outer-border, -fx-inner-border, -fx-body-color;-fx-background-insets: 0, 1, 2;");
        lstBox.setStyle("-fx-background-color:-fx-box-border,-fx-control-inner-background;-fx-background-insets: 0, 1 1 0 1;");
    
    }
    
    public final void addAll(E... e) {
        
        clear();
        
        if (e != null) {
            cmbBox.getItems().addAll(e);
            
        }
    }
    
    public final void add(E e) {
        if (e != null) {
            if (!cmbBox.getItems().contains(e))
                cmbBox.getItems().add(e);
        }
    }
    
    public final void clear() {
        cmbBox.getItems().clear();
        lstBox.getItems().clear();
        btnAdd.setDisable(true);
    }
    
    private void add() {
        if (cmbBox.getValue() != null) {
            if (lstBox.getItems().size() >= (maxDestSize))
                return;
            E out = cmbBox.getValue();
            cmbBox.getItems().remove(out);
            CustomListItem in = new CustomListItem(out);
            lstBox.getItems().add(in);
            setXMinHeight();
            btnAdd.setDisable(true);
            cmbBox.setValue(null);
            cmbBox.getItems().sorted();
            lstBox.getItems().sort(destComparator);
            if (onAddedItem.get() == out)
                onAddedItem.set((E)null);
            onAddedItem.set(out);
            
        }
    }
    
    private void restore(CustomListItem item) {
        if (lstBox.getItems().contains(item)) {
            lstBox.getItems().remove(item);
            E in = item.pTarget;
            cmbBox.getItems().add(in);
            cmbBox.getItems().sort(sourceComparator);
            if (onRemovedItem.get() == in)
                onRemovedItem.set((E)null);
            
            onRemovedItem.set(in);
        }
    }
    
    public final void setOnRemovedItem(ChangeListener<E> listener) {
        onRemovedItem.addListener(listener);
    }
    
    public final void setOnAddedItem(ChangeListener<E> listener) {
        onAddedItem.addListener(listener);
    }

    Comparator<E> sourceComparator = new Comparator<E>() {
        @Override
        public int compare(E left, E right) {
            return left.toString().compareToIgnoreCase(right.toString()); 
        }
    };
    
    Comparator<CustomListItem> destComparator = new Comparator<CustomListItem>() {
        @Override
        public int compare(CustomListItem left, CustomListItem right) {
            return left.toString().compareToIgnoreCase(right.toString()); 
        }
    };

    private void setXMinHeight() {
        int i = Math.min(maxDestSize, lstBox.getItems().size());
        double h = 29 * i + cmbBox.prefHeight(-1) + 6;
        setMinHeight(h);
        setMaxHeight(h);
    }
    
    class CustomListItem extends HBox {
        
        private final E pTarget;
        private final Label label = new Label();
        private final Button btnRemove = new Button("-");
        
        public CustomListItem(E target) {
            setAlignment(Pos.CENTER_LEFT);
            label.setText(target.toString());
            pTarget = target;
            HBox.setHgrow(label, Priority.ALWAYS);
            label.setMaxWidth(Double.MAX_VALUE);
            getChildren().addAll(label, btnRemove);
            btnRemove.setOnAction( e -> { restore(this); });
            btnRemove.setStyle("-fx-background-radius:0px;-fx-font-size:10;-fx-padding:0.25em 0.5em;");
        }
        
        @Override public String toString() {
            return pTarget.toString();
        }
    }
   
}