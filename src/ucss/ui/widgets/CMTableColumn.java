/*
 * This module is apart of the UCSS-Course Management System
 * 
 * Copyright (C) 2017  Joseph Nicklyn
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ucss.ui.widgets;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * This file provides a column label (which can be rotated) and defines the
 * width of a column for a table.
 * 
 * @author Joseph Nicklyn JR
 */
public class CMTableColumn extends Region {
   
    private final Group group = new Group();
    private final Text header = new Text();
    private double prefWidth = 100;
    private CMTable table = null;
    private Pos alignment = Pos.CENTER;
    private double mWidth = -1;
   
    private final boolean rotateHeaderLabel;
    
    public CMTableColumn() {
        this("", 100, Pos.CENTER, false, -1);
    }
    
    public CMTableColumn(double width) {
        this("Column", width);
    }
    
    public CMTableColumn(String text) {
        this(text, 100, Pos.CENTER, false, -1);
    }

    public CMTableColumn(String text, double width) {
        this(text, width, Pos.CENTER, false, -1);
    }
    
    public CMTableColumn(String text, double width, double minimumWidth) {
        this(text, width, Pos.CENTER, false, minimumWidth);
    }
    
    public CMTableColumn(String text, double width, boolean rotated) {
        this(text, width, Pos.CENTER, rotated, -1);
    }
    
    public CMTableColumn(String text, double width, Pos alignment) {
        this(text, width, alignment, false, -1);
    }
    
    public CMTableColumn(String text, double width, Pos alignment, double minimumWidth) {
        this(text, width, alignment, false, minimumWidth);
    }
    
    public CMTableColumn(String text, double width, Pos alignment, boolean rotated) {
        this(text, width, alignment, rotated, -1);
    }
    
    public CMTableColumn(String text, double width, Pos alignment, boolean rotated, double minimumWidth) {
        setMinWidth(minimumWidth);
        rotateHeaderLabel = rotated;
        setAlignment(alignment);
        getStyleClass().setAll("x-table-header");
        header.getStyleClass().setAll("x-table-header-label");
        
        setPWidth(width);
        setText(text);
        getChildren().add(group);
        group.getChildren().add(header);
        if (rotated)
            header.setRotate(-90);
    }
   
    public final Pos getAlignment() {
        return alignment;
    }
    
    public final void setAlignment(Pos pos) {
        alignment = pos;
    }
    
    public final void setText(String value) {
        header.setText(value);
    }
    
    public final String getText() {
        return header.getText();
    }
    
    final void setTable(CMTable value) {
        table = value;
    }
    
    final CMTable getTable() {
        return table;
    }
    
    public final void setPWidth(double value) {
        prefWidth = value;
    }
    
    public final double getPWidth() {
        if (getMinWidth() > 0 && getWidth() < getMinWidth())
            return getMinWidth();
        else
            return prefWidth;
    }

    public final double getTextWidth() {
        return header.prefWidth(-1) + 20;
    }

    public final double getTextHeight() {
        return header.prefHeight(-1) + 20;
    }

    
    public final double getAbsoluteWidth() {
        if (getMinWidth() > 0 && getWidth() < getMinWidth())
            return getMinWidth();
        if (prefWidth >= 0 && prefWidth <= 1) {
            return getWidth();//table.getLayoutBounds().getWidth();//*getWidth();
        } else 
            return prefWidth;
    }
    
    public final boolean isRotated() {
        return rotateHeaderLabel;
    }
    
    private boolean preformingLayout = false;
   
    
    @Override public String toString() {
        return getText() + ", " + getAbsoluteWidth();
    }
    
    @Override public void layoutChildren() {
        preformingLayout = true;
        double h = header.prefHeight(-1);
        double y = getHeight() - ((rotateHeaderLabel)?header.getLayoutBounds().getWidth():header.getLayoutBounds().getHeight());
        double x = getWidth() - ((!rotateHeaderLabel)?header.getLayoutBounds().getWidth():header.getLayoutBounds().getHeight());
        
        y*=0.5;
        x*=0.5;
    
        //if (rotateHeaderLabel)
        //    y-=(h*0.5);

        group.relocate(x, y);
        
        preformingLayout = false;
    }
    
}
