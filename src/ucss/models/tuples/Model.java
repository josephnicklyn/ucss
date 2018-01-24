/*
 * This module is apart of the UCSS-Course Management System
 * 
 * Copyright (C) 2017  Joseph Nicklyn
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ucss.models.tuples;

import java.sql.Connection;
import ucss.models.tuples.momentos.MeetingModelMomento;
import ucss.models.tuples.momentos.Momento;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 * This is the root for all models (a model is a JAVA representation of a tuple
 * from a database table. In addition to providing abstraction for the various
 * models for this project, this class also automatically maintains a list of 
 * the various models. These lists are static, and can be accessed using 
 * getter methods.  A list will request initialization upon creation,
 * which is delegated to a listener of the initialRequest property.
 * 
 * @author Joseph Nicklyn JR.
 */
public abstract class Model {

    /**
     * Member variables
     */
    
    /** 
     * a unique id for this model, each table should have a unique id. 
     * - for this implementation a value larger than zero means the object
     *   was located in the database, a zero (0) is used for temporary 
     *   objects but not stored, and a negative value is used to give a 
     *   unique value to identify a model, but it has not been saved to a 
     *   database, this doubles as an INSERT flag.
     */
    protected int modelID;
    
    protected boolean flaggedForRemoval = false;
    
    protected boolean flaggedForUpdate = false;
    
    protected boolean flaggedForInsert = false;
    
    /**
     * Constructors
     */
    
    /**
     * An empty constructor, this constructor is only accessible from within 
     * this package.  This is intended to be called from a ResultSet creation
     * constructor from the concrete model.
     */
    Model() {
        
    }
    
    /** 
     * a constructor defining the model id.
     * 
     * @param id Integer
     */
    public Model(int id) {
        modelID = id;
    }
    
    public final void flagForRemoval() {
        flagForRemoval(true);
    }
    
    public final void flagForRemoval(boolean value) {
        flaggedForInsert = false;
        flaggedForRemoval = value;
    }
    
    public final boolean toggleForRemovalFlag() {
        flaggedForRemoval = !flaggedForRemoval;
        flaggedForInsert = false;
        return flaggedForRemoval;
    }
    
    public final void flagForInsert() {
        flagForInsert(true);
    }
    
    public final void flagForInsert(boolean value) {
        flaggedForRemoval = false;
        flaggedForInsert = value;
    }
    
    public final boolean toggleForInsertFlag() {
        flaggedForRemoval = false;
        flaggedForInsert = !flaggedForInsert;
        return flaggedForInsert;
    }
    
    
    public final void flagForUpdate() {
        flaggedForUpdate = true;
    }
    
    public final void clearFlags() {
        flaggedForRemoval = false;
        flaggedForUpdate = false;
        flaggedForInsert = false;
    }
    
    public final boolean isFlaggedForUpdate() {
        return flaggedForUpdate;
    }
    
    public final boolean isFlaggedForRemoval() {
        return flaggedForRemoval;
    }
    
    
    
    public final boolean isFlaggedForInsertion() {
        return (getModelID() < 0 || flaggedForInsert);
    }
    
    public final boolean requiresDatabaseAction() {
        return (
            isFlaggedForUpdate()    ||
            isFlaggedForRemoval()   ||   
            isFlaggedForInsertion() 
        );
    }
    
    public String getDatabaseActionString() { return ""; }
    
    public void executeDatabaseAction(Connection conn) throws Exception  {  }
    
    private static int decrementedID = 0;
    protected static final int getDecrementedID() {
        return --decrementedID;
    }
    
    //public Model copyAndStore() throws Exception { return null; }
    
    /** 
     * sets the model id. 
     * @param value Integer
     */
    final void setModelID(int value) {
        modelID = value;
    }
    
    /** 
     * gets the model id.
     * @return Integer
     */
    public final int getModelID() {
        return modelID;
    }
    
    public Model copy() { return this; }
    
    public void set(Model m) { }
    
    public String getString() { return "Model"; }
    
    
    /*************************************************************************
     * BEGIN STATIC METHODS
     ************************************************************************/
    
