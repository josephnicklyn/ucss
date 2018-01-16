/*
 * Copyright (C) 2017 John
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

import com.sun.javafx.collections.TrackableObservableList;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author John
 */
public class EventGroup extends Region implements HelperInterface {
    
    private final Label title = new Label("Title");
    private final Label secondaryTitle = new Label("^");
    private final Rectangle clipper = new Rectangle();
    
    private final Pane forGPanes = new Pane();
    private boolean expanded = true;
    private double scale = 1.0;
    private int offset = 0;
    private int referenceID = 0;
    private static final PseudoClass anchored = PseudoClass.getPseudoClass("anchor");

    private SimpleBooleanProperty dropAnchor = new SimpleBooleanProperty(false);
    private static SimpleObjectProperty<EventGroup> dropAnchorFor = new SimpleObjectProperty(null);
    
    public final static void setOnDropAnchorForChange(ChangeListener<EventGroup> listener) {
        dropAnchorFor.addListener(listener);
    }
    
    public EventGroup() {
        init("Title");
    }
    
    public EventGroup(VPane ... children) {
        items.addAll(children);
        init("Title");
    }
    
    public EventGroup(String text) {
        init(text);
    }
    
    public EventGroup(String text, VPane ... children) {
        items.addAll(children);
        init(text);
    }
    
    public final int getReferenceID() {
        return referenceID;
    }
    
    public final void setReferenceID(int value) {
        referenceID = value;
    }
    
    public final void setSecondaryTitle(String value) {
        if (value == null || value.isEmpty())
            value = "^";
        
        secondaryTitle.setText(value);
    }
    
    public final void setScale(double scale, int offset) {
        this.scale = scale;
        this.offset = offset;
        for(VPane r: items) {
            r.setScale(scale, offset);
        }
    }
    
    public final double getScale() {
        return scale;
    }
    
