package org.caesarj.ui.editor;

import org.caesarj.ui.project.CaesarJProjectTools;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.internal.compiler.SourceElementParser;
import org.eclipse.jdt.internal.core.search.indexing.IndexManager;

public class CJIndexManager extends IndexManager {
	
	public void addSource(IFile resource, IPath containerPath, SourceElementParser parser) {
		if (JavaCore.getPlugin() == null) return;
		if (CaesarJProjectTools.isCJSource(resource)) {
			SearchParticipant participant = SearchEngine.getDefaultSearchParticipant();
			SearchDocument document = new CJSearchDocument(resource.getFullPath().toString(), participant);
			IPath indexLocation = computeIndexLocation(containerPath);
			scheduleDocumentIndexing(document, containerPath, indexLocation, participant);
		}
		else {
			super.addSource(resource, containerPath, parser);
		}
	}

}
