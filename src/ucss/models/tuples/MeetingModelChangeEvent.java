/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.tuples;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author John
 */
public class MeetingModelChangeEvent extends Event {

    private final int oldValue;
    private final MeetingModelChange attributeChanged;
    public static final EventType<MeetingModelChangeEvent> MEETING_VALUE_CHANGE =
            new EventType<MeetingModelChangeEvent>(Event.ANY, "MEETING_VALUE_CHANGE");

    public static final EventType<MeetingModelChangeEvent> ANY = MEETING_VALUE_CHANGE;

    public MeetingModelChangeEvent(
            MeetingModel source, 
            MeetingModelChange changed,
            int value
    ) {
        super(source, null, MEETING_VALUE_CHANGE);
        oldValue = value;
        attributeChanged = changed;
    }
    
    public final MeetingModelChange getAttributeChanged() {
        return attributeChanged;
    }
    
    public int getOldValue() {
        return oldValue;
    }
    
    public int getNewValue() {
        int result = -1;
        switch (attributeChanged) {
            case ROOM:
                result = getMeetingModel().getRoom();
                break;
            case COURSE:
                result = getMeetingModel().getCourse();
                break;
            case SEMESTER:
                result = getMeetingModel().getSemester();
                break;
            case PROFESSOR:
                result = getMeetingModel().getProfessor();
                break;
            case SECTION:
                result = getMeetingModel().getSection();
                break;
            case ONDAY:
                result = getMeetingModel().getOnDay();
                break;
            case STARTTIME:
                result = getMeetingModel().getStartTime();
                break;
            case DURATION:
                result = getMeetingModel().getDuration();
                break;
            default:
                throw new AssertionError(attributeChanged.name());
            
        }
        return result;
    }
    
    public final MeetingModel getMeetingModel() {
        return (MeetingModel)super.getSource();
    }
    
    @Override
    public MeetingModelChangeEvent copyFor(Object newSource, EventTarget newTarget) {
        return (MeetingModelChangeEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends MeetingModelChangeEvent> getEventType() {
        return (EventType<? extends MeetingModelChangeEvent>) super.getEventType();
    }



}
