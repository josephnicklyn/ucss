/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.event.Event;
import javafx.event.EventType;

/**
 *
 * @author John
 */
public class CheckListSelectedEvent extends Event {

    public static final EventType<CheckListSelectedEvent> SELECTED =
            new EventType<CheckListSelectedEvent>(Event.ANY, "SELECTED");

    public static final EventType<CheckListSelectedEvent> ANY = SELECTED;
    public final boolean selected;
    public final Object object;
    
    public CheckListSelectedEvent(Object source, boolean selected) {
        super(SELECTED);
        this.selected = selected;
        this.object = source;
    }
    
    public final Object getSelectedObject() {
        return object;
    }
    
    public final boolean isSelected() {
        return selected;
    }
    
    @Override
    public EventType<? extends CheckListSelectedEvent> getEventType() {
        return (EventType<? extends CheckListSelectedEvent>) super.getEventType();
    }



}