    private void init(String text) {
        //setClip(clipper);
        getChildren().addAll(title, secondaryTitle, forGPanes);
        setText(text);
        title.getStyleClass().add("weekly-title");
        secondaryTitle.getStyleClass().add("weekly-secondary-title");
        title.setOnMouseClicked( e -> {
            toggleExpanded();
        });
        
        secondaryTitle.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.PRIMARY) 
                toggleAnchor();
        });
    }
    
    public final void setAnchor(boolean value) {
        dropAnchor.set(value);
        secondaryTitle.pseudoClassStateChanged(anchored, value);
        
    }
    
    public final boolean getAnchor() {
        return dropAnchor.get();
    }
    
    public final void toggleAnchor() {
        setAnchor(!dropAnchor.get());
    }
    
    public final void setOnAnchorChange(ChangeListener<Boolean> listener) {
        dropAnchor.addListener(listener);
    }
    
    public final void setText(String value) {
        title.setText(value);
    }
    
    public final String getText() {
        return title.getText();
    }
    
    private boolean waitLayout = false;
    
    @Override public void requestLayout() {
        if (waitLayout)
            return;
        super.requestLayout();
    }
    
    @Override public void layoutChildren() {
        waitLayout = true;
        super.layoutChildren();
        double width = getWidth();
        double height = getHeight();
        double tH = title.prefHeight(-1);
        double tH2 = secondaryTitle.prefHeight(-1);
        double tW2 = secondaryTitle.prefWidth(-1);
        double y = 0;
        
        title.resizeRelocate(0, 0, width, tH);
        secondaryTitle.resizeRelocate(width - 10 - tW2, 0, tW2, tH2);
        
        forGPanes.relocate(0, tH);
        
        for(VPane r: items) {
            r.setVisible(expanded);
            r.setManaged(expanded);
            if (expanded) {
                if (r.needsLayoutProperty().get()) {
                    r.layout();
                }

                double h = r.prefHeight(-1);
                r.resizeRelocate(0, y, width, h);
                y+=h;
            }
        }
        
        y+=tH;
        setHeight(y);
        setPrefHeight(y);
        
        clipper.setWidth(width);
        clipper.setHeight(y);
        waitLayout = false;
    }
    
    public final ObservableList<VPane> getItems() {
        return items;
    }
    
    private final ObservableList<VPane> items = new TrackableObservableList<VPane>() {
        @Override
        protected void onChanged(ListChangeListener.Change<VPane> c) {
            while (c.next()) {
                for (VPane g : c.getRemoved()) {
                    if (g != null) {
                        forGPanes.getChildren().remove(g);
                    }
                }
                for (VPane g : c.getAddedSubList()) {
                    if (g != null) {
                        forGPanes.getChildren().add(g);
                        g.setAlternatingBackground((forGPanes.getChildren().size() %2) == 0);
                        g.setAlt((forGPanes.getChildren().size() %2) == 0);
                    }
                }
            }
            requestLayout();
        }
    };
    
    public final void clearEvents() {
        for(VPane r: items) {
            r.getItems().clear();
            r.ensureNotEmpty();
        }
    }
    
    public <R>void removeEventItems(List<R> nodes) {
        for(R ei: nodes) {
            removeEventItem((EventItem)ei);
        }
    }
   
    
    public final boolean removeEventItem(EventItem object) {
        for(VPane r: items) {
            if (r.removeEventItem(object))
                return true;
        }
        return false;
    }
    
    public final boolean addEventItem(EventItem object, int rowIndex) {
            
        if (object != null) {
            if (rowIndex >= 0 && rowIndex < items.size()) {
                items.get(rowIndex).addEventItem(object);
                return true;
            }
        }
        return false;
    }
    
    public final boolean isEmpty() {
        for(VPane r: items) {
            if (!r.isEmpty())
                return false;
        }
        return true;
    }
    
    /**
     * Removes all items from this group
     */
    public final void clear() {
        getItems().clear();
    }
    
    public final boolean getExpanded() {
        return expanded;
    }
    
    public final void setExpanded(boolean value) {
        expanded = value;
        requestLayout();
        layout();
    }
    
    public final void toggleExpanded() {
        setExpanded(!expanded);
        
    }
    
    public final void autoHide() {
       setExpanded(!isEmpty());
    }

    public int overDay(MouseEvent e) {
        
        int i = 0;
        
        for(VPane vp: getItems()) {
            if (vp.mouseOver(e))
                return i;
            i++;
        }
        
        return -1;
    }
    
    public int getOverTime(MouseEvent e, double xOffset) {
        Bounds b = this.localToScreen(this.getBoundsInParent());
        
        double x = e.getScreenX() - b.getMinX() - xOffset;
        
        return((int)((x/scale)/30)*30);
    }
    
    public double getMouseHints(MouseEvent e, double xOffset) {
        
        Bounds b = this.localToScreen(this.getBoundsInParent());
        
        double x = e.getScreenX() - b.getMinX() - xOffset;
        
        double closestTime = (double)((int)((x/scale)/30)*30);
        
        double v = (x/scale)-closestTime;
        
        return v;
    }
    private final String timeToString(double v) {
        int i = (int)v;
        int m = i % 60;
        int h = i/60;
        
        return String.format("%02d:%02d", h, m);
    }

    private Object forObject;
    
    public final void setForObject(Object object) {
        forObject = object;
    }
    
    public final Object getForObject() {
        return forObject;
    }
    private String extraComments = "";
    @Override
    public String getHelperInfo(MouseEvent e) {
        String r = "Event Group\nAn event group contains a group of day items.\n" +
                                 "Monday - Thursday are visible by default, Friday - Sunday\n" +
                                 "will appear as needed.";
                
        if (title.localToScreen(title.getBoundsInLocal()).contains(e.getScreenX(), e.getScreenY())) {
            r += "\n\n" + title.getText() + "\n\t\b  Click on the groups title bar\n\t\b  to toggle its visbility.";
        }
        
        return r + extraComments;
    }

    @Override
    public void setExtraComments(String value) {
        extraComments = value;
    }

}
