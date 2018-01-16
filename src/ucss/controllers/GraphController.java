/*
 * Copyright (C) 2017 Joseph Nicklyn JR.
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
package ucss.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import ucss.controllers.GraphController.RoomItem;
import ucss.models.tuples.*;
import ucss.models.tuples.momentos.MeetingModelMomento;
import ucss.models.tuples.momentos.MomentoInterface;
import ucss.models.tuples.momentos.MomentoType;
import ucss.models.views.OutputBox;
import ucss.ui.widgets.EventGroup;
import ucss.ui.widgets.EventGroupList;
import ucss.ui.widgets.EventItem;
import ucss.ui.widgets.GraphWalker;
import ucss.ui.widgets.HPane;
import ucss.ui.widgets.VPane;
import ucss.ui.widgets.WidgetHelpers;

/**
 * The GraphController, is a centralized entity to create and manage event graphs.
 * One controller will be used for each term/semester, which will maintain an 
 * internal list of graphs created.  This class also deals with the undo/redo
 * operations. 
 * 
 * @author Joseph Nicklyn
 */
public class GraphController {
    
/** STATIC ATTRIBUTES */
    
    /** A hash map of GraphControllers, making this a multiton */
    private static HashMap<TermModel, GraphController> graphs = new HashMap<>();
    /** flag to initialize momento listener */
    private static boolean initialized = false;
    /** context menu for undo/redo operations */
    private static ContextMenu contextMenu;
    /** redo and undo */
    private static MenuItem mnuUndo, mnuRedo;
    
    /** a context menu for meeting models */
    private static ContextMenu meetingModelContextMenu;
    /** delete */
    private static Menu mnuDelete; 
    private static MenuItem mnuDeleteMeeting, mnuDeleteSection;

/** INSTANCE ATTRIBUTES */
    
    /** The term model for this instance */
    private final TermModel termModel;
    /** a list of event graphs for this controller */
    private final ArrayList<EventGroupList> groupLists = new ArrayList<>();
    
    /**
     * Constructor defining a model
     * @param term TermModel
     */
    private GraphController(TermModel term) {
        termModel = term;
    }
    
    /** 
     * Give access to a controller.
     * @param model TermModel
     * @return GraphController
     */
    public static GraphController getGraph(TermModel model) {
        GraphController gr = graphs.get(model);
        
        if (gr == null) {
            gr = new GraphController(model);
            graphs.put(model, gr);
        
            if (!initialized) {
                initialized = true;
                Model.getMeetingModelMomento().setOnUndo(e -> { 
                    doUndoRedo(e.getMomentoInterfaceTarget());
                });

                Model.getMeetingModelMomento().setOnRedo(e -> { 
                    doUndoRedo(e.getMomentoInterfaceTarget());
                });
            }
        }
        return gr;
    }

    public GraphController() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Creates a new graph of a building, this is the most basic creator.
     * @param building the id of a building
     * @return EventGroupList
     * @throws Exception a building with the id [building] must exist
     */
    public final EventGroupList getBuildingGraph(int building) throws Exception {
        return getBuildingGraph(Model.getBuilding(building));
    }
    
    /*
     * Creates a new graph of a building, this is the most basic creator.
     * @param building the id of a building
     * @return EventGroupList
     * @throws Exception the BuildingModel must exist
     */
    public final EventGroupList getBuildingGraph(BuildingModel building) throws Exception {
        if (building == null)
            throw new NullPointerException("BuilingModel can not be null.");
        EventGroupList result = new EventGroupList();
        
        for(RoomModel r: Model.getRooms()) {
            if (r.getBuilding() == building.getModelID()) {
                RoomItem roomItem = new RoomItem(r);
                result.getItems().add(roomItem);
            }        
        }

        // add listners to respond to mouse events
        result.addEventHandler(MouseEvent.MOUSE_PRESSED, ON_MOUSE_PRESSED);
        result.addEventHandler(MouseEvent.MOUSE_DRAGGED, ON_MOUSE_DRAGGED);
        result.addEventHandler(MouseEvent.MOUSE_RELEASED, ON_MOUSE_RELEASED);
        
        groupLists.add(result);
        
        return result;
    }
    
    /**
     * A EventGroupList in a department context.
     */
    public class DepartmentGraph extends EventGroupList {
        
        public DepartmentGraph() {}
        
        /**
         * Adds a department to this department graph.
         * @param dm DepartmentModel
         */
        public final void add(DepartmentModel dm) {
            if (containsDepartment(dm) == null) {
                RoomItem d = new RoomItem(dm);
                d.setForObject(dm);
                getItems().add(d);
            }
        }
        
