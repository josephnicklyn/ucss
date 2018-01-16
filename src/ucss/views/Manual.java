/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ucss.controllers.ProjectController;
import ucss.ui.widgets.FancyLabel;

/**
 *
 * @author John
 */
public class Manual {
    /** member variables */
    
    /** only one DockWindow allowed, the singleton for this class */
    private static Manual instance = null;

    private Stage stage = null;
    private Scene scene = null;
    
    private final StackPane root = new StackPane();
    
    private final FancyLabel label;
    
    /** an empty constructor */
    private  Manual() {
        label = new FancyLabel(
            getClass().getResourceAsStream("/resources/manual/manual.txt")
        );
        root.getChildren().setAll(label);
        setInitialOwner();
        
    }
    
    /**
     * get the singleton instance for this class.
     * 
     * @return DockWindow
     */
    public static Manual getInstance() {
        if (instance == null)
            instance = new Manual();
        return instance;
    }
    
    /**
     * shows the stage.  This method will also initialize the stage if it has 
     * not already been.  
     * 
     * @param e MouseEvent
     */
    public void showManual() {
        if (stage == null) {
            // creates a new stage
            stage = new Stage();
            scene = new Scene(root, 800, 600);
            scene.getStylesheets().add("resources/styles.css");

            stage.setTitle("Manual");
            
            stage.setScene(scene);
            //stage.setX(e.getScreenX());
            //stage.setY(e.getScreenY());
            
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.NONE);
            
            
        } else {
            // if the stage already exits
            if (!stage.isShowing()) {
                // if its not showing, relocate to the screenX/screenY of the drop
                //stage.setX(e.getScreenX());
                //stage.setY(e.getScreenY());
            }
        }
        // show the stage
        stage.show();
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
    public void setInitialOwner() {
        Window window = ProjectController.getInstance().getStage();
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
    
    
}
    
    
    /*
    private static Manual instance = null;
    private final StackPane root = new StackPane();
    private final FancyLabel label;
    private Stage stage = null;
    private Scene scene = null;
    
    private Manual() {
        this.getScene().setRoot(root);
        label = new FancyLabel(
            getClass().getResourceAsStream("/resources/manual/manual.txt")
        );
        root.getChildren().setAll(label);
    }
    
    public static Manual getInstance() {
        if (instance == null)
            instance = new Manual();
        return instance;
    }
    
    @Override public void show() {
            super.show();    
    }
    
}
*/