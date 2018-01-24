/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONObject;
import ucss.controllers.DatabaseController;
import ucss.models.database.APIUtil;
import ucss.models.database.APIUtil.CompleteAction;
import ucss.models.database.APIUtil.SimpleListItem;
import ucss.models.database.JSONAssistant;
import ucss.models.tuples.CourseModel;
import ucss.models.tuples.DepartmentModel;
import ucss.models.tuples.Model;
import ucss.models.tuples.TermModel;

/**
 *
 * @author John
 */
public class APIGetter extends VBox {
    
    private int gotData = 0;
    
    DatabaseController controller;
    private ComboBox<SimpleListItem> cmbTerms = new ComboBox();
    private ComboBox<SimpleListItem> cmbPrefix = new ComboBox();
    private HBox termButtonBar = new HBox(8);
    
    private HBox getGroupLable(String text, Node node) {
        
        Label label = new Label(text);
        label.setPadding(new Insets(0, 12, 0, 0));
        label.setPrefWidth(60);
        label.setMinWidth(60);
        HBox container = new HBox(12, label, node);
        HBox.setHgrow(node, Priority.ALWAYS);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }
    
    public APIGetter(DatabaseController controller) {
        this.controller = controller;
        setSpacing(16);
        setPadding(new Insets(12, 12, 12, 12));
        super.setFillWidth(true);
        super.getChildren().add(getGroupLable("Terms", cmbTerms));
        super.getChildren().add(getGroupLable("Prefix", cmbPrefix));
        super.getChildren().add(new Separator());
        super.getChildren().add(getGroupLable("Get??", termButtonBar));
        termButtonBar.setAlignment(Pos.CENTER);
        cmbTerms.setMaxWidth(Double.MAX_VALUE);
        cmbPrefix.setMaxWidth(Double.MAX_VALUE);
        
        try {
            cmbTerms.setItems(APIUtil.getTermsList(onCompleteAction));
            cmbPrefix.setItems(APIUtil.getPrefixList(onCompleteAction));
        } catch (Exception ex) {
            Logger.getLogger(APIGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private CompleteAction onCompleteAction = new CompleteAction() {
        @Override
        public void actionComplete() {
            gotData++;
            if (gotData == 2) {
                //setDisabled(false);
                termsCrossReference();
                
            }
        }

        @Override
        public void actionFailed() {

        }
        
        @Override
        public void onGotJSON(String json) {
            
        }
        
    };
    
    
    private Stage stage = null;
    private Scene scene = null;
    
    public void showWindow() {
        if (stage == null) {
            // creates a new stage
            stage = new Stage();
            scene = new Scene(this, 400, 600);
            scene.getStylesheets().add("resources/styles.css");

            stage.setTitle("API Getter");
            
            stage.setScene(scene);
            
            stage.initStyle(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            
        } else {
            if (!stage.isShowing()) {
           
            }
        }
        
        stage.show();
    }
    
    private void termsCrossReference() {

        termButtonBar.getChildren().clear();
        
        HashMap<Integer, TermModel> terms = Model.getTerms();
        
        for(SimpleListItem si: cmbTerms.getItems()) {
                
            String key = si.getKey();
            boolean contained = false;
            
            System.out.println(key);
            
            for(Integer i: terms.keySet()) {
                TermModel t = terms.get(i);
                String termTitle =t.getTitle();

                if (key.equalsIgnoreCase(termTitle)) {
                    contained = true;
                    break;
                }
            }
            if (contained == false) {
                Button b = new Button(key);
                b.setStyle("-fx-font-size:20;");
                termButtonBar.getChildren().add(b);
                b.setOnAction( e -> {
                    addTerm(si);
                    termButtonBar.getChildren().remove(b);
                });
            }
        }
        
    }
    
    private void addTerm(SimpleListItem s) {
        String term = s.getKey();
        TermModel tm = new TermModel(s.getKey());
        
        APIUtil.getAPIData(new CompleteAction() {
            @Override
            public void actionComplete() {
            }

            @Override
            public void actionFailed() {
            }

            @Override
            public void onGotJSON(String json) {
                extract(tm, json);
            }
        }, term);
    }
    
    private void extract(TermModel tm, String json) {
        JSONObject obj = new JSONObject(json);
        if (obj != null) {
            JSONArray courses = JSONAssistant.getArray(obj, "courses");
            if (courses != null) {
                for(int i = 0; i < courses.length(); i++) {
                    JSONObject course = JSONAssistant.getArrayObject(courses, i);
                    String academicLevel = JSONAssistant.getString(course, "academicLevel");
                    int capacity = JSONAssistant.getInt(course, "capacity");
                    String courseNumber = JSONAssistant.getString(course, "courseNumber");
                    int credits = JSONAssistant.getInt(course, "credit");
                    String prefix = JSONAssistant.getString(course, "prefix");
                    String section = JSONAssistant.getString(course, "section");
                    String term = JSONAssistant.getString(course, "term");
                    String courseTitle = JSONAssistant.getString(course, "title");
                    String courseDescription = JSONAssistant.getString(course, "description");
                    DepartmentModel dm = Model.getDepartment(prefix);
                    
                    if (dm != null) {
                        CourseModel cm = Model.getCourse(dm, courseNumber);
                        if (cm == null) {
                            cm = new CourseModel(
                                dm.getModelID(),
                                credits,
                                courseNumber,
                                courseTitle,
                                courseDescription
                            );
                        }
                        JSONArray meets = JSONAssistant.getArray(course, "meetingTimes");
                        if (meets != null) {
                            
                        }
                    }
                }
            }
        }
    }
    
}
