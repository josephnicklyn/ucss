/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class CWTableCell extends Region {
    private double px;
    private double py;
    private boolean fillWidth = false;
    
    public CWTableCell(Node node) {
        
        fillWidth = (node instanceof TextField || node instanceof Label);
        
        getStyleClass().add("x-table-cell");
        this.setFocused(true);
        getChildren().add(node);
        setOnMousePressed(e -> {requestFocus(); });
        //node.setOnMousePressed(e -> {setChanged(true);});
        
    }
    
    public final Node getNode() {
        return getChildren().get(0);
    }
    private boolean preformingLayout = false;
    
    @Override public void requestLayout() {
        if (preformingLayout) 
            return;
        super.requestLayout();
    }
    
    @Override public void layoutChildren() {
        
        preformingLayout = true;
        
        double left = getPadding().getLeft(),
               right = getPadding().getRight(),
               top = getPadding().getTop(),
               bottom = getPadding().getBottom();
        
        
        double clientWidth = getWidth() - (left + right),
               clientHeight = getHeight() - (top + bottom);
        
        double cy = Math.floor(clientHeight*0.5);
        double cx = clientWidth*0.5;
        
        
        for(Node n: getChildren()) {
            
            if (py == 0 || px == 0) {
                py = n.prefHeight(-1);
                px = n.prefWidth(-1);
            }
            double x = left, y = top, w = 0, h = 0;
            
            if (fillWidth) {
                w = clientWidth;
                h = clientHeight;
            } else {
                w = px;
                h = py;
                y = top + cy - Math.floor(py * 0.5) - 1;
                x = left + cx - px * 0.5;
            } 
            
            n.resizeRelocate(x, y, w, h);
                
        }
        
        preformingLayout = false;
    }
    
    public void setChanged(boolean value) {
        this.pseudoClassStateChanged(CWTableRow.CHANGED_PSEUDO, value);
    }
    
}
