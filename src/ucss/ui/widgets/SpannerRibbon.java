/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;

/**
 *
 * @author John
 */
public class SpannerRibbon extends Ribbon {
 
    public SpannerRibbon() {
        super("");
        HBox.setHgrow(this, Priority.ALWAYS);
        setMaxWidth(Double.MAX_VALUE);
    }
}
