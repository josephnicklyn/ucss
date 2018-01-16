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

import javafx.css.PseudoClass;
import javafx.scene.layout.Region;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * An EventItem.
 * 
 * @author Joseph Nicklyn
 */
public class EventItem extends Region implements HelperInterface {
    
    private static final PseudoClass muted = PseudoClass.getPseudoClass("muted");
    private static final PseudoClass selected = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass focused = PseudoClass.getPseudoClass("focused");
    private static final PseudoClass deleteFlagged = PseudoClass.getPseudoClass("fordelete");
    
    private int prefSize = 0;
    private int prefPos = 0;
    
    private final Text primaryText = new Text();
    private final Text secondaryText = new Text();
    private final Separator br = new Separator();
    
    private HPane hPane;
    
    public EventItem() {
        init("", "");
    }
    public EventItem(String text) {
        init(text, "");
    }

    public EventItem(String text, int s, int p, int color) {
        setPrefSize(s);
        setPrefPosition(p);
        init(text, "");
        this.setBackgroundColor(color);
    }
    
    public EventItem(String primaryText, String secondaryText, int s, int p) {
        setPrefSize(s);
        setPrefPosition(p);
        init(primaryText, secondaryText);
    }
    
    private int referenceID = 0;
    public final int getReferenceID() {
        return referenceID;
    }
    
    public final void setReferenceID(int value) {
        referenceID = value;
    }
    
    private void init(String pText, String sText) {
        getChildren().addAll(primaryText, br, secondaryText);
        getStyleClass().add("event-item");
        
        primaryText.getStyleClass().add("primary-text");
        secondaryText.getStyleClass().add("secondary-text");
        
        setPrimaryText(pText);
        setSecondaryText(sText);
        setShowSecondaryText(false);
        
    }

    public final void setPrimaryText(String value) {
        primaryText.setText(value);
    }
    
    public final String getPrimaryText() {
        return primaryText.getText();
    }
    
    public final String getSecondaryText() {
        return secondaryText.getText();
    }
    
    
    public final void setSecondaryText(String value) {
        secondaryText.setText(value);
    }
    
    public final void setShowSecondaryText(boolean value) {
        if (secondaryText.getText().isEmpty())
            value = false;
        secondaryText.setManaged(value);
        secondaryText.setVisible(value);
        br.setManaged(value);
        br.setVisible(value);
        requestLayout();
    }
    
    public final boolean getShowSecondaryText() {
        return secondaryText.isVisible();
    }
    
    public final void toggleShowSecondaryText() {
        setShowSecondaryText(!secondaryText.isVisible());
    }
    
    public final int getPrefSize() {
        return prefSize;
    }
    
    public final void setPrefSize(int value) {
        prefSize = value;
        requestLayout();
    }
    
    public final int getPrefPosition() {
        return prefPos;
    }
    
    public final void setPrefPosition(int value) {
        prefPos = value;
        requestLayout();
    }
    
    public final double prefSize(double v) {
        return prefSize * v;
    }
    
    public final double prefPos(double v) {
        return prefPos * v;
    }
    
    public final void setSize(double v) {
        setWidth(prefSize(v));
    }
    
    private boolean waitLayout = false;
    
    @Override public void requestLayout() {
        if (waitLayout)
            return;
        super.requestLayout();
    }

    @Override public void layoutChildren() {
        waitLayout = true;
        
        double innerWidth = getWidth() - (getPadding().getLeft() + getPadding().getRight());
        
        primaryText.setWrappingWidth(innerWidth);
        secondaryText.setWrappingWidth(innerWidth);
        
        double y = getPadding().getTop();
        double x = getPadding().getLeft();
        double h = primaryText.prefHeight(-1);
        
        primaryText.resizeRelocate(x, y, innerWidth, h);
        
        if (secondaryText.isVisible()) {
            double h2 = br.prefHeight(-1);
            y+= h + 4;
            br.resizeRelocate(x, y, innerWidth, h2);
            y+=h2 + 4;
            h2 = secondaryText.prefHeight(-1);
            secondaryText.resizeRelocate(x, y, innerWidth, h2);
            y+=h2;
        } else {
            y += h;
        }
        y+=getPadding().getBottom();
        setHeight(y);
        setMinHeight(y);
        setPrefHeight(y);
        setMaxHeight(y);
        this.setNeedsLayout(true);
        
        
        waitLayout = false;
    }
    
    final void setHPane(HPane p) {
        hPane = p;
    }
    
    public final HPane getHPane() {
        return hPane;
    }
    
    @Override public String toString() {
        return primaryText.getText();
    }

    public final void setBackgroundColor(int i) {
        WidgetHelpers.setBackground(this, WidgetHelpers.pastel60(i), false);
    }

    public final void setDeleteFlag(boolean value) {
        pseudoClassStateChanged(deleteFlagged, value);
    }
    
    public final boolean setMuted(boolean value) {
        pseudoClassStateChanged(muted, value);
        return value;
    }
    
    public final void setSelected(boolean value) {
        pseudoClassStateChanged(selected, value);
        pseudoClassStateChanged(focused, false);
    }
    
    public final void setFocus(boolean value) {
        pseudoClassStateChanged(selected, false);
        pseudoClassStateChanged(focused, value);
    }

    @Override
    public String getHelperInfo(MouseEvent e) {
        String r = "EventItem\nThe EventItem is used to \n" +
               "display reoccuring events.";
        
        return r;
    }

    @Override
    public void setExtraComments(String value) {
        
    }
}
