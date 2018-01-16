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

import com.sun.javafx.stage.StageHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author John
 */
public class WidgetHelpers {
    public static final Color defaultColor = Color.rgb(180, 190, 220);
    public static final Color DK_GREY = Color.rgb(128,128,128);
    public static final Color LT_GREY = Color.rgb(224,224,224);
    
    public final static void setBackground(
        Region region,
        Color color, 
        boolean highlight
    ) {
        BackgroundFill b1 = new BackgroundFill(
            DK_GREY,
            new CornerRadii(2),
            new Insets(0)
        );
        BackgroundFill b2 = new BackgroundFill(
            LT_GREY,
            new CornerRadii(2),
            new Insets(1)
        );
        
        BackgroundFill b3 = new BackgroundFill(
            color,
            new CornerRadii(3),
            new Insets(2)
        );
        
        if (highlight) {
            BackgroundFill b0 = new BackgroundFill(
                color.BLACK,
                new CornerRadii(3),
                new Insets(-1)
            );  
            region.setBackground(new Background(b0, b1, b2, b3));
        } else {            
            region.setBackground(new Background(b1, b2, b3));
        }
    }
    
    public final static Background setSimpleBackground(
        Color color, 
        boolean selected
    ) {
        BackgroundFill b1 = new BackgroundFill(
            color.darker(),
            new CornerRadii(3),
            selected?new Insets(-1):Insets.EMPTY
        );
        BackgroundFill b2 = new BackgroundFill(
            color.brighter(),
            new CornerRadii(2),
            new Insets(1)
        );
        
        BackgroundFill b3 = new BackgroundFill(
            color,
            new CornerRadii(2),
            new Insets(2)
        );
        return (new Background(b1, b2, b3));
    }
    
    public final static Background getBackgroundFrame(
        Color color, Color border, double t, double r, double b, double l
    ) {
        BackgroundFill b1 = new BackgroundFill(
            border,
            CornerRadii.EMPTY,
            Insets.EMPTY
        );
        
        BackgroundFill b2 = new BackgroundFill(
            color,
            CornerRadii.EMPTY,
            new Insets(t, r, b, l)
        );
        return (new Background(b1, b2));
    }
    
    public final static Background getBackgroundFrame(
        Color color
    ) {
        
        BackgroundFill b = new BackgroundFill(
            color,
            CornerRadii.EMPTY,
            Insets.EMPTY
        );
        return (new Background(b));
    }
    
