/*
 * Created on 29.11.2004
 */
package org.caesarj.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
import org.caesarj.compiler.asm.CaesarProgramElementNode;
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
	protected StructureNode node;
	
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
	public CaesarImageDescriptor(StructureNode node, ImageDescriptor baseImage){
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
	public CaesarImageDescriptor(StructureNode node, ImageDescriptor baseImage, boolean decorate){
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
			if(CaesarProgramElementNode.Kind.CONSTRUCTOR == getKind()
					|| ProgramElementNode.Kind.CONSTRUCTOR == getKind()){
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
			if(modifiers.contains(CaesarProgramElementNode.Modifiers.ABSTRACT)
					|| modifiers.contains(ProgramElementNode.Modifiers.ABSTRACT)){
				// ABSTRACT
				draw(JavaPluginImages.DESC_OVR_ABSTRACT.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElementNode.Modifiers.STATIC)
					|| modifiers.contains(ProgramElementNode.Modifiers.STATIC)){
				// STATIC
				draw(JavaPluginImages.DESC_OVR_STATIC.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElementNode.Modifiers.FINAL)
					|| modifiers.contains(ProgramElementNode.Modifiers.FINAL)){
				// FINAL
				draw(JavaPluginImages.DESC_OVR_FINAL.getImageData(), UPPER_RIGHT);
			}
			if(modifiers.contains(CaesarProgramElementNode.Modifiers.SYNCHRONIZED)
					|| modifiers.contains(ProgramElementNode.Modifiers.SYNCHRONIZED)){
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
		if(node instanceof CaesarProgramElementNode){
			return ((CaesarProgramElementNode)node).getCaesarKind();
		}else if(node instanceof ProgramElementNode){
			return ((ProgramElementNode)node).getProgramElementKind();
		}else{
			return null;
		}
	}
	protected List getModifiers(){
		if(node instanceof ProgramElementNode){
			return ((ProgramElementNode)node).getModifiers();
		}else{
			return new ArrayList();
		}
	}
	protected boolean isImplementor(){
		if(node instanceof ProgramElementNode){
			return ((ProgramElementNode)node).isImplementor();
		}else{
			return false;
		}
	}
	protected boolean isOverrider(){
		if(node instanceof ProgramElementNode){
			return ((ProgramElementNode)node).isOverrider();
		}else{
			return false;
		}
	}
	protected boolean isRunnable(){
		if(node instanceof ProgramElementNode){
			return ((ProgramElementNode)node).isRunnable();
		}else{
			return false;
		}
	}
}
