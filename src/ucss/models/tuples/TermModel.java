
package ucss.models.tuples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author John
 */
public class TermModel extends Model {
    
    private static int autoID = -1;
    private String 
        title = "", 
        startDate = "",
        endDate = "";
    
    public static TermModel empty() {
        return new TermModel(0);                                        
    }
    
    public TermModel(int id) {
        super(id);
    }
    
    public TermModel(String title) {
        super((autoID-=1));
        setTitle(title);
        super.add(this);
    }
    
    public TermModel(
            int id,
            String title,
            String startDate,
            String endDate
        ) {
        super(id);
        setTitle(title);
        setStartDate(startDate);
        setEndDate(endDate);
    }
    
    public TermModel(ResultSet rs) throws SQLException, Exception {
        setModelID(rs.getInt("semesterID"));
        setTitle(rs.getString("title"));
        setStartDate(rs.getString("startDate"));
        setEndDate(rs.getString("endDate"));
        add(this);
    }
    
    public final void setTitle(String value) { 
        if (value != null)
            title = value;
        else
            title = "";
    }
    
    public final String getTitle() {
        return title;
    }
    
    public final void setStartDate(String value) { 
        if (value != null)
            startDate = value;
        else
            startDate = "";
    }
    
    public final String getStartDate() {
        return startDate;
    }
    
    public final void setEndDate(String value) { 
        if (value != null)
            endDate = value;
        else
            endDate = "";
    }
    
    public final String getEndDate() {
        return endDate;
    }

    public ArrayList<MeetingModel> meetings;
    
    public final ArrayList<MeetingModel> getMeetings() {
        if (meetings == null || meetings.isEmpty()) {
            meetings = new ArrayList();
            initialMeetingsRequest.set(TermModel.this);
        }
        return meetings;
    }
    
    @Override public String toString() {
        return title;
    }
}
