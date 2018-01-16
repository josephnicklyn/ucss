
package ucss.ui.widgets;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

/**
 * <p>
 * A DockTab is an extended Tab which provides additional functionality to a
 * tab to assist the {@link DockPane}.
 * </p>
 * 
 * <p>
 * A DockTab maintains its original owner (DockPane). It provides listeners
 * for closing, which places the DockTab back onto its owner when closed 
 * while on the secondary stage. It also updates the closeable property of the
 * tab based on its current location according to its {@link DockTabCloseable}
 * attribute.
 * 
 * @author Joseph Nicklyn JR.
 */
public class DockTab extends Tab {
    
    /** member variables */
    
    protected DockPane owner;   /** the original owner (this should be changed 
                                *   carefully or not at all) 
                                */
    /** the current parent for the DockTab */
    protected DockPane current; 
    
    private DockTabCloseable closeable = DockTabCloseable.NOT;

    /** constructors */
    
    /** an empty constructor */
    public DockTab() {
        this("", DockTabCloseable.NOT);
    }
    
    /**
     * A constructor with a title, and ON_SECONDARY as default.
     * @param title Sting
     */
    public DockTab(String title) {
        this(title, DockTabCloseable.ON_BOTH);
    }
    
    /** 
     * a constructor defining the closeable scheme 
     * 
     * @param closeable {@link DockTabCloseable}
     */
    public DockTab(DockTabCloseable closeable) {
        this("", closeable);
    }
    
    /** 
     * a constructor defining a tab text and the closeable scheme 
     * 
     * @param text String
     * @param closeable {@link DockTabCloseable}
     */
    public DockTab(String text, DockTabCloseable closeable) {
        
        this.closeable = closeable;
        setText(text);
        getStyleClass().add("dockable-tab");
        updateClose();
        
        this.setOnCloseRequest(e -> {
            TabPane c = (current == null)?owner:current;
            if (c != null) {
                ((DockPane)c).getDockTabs().remove(this);
                if (c != owner) {
                    if (!owner.getDockTabs().contains(this))
                        owner.getDockTabs().add(this);
                    current = owner;
                    updateClose();
                } else {
                    current = null;   
                    if (getOnDestroy() != null) 
                        getOnDestroy().handle(new ActionEvent());
                    destroy();
                }
            }
        });
        
    }
    
    /**
     * Brings this tab to the front.
     */
    public final void selectTab() {
        
        DockPane dockPane = (current == null)?owner:current;
        
        if (dockPane != null) {
            dockPane.selectTab(this);
        }
        
    }
    
    /**
     * update the DockTabs closeable property according to where it is against
     * the current value of closeable. 
     */
    public final void updateClose() {
        switch (closeable) {
            case NOT:
                break;
            case ON_SECONDARY:
                if (current != null && current != owner) 
                    this.setClosable(true);
                else
                    this.setClosable(false);
                break;
            case ON_BOTH:
                this.setClosable(true);
                getStyleClass().retainAll("tab");
                if (current != owner) {
                    getStyleClass().add("dockable-tab-on-secondary");
                } else {
                    getStyleClass().add("dockable-tab");
                }
                break;
        }
    }
    
    /** 
     * sets the owner of this DockTab
     * 
     * @param dockPane DockPane
     */
    void setOwner(DockPane dockPane) {
        if (owner == null) {
            owner = dockPane;
            current = owner;
        }
    }
    
    /**
     * swaps the location/parent TabPane {@link DockPane} of this DockTab
     * 
     * @param target DockPane: the new location/parent
     */
    void swapTabPane(DockPane target) {
        // ensure valid target and owner (though should be safe)
        if (target != null && owner != null) {
            // remove from owner if present in owner (this could be current)
            if (owner.getDockTabs().contains(this))
                owner.getDockTabs().remove(this);
            else if (current != null) {
                // remove from current if present in current
                if (current.getDockTabs().contains(this))
                    current.getDockTabs().remove(this);
            }
            if (!target.getDockTabs().contains(this))
                target.getDockTabs().add(this);
           
        }
        // update the current location/parent
        current = target;
        System.out.println("SWAPED" + "\n   " + owner + "\n   " + current);

        // update the close property for the tab
        updateClose();
    }

    /**
     * convenience method to put the DockTab back on its original owner.
     */
    public void backToOwner() {
        swapTabPane(owner);
    }

    /**
     * gets the current location/parent of this DockTab.
     * 
     * @return DockPane
     */
    DockPane getCurrent() {
        return current;
    }
    
    /**
     * gets the current owner of this DockTab.
     * 
     * @return DockPane
     */
    DockPane getOwner() {
        return owner;
    }
    
    
    /**
     * Whence a DockTab is dropped on the DockPane of the owner, attempt to 
     * re-order the tabs, with this tab taking the position under the drop.
     * 
     * @param e MouseEvent
     * @return boolean (true if reordered, false if not)
     */
    boolean dropZone(MouseEvent e) {
        DockPane p = (current == null)?owner:current;
        
        if (p != null) {
            return p.reorderTabs(e, this);
        } else {
            return false;
        }
    }

    /**
     * Borrowed from {@link ButtonBase}, reinterpreted to closing the tab from the original 
     * owner
     */
    public final ObjectProperty<EventHandler<ActionEvent>> onDestroyProperty() { return onDestroy; }
    public final void setOnDestroy(EventHandler<ActionEvent> value) { onDestroyProperty().set(value); }
    public final EventHandler<ActionEvent> getOnDestroy() { return onDestroyProperty().get(); }
    private ObjectProperty<EventHandler<ActionEvent>> onDestroy = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        
        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "onDestroy";
        }
    };

    public void destroy() {
        // for child use
    }
}
