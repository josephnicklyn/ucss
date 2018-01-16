/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import java.io.File;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ucss.controllers.DatabaseController;
import ucss.ui.widgets.FancyLabel;
import ucss.ui.widgets.WidgetHelpers;

/**
 *
 * @author John
 */
public class Splash extends StackPane {
    
    private final FancyLabel label;
    private final DatabaseController controller;
    private final LoginView loginView = new LoginView();
    
    public Splash(DatabaseController controller) {
        this.controller = controller;
        label = new FancyLabel(
            getClass().getResourceAsStream("/resources/cm.notes")
        );
        setPadding(new Insets(12));
        getChildren().add(label);
        
        label.setOnLinkSelected( (e, o, n) -> {
            if (n.equalsIgnoreCase("login"))
                fadeOut(label, loginView);
            else if (n.equalsIgnoreCase("local")) {
                getFile();
            } else if (n.equalsIgnoreCase("manual"))
                Manual.getInstance().showManual();
            
        });
        
        loginView.setOnCancel( e -> {
            fadeOut(loginView, label);
        });
        
        loginView.setOnLogin( e -> {
            if (controller != null) {
                controller.login(
                    loginView.getCredentials()
                );
            }
        });
        
        loginView.setOnUselLocalFile( e -> {
            getFile();
        });
    }
    
    private void fadeOut(Node nodeA, Node nodeB) {
        if (!getChildren().contains(nodeB))
            getChildren().remove(nodeB);   
        if (!getChildren().contains(nodeA)) {
            nodeA.setOpacity(1.0);
            getChildren().add(nodeA);   
        }
        
        FadeTransition ft = new FadeTransition(Duration.millis(500), nodeA);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished( e -> {
            getChildren().remove(nodeA);
            nodeB.setOpacity(0.0);
            if (!getChildren().contains(nodeB))
                getChildren().add(nodeB);
                
            fadeIn(nodeB);
        });
        ft.play();
        
    }
    
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void getFile() {
        if (controller != null) {
            try {
                File f = 
                    WidgetHelpers.showOpenDialog(
                        getScene().getWindow(),
                        "Open UCSS Local",
                        "SQLite:*.sqlite",
                        "DB:*.db"
                    );
                controller.useLocalFile(f.getAbsolutePath());
            } catch (Exception ex) {
                //Logger.getLogger(Splash.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
