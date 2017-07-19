package com.amfvt.ui.smart;

import java.awt.Color;

import com.ensoftcorp.atlas.core.highlight.Highlighter;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.StyledResult;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.atlas.ui.scripts.selections.AtlasSmartViewScript;
import com.ensoftcorp.atlas.ui.scripts.selections.FilteringAtlasSmartViewScript;
import com.ensoftcorp.atlas.ui.selection.event.IAtlasSelectionEvent;
import com.ensoftcorp.open.android.essentials.subsystems.AndroidSubsystem;
import com.ensoftcorp.open.commons.subsystems.Subsystems;
import com.ensoftcorp.open.java.commons.analysis.CommonQueries;
import com.ensoftcorp.open.java.commons.analysis.SetDefinitions;

public class SubsystemSmartView extends FilteringAtlasSmartViewScript implements AtlasSmartViewScript {

	public static String SUBSYSTEM = AndroidSubsystem.TAG;
	
	@Override
	public String getTitle() {
		return "Subsystem Interactions";
	}

	@Override
	public String[] getSupportedEdgeTags() {
		return new String[]{};
	}

	@Override
	public String[] getSupportedNodeTags() {
		return new String[]{XCSG.Method};
	}
	
	@Override
	protected StyledResult selectionChanged(IAtlasSelectionEvent selection, Q arg1) {
		Q input = filter(selection);
		
		if(CommonQueries.isEmpty(input)){
			return null;
		}
		
		Highlighter h = new Highlighter();
		h.highlight(input, Color.CYAN);
		
		Q app = SetDefinitions.app();
		Q subsystem = Subsystems.getSubsystemContents(SUBSYSTEM);

		return new StyledResult(CommonQueries.interactions(app, subsystem, XCSG.Call), h);
	}

}
