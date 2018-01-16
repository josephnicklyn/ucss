/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import ucss.controllers.GraphController;

/**
 *
 * @author John
 */
public class HelpRibbonButton extends RibbonButton {
    
    
    
    public HelpRibbonButton() {
        super("Help");
        getStyleClass().add("help-ribbon-button");
        this.setOnMouseDragged(ON_MOUSE_DRAGGED);
        this.setOnMousePressed(ON_MOUSE_PRESSED);
        this.setOnMouseReleased(ON_MOUSE_RELEASED);
        Tooltip t = new Tooltip("Drag the mouse to see the help for an item.");
        Tooltip.install(this, t);
        
    }
    
    private final EventHandler<MouseEvent> ON_MOUSE_DRAGGED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            Node n = WidgetHelpers.getNode(e, HelperInterface.class, true );
        
            if (n != null) {
                setCursor(getImageCursorHigh());
            } else {
                setCursor(getImageCursor());
            }
        }
    };
    
    private final EventHandler<MouseEvent> ON_MOUSE_PRESSED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            setCursor(getImageCursor());
             
        }

    };
    
    private final EventHandler<MouseEvent> ON_MOUSE_RELEASED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            getHelperAt(e);
            setCursor(Cursor.DEFAULT);
        }

    };

    private void getHelperAt(MouseEvent e) {
        Node n = WidgetHelpers.getNode(e, HelperInterface.class, true );
        
        if (n != null) {
            HelperInterface hi = (HelperInterface)n;
            String xr = hi.getHelperInfo(e);
            ShowHelp.getInstance().setMessage(this, e, xr);
            
        }
    }
    
    private static ImageCursor imageCursor; 
    private Cursor getImageCursor() {
        if (imageCursor == null) {
            imageCursor = new ImageCursor(
                new Image(HelpRibbonButton.class.getResourceAsStream("/resources/help-none.png")),
                3,
                30
            );
            
        }
        return imageCursor;
    }
    
    private static ImageCursor imageCursorHi; 
    private Cursor getImageCursorHigh() {
        if (imageCursorHi == null) {
            imageCursorHi = new ImageCursor(
                new Image(HelpRibbonButton.class.getResourceAsStream("/resources/help-go.png")),
                3,
                30
            );
        }
        return imageCursorHi;
    }
    
}
