package org.caesarj.ui.util;

import java.io.PrintStream;
import java.util.Iterator;

import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureNode;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class StructureModelDump {

    protected StructureModel model;

    public StructureModelDump(StructureModel model) {
        this.model = model;
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream out) {
        print("", out, model.getRoot());
    }

    protected void print(String indent, PrintStream out, StructureNode node) {   
        out.println(indent+"["+node.getKind()+"] "+node.getName());
        
        String newIndent = indent+"..";
        
        for(Iterator it=node.getChildren().iterator(); it.hasNext(); ) {
            print(newIndent, out, (StructureNode)it.next());
        }
    }
    
}
