package org.caesarj.ui.marker;

import java.util.Map;

import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.internal.resources.MarkerInfo;
import org.eclipse.core.internal.resources.MarkerManager;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.internal.resources.ResourceInfo;
import org.eclipse.core.internal.resources.ResourceStatus;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.internal.utils.Assert;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class AdviceMarker implements IMarker {

	/** Marker identifier. */
	private long id;
	private Workspace workspace;
	/** Resource with which this marker is associated. */
	private IResource resource;
	private Map attributes;
	public final static java.lang.String ADVICEMARKER = "org.caesarj.advicemarker";
	public final static java.lang.String LINKS = "org.caesarj.links";
	public final static java.lang.String ID = "org.caesarj.adviceid";

	public AdviceMarker(IResource resource, Map attributes) throws CoreException {
		this.resource = resource;
		this.workspace = getWorkspace();
		this.attributes = attributes;
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				addMarker();
			}
		};
		resource.getWorkspace().run(r, null);
	}

	private void addMarker() throws CoreException {
		try {
			workspace.prepareOperation();
			ResourceInfo resourceInfo = getResourceInfo(false, false);
			workspace.beginOperation(true);
			MarkerInfo info = new MarkerInfo();
			info.setType(ADVICEMARKER);
			info.setCreationTime(System.currentTimeMillis());
			workspace.getMarkerManager().add(resource, new MarkerInfo[] { info });
			this.id = info.getId();
		} finally {
			workspace.endOperation(false, null);
		}
		AdviceMarker.this.setAttributes(attributes);
	}

	public ResourceInfo getResourceInfo(boolean phantom, boolean mutable) {
		return workspace.getResourceInfo(this.resource.getFullPath(), phantom, mutable);
	}

	private void checkInfo(MarkerInfo info) throws CoreException {
		if (info == null) {
			String message = Policy.bind("resources.markerNotFound", Long.toString(id)); //$NON-NLS-1$
			throw new ResourceException(
				new ResourceStatus(
					IResourceStatus.MARKER_NOT_FOUND,
					resource.getFullPath(),
					message));
		}
	}

	public void delete() throws CoreException {
		try {
			getWorkspace().prepareOperation();
			getWorkspace().beginOperation(true);
			getWorkspace().getMarkerManager().removeMarker(getResource(), getId());
		} finally {
			getWorkspace().endOperation(false, null);
		}
	}

	private Workspace getWorkspace() {
		return resource == null ? null : (Workspace) resource.getWorkspace();
	}

	public boolean equals(Object object) {
		if (!(object instanceof IMarker))
			return false;
		IMarker other = (IMarker) object;
		return (id == other.getId() && resource.equals(other.getResource()));
	}

	public boolean exists() {
		return getInfo() != null;
	}

	public Object getAttribute(String attributeName) throws CoreException {
		Assert.isNotNull(attributeName);
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttribute(attributeName);
	}

	public int getAttribute(String attributeName, int defaultValue) {
		Assert.isNotNull(attributeName);
		MarkerInfo info = getInfo();
		if (info == null)
			return defaultValue;
		Object value = info.getAttribute(attributeName);
		if (value != null && value instanceof Integer)
			return ((Integer) value).intValue();
		return defaultValue;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		Assert.isNotNull(attributeName);
		MarkerInfo info = getInfo();
		if (info == null)
			return defaultValue;
		Object value = info.getAttribute(attributeName);
		if (value != null && value instanceof String)
			return (String) value;
		return defaultValue;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {
		Assert.isNotNull(attributeName);
		MarkerInfo info = getInfo();
		if (info == null)
			return defaultValue;
		Object value = info.getAttribute(attributeName);
		if (value != null && value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		return defaultValue;
	}
	/**
	 * @see IMarker#getAttributes()
	 */
	public Map getAttributes() throws CoreException {
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttributes();
	}
	/**
	 * @see IMarker#getAttributes(String[])
	 */
	public Object[] getAttributes(String[] attributeNames) throws CoreException {
		Assert.isNotNull(attributeNames);
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getAttributes(attributeNames);
	}
	/**
	 * @see IMarker#getCreationTime
	 */
	public long getCreationTime() throws CoreException {
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getCreationTime();
	}
	/**
	 * @see IMarker#getId
	 */
	public long getId() {
		return id;
	}
	protected MarkerInfo getInfo() {
		return getWorkspace().getMarkerManager().findMarkerInfo(resource, id);
	}
	/**
	 * @see IMarker#getResource
	 */
	public IResource getResource() {
		return resource;
	}
	/**
	 * @see IMarker#getType
	 */
	public String getType() throws CoreException {
		MarkerInfo info = getInfo();
		checkInfo(info);
		return info.getType();
	}

	public int hashCode() {
		return (int) id + resource.hashCode();
	}
	/**
	 * @see IMarker#isSubtypeOf
	 */
	public boolean isSubtypeOf(String type) throws CoreException {
		return getWorkspace().getMarkerManager().isSubtype(getType(), type);
	}
	/**
	 * @see IMarker#setAttribute
	 */
	public void setAttribute(String attributeName, int value) throws CoreException {
		setAttribute(attributeName, new Integer(value));
	}
	/**
	 * @see IMarker#setAttribute
	 */
	public void setAttribute(String attributeName, Object value) throws CoreException {
		Assert.isNotNull(attributeName);
		Workspace workspace = getWorkspace();
		MarkerManager manager = workspace.getMarkerManager();
		try {
			workspace.prepareOperation();
			workspace.beginOperation(true);
			MarkerInfo markerInfo = getInfo();
			checkInfo(markerInfo);

			//only need to generate delta info if none already
			//boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
			//MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
			markerInfo.setAttribute(attributeName, value);
			if (manager.isPersistent(markerInfo))
				((Resource) resource).getResourceInfo(false, true).set(
					ICoreConstants.M_MARKERS_SNAP_DIRTY);
			/*if (needDelta) {
				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
				manager.changedMarkers(resource, new MarkerDelta[] { delta });
			}*/
		} finally {
			workspace.endOperation(false, null);
		}
	}
	/**
	 * @see IMarker#setAttribute
	 */
	public void setAttribute(String attributeName, boolean value) throws CoreException {
		setAttribute(attributeName, value ? Boolean.TRUE : Boolean.FALSE);
	}
	/**
	 * @see IMarker#setAttributes
	 */
	public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {
		Assert.isNotNull(attributeNames);
		Assert.isNotNull(values);
		Workspace workspace = getWorkspace();
		MarkerManager manager = workspace.getMarkerManager();
		try {
			workspace.prepareOperation();
			workspace.beginOperation(true);
			MarkerInfo markerInfo = getInfo();
			checkInfo(markerInfo);

			//only need to generate delta info if none already
			//boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
			//MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
			markerInfo.setAttributes(attributeNames, values);
			if (manager.isPersistent(markerInfo))
				((Resource) resource).getResourceInfo(false, true).set(
					ICoreConstants.M_MARKERS_SNAP_DIRTY);
			/*if (needDelta) {
				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
				manager.changedMarkers(resource, new MarkerDelta[] { delta });
			}*/
		} finally {
			workspace.endOperation(false, null);
		}
	}

	/**
	 * @see IMarker#setAttributes
	 */
	public void setAttributes(Map values) throws CoreException {
		Workspace workspace = getWorkspace();
		MarkerManager manager = workspace.getMarkerManager();
		try {
			workspace.prepareOperation();
			workspace.beginOperation(true);
			MarkerInfo markerInfo = getInfo();
			checkInfo(markerInfo);

			//only need to generate delta info if none already
			//boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
			//MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
			markerInfo.setAttributes(values);
			if (manager.isPersistent(markerInfo))
				((Resource) resource).getResourceInfo(false, true).set(
					ICoreConstants.M_MARKERS_SNAP_DIRTY);
			/*if (needDelta) {
				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
				manager.changedMarkers(resource, new MarkerDelta[] { delta });
			}*/
		} finally {
			workspace.endOperation(false, null);
		}
	}

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found.
	 * <p>
	 * This implementation of the method declared by <code>IAdaptable</code>
	 * passes the request along to the platform's adapter manager; roughly
	 * <code>Platform.getAdapterManager().getAdapter(this, adapter)</code>.
	 * Subclasses may override this method (however, if they do so, they
	 * should invoke the method on their superclass to ensure that the
	 * Platform's adapter manager is consulted).
	 * </p>
	 *
	 * @see IAdaptable#getAdapter
	 * @see Platform#getAdapterManager
	 */
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}