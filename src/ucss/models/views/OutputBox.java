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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author John
 */
public class OutputBox {
    
    private static ObservableList<String> messages = FXCollections.observableArrayList();
    
    private OutputBox() {
        
    }
    
    public static final void print(String value) {
        messages.add(value);
    }
    
    public static final void println(String value) {
        messages.add(value);
    }
    
    public static final void err(String value) {
        messages.add("\b" + value);
    }
    
    
    public static final ObservableList<String> getItems() {
        return messages;
    }

    
}
