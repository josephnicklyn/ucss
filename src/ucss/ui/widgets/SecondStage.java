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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 * @author John
 */
public class SecondStage {
    private Stage stage = null;
    private Scene scene = null;
    private StackPane root = new StackPane();
    
    public SecondStage() {
        
    }
    
    public SecondStage(Window owner) {
        show(owner);
    }
    
    public SecondStage(Window owner, Node node) {
        setContent(node);
        show(owner);
    }
    
    /**
     * shows the stage.  This method will also initialize the stage if it has 
     * not already been.  
     * 
     * @param e MouseEvent
     */
    private void showStage(double x, double y/*MouseEvent e*/) {
        if (stage == null) {
            // creates a new stage
            stage = new Stage();
            scene = new Scene(root, 600, 400);
            scene.getStylesheets().add("resources/styles.css");

            stage.setTitle("University Course Schedule - Secondary");
            
            stage.setScene(scene);
            stage.setX(x);
            stage.setY(y);
            
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.NONE);
            
            stage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    //sendTabsHome();
                }
            });
        } else {
            // if the stage already exits
            if (!stage.isShowing()) {
                // if its not showing, relocate to the screenX/screenY of the drop
                stage.setX(x);
                stage.setY(y);
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
    public void setInitialOwner(Window window) {
        
        if (window != null) {
            primaryWindow = window;
            // the window exists
            window.setOnCloseRequest( e -> {
                if (stage != null) {
                    // close this stage if exists
                    stage.close();
                }
            });
        }
    }
    
    public final void show(Window owner, double x, double y) {
        setInitialOwner(owner);
        showStage(x, y);
    }
    
    public final void show(Window owner) {
        setInitialOwner(owner);
        showStage(800-owner.getWidth()/2, 600-owner.getHeight()/2);
    }
    
    public final void setContent(Node node) {
        root.getChildren().setAll(node);
    }
}
