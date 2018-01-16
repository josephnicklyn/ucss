/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationmain;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ucss.controllers.ProjectController;

/**
 *
 * @author John
 */
public class ApplicationMain extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        
        Scene scene = new Scene(
            ProjectController.begin(primaryStage),
            840, 
            580
        );
        
        scene.getStylesheets().add("resources/styles.css");
        
        primaryStage.setTitle("University Course Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
