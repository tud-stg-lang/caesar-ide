package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;

/**
 * Adds additional methods needed in NodeEliminator Visitor.
 * @see isToRemove
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarProgramElementNode extends ProgramElement {
    public CaesarProgramElementNode(
        String signature,
        IProgramElement.Kind kind,
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
        return "["+getKind()+"] "+getName();
    }
}
