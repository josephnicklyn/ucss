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
package ucss.ui.widgets;

import com.sun.javafx.collections.TrackableObservableList;
import java.util.Iterator;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 * The CMTable control is used to visualize a set number of rows of data,
 * separated by columns.  Unlike the TableView control, this table is 
 * intended to be used to layout controls, but in the form of a table with 
 * many of the features expected from a table. 
 * 
 * <p>The CMTable control has the following features:
 * <ul>
 * <li>{@link CMTableColumn}
 *      Table Columns consist of a number of nodes used to manipulate date
 *      controls such as TextViews or CheckBoxes.
 * <li>Columns can be fixed, or a percentage of the remaining width of the 
 *      table region.  Where a percentage is used, fixed column widths will be 
 *      subtracted from calculated width, and will only include the remaining
 *      width.
 * <li>{@link CWTableCell} can be highlighted to show the user a change has
 *      been made, and the changes may need to be updated.  This allows the 
 *      user a chance to undo changes prior to committing the changes.
 * </ul>
 * 
 * @author Joseph Nicklyn
 */
public class CMTable extends Region implements Iterable<CWTableRow> {
    
    /** member variables */
    
    /** disallows drawing outside of the region */
    private final Rectangle clipper = new Rectangle(0, 0);
    
    /** the currently selected row, for CSS rules */
    private CWTableRow selectedRow = null;
    
    
    private double rowHeight = 30,      /** the default height of a row */
                   headerHeight = 30,   /** 
                                          * The default height for the header
                                          * this may be larger if needed for
                                          * rotated header labels
                                          */
            
                   hGap = 4,            /** the horizontal gap between cells */    
                   vGap = 2;            /** the vertical gap between rows */
    
    /** reduces the number of calls to layoutChildren() */
    private boolean preformingLayout = false;
    
    /** the top row visible*/
    private int topRow = 0;
    
    private final RowRegion forRows = new RowRegion();
    
    /** constructors */
    
    /**
     * Creates a default CMTable with no content.
     */
    public CMTable() {
        getStyleClass().add("x-table");
        setClip(clipper);
        getChildren().add(forRows);
    }
    
    /**
     * Creates a table with predefined column headers.
     * 
     * @param columns array of {@link CMTableColumn}
     */
    public CMTable(CMTableColumn... columns) {
        
        getStyleClass().add("x-table");
        
        if (columns != null)
            for(int i = 0; i < columns.length; i++)
                addColumn(columns[i]);

        getChildren().add(forRows);
        this.setClip(clipper);
    }

    /**
     * A continence method to add a column to the table.
     * 
     * @param text the text for the column
     * @param width the width of the column (0 &gt;= width &gt;= 1 is proportional to 
     *              the width of the table
     */
    public final void addColumn(String text, double width) {
        CMTableColumn tc = new CMTableColumn(text, width);
        tableColumns.add(tc);
    }
    
    /**
     * Adds a column to the table.
     * 
     * @param column {@link CMTableColumn}
     */
    public final void addColumn(CMTableColumn column) {
        tableColumns.add(column);
    }
    
    /**
     * An observable list of {@link CMTableColumn}. Also updates the newly 
     * created/added CMTableColumn with specific information about this table
     * and updates the tables layout.
     */
    private final ObservableList<CMTableColumn> tableColumns = new TrackableObservableList<CMTableColumn>() {
        @Override
        protected void onChanged(Change<CMTableColumn> c) {
            while(c.next()) {
                for(CMTableColumn column: c.getAddedSubList()) {
                    if (column != null) {
                        column.setTable(CMTable.this);
                        getChildren().add(column);
                        
                        if (column.isRotated()) {
                            if (column.getTextWidth() > headerHeight) 
                                headerHeight = column.getTextWidth();
                        } else {
                            if (column.getTextHeight() > headerHeight) 
                                headerHeight = column.getTextHeight();
                        }
                    }
                }
            }
            requestLayout();
        }
    };
   
    /**
     * An observable list of {@link CWTableRow}. Also updates the newly 
     * created/added CWTableRow with specific information about this table
     * and updates the tables layout.
     */
    private final ObservableList<CWTableRow> tableRows = new TrackableObservableList<CWTableRow>() {
        @Override
        protected void onChanged(Change<CWTableRow> c) {
            while(c.next()) {
                for(CWTableRow reg: c.getAddedSubList()) {
                    if (reg != null) {
                        reg.setTable(CMTable.this);
                        forRows.add(reg);
                        
                    }
                }
            }
            requestLayout();
        }
    };
    
    /**
     * provides an iterator for the {@link CWTableRow}
     * @return Iterator
     */
    @Override
    public Iterator<CWTableRow> iterator() {
        return tableRows.iterator();
    }
    
    /**
     * Adds a new row to the table. 
     * <p>
     *  if the number of nodes is greater than 
     *      the number of columns, the excess will be dropped, 
     *  else    
     *      if the number of nodes is less than the number of columns, 
     *      then empty regions will be added to the row
     * </p>
     * 
     * @param nodes a list of nodes to be added to the table
     */
    public void add(Node... nodes) {
        // nothing to add?
        if (nodes == null)
            return;
        
        int i = 0;
        
        // initialize a new row
        CWTableRow region = new CWTableRow(this);
        
        // add the nodes
        for(CMTableColumn tc: tableColumns) {
            if (i < nodes.length)
                region.add(nodes[i]);
            else
                region.add(new Label(""));
            i++;
        }
        tableRows.add(region);
        
    }

