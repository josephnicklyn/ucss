/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import java.util.ArrayList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author John
 */
public class UtilityWindow {
    private static ArrayList<UtilityWindow> activeList = new ArrayList<>();
    private SimpleBooleanProperty isFloating = new SimpleBooleanProperty(false);
    private static PseudoClass FLOATING_PSEUDO_CLASS = PseudoClass.getPseudoClass("floating");
    
    private double initWidth = 120, initHeight = 200;
    
    private Popup popup = null;
    
    private final UtilityRoot root = new UtilityRoot();
    
    public UtilityWindow() {
        this("UCSS", null);
    }
    
    public UtilityWindow(String title) {
        this(title, null);
    }
    
    public UtilityWindow(String title, Node content) {
        activeList.add(UtilityWindow.this);
        isFloating.addListener( (e, o, n) -> {
            root.pseudoClassStateChanged(FLOATING_PSEUDO_CLASS, n);
        });
        setContent(content);
        setTitle(title);
    }
    
    public void destroy() {
        activeList.remove(UtilityWindow.this);

    }
    
    public final void setContent(Node node) {
        root.setContent(node);
    }
    
    public final void setTitle(String title) {
        root.setTitle(title);
    }
    
    private void reset() {
        popup.setAutoHide(true);
        isFloating.set(false);
        popup.hide();
    }
    
