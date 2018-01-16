package ucss.ui.widgets;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Creates a ribbon like container.  Using a tab-inspired selection method,
 * with a custom tool bar-like container where action items are placed.
 * <p>Ribbon overview
 * <pre><code>
 * 
 *  RibonWidget                 // analagous to a tab pane
 *  |---+ Ribbon                // analagous to a tab 
 *      |---+ RibbonItem        // analagous tool bar
 *          |--- RibbonAction   // analagous to a tool bar item w/title and node
 * 
 * </pre></code>
 * <p>
 * {@link Ribbon} contains a title, a tool-bar-like container where 
 * {@link RibbonItem}'s are placed, RibbonItems have a group title and each 
 *  ribbon item is intended to contain controls such as a button, check box or 
 *  combo box. 
 * 
 * @author Joseph Nicklyn JR.
 */
public class RibbonWidget extends VBox  implements HelperInterface {
    
    private double ribbonHeight = 60;
    private Ribbon selectedRibbon = null;
    
    private HBox buttonBar = new HBox();
    private StackPane ribbonBar = new StackPane();
    
    private final StackPane container = new StackPane();
    
    public RibbonWidget() { this(2);   }
    public RibbonWidget(int rows) {
        setSpacing(4);
        getChildren().addAll(buttonBar, ribbonBar, container);
        this.setFillWidth(true);
        buttonBar.getStyleClass().add("ribbon-button-bar");
        container.setStyle("-fx-background-color:green;");
        VBox.setVgrow(container, Priority.ALWAYS);
    }
    
    public final Ribbon getSelectedRibbon() {
        return selectedRibbon;
    }
    
    public final void setSelectedRibbon(Ribbon target) {
        if (ribbons.contains(target)) {
            if (selectedRibbon != target) {
                selectedRibbon = target;
                if (target.isSelectable()) {
                    for(Ribbon r: ribbons) {
                        r.setSelected(target == r);
                    }

                    container.getChildren().clear();
                    ribbonBar.getChildren().clear();

                    if (target.getGroupHeader() != null) {
                        ribbonBar.getChildren().add(target.getGroupHeader());
                    }
                    
                    if (target.getContent() != null) {
                        container.getChildren().add(target.getContent());
                    }
                }
                requestLayout();
            }
        }
    }
    
    private final ObservableList<Ribbon> ribbons = new TrackableObservableList<Ribbon>() {
        @Override
        protected void onChanged(ListChangeListener.Change<Ribbon> c) {
            while(c.next()) {
                
                for (Ribbon ribbon : c.getRemoved()) {
                    if (ribbon != null) {
                        buttonBar.getChildren().remove(ribbon.getHeaderLabel());
                    }
                }
                
                for (Ribbon ribbon : c.getAddedSubList()) {
                    if (ribbon != null) {
                        ribbon.setRibbonWidget(RibbonWidget.this);
                        buttonBar.getChildren().add(ribbon.getHeaderLabel());
                    }
                }
            }
            requestLayout();
        }
    };
    
    public final ObservableList<Ribbon> getRibbons() { return ribbons; }

    @Override
    public String getHelperInfo(MouseEvent e) {
        return "Ribbon Widget\nThe Ribbon Widget is used\n" +
               "to display content in a tabular form.";
        
    }
    
    @Override
    public void setExtraComments(String value) {
        
    }

}
