package org.caesarj.ui.editor;

import org.caesarj.ui.javamodel.AspectsConvertingParser;
import org.caesarj.ui.javamodel.ConversionOptions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jdt.internal.core.util.Util;

public class CJSearchDocument extends SearchDocument {

	private IFile file;
	protected byte[] byteContents;
	protected char[] charContents;
	
	public CJSearchDocument(String documentPath, SearchParticipant participant) {
		super(documentPath, participant);
	}
	
	public byte[] getByteContents() {
		if (this.byteContents != null) return this.byteContents;
		try {
			return Util.getResourceContentsAsByteArray(getFile());
		} catch (JavaModelException e) {
			if (BasicSearchEngine.VERBOSE || JobManager.VERBOSE) { // used during search and during indexing
				e.printStackTrace();
			}
			return null;
		}
	}
	public char[] getCharContents() {
		if (this.charContents != null) return this.charContents;
		try {
			char[] contents = Util.getResourceContentsAsCharArray(getFile());
			AspectsConvertingParser conv = new AspectsConvertingParser(contents);
			conv.convert(ConversionOptions.STANDARD);
			return conv.content;			
		} catch (JavaModelException e) {
			if (BasicSearchEngine.VERBOSE || JobManager.VERBOSE) { // used during search and during indexing
				e.printStackTrace();
			}
			return null;
		}
	}
	public String getEncoding() {
		// Return the encoding of the associated file
		IFile resource = getFile();
		if (resource != null) {
			try {
				return resource.getCharset();
			}
			catch(CoreException ce) {
				try {
					return ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
				} catch (CoreException e) {
					// use no encoding
				}
			}
		}
		return null;
	}
	private IFile getFile() {
		if (this.file == null)
			this.file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getPath()));
		return this.file;
	}
	public String toString() {
		return "SearchDocument for " + getPath(); //$NON-NLS-1$
	}
	
}
