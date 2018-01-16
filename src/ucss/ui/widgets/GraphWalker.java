/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

/**
 *
 * @author John
 */
public interface GraphWalker<R, I> {
    public boolean walkIinR(EventGroupList gl, I item, R room); 
}
