package org.caesarj.ui.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IProgramElement;

/**
 * Collects nodes for elimination.
 * Calling eliminateNodes method removes them from the ASM.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class NodeEliminator extends HierarchyWalker {

    List nodes2del = new LinkedList();
    
    public NodeEliminator() {
        super();
    }

    public void preProcess(IProgramElement node) {       
        if(AsmBuilder.isToRemove(node))
            nodes2del.add(node);       
    }    
    
    public void eliminateNodes() {
        for(Iterator it=nodes2del.iterator(); it.hasNext(); ) {
            IProgramElement node2del = (IProgramElement)it.next();
    
            List parentChildren = node2del.getParent().getChildren();
    
            parentChildren.remove(node2del);
            parentChildren.addAll(node2del.getChildren());

            it.remove();
        }
    }
    
    public void clear() {
        nodes2del.clear();
    }
}
