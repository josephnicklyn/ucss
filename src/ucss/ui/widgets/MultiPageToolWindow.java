/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ucss.ui.widgets;

import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 *
 * @author John
 */
public class MultiPageToolWindow extends ToolWindow {

    public static MultiPageToolWindow show(Node node, String title, Node ... nodes) {
        MultiPageToolWindow t = new MultiPageToolWindow(title, nodes);
        t.show(node);
        return t;
    }
    private final ArrayList<Node> nodes = new ArrayList();
    
    private int onPage = -1;
    
    public MultiPageToolWindow(String title) {
        this(title, (Node)null);
    }
    
    public MultiPageToolWindow(String title, Node ... n) {
        super(title);
        
        super.addButtons(
            ToolWindow.ToolWindowButtons.TWB_HELP,
            ToolWindow.ToolWindowButtons.TWB_CANCEL,
            ToolWindow.ToolWindowButtons.TWB_PREV,
            ToolWindow.ToolWindowButtons.TWB_NEXT,
            ToolWindow.ToolWindowButtons.TWB_OKAY
        );
        
        super.setHasHelp(false);
        
        if (n != null) {
            setPages(n);
        } else {
            setNextPrev();
        }
    }
    
    
    public final void setPages(Node ...node) {
        getContent().getChildren().clear();
        if (node != null) {
            for(Node n: node) 
                nodes.add(n);
        }
        goHomePage();
    }
    
    public final void addPage(Node node) {
        if (node != null) {
            nodes.add(node);
            setNextPrev();
        }
    }
    
    public final void removePage(Node node) {
        if (node != null) {
            nodes.remove(node);
            goHomePage();
        }
    }
    
    public final void clear() {
        nodes.clear();
        onPage = -1;
    }
    
    public final void goHomePage() {
        if (!nodes.isEmpty()) {
            fadeOutAndReplace(nodes.get(0));
            onPage = 0;
        } else {
            getContent().getChildren().clear();
            onPage = -1;
        }
        setNextPrev();
    }
    
    private Node currentNode = null;
    private void fadeOutAndReplace(Node in) {
        if (getContent() == null) 
            return;
        
        final Node node;
        
        if (!getContent().getChildren().isEmpty()) {
            node = currentNode;
        } else {
            fadeIn(in);
            return;
        }
    
        if (node == null || !getContent().getChildren().contains(node)) 
            return;
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(node.getOpacity());
        ft.setToValue(0.0);
        
        ft.setOnFinished( e -> {
            getContent().getChildren().remove(node);
            currentNode = null;
            fadeIn(in);
        });
        ft.play();
    }
    
    private void fadeIn(Node node) {
        if (node != null) {
            node.setOpacity(0.0);
            getContent().getChildren().setAll(node);
        } else {
            return;
        }
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setOnFinished( e -> {
            currentNode = node;
        });
        ft.play();
    }
    
    public final void setNextPrev() {
        if (nodes.size() <= 1) {
            setHasNext(false);
            setHasPrev(false);
        } else {
            setHasPrev(onPage > 0);
            setHasNext(onPage < (nodes.size()-1));
        }
    }
    
    @Override public void onNext() {
        
        int op = onPage + 1;
        if (op > (nodes.size()-1)) {
        } else {
            onPage = op;
            fadeIn(nodes.get(onPage));
        }
        setNextPrev();
    }
    
    @Override public void onPrevious() {
        int op = onPage - 1;
        if (op < 0) {
        } else {
            onPage = op;
            fadeIn(nodes.get(onPage));
        }
        setNextPrev();
    }
}
