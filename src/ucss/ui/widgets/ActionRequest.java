/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.scene.input.KeyEvent;

/**
 *
 * @author John
 */
public interface ActionRequest {
    public void undoRequest();
    public void redoRequest();
    public void onKeyEvent(KeyEvent e);
}
