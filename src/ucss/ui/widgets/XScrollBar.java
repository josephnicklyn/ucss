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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class XScrollBar extends Region {
    
    private int max = 50;
    private int largeChange = 10;
    private SimpleIntegerProperty value = new SimpleIntegerProperty(0);
    private SimpleDoubleProperty fineTune = new SimpleDoubleProperty(0);
    
    private boolean smoothScrolling = true;
    
    private final Pane arrowDecrement = new Pane();
    private final Pane arrowIncrement = new Pane();
    
    private final Pane arrowPageUp = new Pane();
    private final Pane arrowPageDown = new Pane();
    
    private final Pane menuCircle = new Pane();
    
    private final Pane grabber = new Pane();
    
    private final Button btnDecrement = new Button("", arrowDecrement);
    private final Button btnIncrement = new Button("", arrowIncrement);
    
    private final Button btnPageUp = new Button("", arrowPageUp);
    private final Button btnPageDown = new Button("", arrowPageDown);

    private final Button btnMenu = new Button("", menuCircle);

    
    private Button thumb = new Button("", grabber);
    
    private double tSize = 20;
    private boolean asFineTuner = false;
    
    private static PseudoClass XSCROLLBAR_VERT = PseudoClass.getPseudoClass("vertical");
    private static PseudoClass XSCROLLBAR_HORZ = PseudoClass.getPseudoClass("horizontal");

    private double getThumbRatio() {
        double lc = largeChange>=max?1:largeChange;
        return ((double)lc)/((double)(max+1));
    }
    
    public XScrollBar() {
        getStyleClass().add("xscroll-bar");
        btnDecrement.getStyleClass().add("decrement-button");
        btnIncrement.getStyleClass().add("increment-button");
        btnPageUp.getStyleClass().add("decrement-button");
        btnPageDown.getStyleClass().add("increment-button");
        btnMenu.getStyleClass().add("square-button");
        menuCircle.getStyleClass().add("circle");
        arrowPageUp.getStyleClass().add("page-up-arrow");
        arrowPageDown.getStyleClass().add("page-down-arrow");
        grabber.getStyleClass().add("grabber");
        arrowDecrement.getStyleClass().add("decrement-arrow");
        arrowIncrement.getStyleClass().add("increment-arrow");
        thumb.getStyleClass().add("thumb");
        getChildren().addAll(btnDecrement, btnIncrement, btnPageUp, btnPageDown, btnMenu, thumb);
        setOrientation(Orientation.VERTICAL);
        btnIncrement.setOnAction( e -> {increment(); e.consume();});
        btnDecrement.setOnAction( e -> {decrement(); e.consume();});
        btnPageUp.setOnAction( e -> {pageUp(); e.consume();});
        btnPageDown.setOnAction( e -> {pageDown(); e.consume();});
        btnMenu.setOnAction( e -> {fineTune.set(0); e.consume();});
        setOnMouseClicked(e -> {scroll(e);});
        
        this.setOnScroll( e -> {
            if (e.getDeltaY() > 0) {
                decrement();
            } else {
                increment();
            }
        });
        
        initalize();
    }

    public final void setOrientation(Orientation orientation) {
        switch (orientation) {
            case HORIZONTAL:
                pseudoClassStateChanged(XSCROLLBAR_VERT, false);
                pseudoClassStateChanged(XSCROLLBAR_HORZ, true);
                break;
            case VERTICAL:
                pseudoClassStateChanged(XSCROLLBAR_HORZ, false);
                pseudoClassStateChanged(XSCROLLBAR_VERT, true);
                
                break;
            default:
                throw new AssertionError(orientation.name());
            
        }
    }
    public final void setValue(int v) {
        if (v > getMax()) v = getMax();
        if (v < 0) v = 0;
        if (!smoothScrolling) {
            v/=getLargeChange();
            v*=getLargeChange();
        }
        value.set(v);
        requestLayout();
    }
    
    public final int getValue() {
        return value.get();
    }
    
    public final void setMax(int value) {
        max = Math.max(0, value);
        layoutChildren();
    }
    
    public final int getMax() {
        return max;
    }
    
    public final void setLargeChange(int value) {
        if (value >= max) value = max-1;
        largeChange = Math.max(1, value);
    }
    
    public final int getLargeChange() {
        return largeChange;
    }
    
    private boolean waitLayout = false;
    
    @Override public void requestLayout() {
        if (waitLayout) 
            return;
        super.requestLayout();
        
    }
    
    @Override public void layoutChildren() {
        
        waitLayout = true;
        
        double 
            width = getWidth(), 
            height = getHeight();
        
        double s = tSize;
        double innerHeight = height - (s*5);
        double thumbSize = Math.max(innerHeight * (getThumbRatio()), 20);
        
        innerHeight -= thumbSize;
        double val = (double)getValue();
        double valRatio = val/getMax();
        double y = s + valRatio * innerHeight;
        thumb.resizeRelocate(0, y, s, thumbSize);
        btnDecrement.resizeRelocate(0, 0, s, s);
        
        double bs = s*4;
        btnIncrement.resizeRelocate(0, height-bs, s, s);
        bs-=s;
        btnPageUp.resizeRelocate(0, height-bs, s, s);
        bs-=s;
        btnMenu.resizeRelocate(0, height-bs, s, s);
        bs-=s;
        btnPageDown.resizeRelocate(0, height-bs, s, s);
        
        
        setPrefWidth(s);
        setWidth(s);
        waitLayout = false;
    }

    public void decrement() {
        int v = value.get()-1;
        if (v < 0) v = 0;
        value.set(v);
        requestLayout();
    }

    public void increment() {
        int v = value.get()+1;

        if (v > getMax())
            v = getMax();
        value.set(v);
        requestLayout();

    }

    public void pageUp() {
        if (!asFineTuner) {
            int v = value.get()-largeChange;
            if (v < 0) v = 0;
            value.set(v);
            requestLayout();
        } else {
            double v = fineTune.get()-1;
            if (v < 0) v = 0;
            fineTune.set(v);
        }
    }

    public void pageDown() {
        if (!asFineTuner) {
            int v = value.get()+largeChange;
            if (v > getMax())
                v = getMax();
            value.set(v);
            requestLayout();
        } else {
            double v = fineTune.get()+1;
            fineTune.set(v);
        }
    }

    
    private void scroll(MouseEvent e) {
        Bounds b = thumb.getBoundsInParent();
        int v = value.get();
        if (e.getY() > b.getMaxY()) {
            v += getLargeChange();
        } else if (e.getY() < b.getMinY()) {
            v -= getLargeChange();
        }
        
        setValue(v);
    }

    private double grabX = 0, grabY = 0;
    private boolean grabbed = false;
    
    private void initalize() {
    
        thumb.setOnMouseReleased( e -> {
            grabbed = false;
            e.consume();
        });
        
        thumb.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {//e.getButton() == MouseButton.PRIMARY) {
                if (!grabbed) {
                    grabX = 0;//e.getY();///2;
                    grabY = e.getSceneY()-thumb.getBoundsInParent().getMinY();
                    grabbed = true;
             
                } else {
                    double y = e.getSceneY() - grabY - grabX;// + (e.getY() - grabX);
                    double h = y/(getHeight()-(tSize*5)-thumb.getHeight());
                    int v = (int)((h)*(double)getMax());
                    setValue(v);
                }
            }
        });

    }
    
    public final double getSize() {
        return tSize;
    }
    
    public final void setSize(double value) {
        if (value < 20) 
            value = 20;
        if (value > 40) 
            value = 40;
        tSize = value;
    }
    
    
    
    public final void onValueChanged(ChangeListener<Number> listener) {
        value.addListener(listener);
    }
    
    public final void onFineTuneChanged(ChangeListener<Number> listener) {
        fineTune.addListener(listener);
        asFineTuner = true;
    }
    
    public final void setAsFineTuner(boolean value) {
        asFineTuner = value;
    }
    
    public final double getFineTune() {
        return fineTune.get();
    }
 
    public final void setFineTune(double value) {
        fineTune.set(value);
    }
    
    public final void setBubbleTip(String text) {
        if (text != null)
            btnMenu.setTooltip(new Tooltip(text));
        else
            btnMenu.setTooltip(null);
    }
    
}
