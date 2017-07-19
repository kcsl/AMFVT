package com.amfvt.ui.smart;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ensoftcorp.atlas.core.db.graph.Edge;
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

/**
 * A Smart View script to quickly show who will throw/catch exceptions.
 * 
 * @author Tom Deering, Ben Holland
 */
public class ExceptionalFlowsSmartView extends FilteringAtlasSmartViewScript implements AtlasSmartViewScript {
	private static final Color EMPHASIS = new Color(0xFF, 0x6B, 0x6B);
	private static final String RESPONDS = "RESPONDS";
	
	@Override
	public String[] getSupportedEdgeTags() {
		return new String[]{};
	}

	@Override
	public String[] getSupportedNodeTags() {
		return new String[]{XCSG.Type, XCSG.Method, XCSG.ControlFlow_Node};
	}

	@Override
	public String getTitle() {
		return "Exceptional Flows";				
	}

	@Override
	protected StyledResult selectionChanged(IAtlasSelectionEvent selection, Q arg1) {
		Q input = filter(selection);
		
		if(CommonQueries.isEmpty(input)){
			return null;
		}
		
		Q input1 = input.nodesTaggedWithAny(XCSG.Method, XCSG.ControlFlow_Node);
		Q input2 = input.nodesTaggedWithAny(XCSG.Type);
		
		Highlighter h = new Highlighter();
		Q res1 = resultForThrowers(input1, h);
		Q res2 = resultForCatchers(input1, h);
		Q doubleConnected = res1.edgesTaggedWithAny(RESPONDS).retainEdges().intersection(
				res2.edgesTaggedWithAny(RESPONDS).retainEdges());
		res2 = res2.differenceEdges(doubleConnected.induce(res2));
		
		Q res = res1.union(
				res2, 
				resultForExceptionTypes(input2, h));
		
		return new StyledResult(res, h);
	}

	private Q resultForThrowers(Q input, Highlighter h){		
		AtlasSet<GraphElement> resNodes = new AtlasHashSet<GraphElement>();
		AtlasSet<GraphElement> resEdges = new AtlasHashSet<GraphElement>();
		
		Set<Manipulation> edgeAdditions = new HashSet<Manipulation>();
		
		Q groupContext = ThrowableAnalysis.findCatchForThrows(input);
		Q throwContext = groupContext.edgesTaggedWithAny(Attr.Edge.THROW).retainEdges();
		Q catchContext = groupContext.edgesTaggedWithAny(Attr.Edge.CATCH).retainEdges();
		
		Q throwerQ = throwContext.forwardStep(CommonQueries.localDeclarations(input)).retainEdges().roots();
		AtlasSet<Node> throwers = throwerQ.eval().nodes();
		resNodes.addAll(throwers);
		
		UniverseManipulator um = new UniverseManipulator();
		
		Map<String, Object> attr;
		Set<String> tags = new HashSet<String>();
		tags.add(RESPONDS);
		
		for(GraphElement thrower : throwers){
			Q catchForThrows = ThrowableAnalysis.findCatchForThrows(Common.toQ(Common.toGraph(thrower))).intersection(groupContext);
			AtlasSet<GraphElement> responders = new AtlasHashSet<GraphElement>();
			responders.addAll(catchContext.forwardStep(catchForThrows).retainEdges().roots().eval().nodes());
			resNodes.addAll(responders);
			
			attr = new HashMap<String, Object>();
			attr.put(XCSG.name, RESPONDS);
			attr.put(RESPONDS, catchForThrows.eval());
			
			edgeAdditions.add(um.createEdge(tags, attr, new SingletonAtlasSet<GraphElement>(thrower), responders));
		}
		
		um.perform();

		for(Manipulation m : edgeAdditions) resEdges.addAll(m.getResult());
		Q res = Common.toQ(new InducedGraph(resNodes, resEdges));
		h.highlightNodes(res.difference(throwerQ), EMPHASIS);
		
		return res;
	}
	
	private Q resultForCatchers(Q input, Highlighter h){
		AtlasSet<GraphElement> resNodes = new AtlasHashSet<GraphElement>();
		AtlasSet<GraphElement> resEdges = new AtlasHashSet<GraphElement>();
		
		Set<Manipulation> edgeAdditions = new HashSet<Manipulation>();
		
		Q groupContext = ThrowableAnalysis.findThrowForCatch(input);
		Q throwContext = groupContext.edgesTaggedWithAny(Attr.Edge.THROW).retainEdges();
		Q catchContext = groupContext.edgesTaggedWithAny(Attr.Edge.CATCH).retainEdges();
		
		Q catcherQ = catchContext.forwardStep(CommonQueries.localDeclarations(input)).retainEdges().roots();
		AtlasSet<GraphElement> catchers = new AtlasHashSet<GraphElement>();
		catchers.addAll(catcherQ.eval().nodes());
		resNodes.addAll(catchers);
		
		UniverseManipulator um = new UniverseManipulator();
		
		Map<String, Object> attr;
		Set<String> tags = new HashSet<String>();
		tags.add(RESPONDS);
		
		for(GraphElement catcher : catchers){
			Q throwForCatch = ThrowableAnalysis.findThrowForCatch(Common.toQ(Common.toGraph(catcher))).intersection(groupContext);
			AtlasSet<GraphElement> responders = new AtlasHashSet<GraphElement>();
			responders.addAll(throwContext.forwardStep(throwForCatch).retainEdges().roots().eval().nodes());
			resNodes.addAll(responders);
			
			attr = new HashMap<String, Object>();
			attr.put(XCSG.name, RESPONDS);
			attr.put(RESPONDS, throwForCatch.eval());
			
			edgeAdditions.add(um.createEdge(tags, attr, responders, new SingletonAtlasSet<GraphElement>(catcher)));
		}
		
		um.perform();
		
		for(Manipulation m : edgeAdditions) resEdges.addAll(m.getResult());
		Q res = Common.toQ(new InducedGraph(resNodes, resEdges));
		h.highlightNodes(res.difference(catcherQ), EMPHASIS);
		
		return res;
	}
	
	private Q resultForExceptionTypes(Q input, Highlighter h){
		input = input.intersection(Common.edges(XCSG.Supertype).reverse(Common.typeSelect("java.lang","Throwable")));
		Q context = Common.edges(Attr.Edge.THROW, Attr.Edge.CATCH).edgesTaggedWithAny(Attr.Edge.PER_CONTROL_FLOW);
		Q thrownOrCaught = context.reverseStep(input);
		
		h.highlightNodes(thrownOrCaught.retainEdges().roots(), EMPHASIS);
		
		return thrownOrCaught;
	}

}
