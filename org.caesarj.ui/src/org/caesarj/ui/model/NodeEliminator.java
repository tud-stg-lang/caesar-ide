package org.caesarj.ui.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.asm.StructureNode;

/**
 * Collects nodes for elimination.
 * Calling eliminateNodes method removes them from the ASM.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class NodeEliminator extends AbstractAsmVisitor {

    List nodes2del = new LinkedList();

    public void visit(StructureNode node) {       

        if(node instanceof AspectRegistryNode) {
            nodes2del.add(node);
        }
        else if(node instanceof CaesarProgramElementNode) {
            if(((CaesarProgramElementNode)node).isToRemove())
                nodes2del.add(node);;
        }

        super.visit(node);
    }    
    
    public void eliminateNodes() {
        for(Iterator it=nodes2del.iterator(); it.hasNext(); ) {
            StructureNode node2del = (StructureNode)it.next();
    
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
