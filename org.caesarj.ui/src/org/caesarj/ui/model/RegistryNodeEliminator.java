package org.caesarj.ui.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.asm.StructureNode;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class RegistryNodeEliminator extends AbstractAsmVisitor {

    List registryNodes = new LinkedList();

    public void visit(StructureNode node) {       

        if(node instanceof AspectRegistryNode) {
            registryNodes.add(node);
        }

        super.visit(node);
    }    
    
    public void eliminateNodes() {
        for(Iterator it=registryNodes.iterator(); it.hasNext(); ) {
            AspectRegistryNode aspectRegistryNode = (AspectRegistryNode)it.next();
    
            // pull up the children to parent node, eliminate this one
            List parentChildren = aspectRegistryNode.getParent().getChildren();
    
            parentChildren.remove(aspectRegistryNode);
            parentChildren.addAll(aspectRegistryNode.getChildren());

            it.remove();
        }
    }
    
    public void clear() {
        registryNodes.clear();
    }
}
