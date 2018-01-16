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
package ucss.ui.widgets;

/**
 *
 * @author John
 */
public enum Weekdays {
    MON(0), 
    TUE(1),
    WED(2),
    THU(3),
    FRI(4),
    SAT(5),
    SUN(6);
    
    private final int index;
    
    private Weekdays(int value) {
        index = value;
    }
    
    public boolean isEven() {
        return (index % 2) == 0;
    }
    
    public int getIndex() {
        return index;
    }
    
    
}