        /**
         * Removes a department from this graph if present.
         * @param dm DepartmentModel
         */
        public final void remove(DepartmentModel dm) {
            EventGroup rm = containsDepartment(dm);
            if (rm != null) {
                getItems().remove(rm);
            }
        }
        
        /**
         * Determine if this graph contains a EventGroup for the DepartmentModel. 
         * @param dm DepartmentModel
         * @return EventGroup
         */
        private EventGroup containsDepartment(DepartmentModel dm) {
            for(EventGroup g: getItems()) {
                if (g.getForObject() == dm) {
                    return g;
                }
            }
            return null;
        }

        /**
         * Adds a meeting to this graph, locates the correct EventGroup and
         * VPane (day). 
         * @param nodes List:MeetingItem
         */
        private void addMeetings(List<MeetingItem> nodes) {
            if (nodes != null) {
                for(MeetingItem m: nodes) {
                    EventGroup g = containsDepartment(Model.getDepartment(m.getMeetingModel().getDepartmentID()));
                    if (g != null) {
                        RoomItem rm = (RoomItem)g;
                        rm.addEvent(m.getMeetingModel());
                    }
                }
            }
        }

        /**
         * Removes a list of MeetingItems from this DepartmentGraph.
         * @param nodes List:DepartmentModel
         */
        private void removeMeetings(List<MeetingItem> nodes) {
            if (nodes != null) 
                for(MeetingItem m: nodes) {
                    EventGroup g = containsDepartment(Model.getDepartment(m.getMeetingModel().getDepartmentID()));
                    if (g != null) {
                        RoomItem rm = (RoomItem)g;
                        
                        for(VPane p: rm.getItems()) {
                            for(HPane h: p.getItems()) {
                                MeetingItem kill = null;
                                for(EventItem i: h.getItems()) {
                                    if (i instanceof MeetingItem) {
                                        MeetingItem xm = (MeetingItem)i;
                                        if (xm.getMeetingModel().getModelID() == m.getMeetingModel().getModelID()) {
                                            kill = xm;
                                            break;
                                        }
                                    }
                                }
                                if (kill != null) {
                                    h.getItems().remove(kill);
                                }
                            }
                    }
                }
            }
        }
        
        /**
         * Removes a single MeetingModel from this DepartmentGraph. Usually
         * from an undo/redo operation.
         * @param x MeetingModel
         */
        private void removeMeeting(MeetingModel x) {
            EventGroup g = containsDepartment(Model.getDepartment(x.getDepartmentID()));
            if (g != null) {
                ArrayList<MeetingItem> forRemoval = new ArrayList();
                RoomItem rm = (RoomItem)g;
                this.<RoomItem, MeetingItem>eventWalker( 
                    new GraphWalker<RoomItem, MeetingItem>() {
                       
                        @Override public boolean walkIinR(
                                EventGroupList gl, 
                                MeetingItem m, 
                                RoomItem r) {
                                if (m.getMeetingModel().getModelID() == x.getModelID()) {
                                    forRemoval.add(m);
                                }
                                m.setSelected(false);
                            return false;
                        }
                    }
                );
                rm.<MeetingItem>removeEventItems(forRemoval);
            }
        }

        /**
         * Adds a single MeetingModel to this DepartmentGraph. Usually from an
         * undo/redo operation.
         * @param m MeetingModel
         */
        private void addMeeting(MeetingModel m) {
            EventGroup g = containsDepartment(Model.getDepartment(m.getDepartmentID()));
            if (g != null) {
                RoomItem rm = (RoomItem)g;
                rm.addEvent(m);
            }
        }
        
    }
    
    /**
     * Only a single department graph for this object
     */
    private DepartmentGraph departmentGraph = null;
    public final DepartmentGraph getDepartmentGraph() {
        if (departmentGraph == null) {
            departmentGraph = new DepartmentGraph();
            // add listners to respond to mouse events
            departmentGraph.addEventHandler(MouseEvent.MOUSE_PRESSED, ON_MOUSE_PRESSED);
            //departmentGraph.addEventHandler(MouseEvent.MOUSE_DRAGGED, ON_MOUSE_DRAGGED);
            departmentGraph.addEventHandler(MouseEvent.MOUSE_RELEASED, ON_MOUSE_RELEASED);
        }
        
        return departmentGraph;
    }
    
