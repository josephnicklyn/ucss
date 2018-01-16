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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class EventGroupList extends Region {
    private final TimeLine timeLine = new TimeLine();
    private final XScrollBar xScrollBar = new XScrollBar();
    
    public EventGroupList() {
        init();
    }
    
    public EventGroupList(EventGroup... children) {
        init();
        if (children != null)
            getItems().addAll(children);
    }
    
    private boolean waitSet = false;
    
    private void init() {
        getChildren().addAll(timeLine, xScrollBar);
        xScrollBar.toFront();
        xScrollBar.setBubbleTip("Fine tune reset");
        setStyle("-fx-background-color:#666668;");
        timeLine.onScaleChanged( (e, o, n) -> {
            requestLayout();
            layoutChildren();
            //waitSet = true;
            //waitLayout = false;
            //setGraphItems();
            //waitSet = false;
        });
        
        timeLine.onFirstItemChanged( (e, o, n) -> {
            waitSet = true;
            setGraphItems();
            waitLayout = false;
            waitSet = false;
        });
        
        xScrollBar.onValueChanged((e, o, n) -> {
            waitSet = true;
            xScrollBar.setFineTune(0);
            setGraphItems();
            waitLayout = false;
            waitSet = false;
        });
        
        xScrollBar.onFineTuneChanged((e, o, n) -> {
            waitSet = true;
            setGraphItems();
            waitLayout = false;
            waitSet = false;
        });
        
        this.setOnScroll( e -> {
            if (e.getDeltaY() > 0) {
                if (e.isAltDown()) 
                    xScrollBar.pageDown();
                else
                    xScrollBar.decrement();
            } else {
                if (e.isAltDown()) 
                    xScrollBar.pageUp();
                else
                    xScrollBar.increment();
            }
        });
        
    }
    
    public final ObservableList<EventGroup> getItems() {
        return items;
    }
    
    private final ObservableList<EventGroup> items = new TrackableObservableList<EventGroup>() {
        @Override
        protected void onChanged(ListChangeListener.Change<EventGroup> c) {
            while (c.next()) {
                for (EventGroup g : c.getRemoved()) {
                    if (g != null) {
                        getChildren().remove(g);
                    }
                }
                for (EventGroup g : c.getAddedSubList()) {
                    if (g != null) {
                        getChildren().add(g);
                        g.setOnAnchorChange( (e, o, n) -> { dropAnchor(g, n); });
                    }
                }
            }
            timeLine.toFront();
            xScrollBar.toFront();
            xScrollBar.setMax(items.size()-1);
            requestLayout();
        }

    };
    
    private EventGroup anchored = null;
    private boolean waitAnchor = false;
    
    private void dropAnchor(EventGroup g, boolean value) {
        
        if (waitAnchor)
            return;
    
        waitAnchor = true;
        
        if (value) {
            anchored = g;
            for(EventGroup o: getItems()) {
                if (o == g)
                    continue;
                o.setAnchor(false);
            }
        } else
            anchored = null;
        
        if (!waitSet)
            setGraphItems();
        
        waitAnchor = false;
    }
    
    private boolean waitLayout = false;

    
    @Override public void requestLayout() {
        if (waitLayout)
            return;
        super.requestLayout();
    }
    
    private double innerWidth = 0;
    
    @Override public void layoutChildren() {
        waitLayout = true;
        
        double sbW = xScrollBar.getSize();

        innerWidth = getWidth() - sbW;

        timeLine.layout();
        
        timeLine.resizeRelocate(0, 0, innerWidth, timeLine.getHeight());
        xScrollBar.resizeRelocate(innerWidth, 0, sbW, getHeight());
        
        //if (!waitSet)
            setGraphItems();
        //waitSet = false;
        waitLayout = false;
    }
    
    private void setGraphItems() {
        //waitLayout = true;
        double width = getWidth();
        double tlH = timeLine.getHeight();
        double height = getHeight();
        double y=tlH - (xScrollBar.getFineTune() * 20);
        
        for(EventGroup g: items) {
            g.setScale(timeLine.getScale(), timeLine.getFirstVisibleItem());
        }
        int b = xScrollBar.getValue();
        
        if (anchored != null) {
            anchored.setVisible(true);
            anchored.requestLayout();
            anchored.layout();
            double h = anchored.prefHeight(-1);
            anchored.resizeRelocate(0, y, innerWidth, h);
            y+=h;
        }
        
        for(EventGroup g: items) {
            if (g == anchored)
                continue;
            if (y > height) {
                g.setVisible(false);
            } else if (b--<=0) {
                g.setVisible(true);
                g.requestLayout();
                g.layout();
                double h = g.prefHeight(-1);
                g.resizeRelocate(0, y, innerWidth, h);
                y+=h;
            } else {
                g.setVisible(false);
            }
        }
    }
    
    /**
     * Releases all events associated with this EventGroupList.
     */
    public final void clear() {
        items.clear();
    }
    
    public final boolean add(EventGroup group) {
        if (!items.contains(group))
            return items.add(group);
        else
            return false;
    }
    
    public <R, I> void eventWalker(GraphWalker callback) {
        for(EventGroup group: getItems()) {
            for(VPane vp: group.getItems()) {
                for(HPane hp: vp.getItems()) {
                    for(EventItem i: hp.getItems()) {
                        if (callback.walkIinR(EventGroupList.this, (I)i, (R)group))
                            return;
                    }
                }
            }
        }
    }
    
    public <R> void roomWalker(GraphWalker callback) {
        for(EventGroup group: getItems()) {
            if (callback.walkIinR(EventGroupList.this, null, (R)group))
                return;
        }
    }
    
    @Override
    public String toString() {
        return "EvenGroupList";
    }

    void setDaysVisible(int myIndex, boolean hideContent) {
        for(EventGroup group: getItems()) {
            if (myIndex >= 0 && myIndex < group.getItems().size())
                group.getItems().get(myIndex).setHidden(hideContent);
        }
    }
}
