/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import javafx.beans.value.ChangeListener;

/**
 *
 * @author John
 */
public class ListSelectDropWindow<E> extends DropWindow {
    private final ListSelect<E> list;
    public ListSelectDropWindow(String title) {
        super(title, new ListSelect<E>(), 400);
        list = (ListSelect<E>)super.getContent();
    }
    
    public final void addAll(E... e) {
        list.addAll(e);
    }
    
    public final void add(E e) {
        list.add(e);
    }
    
    public final void clear() {
        list.clear();
    }
    
    public final void setOnRemovedItem(ChangeListener<E> listener) {
        list.setOnRemovedItem(listener);
    }
    
    public final void setOnAddedItem(ChangeListener<E> listener) {
        list.setOnAddedItem(listener);
    }
    
    
}
