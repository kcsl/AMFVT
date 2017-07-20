package com.kcsl.amfvt.ui.smart;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Graph;
import com.ensoftcorp.atlas.core.db.graph.GraphElement;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.graph.operation.InducedGraph;
import com.ensoftcorp.atlas.core.db.set.AtlasHashSet;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.db.set.SingletonAtlasSet;
import com.ensoftcorp.atlas.core.highlight.Highlighter;
import com.ensoftcorp.atlas.core.query.Attr;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.script.StyledResult;
import com.ensoftcorp.atlas.core.script.UniverseManipulator;
import com.ensoftcorp.atlas.core.script.UniverseManipulator.Manipulation;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.ensoftcorp.atlas.ui.scripts.selections.AtlasSmartViewScript;
import com.ensoftcorp.atlas.ui.scripts.selections.FilteringAtlasSmartViewScript;
import com.ensoftcorp.atlas.ui.selection.event.IAtlasSelectionEvent;
import com.ensoftcorp.open.java.commons.analysis.CommonQueries;
import com.ensoftcorp.open.java.commons.analysis.ThrowableAnalysis;

public class IntegrityCheckerSmartView extends FilteringAtlasSmartViewScript implements AtlasSmartViewScript {
	
	@Override
	public String[] getSupportedEdgeTags() {
		return new String[]{};
	}

	@Override
	public String[] getSupportedNodeTags() {
		return new String[]{XCSG.Variable, XCSG.DataFlow_Node};
	}

	@Override
	public String getTitle() {
		return "Integrity Checker";				
	}

	@Override
	protected StyledResult selectionChanged(IAtlasSelectionEvent selection, Q arg1) {
		Q input = filter(selection);
		
		if(CommonQueries.isEmpty(input)){
			return null;
		}
		
		Q dfEdges = Common.edges(XCSG.DataFlow_Edge);
		Q assignments = dfEdges.reverse(input).nodesTaggedWithAny(XCSG.Assignment);
		Q assignmentCF = assignments.parent().nodes(XCSG.ControlFlow_Node);
		
		Q governingConditions = CommonQueries.conditionsAbove(assignmentCF);
		
		Highlighter h = new Highlighter();
		Q inputCF = input.parent().nodes(XCSG.ControlFlow_Node);
		h.highlight(input.union(inputCF), Color.CYAN);
		h.highlight(governingConditions, Color.RED);
		
		return new StyledResult(Common.edges(XCSG.ControlFlow_Edge).between(governingConditions, assignmentCF), h);
	}

}
