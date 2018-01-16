/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;

/**
 *
 * @author John
 */
public class RibbonButton extends Ribbon {
    
    private ContextMenu menu;
    public RibbonButton() {
        this("");
        
    }
    
    public RibbonButton(String title) {
        this(title, null);
    }
    
    public RibbonButton(String title, ContextMenu menu) {
        super(title);
        isSelectable = false;
        /*label.*/getStyleClass().clear();
        /*label.*/getStyleClass().add("ribbon-button");
        setMenu(menu);
    }
    
    public final void setMenu(ContextMenu source) {
        menu = source;
    }
    
    @Override public void onSelect() {
        if (menu != null && /*label.*/getScene() != null) {
           menu.show(this, Side.BOTTOM, 0, 0);
        }
    }

}
