/**
 * 
 * Macacos.org 
 *
 * Class: LinkNode
 *
 * Copyright (c) 2005 Macacos.org. All Rights Reserved
 *
 * Created on Apr 21, 2005 by Thiago Tonelli Bartolomei
 * -------------------------------------------------
 *           \                                                                           
 *            \                                                                          
 *               __                                                                      
 *          w  c(..)o                                                                    
 *           \__(o)                                                                      
 *               /\                                                                      
 *            w_/(_)-~                                                                   
 *                /|                                                                     
 *               | \                                                                     
 *               m  m   
 */

package org.caesarj.ui.editor.model;

import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;

/**
 * 
 * TODO - Comments
 *
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 *
 */
public class LinkNode extends ProgramElement {
    
    public static final int LINK_NODE_RELATIONSHIP = 0;
    public static final int LINK_NODE_TARGET = 1;
    
    protected IProgramElement targetElement;
    protected IRelationship relationship;
    protected int type;

    public LinkNode(IRelationship relationship) {
        this.relationship = relationship;
        this.type = LINK_NODE_RELATIONSHIP;
    }
    
    public LinkNode(IRelationship relationship, IProgramElement targetElement) {
        this.relationship = relationship;
        this.targetElement = targetElement;
        this.type = LINK_NODE_TARGET;
    }
    
    public ISourceLocation getSourceLocation() {
        return parent.getSourceLocation();
    }

    /**
     * @return Returns the relationship.
     */
    public IRelationship getRelationship() {
        return relationship;
    }
    /**
     * @return Returns the target.
     */
    public IProgramElement getTargetElement() {
        return targetElement;
    }
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
}
