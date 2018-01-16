/*
 * Copyright (C) 2017 Joseph Nicklyn
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
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * The VPane vertically lays out child HPane's. Similar to a 
 * {@link javafx.scene.layout.VBox} though specific to HPane's. Multiple 
 * HPane's are used with in the same context to avoid conflicting EventItems
 * overlapping.  An additional HPane can be be added to accommodate the 
 * conflicting item 
 * <p/>
 * to add an event/events with conflict resolution use one of the two functions
 * addEvenItem(EventItem) or addEventItems(EventItems ...)
 * </p>
 * to add an event/events without conflict resolution use the getItems() method
 * 
 * @author Joseph Nicklyn
 */
public class VPane extends Region implements HelperInterface {
    
    /** sets this VPane as having an alternate background color */
    private boolean alternatingBackground = false;
    private static PseudoClass AXIS_ALT = PseudoClass.getPseudoClass("alt");

    private final Text axisText = new Text();
    private final Label axisLabel = new Label("", axisText);
    private boolean usingAxisLabel = false;
    private boolean showInScene = true;
//private Label axisLabel;
    private double offset = 0;
    private double scale = 1.0;
    
    private boolean hideContent = false;
    /** an empty VPane */
    public VPane() {
        super();
        init();
    }
    
    /** 
     * a new VPane with a list of HPanes.
     * -> no conflict resolution
     * @param children HPane
     */
    public VPane(HPane... children) {
        super();
        getItems().addAll(children);
        init();
    }
    
    /** 
     * a new VPane with a list of EventItems.
     * -> for conflict resolution
     * @param children EventItem
     */
    public VPane(EventItem... children) {
        super();
        addEventItems(children);
        init();
    }
    
    public VPane(String text) {
        super();
        setText(text);
        init();
    }
    
    public VPane(String text, boolean doShow) {
        super();
        setShow(doShow);
        setText(text);
        init();
    }
    
    /** 
     * a new VPane with a list of HPanes.
     * -> no conflict resolution
     * @param text String
     * @param children HPane
     */
    public VPane(String text, HPane... children) {
        super();
        setText(text);
        getItems().addAll(children);
        init();
    }
    
    /** 
     * a new VPane with a list of EventItems.
     * -> for conflict resolution
     * @param text String
     * @param children EventItem
     */
    public VPane(String text, EventItem... children) {
        super();
        setText(text);
        addEventItems(children);
        init();
    }
    
