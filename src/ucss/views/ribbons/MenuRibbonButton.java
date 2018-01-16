/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views.ribbons;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import ucss.controllers.DatabaseController;
import ucss.controllers.ProjectController;
import ucss.ui.widgets.RibbonButton;
import ucss.views.Manual;

/**
 *
 * @author John
 */
public class MenuRibbonButton extends RibbonButton {
    
    private final MenuItem mnuFileSave = new MenuItem("Save");
    private final MenuItem mnuFileLogout = new MenuItem("Logout");
    private final MenuItem mnuFileManual = new MenuItem("Manual");
    private final MenuItem mnuFileExit = new MenuItem("Exit");
    
    private ContextMenu contextMenu = new ContextMenu(
            mnuFileSave,
            new SeparatorMenuItem(),
            mnuFileLogout,
            new SeparatorMenuItem(),
            mnuFileManual,
            new SeparatorMenuItem(),
            mnuFileExit
        );
    
    public MenuRibbonButton() {
        super("Menu");
        super.setMenu(contextMenu);
        mnuFileManual.setOnAction( e -> { Manual.getInstance().showManual(); });
        mnuFileLogout.setOnAction( e -> { actionLogout(); });
        mnuFileSave.setOnAction( e -> { actionSave(); });
        mnuFileExit.setOnAction( e -> { 
            actionExit();
        });
        
    }

    private void actionLogout() {
        ProjectController.getInstance().logout();
    }

    private void actionSave() {
        ProjectController.getInstance().saveMeetings();
    }

    private void actionExit() {
        ProjectController.getInstance().exitApplcation();
    }
    
    
    
}