    /**
     * Gets the contextMenu, initializes if needed, and updates the menu items
     * to reflect the current state of the undo/redo lists.
     * @return ContextMenu
     */
    private ContextMenu getContextMenu() {
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
            mnuUndo = new MenuItem("undo");
            mnuRedo = new MenuItem("redo");
            contextMenu.getItems().addAll(mnuUndo, mnuRedo);
            mnuUndo.setOnAction( e -> {doAction(true); });
            mnuRedo.setOnAction( e -> {doAction(false); });
        }
        mnuUndo.setDisable(!Model.getMeetingModelMomento().hasUndo());
        mnuRedo.setDisable(!Model.getMeetingModelMomento().hasRedo());
        mnuUndo.setText("undo [" + Model.getMeetingModelMomento().getLastUndo() + "]" );
        mnuRedo.setText("redo [" + Model.getMeetingModelMomento().getLastRedo() + "]" );
        return contextMenu;
    }
    
    
    private MeetingModel meetingTarget = null;
    /**
     * Gets the meetingContextMenu, initializes if needed, and updates the
     * menu items to reflect the current state of the undo/redo lists.
     * @param m MeetingModel
     * @return ContextMenu
     */
    private ContextMenu getMeetingModelContextMenu(MeetingModel m) {
        if (meetingModelContextMenu == null) {
            meetingModelContextMenu = new ContextMenu();
            mnuDeleteMeeting = new MenuItem("This meeting only");
            mnuDeleteSection = new MenuItem("All meetings for this section");
            mnuDelete = new Menu("delete", null, mnuDeleteMeeting, mnuDeleteSection);
            
            meetingModelContextMenu.getItems().addAll(mnuDelete);
            mnuDeleteMeeting.setOnAction( e -> { deleteMeeting(false); });
            mnuDeleteSection.setOnAction( e -> { deleteMeeting(true); });
            
        }
        meetingTarget = m;
        if (m != null) {
            mnuDeleteMeeting.setDisable(false);
            mnuDeleteSection.setDisable(false);
            
            //mnuDeleteMeeting.setText("toggle section [ " + m.getMeetingString()+ " ]" );
        } else {
            mnuDeleteMeeting.setDisable(true);
        }
        return meetingModelContextMenu;
    }
    
    /**
     * Triggers the undo/redo event.
     * @param undo boolean, true  = the request is for an undo 
     *                      false = the request is for an redo 
     */
    private void doAction(boolean undo) {
        if (undo) {
            Model.getMeetingModelMomento().undo();
        } else {
            Model.getMeetingModelMomento().redo();
        }
    }
    
    
    public static void removeMeetingsFor(MeetingModel model) {
        if (model == null)
            return;
        
        GraphController gTarget = graphs.get(Model.getTerm(model.getSemester()));
        if (gTarget != null)
            gTarget.removeEventItemForMeeting(model);
    }

    /**
     * Flags meeting models for deletion.
     */
    private void deleteMeeting(boolean all) {
        
        if (meetingTarget != null) {
            
            int sequenceNumber = MeetingModelMomento.getSequnceCounter();
            
            if (all) {
                for(MeetingModel m: termModel.getMeetings()) {
                    if (m.isSameSection(meetingTarget)) {
                        m.flagForRemoval();
                        MeetingModelMomento.generateMomento(m, MomentoType.REMOVED_ENTITY);
                        removeEventItemForMeeting(m);
                    }
                }
            } else {
                meetingTarget.flagForRemoval();
                removeEventItemForMeeting(meetingTarget);
                MeetingModelMomento.generateMomento(meetingTarget, MomentoType.REMOVED_ENTITY);
            }
            
        }
    }
    
    private static final void muteIt(EventGroupList eg, boolean off) {
        eg.<RoomItem, MeetingItem>eventWalker( 
                new GraphWalker<RoomItem, MeetingItem>() {

                @Override public boolean walkIinR(
                        EventGroupList gl, 
                        MeetingItem m, 
                        RoomItem r) {
                    if (off) {
                        m.setMuted(false);
                    } else {
                        m.setMuted(
                            !Model.getDepartment(
                                m.getMeetingModel().getDepartmentID()
                            ).isSelected());
                    }
                    return false;
                }
            }
        );
    }
    
    /**
     * Sets the mute filter for all graphs (including department graphs).
     */
    public static void updateMuteFilter() {
        boolean off = !DepartmentModel.someSelected();
        
        for(TermModel t: graphs.keySet()) {
            for(EventGroupList eg: graphs.get(t).groupLists) {
                muteIt(eg, off);
            }
            
            if (graphs.get(t).departmentGraph != null)
                muteIt(graphs.get(t).departmentGraph, off);
        }
    }
    
    /**
     * clears the groupList/event graphs for this term/semester.
     */
    public void clear() {
        groupLists.clear();
    }
    
    /**
     * Removes a selected EventGroupList from this term.
     * @param item EventGroupList
     * @return boolean, ture if was present
     */
    public boolean removeGroupList(EventGroupList item) {
        return groupLists.remove(item);
    }
    
    /**
     * A RoomItem, creates and maintains a room for an EventGroupList.
     */
    class RoomItem extends EventGroup  {
        /** the room model associated with this room */
        private final RoomModel forRoom;
        
        private final DepartmentModel forDepartment;
        /** create a new room, with 7 days 3 of which are hidden */
        public RoomItem(RoomModel room) {
            super(
                room.toString(),
                new VPane("MON"),
                new VPane("TUE"),
                new VPane("WED"),
                new VPane("THU"),
                new VPane("FRI", false),
                new VPane("SAT", false),
                new VPane("SUN", false)
            );
            
            setSecondaryTitle(room.getLabTypeName());
            setReferenceID(room.getModelID());
            forDepartment = null;
            forRoom = room;
            populateRoom();
        }
        
        public RoomItem(DepartmentModel dm) {
            super(
                dm.toString(),
                new VPane("MON"),
                new VPane("TUE"),
                new VPane("WED"),
                new VPane("THU"),
                new VPane("FRI", false),
                new VPane("SAT", false),
                new VPane("SUN", false)
            );
            forDepartment = dm;
            forRoom = null;
            
            setReferenceID(dm.getModelID());
            
            populateByDepartment();
        }
        
        /**
         * Populates the room with events from the term.
         */
        private void populateRoom() {
            clearEvents();
            for(MeetingModel m: termModel.getMeetings()) {
                if (m.getRoom() == forRoom.getModelID() && !m.isFlaggedForRemoval()) {
                    addEvent(m);
                }
            }
            autoHide();
        }
        
        /**
         * populates the list with department context.
         */
        private void populateByDepartment() {
            clearEvents();
            for(MeetingModel m: termModel.getMeetings()) {
                if (m.getDepartmentID() == forDepartment.getModelID()) {
                    int forDay = m.getOnDay();
                    if (forDay >= 0 && forDay <= 6) {
                        EventItem i = addEvent(m);
                    }
                }
            }
            autoHide();
        }
        
        /**
         * Adds a meeting model to this room, validating the forDay attribute.
         * @param mi MeetingModel
         */
        private MeetingItem addEvent(MeetingModel mi) {
            int forDay = mi.getOnDay();
            if (forDay >= 0 && forDay <= 6) {
                MeetingItem r = new MeetingItem(mi, RoomItem.this);
                getItems().get(forDay).addEventItem(r);
                
                return r;
            }
            return null;
        }

        /**
         * Get the room model for this group.
         * @return RoomModel
         */
        public final RoomModel getForRoom() {
            return forRoom;
        }
        
        /**
         * Responds to a user dragging and dropping a meeting/section onto this
         * room. The room needs to be associated with the same term.
         * @param target MeetingItem, the meeting item the drag started with.
         * @param nodes List of MeetingItem's, this can not be null or empty
         * @param overDay Integer, the day the drop occurred over
         * @param overTime Integer, the time the left side of the target item
         *                          was where the drop occurred.
         * @param forNew boolean, true = clone a meeting
         * <p>
         *  a clone will be dropped below the target item, the user will 
         *  subsequently have to move the clone to the desired location
         */
        public final void dropEvents(
                    MeetingItem target,
                    List<MeetingItem> nodes,
                    int overDay, 
                    int overTime,
                    boolean forNew) {
            
            if (termModel.getModelID() != target.getMeetingModel().getSemester()) {
                System.out.println("WRONG SEMESTER");
                return;
            }
            if (nodes.isEmpty())
                return;
            
            int targetDay = target.getMeetingModel().getOnDay();
            int newDay = overDay;
            int dayDiff = newDay - targetDay;

            int targetStartTime = target.getMeetingModel().getStartTime();
            int newStartTime = overTime;
            int startTimeDiff = newStartTime - targetStartTime;
            
             if (forNew) {
                // just drop below the target
                MeetingModel newModel; 
                try {
                    newModel = target.getMeetingModel().copyAndStore();
                    // create a sequence number 
                    int sequenceNumber = MeetingModelMomento.getSequnceCounter();
                    // before generating the momento
                    MeetingModelMomento.generateMomento(newModel, MomentoType.NEW_ENTITY);
                    addEvent(newModel);
                } catch (Exception ex) {
                    Logger.getLogger(GraphController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                 // remove the nodes currenly on the graph(s)
                 removeAllMeetings(nodes);
                 if (departmentGraph != null && nodes != null) {
                    departmentGraph.removeMeetings(nodes);
                }
                 // validate the new time/day for the meeting events
                 for(MeetingItem m : nodes)  {
                    int mDay = m.getMeetingModel().getOnDay() + dayDiff;
                    if (mDay < 0 || mDay > 6)
                        return;
                    int mStartTime = m.getMeetingModel().getStartTime() + startTimeDiff;
                    int max = 1440 - m.getMeetingModel().getDuration();
                    if (mStartTime < 0 || mStartTime > max)
                        return;
                }
                // create a sequence number 
                int sequenceNumber = MeetingModelMomento.getSequnceCounter();
                // generate momento(s) and set new time/day for the events
                nodes.forEach((m) -> {
                    
                    MeetingModelMomento.generateMomento(m.getMeetingModel());
                    
                    int mDay = m.getMeetingModel().getOnDay() + dayDiff;
                    int mStartTime = m.getMeetingModel().getStartTime() + startTimeDiff;
                    
                    m.getMeetingModel().setRoom(forRoom.getModelID());
                    m.getMeetingModel().setOnDay(mDay);
                    
                    m.getMeetingModel().setStartTime(mStartTime);
                }); 
                
                if (departmentGraph != null && nodes != null) {
                    departmentGraph.addMeetings(nodes);
                }
                addMeetings(forRoom, nodes);
             }
        }
    }
    
    /**
     * A meeting item on the graph.
     */
    public class MeetingItem extends EventItem {
        
        private RoomItem forRoom;
        private final MeetingModel forMeetingModel;
        
        /** 
         * The default and only constructor for a meeting item.
         * @param meetingModel MeetingModel
         * @param owner RoomItem
         */
        MeetingItem(
                MeetingModel meetingModel,
                RoomItem owner
        ) {
            super(
                String.format(
                    "%s/%d", 
                        meetingModel.getMeetingString(), 
                        meetingModel.getModelID()
                ), 
                meetingModel.getDuration(), 
                meetingModel.getStartTime(),
                meetingModel.getDepartmentID()
            );
            
            if (DepartmentModel.someSelected()) {
                DepartmentModel dm = Model.getDepartment(meetingModel.getDepartmentID());
                if (dm != null) 
                    setMuted(!dm.isSelected());
            }
            
            setReferenceID(meetingModel.getModelID());
            
            forRoom = owner;
            forMeetingModel = meetingModel;
        }
        
        public final MeetingModel getMeetingModel() {
            return forMeetingModel;
        }
        
        public final RoomItem getForRoom() {
            return forRoom;
        }
        
        public final int getOnDay() {
            return getMeetingModel().getOnDay();
        }
        
        @Override public String toString() {
            return forMeetingModel.toString();
        }
    }
    
    /***************************************************************************
     * BEGIN ACTIONS FOR MOUSE EVENTS
     **************************************************************************/
    
    /** a threshold for dragging - if the mouse moved enough */
    private boolean reachedThreshold = false;
    /** the original target of he mouse pressed event */
    private MouseEvent originatingMeeting = null;
    /** the screen position of the mouse on the pressed event */
    private double downX, downY;
    /** a list of items of the same section in the same room */
    private final ArrayList<MeetingItem> inSameRoom = new ArrayList();
    /** the original item from the mouse pressed event */
    private MeetingItem targetMeeting = null;
    /** a class to assist in creating a drag image and positioning */
    private static EventDrag eventDrag = null;
    /** the default limit of mouse movement prior to initiating a drag */
    private final static double DRAG_THRESHHOLD = 12.0d;
    
    /** gets/creates a new EventDrag */
    private static EventDrag getEventDrag() {
        if (eventDrag == null)
            eventDrag = new EventDrag();
        return eventDrag;
    }
    
    /**
     * Responds to a mouse pressed event.
     */
    private final EventHandler<MouseEvent> ON_MOUSE_PRESSED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            originatingMeeting = e;
            if (e.getButton() == MouseButton.PRIMARY) {
                
                reachedThreshold = false;
                
                if (selectMeetingsEvent(e)) {
                    downX = e.getScreenX();
                    downY = e.getScreenY();
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                MeetingItem eventTargetMeeting = getTargetMeeting(e);
                if (eventTargetMeeting == null) {
                    getContextMenu().show(
                            ((Node)e.getSource()).getScene().getWindow(),
                            e.getScreenX(), 
                            e.getScreenY());
                } else {
                    getMeetingModelContextMenu(
                        eventTargetMeeting.getMeetingModel()).show(
                            ((Node)e.getSource()).getScene().getWindow(),
                            e.getScreenX(), 
                            e.getScreenY());
                }
            }
        }
    };
    
    /** 
     * Responds to a mouse dragged event. the mouse needs to be moved by some
     * threshold prior to any action taken.
     */
    private final EventHandler<MouseEvent> ON_MOUSE_DRAGGED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            
            if (e.getButton() == MouseButton.PRIMARY) {
                if (dragThresholdMet(e)) {
                    getEventDrag().doDrag(e);
                }
            } else {
                getEventDrag().cancelDrag();
                reachedThreshold = false;
            }
        }

        /**
         * Determines if the mouse moved enough.
         * @param e MouseEvent
         * @return boolean
         */
        private boolean dragThresholdMet(MouseEvent e) {
            if (reachedThreshold) {
                return true;
            } else {
                if (Math.max(
                        Math.abs(e.getScreenX() - downX),
                        Math.abs(e.getScreenY() - downY)) > DRAG_THRESHHOLD
                        ) {
                    reachedThreshold = true;
                    getEventDrag().beginDrag(
                        e, 
                        targetMeeting, 
                        inSameRoom, 
                        originatingMeeting.isShiftDown()
                    );
                }
            }
            return reachedThreshold;
        }
    };
    
    private final EventHandler<MouseEvent> ON_MOUSE_RELEASED = 
            new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            if (reachedThreshold) 
                getEventDrag().endDrag(e, targetMeeting, inSameRoom);
            else
                getEventDrag().cancelDrag();
        }
    };

    /**
     * Removes all the meeting items in the nodes.
     * @param nodes MeetingItems
     */
    private void removeMeetings(List<MeetingItem> nodes) {
        for(EventGroupList eg: groupLists) {
            for(EventGroup g: eg.getItems()) {
                g.<MeetingItem>removeEventItems(nodes);
            }
        }
    }
    
    /**
     * Removes all the meeting items in the nodes associated with 
     * this meeting item.
     * @param nodes MeetingItems
     */
    private void removeAllMeetings(List<MeetingItem> nodes) {
        if (nodes == null || nodes.isEmpty())
            return;
        List<MeetingItem> forRemoval = new ArrayList(); 
        for(EventGroupList eg: groupLists) {
            eg.<RoomItem, MeetingItem>eventWalker( 
                    new GraphWalker<RoomItem, MeetingItem>() {
                        @Override public boolean walkIinR(
                            EventGroupList gl, 
                            MeetingItem m, 
                            RoomItem r) {
                        if (m != null) {
                            for(MeetingItem mx: nodes) {
                                if (mx.getMeetingModel().getModelID() == m.getMeetingModel().getModelID()) {
                                    forRemoval.add(mx);
                                    break;
                                }
                            }
                        }
                        return false;
                    }
                }
            );
        } 
        removeMeetings(forRemoval);
    }
    
    /**
     * Adds a list of meeting items to a room.
     * @param rm RoomModel
     * @param nodes List:MeetingItem
     */
    private void addMeetings(RoomModel rm, List<MeetingItem> nodes) {
        if (rm == null || nodes == null)
            return;
        for(EventGroupList eg: groupLists) {
            eg.<RoomItem>roomWalker(
                new GraphWalker<RoomItem, MeetingItem>() {
                    @Override public boolean walkIinR(
                            EventGroupList gl, 
                            MeetingItem m, 
                            RoomItem r) {
                        if (r.getForRoom() != null && r.getForRoom().getModelID() == rm.getModelID()) {
                            for(MeetingItem mi: nodes) {
                                r.addEvent(mi.getMeetingModel());
                            }
                        }
                        return false;
                    }
                }
            );
        } 
    }
    
    /**
     * Sets/selects meetings in a particular EventGroupList.
     * @param e MouseEvent
     * @param eg EventGroupList
     * @param eventTargetMeeting MeetingItem
     */
    private final void selectMeetingsEventGraphWalk(
        MouseEvent e,
        EventGroupList eg,
        MeetingItem eventTargetMeeting
    ) {
        
        if (eg == null)
            return;
        
        MeetingModel sourceMeeting = eventTargetMeeting.getMeetingModel();
        eg.<RoomItem, MeetingItem>eventWalker( 
                new GraphWalker<RoomItem, MeetingItem>() {

                @Override public boolean walkIinR(
                        EventGroupList gl, 
                        MeetingItem m, 
                        RoomItem r) {

                    if (r.getForRoom() != null && r.getForRoom().getModelID() == sourceMeeting.getRoom()) {
                        if (    m.getMeetingModel().isSameSection(sourceMeeting) &&
                                ( (e.isControlDown() && (m == eventTargetMeeting)) ||
                                (!e.isControlDown())) ) {    
                            inSameRoom.add(m);
                        }
                    }
                    if (m == eventTargetMeeting) {
                        m.setFocus(true);
                    } else {
                        if (m.getMeetingModel().isSameSection(sourceMeeting)) {
                            m.setSelected(true);
                        } else {
                            m.setSelected(false);
                        }
                    }
                    return false;
                }
            }
        );
    }
    
    /**
     * populates the inSameRoom list with items from the graph.
     * @param e MouseEvent
     * @return boolean
     */
    private boolean selectMeetingsEvent(MouseEvent e) {
        
        MeetingItem eventTargetMeeting = getTargetMeeting(e);
        inSameRoom.clear();
        
        if (eventTargetMeeting == null)
            return false;
        
        targetMeeting = eventTargetMeeting;
        
        for(EventGroupList eg: groupLists) {
            selectMeetingsEventGraphWalk(e, eg, eventTargetMeeting);
        }
        selectMeetingsEventGraphWalk(e, departmentGraph, eventTargetMeeting);
        // it probably is already sorted in the correct order, but just in case
        Collections.sort(inSameRoom, comparator);
        return true;
    }
    
    Comparator<MeetingItem> comparator = new Comparator<MeetingItem>() {
        @Override
        public int compare(MeetingItem left, MeetingItem right) {
            return left.getOnDay() - right.getOnDay(); 
        }
    };
    
    /** 
     * Gets the MeetingItem from a mouse event.  Iterating through parents if
     * need to get to it.
     * @param e MouseEvent
     * @return MeetingItem or null, if not over a meeting item
     */
    public final MeetingItem getTargetMeeting(MouseEvent e) {

        MeetingItem r = null;

        if (e != null) {

            Node n = (Node)e.getTarget();
            while(n != null) {
                if (n instanceof MeetingItem) {
                    r = (MeetingItem)n;
                    break;
                }
                n = n.getParent();
            }
        }

        return r;
    }
    
    /**
     * clears selected items in a particular EventGroupList.
     * @param eg EventGroupList
     */
    private static void clearSelectItemsGraphWalker(EventGroupList eg) {
        eg.<RoomItem, MeetingItem>eventWalker( 
                        new GraphWalker<RoomItem, MeetingItem>() {

                        @Override public boolean walkIinR(
                                EventGroupList gl, 
                                MeetingItem m, 
                                RoomItem r) {
                                m.setSelected(false);
                            return false;
                        }
                    }
                );
    }
    
    /**
     * Removes the "selected" or "focused" pseudo class for all 
     * items on all the graphs for all terms.
     */
    private static void clearSelectItems() {
        for(TermModel t: graphs.keySet()) {
            for(EventGroupList eg: graphs.get(t).groupLists) {
                clearSelectItemsGraphWalker(eg);
            }
            clearSelectItemsGraphWalker(graphs.get(t).departmentGraph);
        }
    }
    
    /** 
     * responds to an undo/redo event.  
     * @param items List:items/Momento
     */
    private static void doUndoRedo(List items) {
        
        clearSelectItems();
        
        if (items != null) {
            
            for(Object e: items) {
                MomentoInterface mo = (MomentoInterface)e;
                MeetingModel prev = (MeetingModel)mo.getObject();
                GraphController g = getController(prev.getSemester());
                
                if (g == null)
                    continue;
                
                MeetingModel now = g.getMeetingModel(prev.getModelID());
                MeetingModel temp = (MeetingModel)now.copy();

                if (now != null && prev != null) {                            
                    if (mo.getMomentoType() == MomentoType.ATTRIBUTE_CHANGE) {
                        if (prev.getOnDay() != now.getOnDay() || 
                            prev.getStartTime() != now.getStartTime()) {
                            
                            now.set(prev);
                            g.removeEventItemForMeeting(now);
                            
                            g.addEventItemForMeeting(now);
                        }
                    } else if (mo.getMomentoType() == MomentoType.NEW_ENTITY) {
                        now.flagForRemoval();
                        g.removeEventItemForMeeting(now);
                        
                    } else if (mo.getMomentoType() == MomentoType.REMOVED_ENTITY) {
                        now.flagForInsert();
                        g.removeEventItemForMeeting(now);
                        g.addEventItemForMeeting(now);
                    }
                }
                mo.updateCopy(temp);
            }
        }
    }
    
    private void addEventItemForMeeting(MeetingModel now) {
        for(EventGroupList eg: groupLists) {
            eg.<RoomItem>roomWalker(
                new GraphWalker<RoomItem, MeetingItem>() {
                    @Override public boolean walkIinR(
                            EventGroupList gl, 
                            MeetingItem m, 
                            RoomItem r) {
                        if (r.getForRoom() != null && r.getForRoom().getModelID() == now.getRoom()) {
                            r.addEvent(now);
                        } 
                        return false;
                    }
                }
            );
        } 
        if (departmentGraph != null)
            departmentGraph.addMeeting(now);
        //if (departmentGraph != null) {
        //    departmentGraph.addMeetingItem(now);
        //}
    }
    
    private void removeEventItemForMeeting(MeetingModel now) {
        
        ArrayList<MeetingItem> forRemoval = new ArrayList<>();
        
        for(EventGroupList eg: groupLists) {
            
            eg.<RoomItem, MeetingItem>eventWalker( 
                    new GraphWalker<RoomItem, MeetingItem>() {
                       
                    @Override public boolean walkIinR(
                            EventGroupList gl, 
                            MeetingItem m, 
                            RoomItem r) {
                            if (m.getMeetingModel().getModelID() == now.getModelID())
                                forRemoval.add(m);
                            m.setSelected(false);
                        return false;
                    }
                }
            );
        }
        if (departmentGraph != null)
            departmentGraph.removeMeeting(now);
        removeMeetings(forRemoval);
    }
    
    private static GraphController getController(int termID) {
        for(TermModel t: graphs.keySet()) {
            if (t.getModelID() == termID) {
                return graphs.get(t);
            }
        }
        return null;
    }
    
    private MeetingModel getMeetingModel(int modelID) {
        for(MeetingModel m: termModel.getMeetings()) {
            if (m.getModelID() == modelID) {
                return m;
            }
        }
        return null;
    }
    
    static class EventDrag extends Popup {
        
        private MeetingItem targetMeeting;
        private double offsetX, offsetY;
        
        private final Pane root = new Pane();
        private boolean forNewItem = false; 
        
        EventDrag() { 
            getScene().setRoot(root);
        }
        
        boolean beginDrag(
            MouseEvent e, 
            MeetingItem target, 
            List<MeetingItem> nodes, 
            boolean shiftDown
        ) {
            forNewItem = shiftDown;
            root.getChildren().clear();
            targetMeeting = target;
            
            cancelDrag();
        
            if (target == null || nodes == null)
                return false;
            
            if (shiftDown && target.getMeetingModel().getModelID() <= 0) {
                OutputBox.err("Can't split a duplicate.");
                return false;
            }
            
            SnapshotParameters sParams = new SnapshotParameters();
            root.getChildren().clear();

            Bounds t = target.localToScreen(target.getBoundsInLocal());

            offsetX = e.getScreenX() - t.getMinX();
            offsetY = e.getScreenY() - t.getMinY();

            Bounds q = null;
            
            for(MeetingItem n: nodes) {

                if (shiftDown) 
                    if (n != target)
                        continue;
                Bounds b = n.localToScreen(n.getBoundsInLocal());

                if (q == null) 
                    q = n.localToScreen(n.getBoundsInLocal());

                double dx = b.getMinX() - q.getMinX(),
                       dy = b.getMinY() - q.getMinY();

                ImageView imgView = new ImageView(n.snapshot(sParams, null));

                imgView.setLayoutX(dx);
                imgView.setLayoutY(dy);

                if (n == target) {
                    offsetX += dx;
                    offsetY += dy;
                }
                root.getChildren().add(imgView);
                if (e.isControlDown()) 
                    break;
            }
            
            if (shiftDown) {
                ImageView iv = new ImageView(getCopyImage());
                iv.setX(-8);
                iv.setY(-8);

                root.getChildren().add(iv);

            }
        
            show(target, e.getScreenX(), e.getScreenY());
            
            return true;
        }
        
        void doDrag(MouseEvent e) {

            if (isShowing()) {

                double tX = (e.getScreenX() - offsetX);
                double tY = (e.getScreenY() - offsetY);

                Node n = WidgetHelpers.getNode(e, RoomItem.class );

                if (n != null) {
                    RoomItem nd = (RoomItem)n;
                    if (nd.overDay(e) != -1)
                        tX -= nd.getMouseHints(e, offsetX);
                }

                setX(tX);
                setY(tY);
            }
        }
        
        void endDrag(
            MouseEvent e, 
            MeetingItem target,
            List<MeetingItem> nodes) {// throws Exception {
        
            if (cancelDrag()) {
                
                double tX = (e.getScreenX() - offsetX);
                double tY = (e.getScreenY() - offsetY);

                Node n = WidgetHelpers.getNode(e, RoomItem.class );

                if (n != null) {
                    RoomItem nd = (RoomItem)n;
                    int overDay = nd.overDay(e);
                    if (overDay != -1) {
                        tX -= nd.getMouseHints(e, offsetX);
                        nd.dropEvents(
                            target, 
                            nodes, 
                            overDay, 
                            nd.getOverTime(e, offsetX), 
                            forNewItem
                        );
                    }
                }
            }
        }
        
        boolean cancelDrag() {
            boolean result = false;
            result = isShowing();
            hide();
            return result;
        }
    } 
       
    private static Image copyImage = null;
    
    private static Image getCopyImage() {
        if (copyImage == null) {
            copyImage = new Image(GraphController.class.getResourceAsStream("/resources/expand.png"));
        }
        return copyImage;
    }
}

