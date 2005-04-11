/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CaesarImageDescriptor.java,v 1.3 2005-04-11 09:04:00 thiago Exp $
 */

package org.caesarj.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;
import org.caesarj.compiler.asm.CaesarProgramElement;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author meffert
 *
 * TODO [documentation]
 */
public class CaesarImageDescriptor extends CompositeImageDescriptor {

	protected static final Point BIG_SIZE = new Point(22, 16);
	protected static final int UPPER_LEFT = 1;
	protected static final int UPPER_RIGHT = 2;
	protected static final int LOWER_LEFT = 3;
	protected static final int LOWER_RIGHT = 4;
	
	protected ImageDescriptor baseImage;
	protected IProgramElement node;
	
	private Point upper_left;
	private Point upper_right;
	private Point lower_left;
	private Point lower_right;
	
	private boolean decorate = true;
	
	/**
	 * Creates a composite image based on the given ImageDescriptor and decorates 
	 * it depending on kind, modifiers and accesibility of StructureNode.
	 * @param node
	 * @param baseImage
	 */
	public CaesarImageDescriptor(IProgramElement node, ImageDescriptor baseImage){
		this.node = node;
		this.baseImage = baseImage;
	}
	
	/**
	 * Creates a composite Image based on the given ImageDescriptor
	 * for the Structure Node. Decorates only if the third parameter is true.
	 * @param node
	 * @param baseImage
	 * @param decorate
	 */
	public CaesarImageDescriptor(IProgramElement node, ImageDescriptor baseImage, boolean decorate){
		this.decorate = decorate;
		this.node = node;
		this.baseImage = baseImage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
	 */
	protected void drawCompositeImage(int width, int height) {
		// initialize position point for drawing adornments
		Point size = getSize();
		upper_left = new Point(0,0);
		upper_right = new Point(size.x,0);
		lower_left = new Point(0,size.y);
		lower_right = new Point(size.x,size.y);

		// draw base image
		ImageData bg = baseImage.getImageData();
		if (bg == null){
			bg = DEFAULT_IMAGE_DATA;
		}
		drawImage(bg, 0, 0);

		// Only decorate image if decorate-flag is set to true.
		if(this.decorate){
			// draw adornments, depends on node
			if(CaesarProgramElement.Kind.CONSTRUCTOR == getKind()
					|| IProgramElement.Kind.CONSTRUCTOR == getKind()){
				// CONSTRUCTOR
				draw(JavaPluginImages.DESC_OVR_CONSTRUCTOR.getImageData(), UPPER_RIGHT);
			}
			if(isImplementor()){
				// IMPLEMENTS
				draw(JavaPluginImages.DESC_OVR_IMPLEMENTS.getImageData(), LOWER_RIGHT);
			}
			if(isOverrider()){
				// OVERRIDES
				draw(JavaPluginImages.DESC_OVR_OVERRIDES.getImageData(), LOWER_RIGHT);
			}
			if(isRunnable()){
				// RUNNABLE
				draw(JavaPluginImages.DESC_OVR_RUN.getImageData(), LOWER_RIGHT);
			}
			
			List modifiers = getModifiers();
			if(modifiers.contains(CaesarProgramElement.Modifiers.ABSTRACT)
					|| modifiers.contains(IProgramElement.Modifiers.ABSTRACT)){
				// ABSTRACT
				draw(JavaPluginImages.DESC_OVR_ABSTRACT.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElement.Modifiers.STATIC)
					|| modifiers.contains(IProgramElement.Modifiers.STATIC)){
				// STATIC
				draw(JavaPluginImages.DESC_OVR_STATIC.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElement.Modifiers.FINAL)
					|| modifiers.contains(IProgramElement.Modifiers.FINAL)){
				// FINAL
				draw(JavaPluginImages.DESC_OVR_FINAL.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElement.Modifiers.SYNCHRONIZED)
					|| modifiers.contains(IProgramElement.Modifiers.SYNCHRONIZED)){
				// SYNCHRONIZED
				draw(JavaPluginImages.DESC_OVR_SYNCH.getImageData(), LOWER_RIGHT);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
	 */
	protected Point getSize() {
		return CaesarImageDescriptor.BIG_SIZE;
	}

	/**
	 * Draws the adornment image on the correct position, depending on
	 * relPos Argument. Valid values for relPos are UPPER_LEFT, UPPER_RIGHT,
	 * LOWER_LEFT, LOWER_RIGHT.
	 * @param img
	 * @param relPos
	 */
	protected void draw(ImageData img, int relPos){
		if(relPos == UPPER_RIGHT){
			upper_right.x = upper_right.x - img.width;
			drawImage(img, upper_right.x, upper_right.y);
		}else if(relPos == UPPER_LEFT){
			drawImage(img, upper_left.x, upper_left.y);
			upper_left.x = upper_left.x + img.width;
		}else if(relPos == LOWER_LEFT){
			drawImage(img, lower_left.x, lower_left.y - img.height);
			lower_left.x = lower_left.x + img.width;
		}else if(relPos == LOWER_RIGHT){
			lower_right.x = lower_right.x - img.width;
			drawImage(img, lower_right.x, lower_right.y - img.height);
		}
	}
	
	protected Object getKind(){
		if(node instanceof CaesarProgramElement){
			return ((CaesarProgramElement)node).getCaesarKind();
		}else if(node instanceof ProgramElement){
			return ((ProgramElement)node).getKind();
		}else{
			return null;
		}
	}
	protected List getModifiers(){
		if(node instanceof ProgramElement){
			return ((ProgramElement)node).getModifiers();
		}else{
			return new ArrayList();
		}
	}
	protected boolean isImplementor(){
		if(node instanceof ProgramElement){
			return ((ProgramElement)node).isImplementor();
		}else{
			return false;
		}
	}
	protected boolean isOverrider(){
		if(node instanceof ProgramElement){
			return ((ProgramElement)node).isOverrider();
		}else{
			return false;
		}
	}
	protected boolean isRunnable(){
		if(node instanceof ProgramElement){
			return ((ProgramElement)node).isRunnable();
		}else{
			return false;
		}
	}
}
