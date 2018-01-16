/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import java.util.HashMap;

/**
 *
 * @author John
 */
public class ToolHelper {
    
    private static ToolHelper instance;
    
    private final HashMap<HelperInterface, String> toolTips = new HashMap();
    
    private ToolHelper() {
       
    }
    
    public static ToolHelper getInstance() {
        if (instance == null) {
            instance = new ToolHelper();
        }
        return instance;
    }
    
}
