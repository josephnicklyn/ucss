package ucss.ui.widgets;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * <p>
 * A dock window is a secondary stage where a {@link DockTab} is to reside 
 * whence not on its original owner.  This class maintains the secondary window
 * which will be hidden automatically when all of its DockTabs are removed.
 * The window will automatically appear when a DockTab is dropped off of the
 * initial stage of the DockTab.
 * 
 * <p>
 * DockTabs can again be closed when on this DockPane, which will then be 
 * relocated to the original owner.  This DockWindow if closed will in turn
 * relocate all of the DockTabs located in this DockPane back to their owners.
 * </p>
 * 
 * <p>
 * This window does not have an owner, this allows the window to appear in the
 * task bar which allows the user to select either of them. Since this window
 * does not have an owner, it will be maintained if the applications main 
 * window is closed.  To close this window when the primary stage is closed
 * invoke the setInitialOwner(Window) method with the parameter being the 
 * primaryStage.
 * 
 * @author Joseph Nicklyn JR.
 */
public class DockWindow {
    /** member variables */
    
    /** only one DockWindow allowed, the singleton for this class */
    private static DockWindow instance = null;

    private Stage stage = null;
    private Scene scene = null;
    
    /** The {@link DockPane} for this window/stage */
    private final DockPane tabPane = new DockPane();
    
    
    private final StackPane root = new StackPane(tabPane);
    
    
    /** an empty constructor */
    private  DockWindow() {
        tabPane.getTabs().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                if (tabPane.getTabs().isEmpty()) {
                    if (stage != null) {
                        stage.close();
                    }
                }
            }
        });        
    }
    
    /**
     * get the singleton instance for this class.
     * 
     * @return DockWindow
     */
    public static DockWindow getInstance() {
        if (instance == null)
            instance = new DockWindow();
        return instance;
    }
    
    /**
     * responds to a {@link DockTab} being dropped onto this window (if visible)
     * or off of the its current window (whether it be this DockPane or another).
     * 
     * @param e MouseEvent
     * @param tab DockTab
     */
    public final void drop(MouseEvent e, DockTab tab) {
        // ignore if already contained
        if (tabPane.getTabs().contains(tab)) {
            return;
        }
        // show the stage
        showStage(e);
        // move the tab to this DockPane
        tab.swapTabPane(tabPane);
        // select/bring to front this tab
        if (tabPane.getTabs().contains(tab))
            tabPane.getSelectionModel().select(tab);
        // bring this stage to front
        stage.toFront();
    }

    /**
     * shows the stage.  This method will also initialize the stage if it has 
     * not already been.  
     * 
     * @param e MouseEvent
     */
    private void showStage(MouseEvent e) {
        if (stage == null) {
            // creates a new stage
            stage = new Stage();
            scene = new Scene(root, 600, 400);
            scene.getStylesheets().add("resources/styles.css");

            stage.setTitle("University Course Schedule - Secondary");
            
            stage.setScene(scene);
            stage.setX(e.getScreenX());
            stage.setY(e.getScreenY());
            
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.NONE);
            
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    sendTabsHome();
                }
            });
        } else {
            // if the stage already exits
            if (!stage.isShowing()) {
                // if its not showing, relocate to the screenX/screenY of the drop
                stage.setX(e.getScreenX());
                stage.setY(e.getScreenY());
            }
        }
        // show the stage
        stage.show();
    }

    /**
     * returns all the containing DockTabs to its owner.  This method is evoked
     * when this stage is closed/hidden
     */
    private void sendTabsHome() {
        // iterate backwards through the list (they will be removed when progressed
        for(int i = tabPane.getDockTabs().size()-1; i >= 0; i--) {
            DockTab tab = tabPane.getDockTabs().get(i);
            tab.backToOwner();
        }
    }
    /**
     * returns all DockTabs of [owner] back to its owner.  
     * @param owner DockPane
     */
    public final void sendTabsHome(DockPane owner) {
        // iterate backwards through the list (they will be removed when progressed
        for(int i = tabPane.getDockTabs().size()-1; i >= 0; i--) {
            DockTab tab = tabPane.getDockTabs().get(i);
            if (tab.getOwner() == owner)
                tab.backToOwner();
        }
    }
    /**
     * get this stage.
     * 
     * @return Stage
     */
    public Stage getStage() {
        return stage;
    }
    
    
    private Window primaryWindow = null;
    
    public Window getPrimaryWindow() {
        return primaryWindow;
    }
    /**
     * sets the initial owner of this stage.  This method should be called 
     * from the applications start method.
     * 
     * @param window Window/Stage
     */
    public void setInitialOwner(Window window) {
        
        if (window != null) {
            primaryWindow = window;
            primaryWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, onCloseRequest);
        }
    }
    private final EventHandler<WindowEvent> onCloseRequest = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if (stage != null) {
                // close this stage if exists
                stage.close();
            }
        }
    };
    
    /**
     * gets the DockPane for this class.
     * 
     * @return {@link DockPane}
     */
    public DockPane getTabPane() {
        return tabPane;
    }
    
    public final void toTopIfContains(Tab target) {
        if (tabPane.getTabs().contains(target) && stage != null) {
            tabPane.getSelectionModel().select(target);
            stage.toFront();
        }
    }
    
    public final Tab selectTab(String title) {
        
        for(Tab d: tabPane.getTabs()) {
           if (d.getText().equalsIgnoreCase(title)) {
               toTopIfContains(d);
               return d;
           }
        }
        return null;
    }
    
    
}
