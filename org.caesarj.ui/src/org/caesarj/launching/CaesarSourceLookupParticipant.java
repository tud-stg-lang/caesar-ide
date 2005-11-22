package org.caesarj.launching;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;

import com.sun.jdi.VMDisconnectedException;

/**
 * A source lookup participant that searches for externalized Caesar classes.
 *  
 * @author meffert
 */
public class CaesarSourceLookupParticipant extends JavaSourceLookupParticipant {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof IAdaptable) {
			IJavaStackFrame frame = (IJavaStackFrame) ((IAdaptable)object).getAdapter(IJavaStackFrame.class);
			try {
				if (frame != null) {
					if (frame.isObsolete()) {
						return null;
					}
					
					// Create filename for externalized classes
					String sourceName = null;
					sourceName = frame.getDeclaringTypeName();
					sourceName = sourceName.replace('.', File.separatorChar);
					sourceName = sourceName.replace('$', File.separatorChar);
					sourceName = sourceName.replaceAll("_Impl", ""); // may remove to many "_Impl"
					
					if (sourceName.length() == 0) {
						// likely a proxy class (see bug 40815)
						sourceName = null;
					} else {
						sourceName = sourceName + ".java"; //$NON-NLS-1$
					}
					return sourceName;	
				}
			} catch (DebugException e) {
				int code = e.getStatus().getCode();
                if (code == IJavaThread.ERR_THREAD_NOT_SUSPENDED || code == IJavaStackFrame.ERR_INVALID_STACK_FRAME ||
						e.getStatus().getException() instanceof VMDisconnectedException) {
					return null;
				}
				throw e;
			}
		}
		if (object instanceof String) {
			// assume it's a file name
			return (String)object;
		}
		return null;
	}

}