    /**
     * Model lists.
     */
    private static ArrayList<BuildingModel> buildings;
    private static ArrayList<CollegeModel> colleges;
    private static ArrayList<CourseModel> courses;
    private static ArrayList<DepartmentModel> departments;
    private static ArrayList<LabModel> labs;
    private static ArrayList<ProfessorModel> professors;
    private static ArrayList<RoomModel> rooms;
    
    static final SimpleObjectProperty<TermModel> initialMeetingsRequest =
            new SimpleObjectProperty(null);
    
    public static void setOnInitialMeetingsRequest(ChangeListener<TermModel> listener) {
        initialMeetingsRequest.addListener(listener);
    }
    
    /**
     * provides access to an initialization request of a model list.
     */
    private static final SimpleObjectProperty<ProjectModels> initialRequest =
            new SimpleObjectProperty(null);
    
    /**
     * Adds a listener to the initialRequest property.
     * 
     * @param listener ChangeListener
     */
    public static void setOnInitialRequest(ChangeListener<ProjectModels> listener) {
        initialRequest.addListener(listener);
    }
    
    public static ArrayList<BuildingModel> getBuildings() {
        if (buildings == null) {
            buildings = new ArrayList();
            initialRequest.set(ProjectModels.BUILDING_MODEL);
        }
        return buildings;
    }
    
    public static ArrayList<CollegeModel> getColleges() {
        if (colleges == null) {
            colleges = new ArrayList();
            initialRequest.set(ProjectModels.COLLEGE_MODEL);
        }
        return colleges;
    }
    
    public static ArrayList<CourseModel> getCourses() {
        if (courses == null) {
            courses = new ArrayList();
            initialRequest.set(ProjectModels.COURSE_MODEL);
        }
        return courses;
    }
    
    public static ArrayList<DepartmentModel> getDepartments() {
        if (departments == null) {
            departments = new ArrayList();
            initialRequest.set(ProjectModels.DEPARTMENT_MODEL);
        }
        return departments;
    }
    
    public static ArrayList<LabModel> getLabs() {
        if (labs == null) {
            labs = new ArrayList();
            initialRequest.set(ProjectModels.LAB_MODEL);
        }
        return labs;
    }
   
    public static ArrayList<ProfessorModel> getProfessors() {
        if (professors == null) {
            professors = new ArrayList();
            initialRequest.set(ProjectModels.PROFESSOR_MODEL);
        }
        return professors;
    }
    
    public static ArrayList<RoomModel> getRooms() {
        if (rooms == null) {
            rooms = new ArrayList();
            initialRequest.set(ProjectModels.ROOM_MODEL);
        }
        return rooms;
    }
    
    private static HashMap<Integer, TermModel> terms;
    public static HashMap<Integer, TermModel> getTerms() {
        if (terms == null) {
            terms = new HashMap<>();
            initialRequest.set(ProjectModels.TERM_MODEL);
        }
        return terms;
    }
    
    /**
     * gets the building with the buildingID [id].
     * @param id integer
     * @return TermModel
     */
    public static TermModel getTerm(int id) {
        return getTerms().get(id);
    }
    
   
    public static void reset() {
        
        getMeetingModelMomento().clear();
        
        departmentMap.clear();
        professorMap.clear();
        labMap.clear();
        roomMap.clear();
        
        buildingMap.clear();
        
        initialRequest.set(ProjectModels.EMPTY);
        initialMeetingsRequest.set(TermModel.empty());
        
        
        if (buildings != null)      buildings.clear();      buildings = null;
        if (colleges != null)       colleges.clear();       colleges = null;
        if (courses != null)        courses.clear();        courses = null;     
        if (departments != null)    departments.clear();    departments = null;
        if (labs != null)           labs.clear();           labs = null;
      
        if (professors != null)     professors.clear();     professors = null;
        if (rooms != null)          rooms.clear();          rooms = null;
        
        if (terms != null)          terms.clear();          terms = null;
        
    }
    
