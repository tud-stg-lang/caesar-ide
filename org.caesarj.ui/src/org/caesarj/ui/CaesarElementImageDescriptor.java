package org.caesarj.ui;

import org.caesarj.ui.model.CaesarProgramElementNode;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * A JavaImageDescriptor consists of a base image and several adornments. The adornments
 * are computed according to the flags either passed during creation or set via the method
 * <code>setAdornments</code>. 
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0 
 */
public class CaesarElementImageDescriptor extends CompositeImageDescriptor {

	/** Flag to render the abstract adornment */
	public final static int ABSTRACT = 0x001;

	/** Flag to render the final adornment */
	public final static int FINAL = 0x002;

	/** Flag to render the synchronized adornment */
	public final static int SYNCHRONIZED = 0x004;

	/** Flag to render the static adornment */
	public final static int STATIC = 0x008;

	/** Flag to render the runnable adornment */
	public final static int RUNNABLE = 0x010;

	/** Flag to render the waring adornment */
	public final static int WARNING = 0x020;

	/** Flag to render the error adornment */
	public final static int ERROR = 0x040;

	/** Flag to render the 'override' adornment */
	public final static int OVERRIDES = 0x080;

	/** Flag to render the 'implements' adornment */
	public final static int IMPLEMENTS = 0x100;

	/** Flag to render the 'constructor' adornment */
	public final static int CONSTRUCTOR = 0x200;

	private int computeJavaAdornmentFlags(CaesarProgramElementNode node) {
		int flags = 0;
		if(node == null)return flags;
		//String modifiers = node.getModifiers().toString();
		if (node.isImplementor())
			flags |= IMPLEMENTS;
		if (node.isOverrider())
			flags |= OVERRIDES;
		if (node.isRunnable())
			flags |= RUNNABLE;

		int modif = node.getCAModifiers();
		if ((modif / 8) % 2 == 1)
			flags |= STATIC;
		if ((modif / 16) % 2 == 1)
			flags |= FINAL;
		if ((modif / 32) % 2 == 1)
			flags |= SYNCHRONIZED;
		//if((modif/64)%2==1) flags |= VOLATILE;
		//if((modif/128)%2==1) flags |= TRANSIENT;
		if((modif/256)%2==1) flags |= WARNING;
		if((modif/512)%2==1) flags |= ERROR;
		if ((modif / 1024) % 2 == 1)
			flags |= ABSTRACT;
		return flags;
	}

private ImageDescriptor fBaseImage;
private int fFlags;
private Point fSize;
private boolean interfaceFlag;

public CaesarElementImageDescriptor(
	ImageDescriptor baseImage,
	CaesarProgramElementNode node,
	Point size,
	boolean interfaceFlag) {
	fBaseImage = baseImage;
	this.interfaceFlag = interfaceFlag;
	Assert.isNotNull(fBaseImage);
	fFlags = this.computeJavaAdornmentFlags(node);
	Assert.isTrue(fFlags >= 0);
	fSize = size;
	Assert.isNotNull(fSize);
}

public void setAdornments(int adornments) {
	Assert.isTrue(adornments >= 0);
	fFlags = adornments;
}

public int getAdronments() {
	return fFlags;
}

public void setImageSize(Point size) {
	Assert.isNotNull(size);
	Assert.isTrue(size.x >= 0 && size.y >= 0);
	fSize = size;
}

public Point getImageSize() {
	return new Point(fSize.x, fSize.y);
}

/* (non-Javadoc)
 * Method declared in CompositeImageDescriptor
 */
protected Point getSize() {
	return fSize;
}

/* (non-Javadoc)
 * Method declared on Object.
 */
public boolean equals(Object object) {
	if (object == null || !CaesarElementImageDescriptor.class.equals(object.getClass()))
		return false;

	CaesarElementImageDescriptor other = (CaesarElementImageDescriptor) object;
	return (
		fBaseImage.equals(other.fBaseImage) && fFlags == other.fFlags && fSize.equals(other.fSize));
}

/* (non-Javadoc)
 * Method declared on Object.
 */
public int hashCode() {
	return fBaseImage.hashCode() | fFlags | fSize.hashCode();
}

/* (non-Javadoc)
 * Method declared in CompositeImageDescriptor
 */
protected void drawCompositeImage(int width, int height) {
	ImageData bg;
	if ((bg = fBaseImage.getImageData()) == null)
		bg = DEFAULT_IMAGE_DATA;

	drawImage(bg, 0, 0);
	drawTopRight();
	drawBottomRight();
	drawBottomLeft();
}

private void drawBottomRight() {
	Point size = getSize();
	int x = size.x;
	ImageData data = null;
	if ((fFlags & OVERRIDES) != 0) {
		data = JavaPluginImages.DESC_OVR_OVERRIDES.getImageData();
		x -= data.width;
		drawImage(data, x, size.y - data.height);
	}
	if ((fFlags & IMPLEMENTS) != 0) {
		data = JavaPluginImages.DESC_OVR_IMPLEMENTS.getImageData();
		x -= data.width;
		drawImage(data, x, size.y - data.height);
	}
	if ((fFlags & SYNCHRONIZED) != 0) {
		data = JavaPluginImages.DESC_OVR_SYNCH.getImageData();
		x -= data.width;
		drawImage(data, x, size.y - data.height);
	}
	if ((fFlags & RUNNABLE) != 0) {
		data = JavaPluginImages.DESC_OVR_RUN.getImageData();
		x -= data.width;
		drawImage(data, x, size.y - data.height);
	}
}

private void drawBottomLeft() {
	Point size = getSize();
	int x = 0;
	ImageData data = null;
	if ((fFlags & ERROR) != 0) {
		data = JavaPluginImages.DESC_OVR_ERROR.getImageData();
		drawImage(data, x, size.y - data.height);
		x += data.width;
	}
	if ((fFlags & WARNING) != 0) {
		data = JavaPluginImages.DESC_OVR_WARNING.getImageData();
		drawImage(data, x, size.y - data.height);
		x += data.width;
	}
}

private void drawTopRight() {
	int x = getSize().x;
	ImageData data = null;
	if ((fFlags & ABSTRACT) != 0) {
		if (interfaceFlag)
			data = CaesarPluginImages.DESC_COLLAB_CO.getImageData();
		else
			data = JavaPluginImages.DESC_OVR_ABSTRACT.getImageData();
		x -= data.width;
		drawImage(data, x, 0);
	}
	if ((fFlags & CONSTRUCTOR) != 0) {
		data = JavaPluginImages.DESC_OVR_CONSTRUCTOR.getImageData();
		x -= data.width;
		drawImage(data, x, 0);
	}
	if ((fFlags & FINAL) != 0) {
		data = JavaPluginImages.DESC_OVR_FINAL.getImageData();
		x -= data.width;
		drawImage(data, x, 0);
	}
	if ((fFlags & STATIC) != 0) {
		data = JavaPluginImages.DESC_OVR_STATIC.getImageData();
		x -= data.width;
		drawImage(data, x, 0);
	}
}
}