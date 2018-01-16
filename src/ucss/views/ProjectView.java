/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ucss.controllers.ProjectController;
import ucss.ui.widgets.HelpRibbonButton;
import ucss.ui.widgets.RibbonWidget;
import ucss.ui.widgets.SpannerRibbon;
import ucss.views.ribbons.CommonObjectRibbon;
import ucss.views.ribbons.EventGraphRibbon;
import ucss.views.ribbons.MenuRibbonButton;
import ucss.views.ribbons.TermRibbon;

/**
 *
 * @author John
 */
public class ProjectView extends RibbonWidget {
    
    private final EventGraphRibbon eventGraphRibbon = new EventGraphRibbon();
    private final CommonObjectRibbon commonObjectRibbon = new CommonObjectRibbon();
    private final MenuRibbonButton ribbonButton = new MenuRibbonButton();
    private final HelpRibbonButton helpButton = new HelpRibbonButton();
    private final TermRibbon termRibbon = new TermRibbon();
    
    public ProjectView(ProjectController project) {
        this.getRibbons().addAll(
            ribbonButton,
            termRibbon,
            eventGraphRibbon,
            commonObjectRibbon,
            new SpannerRibbon(),
            helpButton
        );
    }
    
    public final void setOnClose(EventHandler<ActionEvent> value) {
       //btnClose.setOnAction(value);
    }

    public final void close() {
        //if (gv != null) gv.close();
    }
}
