/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.tuples;

import javafx.geometry.HPos;
import javafx.geometry.VPos;

/**
 *
 * @author John
 */
public class PrettyPrint {
    
    private static String pad(
            HPos a, 
            String c, 
            int w, 
            char p,
            char s, 
            char e
    ) {
    
        StringBuilder b = new StringBuilder();
        
        if (s != 0) {
            b.append(s);
        }
        
        if (null != a) switch (a) {
            case LEFT:
                b.append(c);
                w-=c.length();
                break;
            case CENTER:
                int hl = (w-c.length()) / 2;
                w-=c.length();
                while(hl > 0) {
                    b.append(p);
                    w--;
                    hl--;
                }   b.append(c);
                break;
            case RIGHT:
                w-=c.length();
                while(w > 0) {
                    b.append(p);
                    w--;
                }   b.append(c);
                break;
            default:
                break;
        }
        
        while(w > 0) {
            b.append(p);
            w--;
        }
        
        if (e != 0) {
            b.append(e);
        }
        
        return b.toString();
        
    }
    
    public static String getBar(int hdr, int... parts) {
        StringBuilder b = new StringBuilder();
        if (hdr > 0) 
            b.append(pad(HPos.LEFT, "", hdr, '-', '+', '+'));
        else
            b.append("+");
        
        for(int part: parts) {
            b.append(pad(HPos.LEFT, "", part, '-', '\0', '+'));
        }
        
        b.append("\n");
        return b.toString();
    }
    
    public static String prettyPrint(boolean includeIndex, String title, String [][] r) {
        
        int tabLength = includeIndex?8:0;
        if (r != null) {
            
            int[] widths = new int[r.length];
            int totalWidth = includeIndex?tabLength:tabLength-1;
            for(int i = 0; i < r.length; i++) {
                int w = 0;
                for(int j = 0; j < r[i].length; j++) {
                    int p = r[i][j].length();
                    if (p > w)
                        w = p;
                }
                widths[i] = w + 2;
                totalWidth+=widths[i]+1;
            }
            
            StringBuilder b = new StringBuilder();
            String bar = getBar(tabLength, widths);
            String xBar = pad(HPos.LEFT, "", totalWidth, '-', '+', '+') + "\n" ;
            b.append(xBar);
            b.append(pad(HPos.CENTER, title, totalWidth, ' ', '|', '|')).append("\n");                
            
            b.append(bar);
            if (includeIndex)
                b.append(pad(HPos.LEFT, "", tabLength, ' ', '|', '|'));
            else
                b.append("|");
            
            for(int i = 0; i < r.length; i++) {
                int w = widths[i];
                b.append(pad(HPos.CENTER, r[i][0] ,w , ' ','\0' ,'|'));    
            }
            b.append("\n");
            b.append(bar);
            
            for(int k = 1;k < r[0].length;k++) {
                if (includeIndex)
                    b.append(pad(HPos.LEFT, String.valueOf(k), tabLength, ' ', '|', '|'));
                else
                    b.append("|");
                for(int i = 0; i < r.length; i++) {
                    int w = widths[i];
                    b.append(pad(HPos.LEFT, ' ' + r[i][k] ,w , ' ','\0' ,'|'));    
                }
                b.append("\n");    
            }
            b.append(bar);
            
            return b.toString();
        } else {
            return "EMPTY";
        }
    }
    
    public static String prettyPrintBuildings() {
        
        String[][] r = new String[3][Model.getBuildings().size()+1];
        r[0][0] = "ID";
        r[1][0] = "Building Code";
        r[2][0] = "Building Name";
        
        int p = 1;
        
        for(BuildingModel b: Model.getBuildings()) {
            r[0][p] = String.valueOf(b.getModelID());
            r[1][p] = b.getBuildingCode();
            r[2][p] = b.getBuildingName();
            p++;
        }
        return prettyPrint(false, "BUILDINGS", r);
    }
    
    public static String prettyPrintColleges() {
        
        String[][] r = new String[2][Model.getColleges().size()+1];
        r[0][0] = "ID";
        r[1][0] = "CollegeName";
        
        int p = 1;
        
        for(CollegeModel b: Model.getColleges()) {
            r[0][p] = String.valueOf(b.getModelID());
            r[1][p] = b.getCollegeName();
            p++;
        }
        return prettyPrint(false, "COLLEGES", r);
    }
    
    public static String prettyPrintDepartments() {
        
        String[][] r = new String[10][Model.getDepartments().size()+1];
        r[0][0] = "ID";
        r[1][0] = "DepartmentName";
        r[2][0] = "Description";
        
        int p = 1;
        
        for(DepartmentModel b: Model.getDepartments()) {
            r[0][p] = String.valueOf(b.getModelID());
            r[1][p] = b.getDepartmentName();
            r[2][p] = b.getDescription();
            p++;
        }
        return prettyPrint(false, "DEPARTMENTS", r);
    }
    
    public static String prettyPrintCourses() {
        
        String[][] r = new String[10][Model.getCourses().size()+1];
        r[0][0] = "ID";
        r[1][0] = "department";
        r[2][0] = "credits";
        r[3][0] = "courseNumber";
        r[4][0] = "courseTitle";
        r[5][0] = "options";
        r[6][0] = "labType";
        r[7][0] = "prerequisites";
        r[8][0] = "Hints";
        r[9][0] = "courseDescription";
        
        int p = 1;
        
        char[] ops = {' ', ' ', ' ', ' ', ' ', ' '};
        for(CourseModel b: Model.getCourses()) {
            r[0][p] = String.valueOf(b.getModelID());
            
            r[1][p] = Model.getDefaultValue(
                    b.getDepartment(), 
                    Model.getDepartment(b.getDepartment())
            );
            
            r[2][p] = String.valueOf(b.getCredits());
            r[3][p] = b.getCourseNumber();
            r[4][p] = b.getCourseTitle();
            
            ops[0] = b.getHasWhiteboard()?'w':'-';
            ops[1] = b.getHasChalkboard()?'c':'-';
            ops[2] = b.getHasProjector()?'p':'-';
            ops[3] = b.getHasTieredSeating()?'t':'-';
            ops[4] = b.getHasMoveableSeating()?'m':'-';
            ops[5] = b.getHasWindows()?'w':'-';
            
            r[5][p] = String.valueOf(ops, 0, 5);
            r[6][p] = Model.getDefaultValue(
                    b.getDepartment(), 
                    Model.getLab(b.getLabType())
            );
            r[7][p] = b.getPrerequisites();
            r[8][p] = String.format("%d/%d", b.getMeetingsPerWeekHint(), b.getMeetingsPerWeekAreLabHints());
            
            r[9][p] = b.getCourseDescription();
            
            p++;
        }
        return prettyPrint(false, "COURSES", r);
    }
    
}
