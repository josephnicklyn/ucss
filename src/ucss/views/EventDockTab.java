/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import java.util.logging.Level;
import java.util.logging.Logger;
import ucss.controllers.GraphController;
import ucss.models.tuples.BuildingModel;
import ucss.models.tuples.TermModel;
import ucss.ui.widgets.DockTab;
import ucss.ui.widgets.EventGroupList;

/**
 *
 * @author John
 */
public class EventDockTab extends DockTab {
    private final EventGroupList theGraph;
    private final TermModel forTerm; 
    public EventDockTab(String title, BuildingModel building, TermModel term) {
        super(title);
        forTerm = term;
        EventGroupList graph = null;
        try {
            graph = GraphController.getGraph(term).getBuildingGraph(building);
        } catch (Exception ex) {
            Logger.getLogger(EventDockTab.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        theGraph = graph;
        super.setContent(theGraph);
    }
    
    @Override public final void destroy() {
        GraphController.getGraph(forTerm).removeGroupList(theGraph);
        System.out.println("DESTROY = " + theGraph.toString());
    }
}
