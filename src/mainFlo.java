import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.GraphParseException;

import classification.GraphManager;
import factory.PostLinksFactory;
import model.PostLink;

public class mainFlo {
	public static void main(String[] args) {
		MultiGraph graph = new MultiGraph("Graphes des questions transformées en liste de keywords");
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
		
		Graph postLinkGraph = new MultiGraph("PostLink");
		postLinkGraph.setAutoCreate(true);
		postLinkGraph.setStrict(false);
		int questionId,linkedQuestionId;
		for (PostLink postLink : postLinks) {
			questionId = postLink.getQuestion().getId();
			linkedQuestionId = postLink.getLinkedQuestion().getId();
			postLinkGraph.addEdge(questionId+"."+linkedQuestionId,
					Integer.toString(questionId),
					Integer.toString(linkedQuestionId),
					true);
		}
		System.out.println("==================================");
		System.out.println("PostLinkGraph \t|\t Graph");
		System.out.println("#Node : "+ postLinkGraph.getNodeCount()+"\t|\t "+graph.getNodeCount());
		System.out.println("#Edge : "+ postLinkGraph.getEdgeCount()+"\t|\t "+graph.getEdgeCount());
		System.out.println("==================================");
		System.out.println(GraphManager.checkSimilarity(postLinkGraph, graph));
	}
}
