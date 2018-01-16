/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.controllers;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ucss.models.database.DBConnect.DatabaseMessages;
import ucss.ui.widgets.ActionRequest;
import ucss.ui.widgets.DockWindow;
import ucss.ui.widgets.WidgetHelpers;
import ucss.views.ProjectView;
import ucss.views.Splash;

/**
 *
 * @author John
 */
public class ProjectController {
    
    private StackPane root;
    private Stage primaryStage;
    private DatabaseController dbController;
    
    private static ProjectController instance;
    
    private Node currentNode = null;
    
    public ProjectController() {
        
    }
    
    public final Stage getStage() {
        return primaryStage;
    }
    public ProjectController(Stage pStage) {
        primaryStage = pStage;
        DockWindow.getInstance().setInitialOwner(pStage);
        primaryStage.setOnCloseRequest( e -> {
            if (dbController != null) {
                logout();
            }
        });
        initStage();
    }
    
    public void exitApplcation() {
        logout();
        primaryStage.close();
        Platform.exit();

    }
    
    public static ProjectController getInstance() {
        if (instance == null) 
            instance = new ProjectController();
        return instance;
    }
    
    public static StackPane begin(Stage pStage) {
        if (instance == null) {
            instance = new ProjectController(pStage);
        }
        return instance.getRoot();
    }
    
    public StackPane getRoot() {
        if (root == null) {
            root = new StackPane();
            currentNode = new Splash(getDatabaseController());
            root.getChildren().add(currentNode);
        }
        return root;
    }

     private boolean alertConfirmation(String header, String text) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setHeaderText(header);
        dialog.setContentText(text);
        dialog.setResizable(true);
        dialog.initOwner(primaryStage);
        dialog.getDialogPane().setPrefSize(320, 160);
        final Optional<ButtonType> result = dialog.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public void logout() {
        if (getDatabaseController().isConnected()) {
            if (getDatabaseController().hasAChange()) {
                if (alertConfirmation("UCSS", "Do you want to save changes to the project?")) {
                    saveMeetings();
                }
            }
        }
        
        getDatabaseController().disconnect();
    }

    public void saveMeetings() {
        try {
            getDatabaseController().saveMeetings();
        } catch (Exception ex) {
            Logger.getLogger(ProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final DatabaseController getDatabaseController() {
        if (dbController == null) {
            dbController = new DatabaseController();
            dbController.setOnDatabaseMessage( (e, o, n) -> {
                dbConnectionUpdateView(n);
            });
        }
        return dbController;
    }
    
    private void fadeOutAndReplace(Node in) {
        if (root == null) 
            return;
        
        final Node node;
        
        if (!root.getChildren().isEmpty()) {
            node = currentNode;
        } else {
            return;
        }
    
        if (node == null || !root.getChildren().contains(node)) 
            return;
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(node.getOpacity());
        ft.setToValue(0.0);
        
        ft.setOnFinished( e -> {
            root.getChildren().remove(node);
            currentNode = null;
            fadeIn(in);
        });
        
        
        ft.play();
       
    }
    
    private void fadeIn(Node node) {
        if (node != null) {
            node.setOpacity(0.0);
            getRoot().getChildren().setAll(node);
        } else {
            return;
        }
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setOnFinished( e -> {
            currentNode = node;
        });
        ft.play();
    }

    private void dbConnectionUpdateView(DatabaseMessages n) {
        if (n == DatabaseMessages.DBM_CONNECT) {
            fadeOutAndReplace(newProjectView());
        } else if (n == null || n == DatabaseMessages.DBM_DISCONNECT) {
            fadeOutAndReplace(new Splash(getDatabaseController()));
        }
    }
    private ProjectView projectView;
    
    private ProjectView newProjectView() {
        
        if (projectView != null) {
            fadeOutAndReplace(null);
        }
        
        projectView = new ProjectView(ProjectController.this);
        
        projectView.setOnClose( e -> {
            projectView.close();
            getDatabaseController().disconnect();
        });
        return projectView;
    }

    private void initStage() {
        if (primaryStage != null) {
            primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, onKeyPressed);
        }
    }
    
    private final EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.Z ||
                event.getCode() == KeyCode.Y)
            sendActionEvent(event);
        }

        private void sendActionEvent(KeyEvent event) {
            Node n = WidgetHelpers.getTopClass(root, ActionRequest.class);
            if (n != null) {
                ((ActionRequest)n).onKeyEvent(event);
            }
            
            
        }
    };

    

}