    public final void setPrefSize(double w, double h) {
        root.setNewSize(w, h, true);
    }
    
    
    EventHandler<javafx.scene.input.MouseEvent> mousePressedHandler = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 

        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            if (e.getY() < 23) {
                Window w = DockWindow.getInstance().getPrimaryWindow();
                if (w != null)
                    ((Stage)w).toFront();
            }
        } 
    };
    
    public void show(Node owner) {
        Bounds b = owner.localToScreen(owner.getBoundsInLocal());
      
        double tLeft    = b.getMinX(),
               tTop     = b.getMaxY();
        
        if (popup == null) {
            popup = new Popup();
            popup.getScene().setRoot(root);
            popup.setAutoHide(true);
            popup.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        }
        
        if (isFloating.get())
            return;
        
        if (popup.isShowing()) {
            popup.hide();
            return;
        }
        
        
        root.setNewSize(initWidth, initHeight, true);
        
        popup.show(owner, tLeft, tTop);        
    }

    private boolean grabbed = false;
    
    public static Node getUtilityWindow(double x, double y) {
        for(UtilityWindow w:activeList) {
            if (w.popup != null)
                if (w.popup.getScene().getRoot() != null) {
                    if (w.popup.getScene().getRoot().localToScreen(w.popup.getScene().getRoot().getBoundsInLocal()).contains(x, y)) {
                        return w.popup.getScene().getRoot();
                    }
                }
        }
        return null;
    }
    
    class UtilityRoot extends Pane {
        private final Rectangle clipper = new Rectangle(0, 0);
        private final StackPane container = new StackPane();
        private final Text title = new Text("Floating Window");
        private final Region closeButton = new Region();
        private final Region gripper = new Region();
        UtilityRoot() {
            listeners();
            closeButton.getStyleClass().add("wnd-shapes-close-button");
            gripper.getStyleClass().add("wnd-resize-grip");
            title.getStyleClass().add("wnd-title");
            getStyleClass().add("utility-window");
            getChildren().addAll(closeButton, gripper, title, container);
            container.setClip(clipper);
        
        }
        
        private boolean preformingLayout = false;
        
        @Override public void requestLayout() {
            if (preformingLayout) 
                return;
            super.requestLayout();
        }
        
        @Override public void layoutChildren() {
            preformingLayout = true;
            
            double 
                width   = getWidth(),
                height  = getHeight(),
                left    = getPadding().getLeft(),
                top     = getPadding().getTop(),
                right   = getPadding().getRight(),
                bottom  = getPadding().getBottom();
            
            double ncRight = width - 6,
                   ncTop   = 6,
                   ncLeft  = 6;
            
            double y1 = ncTop;
            double x2 = ncRight;
            
            double cbWidth  = closeButton.prefWidth(-1),
                   cbHeight = closeButton.prefHeight(-1);
            
            x2-=cbWidth;
            closeButton.resizeRelocate(6, 6, cbWidth, cbHeight);
            title.relocate(22, 4);
            
            gripper.resizeRelocate(width - 11, height - 11, 9 , 9);
            clipper.setWidth(width-12);
            clipper.setHeight(height-30);
            container.resizeRelocate(ncLeft, 24, width-12, height - 30);
            
            preformingLayout = false;
        }
    
        void setTitle(String title) {
            this.title.setText(title);
        }
        private double grabX, grabY;
        private boolean moveIntialized = false;
        private boolean forResize = false;
        private Bounds screenBounds;
        private boolean fromFalse = false;
        private Bounds getScreenBounds() {
            if (screenBounds == null) {
                screenBounds = localToScreen(getBoundsInLocal());
            }
            return screenBounds;
        }
        boolean downOnMoveable = false;
        private void listeners() {
            
            closeButton.setOnMouseClicked( e -> {
                reset();
                
            });
            
            gripper.setOnMousePressed( e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    grabX = 9-e.getX();
                    grabY = 9-e.getY();
                }
                e.consume();
            });
            
            gripper.setOnMouseDragged( e -> {
                if (e.isPrimaryButtonDown()) {
                    
                    double newW = (e.getScreenX() + grabX) - getScreenBounds().getMinX(),
                           newH = (e.getScreenY() + grabY) - getScreenBounds().getMinY();
                    
                    setNewSize(newW, newH, true);
                }
                e.consume();
            });
            
            gripper.setOnMouseReleased( e -> {
                screenBounds = null;
                e.consume();
            });
            
            setOnMousePressed( e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getY() < 24) {
                    if (e.getClickCount() == 2 && isFloating.get()) {
                        if (!fromFalse)
                            setNewSize(initWidth, initHeight, true);
                        else {
                            setNewSize(initWidth, 22, false);
                        }
                    }
                    grabX = e.getX();
                    grabY = e.getY();
                    downOnMoveable = true;
                } 
            });
            
            setOnMouseDragged( e -> {
                if (e.isPrimaryButtonDown() && downOnMoveable) {
                    if (!moveIntialized) {
                        double threshold = 
                            Math.max(
                                Math.abs(e.getX() - grabX),
                                Math.abs(e.getY() - grabY)
                            );
                        
                        if (threshold > 8) {
                            moveIntialized = true;
                        }
                    } else {
                        double newX = e.getScreenX() - grabX,
                               newY = e.getScreenY() - grabY;
                        
                        popup.setX(newX);
                        popup.setY(newY);
                        
                    }
                }
            });
            
            setOnMouseReleased(e -> {
                if (moveIntialized && downOnMoveable) {
                    isFloating.set(true);
                    popup.setAutoHide(false);
                }
                moveIntialized = false;
                downOnMoveable = false;
            });
        }

        private void setNewSize(double newW, double newH, boolean updateInits) {
            if (newW < 120) newW = 120;
            if (newW > 720) newW = 720;
            if (newH < 24) newH = 24;
            if (newH > 620) newH = 620;
            
            root.setMinWidth(newW);
            root.setMinHeight(newH);
            root.setPrefWidth(newW);
            root.setPrefHeight(newH);
            root.setMaxWidth(newW);
            root.setMaxHeight(newH);
            if (updateInits) {
                initWidth = newW;
                initHeight = newH;
            }
            fromFalse = updateInits;
        }

        private void setContent(Node node) {
            container.getChildren().clear();
            if (node != null) {
                container.getChildren().add(node);
            }
        }
        
        
        
    }
    
}