    /** 
     * Gets the width of a column. 
     * 
     * @param index the column's index
     * @return double
     */
    public final double getColumnWidth(int index) {
        if (index >= 0 && index < tableColumns.size())
            return tableColumns.get(index).getAbsoluteWidth();
        else
            return 0;
    }
    
    
    /**
     * Get the height of the row(s)
     * 
     * @return double
     */
    public final double getRowHeight() {
        return rowHeight;
    }
    
    /**
     * Give access to the columns.
     * 
     * @return ObservableList::CMTableColumn
     */
    final ObservableList<CMTableColumn> getTableColumns() {
        return tableColumns;
    }
    
    /**
     * gets the horizontal gap (between cells)
     * 
     * @return double
     */
    public final double getHGap() {
        return hGap;
    }
    
    /**
     * gets the vertical gap (between rows)
     * 
     * @return double
     */
    public final double getVGap() {
        return vGap;
    }
    
    /**
     * get the height of the header
     * 
     * @return double
     */
    public final double getHeaderHeight() {
        return headerHeight;
    }
    
    
    /**
     * gets the number of rows.
     * 
     * @return integer
     */
    public final int getRowSize() {
        return tableRows.size();
    }
    
    /**
     * gets the variable width. That is the width of the table minus the 
     * sum of the fixed with columns
     * 
     * @return double
     */
    public final double getVariableWidth() {
        double result = getLayoutBounds().getWidth();
        
        for(CMTableColumn c: tableColumns) {
            if (c.getPWidth() > 1) 
                result -= c.getPWidth();
        }
        
        return result;
    }
    
    /**
     * (package visablity only) sets the selected row. This allows a row to 
     * be marked when it the table or scene loses focus.
     * 
     * @param row {@link CWTableRow}
     */
    void setSelected(CWTableRow row) {
        
        if (selectedRow != null) {
            selectedRow.setSelected(false);
        }
        
        selectedRow = row;
        selectedRow.setSelected(true);
    }
    
    /**
     * sets the current top row for the table.
     * 
     * @param value integer
     */
    public final void setTopRow(int value) {
        if (value < 0) 
            value = 0;
        
        if (value > tableRows.size()-1)
            value = tableRows.size()-1;
        
        if (topRow != value) {
            topRow = value;
            requestLayout();
        }
        
    }
    
    /**
     * gets the current top row of the table.
     * 
     * @return integer
     */
    public final int getTopRow() {
        return topRow;
    }
    
    /**
     * gets the maximum number of rows visible.
     * 
     * @return integer
     */
    public final int getVisibleRows() {
        return (int)Math.floor((getBoundsInLocal().getHeight()-headerHeight)/rowHeight);
    }
    
    /**
     * update the table
     */
    @Override public void requestLayout() {
        if (preformingLayout)
            return;
        super.requestLayout();
        
    }
    
    /**
     * updates the layout of the table header and the rows
     */
    @Override public void layoutChildren() {
        
        preformingLayout = true;
        
        // resize the clip
        clipper.setWidth(getWidth());
        clipper.setHeight(getHeight());
        
        
        double x = 0;               
        double y = headerHeight;    // first row after the header
        
        // update the column headers
        for(CMTableColumn tc: tableColumns) {
            double width = tc.getPWidth();

            if (width >= 0 && width <= 1) {
                width = getVariableWidth() * tc.getPWidth();
            }
            
            if (tc.getMinWidth() > 0) {
                if (width < tc.getMinWidth())
                    width = tc.getMinWidth();
            }
            
            tc.resizeRelocate(x, 0, width, headerHeight);
            
            x+=width;
        }
        if (getRowSize() < getVisibleRows())
            topRow = 0;
        hTop = (topRow * rowHeight);
        forRows.resizeRelocate(
            0, 
            headerHeight - hTop, 
            getWidth(), 
            getHeight() - headerHeight
        );
        
        forRows.layoutChildren();
        
        preformingLayout = false;
    }
    
    private double hTop = 0;

    private ScrollBar vScrollBar = null;
    
    /**
     * sets the scroll bar used by this table.
     * 
     * @param vertScrollBar  ScrollBar
     */
    void setScrollBar(ScrollBar vertScrollBar) {
        vScrollBar = vertScrollBar;
    }
    
    /**
     * resets the scrollbar range.
     */
    void resetScrollBar() {
        if (vScrollBar == null) 
            return;
        vScrollBar.setMax((int)getRowSize()-getVisibleRows() + 1);
        double f = ((double)getVisibleRows()/(double)getRowSize());
        vScrollBar.setVisibleAmount(vScrollBar.getMax() * f);
        if (getRowSize() < getVisibleRows())
            vScrollBar.setValue(0.0);
        
    }

    /**
     * gets the currently selected table row.
     * 
     * @return  CWTableRow
     */
    public final CWTableRow getSelectedRow() {
        return selectedRow;
    }
    
    private class RowRegion extends Region {
        
        private Rectangle rClipper = new Rectangle();
        
        public RowRegion() { setClip(rClipper); }
        
        
        @Override public void layoutChildren() {
            // update the rows
            double y = Math.abs(hTop);
            resetScrollBar();
            rClipper.setWidth(getWidth());
            rClipper.setHeight(getParent().getLayoutBounds().getHeight() - headerHeight);
            rClipper.setY(y);
            
            y = 0;
            for(CWTableRow p: tableRows) {
                p.resizeRelocate(0, y, getWidth(), rowHeight);
                y+=rowHeight;
            }
            
        }

        private void add(CWTableRow reg) {
            getChildren().add(reg);
        }
        
    }
    
}
