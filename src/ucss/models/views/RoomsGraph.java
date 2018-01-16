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
package ucss.models.views;

import ucss.models.tuples.*;
import ucss.models.tuples.momentos.*;
import ucss.ui.widgets.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


/**
 *
 * @author John
 */
public class RoomsGraph extends EventGroupList {
    
    
    private final TermModel forTerm;
    
    public final TermModel getForTerm() {
        return forTerm;
    }
    
    public RoomsGraph(BuildingModel building, TermModel term) {
        super();
        forTerm = term;
    }
    
    private void populateUsingBuilding(BuildingModel building) {
        
    }
}