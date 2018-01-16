/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.views.ribbons;

import javafx.animation.FadeTransition;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ucss.models.views.CourseWidget;
import ucss.models.views.ProfessorWidget;
import ucss.models.views.RoomWidget;
import ucss.ui.widgets.Ribbon;
import ucss.ui.widgets.RibbonAction;
import ucss.ui.widgets.RibbonItem;

/**
 *
 * @author John
 */
public class CommonObjectRibbon extends Ribbon {
    
    private final RadioButton[] btnSelector = {
        new RadioButton("Courses"),
        new RadioButton("Rooms"),
        new RadioButton("Professors")
    };
    
    private int prevSelector = -1;
    
    
    private RoomWidget roomWidget = null;
    private CourseWidget courseWidget = null;
    private ProfessorWidget professorWidget = null;
    
    private final Label label = new Label("Select a component to continue");
    private final StackPane sp = new StackPane(label);
    
    private final RibbonItem rbObjectSelector = new RibbonItem(
        "select",
        new HBox(),
        new RibbonAction("", btnSelector[0]),
        new RibbonAction("", btnSelector[1]),
        new RibbonAction("", btnSelector[2])
    );
    
    
    private final ToggleGroup tg = new ToggleGroup();
    public CommonObjectRibbon() {
        super("Campus Common");
        sp.setStyle("-fx-background-color:#eaeaef;");
        label.setStyle("-fx-font-size:3.0em;");
        
        super.setContent(sp);
        getItems().addAll(
            rbObjectSelector
        );
        
        btnSelector[0].setToggleGroup(tg);
        btnSelector[1].setToggleGroup(tg);
        btnSelector[2].setToggleGroup(tg);
        
        btnSelector[0].selectedProperty().addListener(
                (e, o, n) -> { selectWidget(0, o, n); });
        
        btnSelector[1].selectedProperty().addListener(
                (e, o, n) -> { selectWidget(1, o, n); });
        
        btnSelector[2].selectedProperty().addListener(
                (e, o, n) -> { selectWidget(2, o, n); });
        
        super.setExtraComments("\n\n\bCampus Common\n\tDisplays objects which are independent of\n\tterm meetings such as rooms and professors.");
    }

    private boolean waitSelect = false;
    
    private void selectWidget(int i, boolean oldValue, boolean newValue) {
        
        if (waitSelect) {
            return;
        }
        
        waitSelect = true;

                
        switch (i) {
            case 0:
                if (newValue && roomWidget == null) {
                    roomWidget = new RoomWidget();
                    fadeOutAndReplace(roomWidget);
                } else if (newValue && roomWidget != null) {
                    fadeOutAndReplace(roomWidget);
                }
                break;
            case 1:
                if (newValue && courseWidget == null) {
                    courseWidget = new CourseWidget();
                    fadeOutAndReplace(courseWidget);
                } else if (newValue && courseWidget != null) {
                    fadeOutAndReplace(courseWidget);
                }
                break;
            case 2: 
                if (newValue && professorWidget == null) {
                    professorWidget = new ProfessorWidget();
                    fadeOutAndReplace(professorWidget);
                } else if (newValue && professorWidget != null) {
                    fadeOutAndReplace(professorWidget);
                }
                break;
        }
        
        waitSelect = false;
        
        prevSelector = i;
    }

    private Node currentNode = label;
    
    private void fadeOutAndReplace(Node in) {
        final Node node;
        
        if (!sp.getChildren().isEmpty()) {
            node = currentNode;
        } else {
            return;
        }
    
        if (node == null || !sp.getChildren().contains(node)) 
            return;
        
        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(node.getOpacity());
        ft.setToValue(0.0);
        System.out.println("IN = " + in);
        
        ft.setOnFinished( e -> {
            sp.getChildren().remove(node);
            currentNode = null;
            fadeIn(in);
        });
        
        
        ft.play();
       
    }
    
    private void fadeIn(Node node) {
        if (node != null) {
            node.setOpacity(0.0);
            sp.getChildren().setAll(node);
        } else {
            return;
        }
        
        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setOnFinished( e -> {
            currentNode = node;
        });
        ft.play();
    }
    
}
