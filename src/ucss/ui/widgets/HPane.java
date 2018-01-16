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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * The HPane lays out EventItems in a single horizonal row. Similar to a 
 * {@link javafx.scene.layout.HBox} though specific to EventItems, which define
 * its own horizontal position and width (AKA duration), and will be sized 
 * and positioned according to the current value of the scale.
 * 
 * @author Joseph Nicklyn
 */

public class HPane extends Region {
    
    /** the scale of the HPane */
    private double scale = 1.0;   
    /** to show time lines (at 1/2 hour increments) */
    private final Group lines = new Group();
    
    /** to vertical position of an EventItem */
    private static VPos vPos = VPos.CENTER;
    /** background colors can be alternated */
    private static Color[] backgroundColors = {
        Color.WHITE,
        Color.rgb(238, 238, 242)
    };
    /** background colors can be alternated */
    private static Color[] gridColors = {
        Color.rgb(224, 224, 224),
        Color.rgb(240, 240, 240)
    };
    
    /**
     * Creates an HPane with no children.
     */
    public HPane() {
        super();
        init();
    }
    
    /**
     * Creates an HPane with alternating colors set.
     */
    public HPane(boolean alt) {
        super();
        init();
        setAlternatingBackground(alt);
    }
    
    /**
     * Creates an HPane with 1 or more children.
     * @param children EventItems[]
     */
    public HPane(EventItem... children) {
        super();
        init();
        getItems().addAll(children);
    }
    
    /**
     * Initializes the HPane. Sets the border style, padding and grid lines
     */
    private final void init() {
        
        //setStyle("-fx-border-color:#ffffff00 #ffffff00 #fff #ffffff00;-fx-border-width:0 0 1 0;");
        
        setPadding(new Insets(4, 0, 4, 0));
        
        for(int i = 0; i < 24; i++) {
            Line a = new Line();
            Line b = new Line();
            a.setStroke(gridColors[0]);
            b.setStroke(gridColors[1]);
            lines.getChildren().addAll(a, b);
        }
        
        getChildren().addAll(lines);
        setAlternatingBackground(true);
    }
    
    /** 
     * Sets the HPane's alternating background scheme.
     * @param forEvenItem backgroundColors 0 or 1
     * 
     */
    public final void setAlternatingBackground(boolean forEvenItem) {
        setBackground(
            new Background(
                new BackgroundFill(
                    backgroundColors[(forEvenItem)?0:1], 
                    CornerRadii.EMPTY, 
                    Insets.EMPTY
                )
            )
        );
    }
    
    /** 
     * Gets the current scale of the HPane.
     * @return double the current scale used when drawing an {@link EventItem}
     */
    public final double getScale() {
        return scale;
    }
    
    /**
     * Sets the current scale of the HPane.
     * @param v double the new scale to set when drawing an {@link EventItem}
     */
    public final void setScale(double v) {
        if (v != scale) {
            scale = v;
            update();
        }
    }
    
    /** reduces the number of hits while refreshing the scene graph */
    private boolean waitLayout = false;
    
    /** Requests a layout pass. */
    @Override public void requestLayout() {
        if (waitLayout)
            return;
        super.requestLayout();
    }
    
