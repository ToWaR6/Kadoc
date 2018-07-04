import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.GraphParseException;

import classification.GraphManager;
import factory.PostLinksFactory;
import model.PostLink;

public class mainFlo {
	public static void main(String[] args) {
		SingleGraph graph = new SingleGraph("Graphes des questions transformées en liste de keywords");
		graph.setAutoCreate(true);
		graph.setStrict(false);
		try {
			graph.read("graph-0.6.dgs");
		} catch (ElementNotFoundException | IOException | GraphParseException e) {
			e.printStackTrace();
		}
		PostLinksFactory postLinksFactory = new PostLinksFactory();
		ArrayList<PostLink> postLinks = null;
		try {
			postLinks = postLinksFactory.getPostLinks("LinkedPosts");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		MultiGraph postLinkGraph = new MultiGraph("Model");
		postLinkGraph.setAutoCreate(true);
		postLinkGraph.setStrict(false);
		int questionId,linkedQuestionId;
		for (PostLink postLink : postLinks) {
			questionId = postLink.getQuestion().getId();
			linkedQuestionId = postLink.getLinkedQuestion().getId();
			postLinkGraph.addEdge(questionId+"."+linkedQuestionId,
					Integer.toString(questionId),
					Integer.toString(linkedQuestionId));
		}
		System.out.println("==================================");
		System.out.println("PostLinkGraph \t|\t Graph");
		System.out.println("#Node : "+ postLinkGraph.getNodeCount()+"\t|\t "+graph.getNodeCount());
		System.out.println("#Edge : "+ postLinkGraph.getEdgeCount()+"\t|\t "+graph.getEdgeCount());
		System.out.println("==================================");
		GraphManager.getSimilarComponent(postLinkGraph, graph).display();
		HashSet<String> nodes = GraphManager.markedSimilarComponentNode(postLinkGraph, graph);
		graph.setAttribute("ui.stylesheet", "node {\r\n" + 
				"        fill-color: black;\r\n" + 
				"    }\r\n" + 
				"    node.marked {\r\n" + 
				"        fill-color: red;\r\n" + 
				"    }");
		for(String node : nodes) {
			graph.getNode(node).addAttribute("ui.class", "marked");
		}
		graph.display();
	}
}