    /**
     * set a border color .. use CSS in future
     */
    private final void init() {
        axisLabel.getStyleClass().add("day-label");
        getStyleClass().add("vpane");
        ensureNotEmpty();
        axisLabel.setOnMouseClicked( e -> {
            
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                EventGroupList p = getEventGroupList();
                if (p != null)
                    p.setDaysVisible(getMyIndex(), !hideContent);
            }
        });
    }
    
    /**
     * sets the scale for the HPanes.
     * @param v double
     */
    public final void setScale(double v, int o) {
        scale = v;
        offset = -(v*o*60);
        for(HPane p: getItems()) {
            p.setScale(v);
        }
    }
    
    public final boolean hasEvents() {
        
        for(HPane p: getItems()) {
            if (!p.getItems().isEmpty())
                return true;
        }
        
        return false;
    }
    
    /** will wait until the current layout is completed */
    private boolean waitLayout = false;
    
    /** overrides parent's request of layout */
    @Override public void requestLayout() {
        if (waitLayout)
            return;
        super.requestLayout();
    }
    
    /** overrides parent's of layout children */
    @Override public void layoutChildren() {
        
        waitLayout = true;
        
        double y = 0;
        double w = 1440 * scale;
        boolean showMe = showInScene;
        if (!showMe) {
            showMe = hasEvents();
        }
        
        setVisible(showMe);
        
        if (showMe) {
        
            double x = (!usingAxisLabel)?1:axisLabel.getPrefWidth() + 1;
            double min = 30;
            if (hideContent) {
                for(HPane p: getItems())
                    p.setVisible(false);
                min = 10;
                axisText.setVisible(false);
            } else {
                axisText.setVisible(true);
                for(HPane p: getItems()) {
                    p.setVisible(true);
                    if (p.needsLayoutProperty().get())
                        p.layout();
                    double h = p.prefHeight(-1);
                    p.resizeRelocate(x + offset, y, w, h);

                    y+=h;
                }
            }
            y = Math.max(y, min);

            if (axisLabel != null) {
                axisLabel.resizeRelocate(0, 0, axisLabel.getPrefWidth(), y);
            }
        }
        
        setMinHeight(y);
        setPrefHeight(y);
        setMaxHeight(y);
        setHeight(y);
        setWidth(w);
        
        waitLayout = false;
    }

    /**
     * Provides public access to the HPane's.
     * @return ObservableList:HPane
     */
    public final ObservableList<HPane> getItems() {
        return items;
    }
    
    /**
     * responds to list events
     */
    private final ObservableList<HPane> items = new TrackableObservableList<HPane>() {
        @Override
        protected void onChanged(ListChangeListener.Change<HPane> c) {
            while (c.next()) {
                for (HPane g : c.getRemoved()) {
                    if (g != null) {
                        getChildren().remove(g);
                    }
                }
                for (HPane g : c.getAddedSubList()) {
                    if (g != null) {
                        getChildren().add(g);
                        g.setAlternatingBackground(alternatingBackground);
                    }
                }
            }
            ensureAxisFront();
        }

    };

    private boolean ensureAxisFront() {
        if (getChildren().contains(axisLabel)) {
            axisLabel.toFront();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the alternatingBackground variable. This will be used to 
     * mark any VPane's background color
     * @param v boolean
     */
    public final void setAlternatingBackground(boolean v) {
        alternatingBackground = v;
        for(HPane p: getItems()) {
            p.setAlternatingBackground(v);
        }
    }

    /**
     * Adds a new EventItem with conflict resolution. This is the preferred 
     * method for adding EventItems, it will override the getItems() methods.
     * @param item EventItem
     */
    public final void addEventItem(EventItem item) {
        HPane pane = null;
        
        for(HPane p: getItems()) {
            if (p.conflicts(item) == null) {
                pane = p;
                break;
            }
        }
    
        if (pane == null) {
            pane = new HPane(item);
            getItems().add(pane);
            pane.setAlternatingBackground(alternatingBackground);
        } else
            pane.getItems().add(item);
        
    }
    
    /**
     * Adds a list of  EventItems with conflict resolution. 
     * @param items EventItem
     */
    public final void addEventItems(EventItem... items) {
        if (items != null)
            for(EventItem i: items) {
                addEventItem(i);
            }
    }
    
    /**
     * Removes an event item if present from this VPane. In addition to 
     * removing the item, other items below if appropriate will be pulled up.
     * Also, any empty HPanes will be removed, but at least 1 will remain.
     * @param object EventItem
     * @return boolean
     */
    public final boolean removeEventItem(EventItem object) {
        
        HPane hPane = getHPaneFor(object);
        
        if (hPane != null) {
        
            hPane.getItems().remove(object);
            
            List<EventItem> l = getConflictingItems(object);
            
            for(EventItem item: l) {
                HPane pane = item.getHPane();
                if (pane != null) {
                    pane.getItems().remove(item);
                }
            }
            
            for(EventItem item: l) {
                addEventItem(item);
            }
            // remove empty VPanes
            for(int i = items.size()-1; i >= 0; i--) {
                if (items.get(i).getItems().isEmpty()) {
                    items.remove(i);
                }
            }
            
            ensureNotEmpty();
            
            return true;
            
        } else {
            return false;
        }
        
    }
    
    /**
     * Get the parent HPane for an EventItem.
     * @param object EventItem
     * @return HPane
     */
    public final HPane getHPaneFor(EventItem object) {
        
        for(HPane i: items)
            if (i.getItems().contains(object))
                return i;
        
        return null;
    }

    /**
     * Retrieves a list of EventItems which conflict with the target object.
     * @param object EventItem target
     * @return List::EventItem
     */
    private List<EventItem> getConflictingItems(EventItem object) {
        ArrayList<EventItem> result = new ArrayList();
        for(HPane hPane: items) {
            EventItem conflict = hPane.conflicts(object);
            if (conflict != null)
                result.add(conflict);
        }
        return result;
    }

    /**
     * Ensures at least 1 HPane is present.
     */
    public void ensureNotEmpty() {
        if (items.isEmpty())
            items.add(new HPane(alternatingBackground));
    }
    
    /**
     * Puts or removes the axis label from this region.
     * @param use boolean
     */
    public final void useAxisLabel(boolean use) {
        usingAxisLabel = use;
        if (getChildren().contains(axisLabel) && !use) {
            getChildren().remove(axisLabel);
        } else if (use && !getChildren().contains(axisLabel)) {
            getChildren().add(0, axisLabel);
            axisLabel.setPrefWidth(60);
        }
    }
    
    /**
     * gets the usingAxisLabel attribute.
     * @return boolean
     */
    public final boolean usingAxisLabel() {
        return usingAxisLabel;
    }
    
    /**
     * sets the axis label text. if null, the axis label will be removed
     * @param value String
     */
    public final void setText(String value) {
        axisText.setText(value);
        useAxisLabel(value != null);
    }
    
    /**
     * gets the axis label text.
     * @return String
     */
    public final String getText() {
        return axisText.getText();
    }
    
    /**
     * sets the pseudo class for the axis label.
     * @param b boolean
     */
    void setAlt(boolean b) {
        axisLabel.pseudoClassStateChanged(AXIS_ALT, b);
    }

    public final void setShow(boolean doShow) {
        showInScene = doShow;
        requestLayout();
    }
    
    public final boolean getShow() {
        return showInScene;
    }
    
    public final boolean isEmpty() {
        for(HPane hPane: items) {
            if (!hPane.isEmpty())
                return false;
        }
        return true;
    }
    
    public boolean mouseOver(MouseEvent e) {
        return localToScreen(getBoundsInLocal()).contains(e.getScreenX(), e.getScreenY());
    }
    
    @Override public String toString() {
        return axisText.getText();
    }

    public final void toggleHidden() {
        setHidden(!hideContent);
       
    }
    
    public final int getMyIndex() {
        Node n = this;
        while(n != null) {
            if (n instanceof EventGroup) {
                EventGroup g = (EventGroup)n;
                return g.getItems().indexOf(this);
            }
            n = n.getParent();
        }
        return -1;
    }
    
    public final EventGroupList getEventGroupList() {
        Node n = this;
        while(n != null) {
            if (n instanceof EventGroupList) {
                return (EventGroupList)n;
            }
            n = n.getParent();
        }
        return null;
    }
    
    public final void setHidden(boolean value) {
        if (hideContent != value) {
            hideContent = value;
            requestLayout();
        }
    }
    
    public final boolean getHidden() {
        return hideContent;
    }
    
    @Override
    public String getHelperInfo(MouseEvent e) {
        String r = "VBox\nThe VBox contains: \n" +
               "\t- an axis label - usually denoting a day\n " +
               "\t- and a group of HBoxes which contain\n\t  EventItems.";
        
        if (axisLabel.localToScreen(axisLabel.getBoundsInLocal()).contains(e.getScreenX(), e.getScreenY())) {
            r += "\n" + axisText.getText() + "\n\t\b  Double click on this Axis label \n\t\b  to toggle its visbility.";
        }
        
        return r;
    }

    @Override
    public void setExtraComments(String value) {
        
    }
}