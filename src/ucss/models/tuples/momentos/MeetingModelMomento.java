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

import ucss.models.tuples.MeetingModel;
import ucss.models.tuples.Model;

/**
 *
 * @author John
 */
public class MeetingModelMomento implements MomentoInterface {
    
    private static int sequenceCounter = 0;

    public static MeetingModelMomento generateMomento(MeetingModel meetingModel, MomentoType mType) {
        
        return new MeetingModelMomento(
            mType, meetingModel
        );
    }
    
    public static MeetingModelMomento generateMomento(MeetingModel meetingModel) {
        
        return new MeetingModelMomento(
            //meetingModel.getModelID() == 0?
            //MomentoType.NEW_ENTITY:MomentoType.ATTRIBUTE_CHANGE,
            MomentoType.ATTRIBUTE_CHANGE,
            meetingModel
        );
    }
    
    private final int sequenceID;
    private final MeetingModel copyOfMeetingModel;
    private MomentoType momentoType;
    
    MeetingModelMomento(MomentoType momentoType, MeetingModel sourceModel) {
    
        this.sequenceID = sequenceCounter;
        this.copyOfMeetingModel = (MeetingModel) sourceModel.copy();
        this.momentoType = momentoType;
        Model.getMeetingModelMomento().add(MeetingModelMomento.this);
        
    }
    
    public static int getSequnceCounter() {
        return (++sequenceCounter);
    }
    public final MeetingModel getMomentoObject() {
        return copyOfMeetingModel;
    }
    
    @Override public int getSequenceID() {
        return sequenceID;
    }
    
    @Override public Model getObject() {
        return copyOfMeetingModel;
    }
    
    @Override public MomentoType getMomentoType() {
        return momentoType;
    }
   
    @Override public void updateCopy(Model source) {
        if (source != null) {
            
            if (momentoType == MomentoType.NEW_ENTITY)
                momentoType = MomentoType.REMOVED_ENTITY;
            else if (momentoType == MomentoType.REMOVED_ENTITY)
                momentoType = MomentoType.NEW_ENTITY;
            
            copyOfMeetingModel.set(source);
        }
    }

    @Override
    public void setMomentoType(MomentoType momentoType) {
        this.momentoType = momentoType;
    }
    
}
