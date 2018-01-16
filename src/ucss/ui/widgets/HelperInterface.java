/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.scene.input.MouseEvent;

/**
 *
 * @author John
 */
public interface HelperInterface {
    public String getHelperInfo(MouseEvent e);
    public void setExtraComments(String value);
}
