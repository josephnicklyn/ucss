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
public class MouseOverTime {
    
    private int overDay;
    private int overTime;
    private double x, y;
    
    private double granularX, granularY;
    
    public MouseOverTime(double gX, double gY, double x, double y, int day, int time) {
        
        this.x = x;
        this.y = y;
        
        this.granularX = gX;
        this.granularY = gY;
        
        this.overDay = day;
        this.overTime = time;
        
    }
    
     public MouseOverTime(double gX, double gY, int day, int time) {
        
        this.granularX = gX;
        this.granularY = gY;
        
        this.overDay = day;
        this.overTime = time;
    }
    
    
    public int getOverDay() {
        return overDay;
    }
    
    public int getOverTime() {
        return overTime;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getGranularX() {
        return granularX;
    }
    
    public double getGranularY() {
        return granularY;
    }
    
    
    @Override public String toString() {
       
        return "MouseOverTime [day = " + getOverDay()   + 
                ", time = " + getOverTime()             +
                ", granularX = " + getGranularX()       +
                ", granularY = " + getGranularY()       +
                "]";
    }
    
}
