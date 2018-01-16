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
package ucss.models.views;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author John
 */
public class OutputPane extends ScrollPane {
    
    public VBox box = new VBox(4);
    private boolean large = true;
    public OutputPane() {
        
        box.setPadding(new Insets(4, 10, 4, 10));
        setContent(box);
        setMinHeight(120);
        setPrefHeight(120);
        setMaxHeight(120);
        OutputBox.getItems().addListener(outputBoxListener);
        box.setOnMouseClicked( e -> {
            large = !large;
            if (large) {
                setMinHeight(120);
                setPrefHeight(120);
                setMaxHeight(120);
                setHeight(120);
            } else {
                setMinHeight(32);
                setPrefHeight(32);
                setMaxHeight(32);
                setHeight(32);
            }
        });
    }
    
    public final int toInt(String v) {
        if (v == null)
            return 0;
        int r = 0;
        for(char c: v.toCharArray()) {
            if (c >= '0' && c <= '9') {
                r*=10;
                r+=(int)(c-'0');
            }
        }
        return r;
    }
    
    public final void add(String value) {
        Color c = (value.contains("\b")?Color.RED:Color.BLACK);
        String xValue = value.replace("\b", "").replace("\n", "");
        if (!box.getChildren().isEmpty()) {
            Text p = (Text)box.getChildren().get(0);
            
            int v = toInt(p.getId());
            
            
            p.setId(String.valueOf(v+1));
            if (p.getText().startsWith(xValue)) {
                p.setText(xValue + " [ " + String.valueOf(v+1) + " ]");
                return;
            }
            p.setText("\t" + p.getText());
            p.setFont(Font.font("san-serif", FontWeight.NORMAL, 14));
            
        }
        Text t = new Text();
        
        t.setText(xValue);
        t.setFill(c);
        t.setFont(Font.font("san-serif", FontWeight.NORMAL, 16));
        
        box.getChildren().add(0, t);
        
        
        
    }
    
    private final ListChangeListener<String> outputBoxListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends String> c) {
        
            while (c.next()) {
                 if (c.wasPermutated()) {
                     for (int i = c.getFrom(); i < c.getTo(); ++i) {
                          //permutate
                     }
                 } else if (c.wasUpdated()) {
                          //update item
                 } else {
                     for (String remitem : c.getRemoved()) {
                         
                     }
                     for (String additem : c.getAddedSubList()) {
                         add(additem);
                     }
                 }
             }
            
            
        }
        
    };
    
}
