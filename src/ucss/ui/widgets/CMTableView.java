/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 *
 * @author John
 */
public class CMTableView extends Region {
    
    private final CMTable theTable;
    private final ScrollBar vertScrollBar = new ScrollBar();
    
    private boolean preformingLayout = false;
    private final Label pageInfoLabel = new Label("");
    private final Button btnNext = new Button(">"),
                         btnLast = new Button(">|"),
                         btnPrev = new Button("<"),
                         btnFirst = new Button("|<");
    
    private HBox userContent = new HBox(4);
    private final HBox controlBar = new HBox(4, btnFirst, btnPrev, pageInfoLabel, btnNext, btnLast, userContent);
    
    private final SimpleIntegerProperty onPage = new SimpleIntegerProperty();
    private int maxPage = 1;
    
    private final CMTableActions targetActions;
    
    public CMTableView(CMTableActions target) {
        targetActions = target;
        theTable = new CMTable();
        initView();
    }
    
    public CMTableView(CMTableActions target, CMTableColumn... columns) {
        theTable = new CMTable(columns);
        targetActions = target;
        initView();
    }
    
    
    public final HBox getUserContent() {
        return userContent;
    }
    
    private void initView() {

        userContent.setPadding(new Insets(2, 2, 2, 10));
        userContent.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(userContent, Priority.ALWAYS);
        btnNext.setFocusTraversable(false);
        btnLast.setFocusTraversable(false);
        btnPrev.setFocusTraversable(false);
        btnFirst.setFocusTraversable(false);
        
        controlBar.getStyleClass().setAll("x-table-control-panel");
        controlBar.setFillHeight(true);
        vertScrollBar.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(theTable, vertScrollBar, controlBar);
        pageInfoLabel.getStyleClass().setAll("x-table-page-label");
        vertScrollBar.valueProperty().addListener( (e, o, n) -> {
            theTable.setTopRow(n.intValue());
        });
        
        theTable.setScrollBar(vertScrollBar);
        setText("1 of " + maxPage);
        
        btnNext.setOnAction( e -> {
            if (onPage.get() < maxPage-1)
                setPage(onPage.get()+1);
        });
        
        btnPrev.setOnAction( e -> {
            if (onPage.get() > 0)
                setPage(onPage.get()-1);
        });
        
        btnLast.setOnAction( e -> {
            setPage(maxPage-1);
        });
        
        btnFirst.setOnAction( e -> {
            setPage(0);
        });
        
        onPage.addListener((e, o, n) -> {updateView(); });
        
        theTable.setOnMouseClicked( e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                System.out.println(theTable.getSelectedRow());
                
            }
        });
        
    }
    
    public final void setPage(int value) {
        if (value >= 0 && value < maxPage) {
            onPage.set(value);
            setText((onPage.get() + 1) + " of " + maxPage);
            vertScrollBar.setValue(0);
        }
    }
    
    public final void setMaxPage(int page) {
        maxPage = page;
        
        if (onPage.get() != 0)
            onPage.set(0);
        else
            updateView();
        
        setText((onPage.get() + 1) + " of " + maxPage);
    }
    
    public final void setMaxRows(int value) {
        int p = (getTable().getRowSize() == 0)?0:(int)Math.ceil((double)value/getTable().getRowSize());
        setMaxPage(p);
    }
    
    public final CMTable getTable() {
        return theTable;
    }
    
    public final void setText(String value) {
        pageInfoLabel.setText(value);
    }
    
    public final void setOnPageChange(ChangeListener<Number> listener) {
        onPage.addListener(listener);
    }
    
    @Override public void requestLayout() {
        if (preformingLayout) 
            return;
        
        super.requestLayout();
    }
    
    @Override public void layoutChildren() { 
        preformingLayout = true;
        
        double left = getPadding().getLeft(),
               right = getPadding().getRight(),
               top = getPadding().getTop(),
               bottom = getPadding().getBottom();
        
        double width = getWidth(),
               height = getHeight();
        
        double clientWidth = width - (left + right),
               clientHeight = height - (top + bottom);
        
        double x2 = width - right,
               y2 = height - bottom;
        
        double vBarWidth = vertScrollBar.prefWidth(-1);
        double hBoxHeight = controlBar.prefHeight(-1);
        
        theTable.resizeRelocate(
            top, 
            left, 
            clientWidth - vBarWidth, 
            clientHeight - hBoxHeight
        );
        
        vertScrollBar.resizeRelocate(
            x2 - vBarWidth,
            0,
            vBarWidth,
            clientHeight - hBoxHeight
        );
        
        controlBar.resizeRelocate(
            0,
            y2 - hBoxHeight,
            clientWidth,
            hBoxHeight
        );
       
        preformingLayout = false;
    }
    
    public void updateView() {
        if (targetActions != null) {
            int p = onPage.get() * getTable().getRowSize();
            for(CWTableRow r: getTable()) {
                targetActions.setRowValuesFor(r, p++);
            }
        }

    }
}
