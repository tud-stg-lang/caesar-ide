package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.ProgramElementNode.Kind;
import org.aspectj.bridge.ISourceLocation;

/**
 * ...
 * 
 * @author ivica
 */
public class CaesarProgramElementNode extends ProgramElementNode {
    public CaesarProgramElementNode(
        String signature,
        Kind kind,
        List children
    ) {
        super(
            signature,
            kind,
            children
        );
    }
    
    public CaesarProgramElementNode(
        String signature,
        Kind kind,
        ISourceLocation sourceLocation,
        int modifiers,
        String formalComment,
        List children
    ) {
        super(
            signature,
            kind,
            sourceLocation,
            modifiers,
            formalComment,
            children
        );
    }
    
    public String toString() {
        return "["+getKind()+"] "+super.toString();
    }


	public boolean isToRemove() {
		return 
            getName().equals("_getBinding")
            || getName().equals("_getProviding")
            || getName().equals("_getProvidingReference")
            || getName().equals("_getWrappee");
	}
}
