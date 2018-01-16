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
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBase;

/**
 *
 * @author John
 * @param <E>
 */
public class Momento<E extends MomentoInterface> {
    
    private final List<E> undo = new ArrayList<>();
    
    private final List<E> redo = new ArrayList<>();
    
    public Momento() { }
    
    public final void add(E momento) {
        
        redo.clear();
        
        if (undo.add(momento)) {
            if (getOnAddedMomento() != null)
                getOnAddedMomento().handle(new MomentoEvent(Momento.this, momento, undo.size(), redo.size()));
        }
        
        if (getOnAction() != null)
            getOnAction().handle(new ActionEvent());
    }
    
    public final int undo() {
        
        int result = 0;
        
        int id = -1;
        
        if (!undo.isEmpty()) {
            List<E> list = new ArrayList();
            
            for(int i = undo.size()-1; i >= 0; i--) {
                E t = undo.get(i);
                if (id == -1)
                    id = t.getSequenceID();
                
                if (t.getSequenceID() != id) {
                    break;
                } else {
                    list.add(t);
                    redo.add(t);
                    undo.remove(t);
                }
            }
            
            if (!list.isEmpty()) {
                if (getOnUndo() != null )
                    getOnUndo().handle(new MomentoEvent(Momento.this, list, undo.size(), redo.size()));
                list.clear();
            }
            if (getOnAction() != null)
                getOnAction().handle(new ActionEvent());
            
        }
        
        return result;
    }
    
    public final int redo() {
        
        int result = 0;
        
        int id = -1;
        
        if (!redo.isEmpty()) {
            List<E> list = new ArrayList();
            
            for(int i = redo.size()-1; i >= 0; i--) {
                E t = redo.get(i);
                if (id == -1)
                    id = t.getSequenceID();
                
                if (t.getSequenceID() != id) {
                    break;
                } else {
                    list.add(t);
                    undo.add(t);
                    redo.remove(t);
                }
            }
            
            if (!list.isEmpty()) {
                if (getOnRedo() != null ) {
                    getOnRedo().handle(new MomentoEvent(Momento.this, list, undo.size(), redo.size()));
                }
                list.clear();
            }
            if (getOnAction() != null)
                getOnAction().handle(new ActionEvent());
            
        }
        
        return result;
    }
    
    public final void clear() {
        
        redo.clear();
        undo.clear();
        
        if (this.getOnReset() != null) 
            getOnReset().handle(new MomentoEvent(Momento.this));
    }
    
    public final ObjectProperty<EventHandler<MomentoEvent>> onAddedMomentoProperty() { return onAddedMomento; }
    public final void setOnAddedMomento(EventHandler<MomentoEvent> value) { onAddedMomentoProperty().set(value); }
    public final EventHandler<MomentoEvent> getOnAddedMomento() { return onAddedMomentoProperty().get(); }
    private final ObjectProperty<EventHandler<MomentoEvent>> onAddedMomento = new ObjectPropertyBase<EventHandler<MomentoEvent>>() {
        @Override protected void invalidated() {
            //setEventHandler(MomentoEvent.MOMENTO_ACTION, get());
        }

        @Override
        public Object getBean() {
            return Momento.this;
        }

        @Override
        public String getName() {
            return "onAddedMomento";
        }
    };
    
    public final ObjectProperty<EventHandler<MomentoEvent>> onUndoMomentoProperty() { return onUndoMomento; }
    public final void setOnUndo(EventHandler<MomentoEvent> value) { onUndoMomentoProperty().set(value); }
    public final EventHandler<MomentoEvent> getOnUndo() { return onUndoMomentoProperty().get(); }
    private final ObjectProperty<EventHandler<MomentoEvent>> onUndoMomento = new ObjectPropertyBase<EventHandler<MomentoEvent>>() {
        @Override protected void invalidated() {
            //setEventHandler(MomentoEvent.MOMENTO_ACTION, get());
        }

        @Override
        public Object getBean() {
            return Momento.this;
        }

        @Override
        public String getName() {
            return "onUndoMomento";
        }
    };
    
    
    public final ObjectProperty<EventHandler<MomentoEvent>> onRedoMomentoProperty() { return onRedoMomento; }
    public final void setOnRedo(EventHandler<MomentoEvent> value) { onRedoMomentoProperty().set(value); }
    public final EventHandler<MomentoEvent> getOnRedo() { return onRedoMomentoProperty().get(); }
    private final ObjectProperty<EventHandler<MomentoEvent>> onRedoMomento = new ObjectPropertyBase<EventHandler<MomentoEvent>>() {
        @Override protected void invalidated() {
            //setEventHandler(MomentoEvent.MOMENTO_ACTION, get());
        }

        @Override
        public Object getBean() {
            return Momento.this;
        }

        @Override
        public String getName() {
            return "onRedoMomento";
        }
    };
    
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
    public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
    public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
        @Override protected void invalidated() {
            //setEventHandler(ActionEvent.ACTION, get());
        }

        @Override
        public Object getBean() {
            return Momento.this;
        }

        @Override
        public String getName() {
            return "onAction";
        }
    };
    public final ObjectProperty<EventHandler<MomentoEvent>> onResetMomentoProperty() { return onResetMomento; }
    public final void setOnReset(EventHandler<MomentoEvent> value) { onResetMomentoProperty().set(value); }
    public final EventHandler<MomentoEvent> getOnReset() { return onResetMomentoProperty().get(); }
    private final ObjectProperty<EventHandler<MomentoEvent>> onResetMomento = new ObjectPropertyBase<EventHandler<MomentoEvent>>() {
        @Override protected void invalidated() {
            //setEventHandler(MomentoEvent.MOMENTO_ACTION, get());
        }

        @Override
        public Object getBean() {
            return Momento.this;
        }

        @Override
        public String getName() {
            return "onResetMomento";
        }
    };

    public boolean hasUndo() {
        return !undo.isEmpty();
    }

    public boolean hasRedo() {
        return !redo.isEmpty();
    }
    
    public final String getLastUndo() {
        if (!undo.isEmpty()) {
            return undo.get(undo.size()-1).getMomentoType().toString();
        } else {
            return "empty";
        }
    }
    
    public final String getLastRedo() {
        if (!redo.isEmpty()) {
            return redo.get(redo.size()-1).getMomentoType().toString();
        } else {
            return "empty";
        }
    }
    
}
