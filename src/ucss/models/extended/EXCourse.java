/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.models.extended;

import ucss.models.tuples.CourseModel;
import ucss.models.tuples.DepartmentModel;
import ucss.models.tuples.LabModel;
import ucss.models.tuples.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author John
 */
public class EXCourse {
    
    private LabModel forLab;
    private CourseModel forCourse;
    private DepartmentModel forDepartment;
    
    public EXCourse(CourseModel model) {
        setValues(model);
    }
    
    public CourseModel getCourseModel() {
        return forCourse;
    }
    
    public void setCourseModel(CourseModel model) {
        setValues(model);
    }
    
    public LabModel getLabModel() {
        return forLab;
    }
    
    public void setLabModel(LabModel model) {
        forLab = model;
        forCourse.setLabType((model != null)?model.getModelID():0);
    }
    
    public DepartmentModel getDepartmentModel() {
        return forDepartment;
    }
    
    public void setDepartmentModel(DepartmentModel model) {
        forDepartment = model;
        forCourse.setDepartment((model != null)?model.getModelID():0);
    }
    
    public Integer getModelID() {
        return forCourse.getModelID();
    }
    
    public String getLab() {
        if (forLab == null) 
            return "";
        else
            return forLab.toString();
    }
    
    private void setValues(CourseModel model) {
        
        forCourse = model;
        forDepartment = Model.getDepartment(model.getDepartment());
        forLab = Model.getLab(model.getLabType());
        
    }
    
    @Override public String toString() {
        return forCourse.toString();
    }
    
    public static ObservableList<EXCourse> getCourses() {
        ObservableList<EXCourse>  result = FXCollections.observableArrayList();
        for(CourseModel c: Model.getCourses()) {
            result.add(new EXCourse(c));
        }
        return result;
    }
    
    public static ObservableList<EXCourse> getCoursesByDepartments(int ... departments) {
        
        ObservableList<EXCourse>  result = FXCollections.observableArrayList();
        for(CourseModel c: Model.getCourses()) {
            boolean found = false;
            
            for(int i: departments) {
                if (i == c.getDepartment()) {
                    found = true;
                    break;
                }
            }
            if (found)
                result.add(new EXCourse(c));
        }
        return result;
    }
    
    public static ObservableList<EXCourse> getCoursesHasLab() {
        
        ObservableList<EXCourse>  result = FXCollections.observableArrayList();
        for(CourseModel c: Model.getCourses()) {
            if (c.getLabType() != 0)
                result.add(new EXCourse(c));
        }
        return result;
    }
}
