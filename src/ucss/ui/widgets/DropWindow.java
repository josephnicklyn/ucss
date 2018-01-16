/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

/**
 *
 * @author John
 */

import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

/**
 *
 * @author John
 */
public class DropWindow extends Button {
    
    private final double preferedWidth;
    private final Pane container;
    
    public DropWindow(String text, Pane pane, double prefWidth) {
        
        container = pane;
        
        preferedWidth = prefWidth;
        setText(text);
        
        container.setStyle(
            "-fx-background-color: -fx-outer-border, white;" +
            "-fx-background-insets: 0 , 1;" +
            "-fx-background-radius: 2px;"   +
            "-fx-padding:2px;"
        );
        
        this.setOnAction( e -> {
            if (wnd != null) {
                if (wnd.isShowing())
                    wnd.hide();
                else
                    doPopup();
            } else 
                doPopup();
        });
        
    }
    
    public DropWindow(String text, Pane pane) {
        this(text, pane, -1);
    }
    
    public final Pane getContent() {
        return container;
    }
    
    private Popup wnd = null;
    
    public void doPopup() {
        if (wnd == null) {
            wnd = new Popup();
            wnd.getScene().setRoot(container);
            wnd.setAutoHide(true);
        } 
        System.out.println("SHOW???");
        show();
    }
    
    private final void show() {
        if (wnd == null) return;
        activate();
        Bounds b = this.localToScreen(this.getBoundsInLocal());
        double nx = b.getMinX();
        double ny = b.getMaxY();
        double nw = (preferedWidth < 0)?b.getWidth():preferedWidth;
        container.setPrefWidth(nw);
        container.setMaxWidth(nw);
        wnd.show(this, nx, ny);
        
    }
    
    public void activate() {}
    
}