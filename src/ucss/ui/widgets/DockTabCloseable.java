
package ucss.ui.widgets;

/**
 * This enumeration defines when/where tabs are closeable 
 * 
 * 
 * @author Joseph Nicklyn JR.
 */
public enum DockTabCloseable {
    NOT,                // not when on owner or on the secondary stage
    
    ON_SECONDARY,       // only when on the secondary stage
    
    ON_BOTH;            // on either the owner or the secondary stage
}