    /** Lays out the EventItems for this HPane {@link EventItem} */
    @Override public void layoutChildren() {
        waitLayout = true;
        
        double tallest = 0;
        
        if ((tallest = getTallest()) > 0) {
            
            double top = getPadding().getTop();
            double bottom = getHeight() - getPadding().getBottom();
            double verticalPadding = top+getPadding().getBottom();
            double centerY = (tallest) * 0.5 + top;
            
            tallest+=verticalPadding;
            double y = 0;
            //forItems.relocate(), 0);
            for(EventItem n: getItems()) {
                if (n.isManaged()) {
                    n.setVisible(true);
                    n.layout();
                    double h = n.prefHeight(-1);
                    double w = n.prefSize(getScale());
                    double x = n.prefPos(getScale());
                    
                    switch (vPos) {
                        case TOP:
                            y = top;
                            break;
                        case CENTER:
                        case BASELINE:
                            y = centerY - h * 0.5;
                            break;
                        case BOTTOM:
                            y = bottom - h;
                            break;
                        default:
                            throw new AssertionError(vPos.name());
                        
                    }
                    n.resizeRelocate(x, y, w, h);
                } else {
                    n.setVisible(false);
                }
            }
        }    
        
        lines.relocate(0, 0);
        tallest = Math.max(tallest, 30);
        double x = 0;
        double s = scale * 30; // for half hour increments
        for(Node n: lines.getChildren()) {
            Line l = (Line)n;

            l.setStartX(x);
            l.setEndX(x);

            l.setStartY(1);
            l.setEndY(tallest-1);

            x+=s;
        }

        setMinHeight(tallest);
        setPrefHeight(tallest);
        setMaxHeight(tallest);
        setHeight(tallest);

        waitLayout = false;
    }

    /**
     * Gets the tallest {@link EventItem} for the HPane. This will help to 
     * 1. adjust the HPane's height, and to align the several EventItems
     * @return double the preferred height of the tallest EventItem 
     */
    private double getTallest() {
        
        double tallest = -1;
        
        for(EventItem n: getItems()) {
            if (n.isManaged()) {
                n.layout();
                double h = n.prefHeight(-1);
                if (h > tallest)
                    tallest = h;
            }
        }
        return (tallest);
    }
    
    /**
     * updates all the (@link EventItem} with a new scale
     */
    public final void update() {
        waitLayout = true;
        for(EventItem n: getItems()) {
            n.setSize(getScale());
        }
        waitLayout = false;
        requestLayout();
        layout();
    }
    
    /** 
     * public access to the items list.
     * @return ObservableList
     */
    public final ObservableList<EventItem> getItems() {
        return items;
    }
    
    private final ObservableList<EventItem> items = new TrackableObservableList<EventItem>() {
        @Override
        protected void onChanged(ListChangeListener.Change<EventItem> c) {
            while (c.next()) {
                for (EventItem g : c.getRemoved()) {
                    if (g != null) {
                        g.setHPane(null);
                        getChildren().remove(g);
                    }
                }
                for (EventItem g : c.getAddedSubList()) {
                    if (g != null) {
                        g.setHPane(HPane.this);
                        getChildren().add(g);
                        g.setSize(getScale());
                    }
                }
            }
            requestLayout();
        }
    };
    
    public final EventItem conflicts(EventItem canidate) {
        
        int xs1 = canidate.getPrefPosition(),
            xe1 = xs1 + canidate.getPrefSize();
        
        for(EventItem n: getItems()) {
            if (n == canidate)
                continue;
            int xs2 = n.getPrefPosition(),
                xe2 = xs2 + n.getPrefSize();
        
            if ( (xs2 >= xs1 && xs2 <= xe1) ||
                 (xe2 >= xs1 && xe2 <= xe1) ||
                 (xs1 >= xs2 && xs1 <= xe2) ||
                 (xe1 >= xs2 && xe1 <= xe2)
               ) {
                return n;
            }
                
        }
         
        return null;
    }
   
    @Override public String toString() {
        StringBuilder b = new StringBuilder();
        
        
        for(EventItem n: getItems()) {
            if (b.length() != 0)
                b.append(", ");
            b.append("[").append(n).append("]");
        }
        
        return "HPane: " + b.toString();
    }
    
    public final VPane getVPane() {
        if (getParent() != null) {
            if (getParent() instanceof VPane) {
                return (VPane)getParent();
            }
        }
        return null;
    }
    
    public final boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean mouseOver(MouseEvent e) {
        return localToScreen(getBoundsInLocal()).contains(e.getScreenX(), e.getScreenY());
    }
    
}