    public final static DropShadow dropShadow(double x, double y, double radius, double level) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(radius);
        dropShadow.setOffsetX(x);
        dropShadow.setOffsetY(y);
        dropShadow.setColor(Color.color(level, level, level)); 
        return dropShadow;
    }
    
    public final static DropShadow dropShadow() {
        return dropShadow(0, 0, 3, 0.0);
    }
    
    public final static Color generatePastel(int n, int i) {
        
        if (i == -1)
            return defaultColor;
        
        if (n <= 0) 
            n = 10;
        if (n >= 64)
            n = 64;
        i = i % n;
        
        double r = 0.2 + (0.4/(double)n*i);
        double h = 120+200/(double)((i+1)*2);
        return Color.hsb(h, r, 1.0);
    }
    
    public static Color pastel60(int value) {
        return pastels(
            ((value%60)*6),
            true
        );
    }
    
    public static Color pastel20(int value) {
        return pastels(
            ((value%20)*18),
            true
        );
    }
    
    public static Color pastels(int hue, boolean lo) {
        
        return Color.hsb(
                (double)(hue % 360),
                (lo?0.24:0.48),
                0.875);
    }

    public static void setPastel60(Region r, int code) {

        r.setBackground(
            new Background(
                new BackgroundFill(
                    pastel20(code),
                    CornerRadii.EMPTY,
                    Insets.EMPTY
                )
            )
        );

    }

    public static Image createImageForNodes(List<Node> nodes) {
        
        if (nodes == null)
            return null;
        
        Group group = new Group();
        
        SnapshotParameters sParams = new SnapshotParameters();
        
        for(Node n: nodes) {
            
            ImageView imgView = new ImageView(n.snapshot(sParams, null));
            
            Bounds b = n.localToScreen(n.getBoundsInLocal());
            
            imgView.setLayoutX(b.getMinX());
            imgView.setLayoutY(b.getMinY());
            
            group.getChildren().add(imgView);
        }
        
        sParams.setFill(Color.TRANSPARENT);
        
        return group.snapshot(sParams, null);
        
    }
    private static double offX, offY;
    public static Pane createDraggableImage(MouseEvent e, Node target, List<Node> nodes) {
        
        if (nodes == null)
            return null;
        
        Pane group = new Pane();
        
        SnapshotParameters sParams = new SnapshotParameters();
        Bounds t = target.localToScreen(target.getBoundsInLocal());
        
        double dx = e.getScreenX() - t.getMinX();
        double dy = e.getScreenY() - t.getMinY();
        
        offX = dx;
        offY = dy;
        
        System.out.println(dx + " : " + dy);
        double px = 0, py = 0;
        for(Node n: nodes) {
            
            ImageView imgView = new ImageView(n.snapshot(sParams, null));
            
            Bounds b = n.localToScreen(n.getBoundsInLocal());
            dx = b.getMinX() - t.getMinX();
            dy = b.getMinY() - t.getMinY();
            System.out.println(" -> " + dx + " : " + dy);
            if (dx < px) px = dx;
            if (dy < py) py = dy;
            imgView.setLayoutX(dx);
            imgView.setLayoutY(dy);
            
            group.getChildren().add(imgView);
        }
        offX-=px;
        offY-=py;
        
        return group;//new Pane(new ImageView(group.snapshot(sParams, null)));
        
    }

    public static void cursor(Node target,  List<Node> nodes) {
        if (target == null)
            return;
        
        Image img = createImageForNodes(nodes);
        ImageCursor ic = new ImageCursor(img);
        target.setCursor(ic);
    }
    
    private static Popup popup_cursor = null;
    private static Pane popup_pane = new Pane();
    
    public static boolean START_POPUP_CURSOR(MouseEvent e, Node target, List<Node> nodes) {
        
        if (popup_cursor == null) {
            popup_cursor = new Popup();
            popup_cursor.getScene().setRoot(popup_pane);
            popup_cursor.setAutoFix(false);
        } else {
            popup_cursor.hide();
        }
        
        if (target == null)
            return false;
        
        popup_pane.getChildren().setAll(createDraggableImage(e, target, nodes));
        
        popup_cursor.show(target, 0, 0);
        
        return true;
    }
    
    public static void MOVE_POPUP_CURSOR(MouseEvent e) {
        if (popup_cursor != null) {
            popup_cursor.setX(e.getScreenX()-offX);
            popup_cursor.setY(e.getScreenY()-offY);
            
        }
    }
    
    public static void MOVE_POPUP_RELEASE(MouseEvent e) {
        if (popup_cursor != null) {
            popup_cursor.hide();
            
        }
    }
    
    public static Node getTopClass(Node node, Class<?> targetClass) {
        if (node instanceof Parent)
            return getTopNode(node, targetClass);
        return null;
    }
    
    public static Node getTopNode(Node node, Class<?> targetClass) {
        
        if (targetClass != null) {
            if (targetClass.isAssignableFrom(node.getClass())) {
                return node;
            }
        }
        
        if (node instanceof Parent) {
            ObservableList<Node> rev = ((Parent)node).getChildrenUnmodifiable();
            for(int i = rev.size()-1;i >= 0; i--) {
                Node n = rev.get(i);
                if (n.isVisible()) {
                    Node r = getTopNode(n, targetClass);
                    if (r != null)
                        return r;
                }
                
            }
        }
        return null;
    }
    
    
     /**
     * gets a stage belonging to this application at screenX and screenY
     * 
     * @param screenX double (from mouse event)
     * @param screenY double (from mouse event)
     * @return Stage: the stage or null if none at screenX and screenY
     */
    public static Stage getStage(double screenX, double screenY) {
        
        Stage over = null;  // default is null
        
        // looping through the application stages
        
        
        for(int i = StageHelper.getStages().size()-1; i >= 0; i--) {
            Stage s = StageHelper.getStages().get(i);
            
            
            if (screenX >= s.getX() && screenX <= (s.getWidth() + s.getX()))
                if (screenY >= s.getY() && screenY <= (s.getHeight() + s.getY())) {
                    
                    
                    if (s.isFocused()) {
                        over = s;
                        break;
                    } else { 
                        over = s;
                    }
                }
        }
        
        return over;
    }
    
    public static Node getNode(Stage p, double screenX, double screenY, Class<?> targetClass) {
        return getNode(p, screenX, screenY, targetClass, false);
    }
    
    /**
     * gets the node from the scene graph of the stage whose class matches the
     * target class.
     * 
     * @param p Stage: a stage belonging to this application
     * @param screenX double
     * @param screenY double
     * @param targetClass Class: the class being targeted
     * @param bottomUp boolean
     * @return Node
     */
    public static Node getNode(Stage p, double screenX, double screenY, Class<?> targetClass, boolean bottomUp) {
        if (p != null)
            return getNode(p.getScene().getRoot(), screenX, screenY, targetClass, bottomUp);
        else
            return null;
    }
    
    public static Node getNode(Node n, double screenX, double screenY, Class<?> targetClass) {
        return getNode(n, screenX, screenY, targetClass, false);
    }
    /**
     * gets the node from the scene graph of the node(parent) whose class matches the
     * target class.
     * 
     * @param n Node: the node being examined
     * @param screenX double
     * @param screenY double
     * @param targetClass Class: the class being targeted
     * @param bottomUp boolean
     * @return Node
     */
    
    public static Node getNode(Node n, double screenX, double screenY, Class<?> targetClass, boolean bottomUp) {
        
        if (n == null)
            return null;
            
        Point2D p = n.screenToLocal(screenX, screenY);
        
        if (!n.contains(p)) return null;
        
        if (targetClass != null) {
            if (bottomUp) {
                if (targetClass.isInterface()) {
                    if (targetClass.isAssignableFrom(n.getClass())) 
                        gGetterNode = n;
                }
                    
            } else if (targetClass.isAssignableFrom(n.getClass())) 
                return n;
        }
        
        // only parents are considered
        if (n instanceof Parent) {
            if (!bottomUp) {
                for(int i = ((Parent)n).getChildrenUnmodifiable().size()-1;
                        i>=0;
                        i--) {

                    Node child = ((Parent)n).getChildrenUnmodifiable().get(i);

                    p = child.screenToLocal(screenX, screenY);
                    // some nodes can be not visible yet, contain p, only consider
                    // the visible nodes
                    if (child.isVisible() && child.contains(p)) {// && isSame) {
                        return getNode(child, screenX, screenY, targetClass, bottomUp);
                    }
                }
            } else {
                for(Node child: ((Parent)n).getChildrenUnmodifiable()) {

                    p = child.screenToLocal(screenX, screenY);
                    // some nodes can be not visible yet, contain p, only consider
                    // the visible nodes
                    if (child.isVisible() && child.contains(p)) {// && isSame) {
                        return getNode(child, screenX, screenY, targetClass, bottomUp);
                    }
                }
            }

        }
        return gGetterNode;
    }

    public static Node getNode(MouseEvent e, Class<?> targetClass) {
        return getNode(e, targetClass, false);
    }
    private static Node gGetterNode = null;
    /**
     * gets a node under the mouse whose class matches the target class.
     * 
     * @param e MouseEvent
     * @param targetClass Class
     * @param bottomUp boolean
     * @return Node
     */
    public static Node getNode(MouseEvent e, Class<?> targetClass, boolean bottomUp) {
        gGetterNode = null;
        Stage stage = getStage(e.getScreenX(), e.getScreenY());
        
        if (stage != null) {
            return getNode(stage, e.getScreenX(), e.getScreenY(), targetClass, bottomUp);
        }
        return null;
    }
    
    /** member variables */
    
    public static Window primaryStage = null;
    
    private static double 
        offsetX,    /** the offset of the mouse on a node when mouse pressed */ 
        offsetY;    /** the offset of the mouse on a node when mouse pressed */ 
    
    //private static Node 
    //    nodeSource = null;  /** the node being acted upon */

    //private static Popup dragVisual = null;
    //private static Pane dragContent = new Pane();
    
    /**
     * gets the X offset of the mouse on the node.
     * 
     * @return double
     */
    public final static double getOffsetX() {
        return offsetX;
    }
    
    /**
     * gets the Y offset of the mouse on the node.
     * 
     * @return double
     */
    public final static double getOffsetY() {
        return  offsetY;
    }
    
    /**
     * initializes a drag of the node specified.
     * 
     * @param source Node
     * @param e MouseEvent
     * 
     * @return boolean
     */
    public static boolean dragBegin(Node source, MouseEvent e) {
        
        if (source == null || e == null)
            return false;
        
       // nodeSource = source;
        
        Bounds b = source.localToScreen(source.getBoundsInLocal());
        
        offsetX = e.getScreenX() - b.getMinX();
        offsetY = e.getScreenY() - b.getMinY();
        
        return true;
    }
    
    /**
     * helper method to initialize the dragging of a Tab (prefered DockTab).
     * 
     * @param tabThumb Node: the tab
     * @param tabContent the content of the tab
     * @param e MouseEvent
     * @throws java.lang.Exception if getSnapshot() returned an error
     */
    
    public static void dragBeginForTab(Node tabThumb, Node tabContent, MouseEvent e) throws Exception {
        
        if (tabContent == null || tabThumb == null || e == null)
            return;
        
        if (dragBegin(tabThumb, e)) {
            Image thumb = getSnapshot(tabThumb, 1.0, false);       // get thumb image
            Image content = getSnapshot(tabContent, 0.6, true);   // get and scale content image
            ImageView iThumb = new ImageView(thumb);
            ImageView iContent = new ImageView(content);
            
            System.out.println("tabContent = " + tabContent.getBoundsInLocal());
            
            // make semi-transparent
            iContent.setOpacity(0.8);                   
            iThumb.setOpacity(0.8);
            
            double ht = thumb.getHeight();
            // a group for the 2 images
            Group group = new Group(iThumb, iContent);
            // place scaled content image below the thumb image
            iContent.relocate(0, ht);
            
            SnapshotParameters snapshotParams = new SnapshotParameters();
            snapshotParams.setFill(Color.TRANSPARENT);
            
            Image imgGroup = group.snapshot(snapshotParams, null);
            ImageView iGroup = new ImageView(imgGroup);
            ImageCursor ic = new ImageCursor(iGroup.getImage());
            // set the cursor
            tabThumb.setCursor(ic);
        }
    }
    
    /**
     * gets a snapshot image of a node
     * 
     * @param node Node
     * @param size double: the desired scale
     * @param setRect boolean: adjust rectangle if part of node is empty
     * @return Image
     * 
     * @throws java.lang.Exception if the node is null
     */
    public static Image getSnapshot(Node node, double size, boolean setRect) throws Exception {
        if (node == null)
            throw new Exception("DragAssistant:getStapshot (node can not be null)");
        if (size <= 0.25) 
            size = 0.25;
        else if (size > 1.0) 
            size = 1.0;
        
        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setTransform(Transform.scale(size, size));
        if (setRect) {
            Bounds b = node.getLayoutBounds();
            snapshotParams.setViewport(new Rectangle2D(0, 0, b.getWidth()*size, b.getHeight()*size));
        }
        Image wImage = (Image)node.snapshot(snapshotParams, null);
        
        return wImage;
    }
    
    /**
     * test a drop of a DockTab and changes its associated DockPane if 
     * the context applies.
     * 
     * @param e MouseEvent
     * @param tab {@link DockTab}
     */
    public static void dropForDockPane(MouseEvent e, DockTab tab) {
        
        Stage overStage = getStage(e.getScreenX(), e.getScreenY());
        
        if (overStage != null) {
            // maybe the drop was to reoreder a tab, not to change parents
            if (tab.dropZone(e))
                return;
        }
        
        if (overStage == DockWindow.getInstance().getStage()) {
            // on the DockWindow
            DockWindow.getInstance().drop(e, tab);
            tab.dropZone(e);
        } else if (overStage != null && tab.getOwner() == DockWindow.getInstance().getTabPane()) {
            tab.backToOwner();
            tab.dropZone(e);
        } else {
            if (!DockWindow.getInstance().getTabPane().getTabs().contains(tab)) {
                if (overStage == DockWindow.getInstance().getStage() 
                   || overStage == null) {
                    DockWindow.getInstance().drop(e, tab);    
                }
            } else {
                tab.backToOwner();
            }
        }
        
    }
    
    public static Image getImageFromResource(String resourceName) {
        if (!resourceName.contains(".")) resourceName += ".png";
            InputStream i = WidgetHelpers.class.getResourceAsStream("/resources/" + resourceName);
        if (i != null) 
            return new Image(i);
        
        else
            return null;
    }
    
    public static ImageView getImageViewFromResource(String resourceName) {
        return new ImageView(getImageFromResource(resourceName));
    }
    
    public static File showOpenDialog(Window window, String title, String... filters) 
        throws Exception {
        
        FileChooser fileChooser = new FileChooser();   

        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.dir"))
        );    

        for(String s: filters) {
            String[] p = s.split(":");
            if (p.length == 2) {
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                        p[0], p[1])
                    );
            }
        }

        File file = fileChooser.showOpenDialog(window);
        
        if (file != null) {
            return file;
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Invalid file");
            a.setHeaderText("No file was selected");
            a.setContentText("Corrections needed...");
            a.initOwner(window);
            a.showAndWait();
            throw new FileNotFoundException();
        }
    }

    private static Object getScene() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
