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
package ucss.models.tuples.momentos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.event.ActionEvent;
import static javafx.event.ActionEvent.ACTION;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 *
 * @author John
 */
public class MomentoEvent<E extends MomentoInterface> extends Event {
    
    
    public static final EventType<MomentoEvent> MOMENTO_ACTION =
            new EventType<MomentoEvent>(Event.ANY, "MOMENTO_ACTION");
    
    public static final EventType<MomentoEvent> ANY = MOMENTO_ACTION;
    
    private final Momento source;
    private final int undoSize; 
    private final int redoSize;
    private final List<E> target;
    
    
    public MomentoEvent(Momento source) {
        super(MOMENTO_ACTION);
        this.source = source;
        this.undoSize = 0;
        this.redoSize = 0;
        this.target = null;
    }
    
    public MomentoEvent(Momento source, List<E> target, int undoSize, int redoSize) {
        super(source, null, MOMENTO_ACTION);
        this.source = source;
        this.undoSize = undoSize;
        this.redoSize = redoSize;
        this.target = new ArrayList( target );
    }
    
    public MomentoEvent(Momento source, E target, int undoSize, int redoSize) {
        super(source, null, MOMENTO_ACTION);
        this.source = source;
        this.undoSize = undoSize;
        this.redoSize = redoSize;
        this.target = new ArrayList( );
        this.target.add(target);
    }
    
     @Override
    public EventType<? extends MomentoEvent> getEventType() {
        return (EventType<? extends MomentoEvent>) super.getEventType();
    }
    
    public final Momento getMomento() {
        return source;
    }
    
    public final int getRedoSize() {
        return redoSize;
    }
    
    public final int getUndoSize() {
        return undoSize;
    }
    
    public final boolean isRedoEmpty() {
        return (redoSize <= 0);
    }
    
    public final boolean isUndoEmpty() {
        return (undoSize <= 0);
    }
    
    public final List<E> getMomentoInterfaceTarget() {
        return target;
    }
    
    
}
