package ucss.ui.widgets;

import com.sun.javafx.collections.TrackableObservableList;
import java.util.Set;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * <p>
 * A dock pane is an extension of the TabPane, which allows its children to be
 * pulled off and placed on to a 2nd stage window.  This control can contain
 * regular Tabs which are not dockable or {@link DockTab} which are dockable.
 * 
 * The DockPane currently needs to be oriented on the top. for re-ordering.
 * </p>
 * 
 * <p>
 * DockTabs are pulled off when the mouse is pressed and then dragged by a pre-
 * defined threshold (default 8 pixels), whence the drag begins.  The DockTab
 * can now be re-ordered if the drop occurs in line with the tab-bar. Or placed
 * onto a/the {@link DockWindow} if dropped off the current window (if the drop
 * occurs on the same window but not on the tab-bar the action is ignored).  
 * Additionally if the DockWindow is currently showing, the drop order can be
 * set at the same time by dropping the dragged tab on the targets tab-bar.
 * 
 * @author Joseph Nicklyn JR.
 */
public class DockPane extends TabPane {
    /** constructor */
    
    /** 
     * an empty constructor, no initialization is needed.
     */
    public DockPane() {  }
    
    /**
     * a constructor defining one or more tabs
     * 
     * @param items DockTab array
     */
    public DockPane(DockTab... items) {
        getDockTabs().addAll(items);
    }
    
    /**
     * gets the list containing DockTabs.
     * @return ObservableList:DockTab
     */
    public final ObservableList<DockTab> getDockTabs() { return dockTabs; }
    //public final List<DockTab> ownedTabs = new ArrayList<>();
    /**
     * Maintains the list of DockTabs.
     */
    private final ObservableList<DockTab> dockTabs = new TrackableObservableList<DockTab>() {
        @Override
        protected void onChanged(ListChangeListener.Change<DockTab> p) {
             while(p.next()) {
                // remove from the list
                for (DockTab tab: p.getRemoved()) {
                    if (dockTabs != null && !dockTabs.contains(tab)) {
                        getTabs().remove(tab);
                        
                    }
                }
                
                // adds to the list
                for (DockTab tab: p.getAddedSubList()) {
                    if (tab != null) {
                        // initialize TabPane listeners on 1st call 
                        if (!initialized)
                            initListeners();
                        // set the initial owner
                        tab.setOwner(DockPane.this); 
                        getTabs().add(tab);
                    }
                }
            }
        }
    };
    
    /** member variables concerned with dragging */
    private boolean initialized = false;    /** TabPane listeners */
    private boolean dragged = false;        /** current action is drag */
    private boolean canDrag = false;
    private double dragX, dragY;            /** the x/y screen pos on pressed */
    
    /**
     * initializes the listeners for this TabPane.
     */
    private void initListeners() {
        
        initialized = true;
        
        /**
         * sets the x/y offset on the press, and reset dragged
         */
        setOnMousePressed( e -> {
            
            canDrag = false;
            
            if (e.getButton() == MouseButton.PRIMARY) {
                
                Node n = getTabNode(getSelectionModel().getSelectedIndex());
                
                if (n != null) {
                    canDrag = n.localToScreen(n.getBoundsInLocal()).contains(e.getScreenX(), e.getScreenY());
                } 
                
                dragX = e.getScreenX();
                dragY = e.getScreenY();
                dragged = false;
            }
        });
        
        /**
         * responds to the mouse being dragged
         */
        this.setOnMouseDragged( e -> {
            // only on primary button
            if (e.getButton() == MouseButton.PRIMARY && canDrag) {
                
                // calculate the amount of change from when the mouse was pressed
                double threshhold = Math.max(
                    Math.abs(e.getScreenX() - dragX),
                    Math.abs(e.getScreenY() - dragY)
                );
                
                // begin dragging only if the threshold is met
                if (threshhold > 8) 
                {
                    if (!dragged) {
                        Tab cTab = getSelectionModel().getSelectedItem();
                        // only DockTabs... thank you
                        if (dockTabs.contains(cTab)) {
                            if (cTab.getContent() != null) {
                                Node n = getTabNode(getTabs().indexOf(cTab));
                                if (n != null)
                                    try {
                                        WidgetHelpers.dragBeginForTab(n, cTab.getContent(), e);
                                        dragged = true;
                                    } catch (Exception ex) {
                                        // silent
                                    }
                            }
                        }
                    }
                }
            }
        });
        
        /**
         * respond to a "drop"
         */
        setOnMouseReleased( e -> {
            if (dragged && canDrag) {
                Tab cTab = getSelectionModel().getSelectedItem();
                // only DockTabs... thank you
                if (dockTabs.contains(cTab)) {
                    Node n = getTabNode(getTabs().indexOf(cTab));
                    if (n != null)
                        n.setCursor(Cursor.DEFAULT);
                    // delegate the rest to the WidgetHelpers
                    WidgetHelpers.dropForDockPane(e, (DockTab)cTab);
                }
            }
            dragged = false;
            canDrag = false;
        });
    }

    /**
     * gets the Tab @index.
     * 
     * @param index Integer
     * @return Node
     */
    private Node getTabNode(int index) {
        Node node = null;
        Set<Node> nodes = lookupAll(".tab");
        if (index >= 0 && index < nodes.size()) {
            for(Node n: nodes) {
                if (index-- == 0) {
                    node = n;
                    break;
                }
            }
        }
        return node;
    }

    /**
     * Reorders the tabs according to drop location (screenX and screenY).
     * 
     * @param e MouseEvent
     * @param target Tab
     * @return boolean - if MouseEvent occurs over a tab of this TabPane
     *  or past then maxX of the last tab
     */
    boolean reorderTabs(MouseEvent e, Tab target) {

        double x = e.getScreenX();
        double y = e.getScreenY();

        int at = -1;

        for(int i = 0; i < getTabs().size(); i++) {
            Node n = getTabNode(i);
            Bounds b = n.localToScreen(n.getBoundsInLocal());
            if (i == (getTabs().size()-1) && x > b.getMinX() && y < b.getMaxY() && y > b.getMinY()) 
                    at = i;
            else if (b.contains(x, y)) {
                at = (i);
                break;
            }
        }        
        if (at != -1) {
            if (getTabs().indexOf(target) != at) {
                getTabs().remove(target);
                getTabs().add(at, target);
                this.getSelectionModel().select(target);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a tab by its title.
     * @param name String
     * @return DockTab
     */
    public final DockTab getTabByTitle(String name) {
        for(DockTab t: dockTabs) {
            if (t.getText().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return (DockTab) DockWindow.getInstance().selectTab(name);
    }
    
    /**
     * Gets a tab by its Id.
     * @param id String
     * @return DockTab
     */
    public final DockTab getTabByID(String id) {
        for(DockTab t: dockTabs) {
            if (t.getId() != null && t.getId().equalsIgnoreCase(id)) {
                return t;
            }
        }
        return null;
    }
    
    public void selectTab(DockTab target) {
        if (getTabs().contains(target)) {
            if (this.getSelectionModel().getSelectedItem() != target)
                getSelectionModel().select(target);
        } else {
            DockWindow.getInstance().toTopIfContains(target);
        }
    }
    
    public boolean selectTab(String title) {
        
        for(DockTab d: getDockTabs()) {
           if (d.getText().equalsIgnoreCase(title)) {
               getSelectionModel().select(d);
               return true;
           }
        }
        return DockWindow.getInstance().selectTab(title) != null;
    }
    
}
