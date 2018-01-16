/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import java.util.Iterator;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class CWTableRow extends Region {
    
    private CMTable table = null;
    public final static PseudoClass ODD_PSEUDO = PseudoClass.getPseudoClass("odd");
    public final static PseudoClass SELECTED_PSEUDO = PseudoClass.getPseudoClass("selected");
    public final static PseudoClass FOCUS_PSEUDO = PseudoClass.getPseudoClass("focus");
    public final static PseudoClass EDITING_PSEUDO = PseudoClass.getPseudoClass("editing");
    public final static PseudoClass CHANGED_PSEUDO = PseudoClass.getPseudoClass("changed");
    
    public CWTableRow() {
        this(null);
    }

    public CWTableRow(CMTable table) {
        this.table = table;
        getStyleClass().add("x-table-row");
        this.setFocusTraversable(true);
        this.setOnMouseClicked( e -> {requestFocus(); });
        this.focusedProperty().addListener((e, o, n) -> {
            onFocusChange(this, n);
            
        });
    }
    
    private boolean preformingLayout = false;
    
    public final Node get(int index) {
        return ((CWTableCell)getChildren().get(index)).getNode();
    }
    
    @Override public void requestLayout() {
        if (preformingLayout)
            return;
        super.requestLayout();
    }
    
    @Override public void layoutChildren() {
        if (table == null)
            return;
        preformingLayout = true;
        
        double x = 0,
               m = 0;
        
        for(Node n: getChildren()) {
            double h = n.getLayoutBounds().getHeight();
            if (m < h)
                m = h;
        }
        double rh = table.getRowHeight();
        double hg = 1;//table.getHGap();
        double vg = 1;//table.getVGap();
        
        Iterator<Node> it = getChildren().iterator();
        for(CMTableColumn tc: table.getTableColumns()) {
            Node n = null;
            
            if ((n = it.next()) != null) {
                double w = tc.getAbsoluteWidth();
                double wx = w - (hg);
                double xp = x + hg;
                double y = (m - n.getLayoutBounds().getHeight())*0.5;
                double h = rh;
                
                if (h <= 0)
                    h = rh;
                n.resizeRelocate(xp, vg, wx, h-(vg));
                
                x+=w;
            }
        }
        
        preformingLayout = false;
    }

    void setTable(CMTable value) {
        table = value;
        this.pseudoClassStateChanged(ODD_PSEUDO, (value.getRowSize() %2) == 0);
    }

    void add(Node node) {
        if (node != null) {
            CWTableCell r = new CWTableCell(node);
            r.setDisable(true);
            getChildren().add(r);
            r.focusedProperty().addListener( (e, o, n) -> { onFocusChange(r, n); });
            node.focusedProperty().addListener( (e, o, n) -> { onFocusChange(node, n); });
            
            node.setOnKeyPressed( e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    this.requestFocus();
                    e.consume();
                }
            });
            
        }
    }

    private void disableChildren(boolean b) {
        for (Node n: getChildren()) {
            n.setDisable(b);
            
        }
    }
    
    private void onFocusChange(Node n, Boolean b) {
        this.pseudoClassStateChanged(FOCUS_PSEUDO, b);
        disableChildren(!b);
        
        if (n == this) {
            this.pseudoClassStateChanged(EDITING_PSEUDO, false);
        } else {
            this.pseudoClassStateChanged(EDITING_PSEUDO, b);
        }
        
        if (n == this && b) {
            table.setSelected(this); 
        }
    }
    
    void setSelected(boolean b) {
        this.pseudoClassStateChanged(SELECTED_PSEUDO, b);
    }
    
}
