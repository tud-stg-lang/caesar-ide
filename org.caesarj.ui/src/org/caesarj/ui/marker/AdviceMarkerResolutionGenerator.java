package org.caesarj.ui.marker;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class AdviceMarkerResolutionGenerator implements IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

	static Logger logger = Logger.getLogger(AdviceMarkerResolutionGenerator.class);
	
	public IMarkerResolution[] getResolutions(IMarker marker) {
		IMarkerResolution res[] = null;
		try {
			LinkNode advices[] = (LinkNode[]) marker.getAttribute(AdviceMarker.LINKS);
			res = new IMarkerResolution[advices.length];
			for (int i = 0; i < advices.length; i++)
				res[i] =new AdviceMarkerResolution(advices[1]) ;
		} catch (CoreException e) {
			logger.error("Fehler beim auslesen der LINKS aus AdviceMarker",e);
		}
		return res;
	}

	/* (Kein Javadoc)
	 * @see org.eclipse.ui.IMarkerResolutionGenerator2#hasResolutions(org.eclipse.core.resources.IMarker)
	 */
	public boolean hasResolutions(IMarker marker) {
		return true;
	}
}
