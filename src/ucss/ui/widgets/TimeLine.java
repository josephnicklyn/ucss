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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class TimeLine extends Region  implements HelperInterface {
    
    private DoubleProperty scale = new SimpleDoubleProperty(1.0);
    private IntegerProperty prefVisible = new SimpleIntegerProperty(14);
    private IntegerProperty actualVisible = new SimpleIntegerProperty(14);

    private IntegerProperty firstVisible = new SimpleIntegerProperty(8);
    private int prefFirst = 8;
    private static PseudoClass OFF_HOURS = PseudoClass.getPseudoClass("dark");

    private final Button btnLess = new Button("<");
    private final Button btnMore = new Button(">");
    private final HBox hBox = new HBox(4, btnLess, btnMore);
    
    private final Label[] timeLabel = {
        new Label("00:00"), new Label("01:00"), new Label("02:00"), new Label("03:00"),
        new Label("04:00"), new Label("05:00"), new Label("06:00"), new Label("07:00"),
        new Label("08:00"), new Label("09:00"), new Label("10:00"), new Label("11:00"),
        new Label("12:00"), new Label("13:00"), new Label("14:00"), new Label("15:00"),
        new Label("16:00"), new Label("17:00"), new Label("18:00"), new Label("19:00"),
        new Label("20:00"), new Label("21:00"), new Label("22:00"), new Label("23:00")  
    };
    
    public TimeLine() {
        for(Label l: timeLabel) {
            l.getStyleClass().add("time-label");
            getChildren().add(l);
        }
        
        for(int i = 0; i < 8; i++) {
            timeLabel[i].pseudoClassStateChanged(OFF_HOURS, true);
        }
        getChildren().add(hBox);
        hBox.setAlignment(Pos.CENTER);
        timeLabel[22].pseudoClassStateChanged(OFF_HOURS, true);
        timeLabel[23].pseudoClassStateChanged(OFF_HOURS, true);
        hBox.setStyle("-fx-background-color:#eaeaef;");
        
        btnLess.setOnAction( e -> {goPrev();});
        
        btnMore.setOnAction( e -> { goNext();});
    }
    
    
    public final void onScaleChanged(ChangeListener<Number> listener) {
        scale.addListener(listener);
    }
    
    public final void onVisibleCountChanged(ChangeListener<Number> listener) {
        actualVisible.addListener(listener);
    }
    
    public final void onFirstItemChanged(ChangeListener<Number> listener) {
        firstVisible.addListener(listener);
    }
    
    public final int getVisibleCount() {
        return actualVisible.get();
    }
    
    public final int getFirstVisibleItem() {
        return firstVisible.get();
    }
    
    public final double getScale() {
        return scale.get();
    }
    
    
    private boolean waitLayout = false;
    
    @Override public void requestLayout() {
        if (waitLayout) 
            return;
        super.requestLayout();
        
    }
    
    public final double getPrefScale() {
        double r = 1.0;
        
        double w = getWidth() - 60;
        
        double n = Math.floor(w/60);
        
        double v = (double)prefVisible.get();
        
        if (v > n)
            v = n;
        
        actualVisible.set((int)v);
        double t = v * 60;
        r = (w/t);
        
        
        return r;
    }
    
    @Override public void layoutChildren() {
        
        waitLayout = true;
        
        double scaled = getPrefScale();
        scale.set(scaled);
        double iWidth = 60 * scaled;
        int p = prefFirst;
        if (p+ actualVisible.get() > 24)
            p = 24 - (actualVisible.get());

        firstVisible.set(p);

        int first = p;
        int last = first + actualVisible.get();
        double h = btnLess.prefHeight(-1) + 6;
        hBox.resizeRelocate(0, 0, 60, h);
        
        double x = 60;
        for(int i = 0; i < timeLabel.length; i++) {
            Label l = timeLabel[i];
            if (i >= first && i < last) {
                l.setVisible(true);
                l.resizeRelocate(x, 0, iWidth, h);
                x+=iWidth;
            } else {
                l.setVisible(false);
            }
        }
        
        setHeight(h);
        setPrefHeight(h);
        waitLayout = false;
    }
    
    public final void goNext() {
        int p = firstVisible.get() + 1;
        //int maxV = 24 - actualVisible.get();
        //if (p > maxV) p = maxV;
        prefFirst = p;
        firstVisible.set(p);
        layoutChildren();
    }
    
    public final void goPrev() {
        int p = firstVisible.get();
        p--;
        if (p >= 0) {
            prefFirst = p;
            firstVisible.set(p);
            
            layoutChildren();
        }
    }

    @Override
    public String getHelperInfo(MouseEvent e) {
        return "Time Scale\nThe time scale allows adjustments to the range\n" +
                            "time visible on a graph.  The scale will attempt\n" +
                            "to show a preferred 14 hours range, if that 14 hours\n" +
                            "does not force an hour segment to get too small.\n" + 
                            "use the arrows on the upper left to adjust the 1st hour.";
    }

    @Override
    public void setExtraComments(String value) {
    }
}
