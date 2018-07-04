package classification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.ConnectedComponents.ConnectedComponent;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import factory.QuestionFactory;
import model.Question;

/**
 * Allow to generate graph from SQL or File text
 * @author Dell'omo/Crauser
 *
 * @param <T> the type of graph
 */
public class GraphManager<T extends Graph> {
	private double rate;
	private HashMap<Integer, ArrayList<String>> questionsKeywords;
	private HashMap<Integer, Question> questions;
	
	public GraphManager(double rate,T graph) {
		this.rate = rate;
		this.questionsKeywords = new HashMap<Integer, ArrayList<String>>();
		this.questions = new HashMap<Integer, Question>();
	}
	/**
	 * Allows to initialized questions and keywords with the database
	 * @param save
	 * @return
	 */
	public GraphManager<T> initializeWithSQL(String keywordsFilepath,boolean save) {
		QuestionFactory questionFactory = new QuestionFactory();
		questions  = questionFactory.getAllSqlQuestions();
		initKeywords(keywordsFilepath);
		if(save)
			saveQuestions();
		return this;
	}
	
	/**
	 * Allows to initialized questions and questionsKeywords with the file serialized
	 * @param questionsFilePath
	 * @param keywordsFilePath
	 * @return
	 */
	public GraphManager<T> initializeWithSer(String questionsFilePath,String keywordsFilepath) {
		QuestionFactory questionFactory = new QuestionFactory();
		questions = questionFactory.getAllSerializedQuestions(questionsFilePath);
		initKeywords(keywordsFilepath);
		return this;
	}
	
	public GraphManager<T> generateGraph(T graph){
		return generateGraph(graph,false);
	}
	public GraphManager<T> generateGraph(T graph,boolean displayProgress) {
		SimilarityMeasure<ArrayList<String>> similarityMeasure = new TrivialSimiliratyMeasure(questionsKeywords, rate);
		Question question;
		graph.setStrict(false);
		graph.setAutoCreate(true);
		int cpt = 0;
		int nbATraite = questionsKeywords.size();
		int onePercent = nbATraite/100;
		for (int id : questionsKeywords.keySet()) {
			question = questions.get(id);
			if (displayProgress & cpt%onePercent==0) 
				System.out.print(cpt/onePercent+"% \r");
			int mostSimilarId = similarityMeasure.getMostSimilarQuestion(question);
			if (mostSimilarId != -1)
				graph.addEdge(id+"."+mostSimilarId, Integer.toString(id), Integer.toString(mostSimilarId), true);
			cpt++;
		}
		return this;
	}
	
	public GraphManager<T> deleteLonelyNode(T graph){
		for (int id : questionsKeywords.keySet()) {
			Node n = graph.getNode(Integer.toString(id));
			if(n.getDegree()==0)
				graph.removeNode(n.getIndex());
		}
		return this;
	}
	/**
	 * This function returns the graph where the component are similar
	 * It loops through all the pair of node in a component of the model 
	 * and try to find if two nodes are in the same component in the tested graph 
	 * If these two are in the same component then an edge is created
	 * @param modelGraph
	 * @param testedGraph
	 * @return
	 */
	public static <T extends Graph> Graph getSimilarComponent(T modelGraph, T testedGraph) {
		String countAttribute = "idComposante";
		ConnectedComponents connectedComponentsModelGraph = 
				new ConnectedComponents(modelGraph);
		connectedComponentsModelGraph.compute();
		connectedComponentsModelGraph.setCountAttribute(countAttribute);
		
		ConnectedComponents connectedComponentsTestedGraph =
				new ConnectedComponents(testedGraph);
		connectedComponentsTestedGraph.compute();
		connectedComponentsTestedGraph.setCountAttribute(countAttribute);
		
		Iterator<ConnectedComponent> iteratorConnectedComponent = 
				connectedComponentsModelGraph.iterator();
		ConnectedComponent connectedComponent;
		Node node0Tested,node1Tested = null;
		
		Graph graphOfGoodComponents = new SingleGraph("Good component");
		graphOfGoodComponents.setStrict(false);
		graphOfGoodComponents.setAutoCreate(true);
		while(iteratorConnectedComponent.hasNext()) {
			connectedComponent = iteratorConnectedComponent.next();
			for(Node node0 : connectedComponent.getEachNode()) {
				node0Tested = testedGraph.getNode(node0.toString());
				if(node0Tested != null) {
					for(Node node1 : connectedComponent.getEachNode()) {
						node1Tested = testedGraph.getNode(node1.toString());
						if(node1Tested != null && !node1Tested.getId().equals(node0Tested.getId())
								&& node1Tested.getIndex() > node0Tested.getIndex()) { 
							if (node0Tested.getAttribute(countAttribute) == node1Tested.getAttribute(countAttribute)) {
								graphOfGoodComponents.addEdge(
										node0Tested.getId()+","+node1Tested.getId(),
										node0Tested.getId(),
										node1Tested.getId());
							}
						}
					}
				}
			}
		}
		return graphOfGoodComponents;
	}
	
	/**
	 * This function returns the graph where the component are similar
	 * It loops through all the pair of node in a component of the model 
	 * and try to find if two nodes are in the same component in the tested graph 
	 * If these two are in the same component then an edge is created
	 * @param modelGraph
	 * @param testedGraph
	 * @return
	 */
	public static <T extends Graph> HashSet<String> markedSimilarComponentNode(T modelGraph, T testedGraph) {
		String countAttribute = "idComposante";
		ConnectedComponents connectedComponentsModelGraph = 
				new ConnectedComponents(modelGraph);
		connectedComponentsModelGraph.compute();
		connectedComponentsModelGraph.setCountAttribute(countAttribute);
		
		ConnectedComponents connectedComponentsTestedGraph =
				new ConnectedComponents(testedGraph);
		connectedComponentsTestedGraph.compute();
		connectedComponentsTestedGraph.setCountAttribute(countAttribute);
		
		Iterator<ConnectedComponent> iteratorConnectedComponent = 
				connectedComponentsModelGraph.iterator();
		ConnectedComponent connectedComponent;
		Node node0Tested,node1Tested = null;
		HashSet <String> nodes = new HashSet<>();
		while(iteratorConnectedComponent.hasNext()) {
			connectedComponent = iteratorConnectedComponent.next();
			for(Node node0 : connectedComponent.getEachNode()) {
				node0Tested = testedGraph.getNode(node0.toString());
				if(node0Tested != null) {
					for(Node node1 : connectedComponent.getEachNode()) {
						node1Tested = testedGraph.getNode(node1.toString());
						if(node1Tested != null && !node1Tested.getId().equals(node0Tested.getId())
								&& node1Tested.getIndex() > node0Tested.getIndex()) { 
							if (node0Tested.getAttribute(countAttribute) == node1Tested.getAttribute(countAttribute)) {
								nodes.add(node0Tested.toString());
								nodes.add(node1Tested.toString());
							}
						}
					}
				}
			}
		}
		return nodes;
	}
	
	@SuppressWarnings("unchecked")
	private void initKeywords(String keywordsFilepath) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(keywordsFilepath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			questionsKeywords = (HashMap<Integer, ArrayList<String>>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Problem while loading...");
		}
	}
	
	private void saveQuestions() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("questions.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(questions);
			oos.close();
		} catch (IOException  e) {
			e.printStackTrace();
		}
		
	}
}