    static void add(Model model) {
        
        int hash = (model.getClass().hashCode());
        
        if (hash == BuildingModel.class.hashCode()) {
            getBuildings().add((BuildingModel)model);
        } else if (hash == CollegeModel.class.hashCode()) {
            getColleges().add((CollegeModel)model);
        } else if (hash == CourseModel.class.hashCode()) {
            getCourses().add((CourseModel)model);
        } else if (hash == DepartmentModel.class.hashCode()) {
            getDepartments().add((DepartmentModel)model);
        } else if (hash == LabModel.class.hashCode()) {
            getLabs().add((LabModel)model);
        } else if (hash == MeetingModel.class.hashCode()) {
            TermModel tm = getTerms().get(((MeetingModel)model).getSemester());
            if (tm != null) {
                tm.getMeetings().add((MeetingModel)model);
            }
        } else if (hash == ProfessorModel.class.hashCode()) {
            getProfessors().add((ProfessorModel)model);
        } else if (hash == RoomModel.class.hashCode()) {
            getRooms().add((RoomModel)model);
        } else if (hash == TermModel.class.hashCode()) {
            getTerms().put(model.getModelID(), (TermModel)model);
        }        
    }
    
    private static HashMap<Integer, BuildingModel> buildingMap = new HashMap();
    
    /**
     * gets the building with the buildingID [value].
     * @param value integer
     * @return BuildingModel
     */
    public static BuildingModel getBuilding(int value) {
        
        BuildingModel result = buildingMap.get(value);
         
        if (result == null) {
            for(BuildingModel m: getBuildings()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                buildingMap.put(value, result);
        }
        
        return result;
    }
    
    private static HashMap<Integer, CourseModel> courseMap = new HashMap();
    
    public static CourseModel getCourse(DepartmentModel dm, String value) {
        
        for(CourseModel m: getCourses()) {
            if (m.getDepartmentModel() == dm) {
                if (m.getCourseNumber().equalsIgnoreCase(value))
                    return m;
            }
        }
        
        return null;
    }
    
    
    public static CourseModel getCourse(int value) {
        
        CourseModel result = courseMap.get(value);
         
        if (result == null) {
            for(CourseModel m: getCourses()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                courseMap.put(value, result);
        }
        
        return result;
    }
    
    private static HashMap<Integer, RoomModel> roomMap = new HashMap();
    
    public static RoomModel getRoom(int value) {
        
        RoomModel result = roomMap.get(value);
         
        if (result == null) {
            for(RoomModel m: getRooms()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                roomMap.put(value, result);
        }
        
        return result;
    }
    
    private static HashMap<Integer, DepartmentModel> departmentMap = new HashMap();
    
    public static DepartmentModel getDepartment(String value) {
        
        for(DepartmentModel dm: getDepartments()) {
            if (dm.getDepartmentName().equalsIgnoreCase(value))
                return dm;
        }
        return null;
    }
    
    public static DepartmentModel getDepartment(int value) {
        
        DepartmentModel result = departmentMap.get(value);
         
        if (result == null) {
            for(DepartmentModel m: getDepartments()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                departmentMap.put(value, result);
        }
        
        return result;
    }
    
    private static HashMap<Integer, ProfessorModel> professorMap = new HashMap();
    
    public static ProfessorModel getProfessor(int value) {
        
        ProfessorModel result = professorMap.get(value);
         
        if (result == null) {
            for(ProfessorModel m: getProfessors()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                professorMap.put(value, result);
        }
        
        return result;
    }
    
    private static HashMap<Integer, LabModel> labMap = new HashMap();
    
    public static LabModel getLab(int value) {
        
        LabModel result = labMap.get(value);
         
        if (result == null) {
            for(LabModel m: getLabs()) {
                if (m.getModelID() == value) {
                    result = m;
                    break;
                }
            }
            if (result != null)
                labMap.put(value, result);
        }
        
        return result;
    }
    
    public static String getDefaultValue(int id, Model m) {
        if (m != null)
            return m.toString();
        else
            return String.valueOf(id);
    }
    
    /**
     * Momento 
     */
    private static Momento<MeetingModelMomento> meetingModelMomento;
    
    public static Momento<MeetingModelMomento> getMeetingModelMomento() {
        if (meetingModelMomento == null) 
            meetingModelMomento = new Momento();
        
        return meetingModelMomento;
    }
    
}
