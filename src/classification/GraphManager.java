package classification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
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
 * 
 * @author Dell'omo/Crauser
 *
 * @param <T> the type of graph
 */
public class GraphManager<T extends Graph> {
	private double rate;
	private HashMap<Integer, ArrayList<String>> questionsKeywords;
	private HashMap<Integer, Question> questions;
	
	public GraphManager(double similarityThreshold) {
		this.rate = similarityThreshold;
		this.questionsKeywords = new HashMap<Integer, ArrayList<String>>();
		this.questions = new HashMap<Integer, Question>();
	}
	
	/**
	 * Allows to initialize questions and keywords with the database
	 * 
	 * @param save
	 * @return this, the current GraphManager object
	 * @throws SQLException 
	 */
	public GraphManager<T> initializeWithSQL(String keywordsFilepath,boolean save) throws SQLException {
		QuestionFactory questionFactory = new QuestionFactory();
		questions  = questionFactory.getAllSqlQuestions();
		initKeywords(keywordsFilepath);
		if(save) {
			saveQuestions();
		}
		return this;
	}
	
	/**
	 * Allows to initialize questions and questionsKeywords with the serialized file
	 * 
	 * @param questionsFilePath
	 * @param keywordsFilePath
	 * @return this, the current GraphManager object
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public GraphManager<T> initializeWithSer(String questionsFilePath,String keywordsFilepath) throws ClassNotFoundException, IOException {
		QuestionFactory questionFactory = new QuestionFactory();
		questions = questionFactory.getAllSerializedQuestions(questionsFilePath);
		initKeywords(keywordsFilepath);
		return this;
	}
	/**
	 * Call {@link generateGraph(T,boolean)} with the boolean false
	 * 
	 * @param graph the reference of the graph to modify
	 * @return The caller of the method
	 */
	public GraphManager<T> generateGraph(T graph){
		return generateGraph(graph,false);
	}
	
	/**
	 * Generate a graph with the questionsKeywords and the questions <br>
	 * It measures the similarity between to question and create a edge if the similiraty is over a threshold 
	 * 
	 * @param graph the reference of the graph to modify
	 * @param displayProgress True if the user want to know the progress of the generation
	 * @return The caller of the method
	 */
	public GraphManager<T> generateGraph(T graph,boolean displayProgress) {
		TrivialSimiliratyMeasure similarityMeasure = new TrivialSimiliratyMeasure(questionsKeywords, rate);
		Question question;
		graph.setStrict(false);
		graph.setAutoCreate(true);
		int cpt = 0;
		int nbATraiter = questionsKeywords.size();
		int onePercent = (int)Math.ceil(nbATraiter/100D);
		for (int id : questionsKeywords.keySet()) {
			question = questions.get(id);
			if (displayProgress & cpt%onePercent==0) {
				System.out.print(cpt/onePercent+"% \r");
			}
			int mostSimilarId = similarityMeasure.getMostSimilarQuestion(question);
			if (mostSimilarId != -1) {
				graph.addEdge(id+"."+mostSimilarId, Integer.toString(id), Integer.toString(mostSimilarId), true);
			}
			cpt++;
		}
		return this;
	}
	
	/**
	 * Delete node that has no neighbor
	 * 
	 * @param graph
	 * @return this, the current GraphManager object
	 */
	public GraphManager<T> deleteLonelyNode(T graph){
		for (int id : questionsKeywords.keySet()) {
			Node node = graph.getNode(Integer.toString(id));
			if(node!=null && node.getDegree()==0) {
				graph.removeNode(node.toString());
			}
		}
		return this;
	}
	
	/**
	 * This function returns the graph where the component are similar
	 * It loops through all the pair of node in a component of the model 
	 * and try to find if two nodes are in the same component in the tested graph 
	 * If these two are in the same component then an edge is created
	 * 
	 * @param modelGraph
	 * @param testedGraph
	 * @return the graph with similar question connect
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
	 * This function returns an arrayList of node where the component are similar
	 * It loops through all the pair of node in a component of the model 
	 * and try to find if two nodes are in the same component in the tested graph 
	 * If these two are in the same component then an edge is created
	 * 
	 * @param modelGraph
	 * @param testedGraph
	 * @return list of marked node's id
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
	
	/**
	 * Initialize the questions' keywords
	 * 
	 * @param keywordsFilepath
	 */
	@SuppressWarnings("unchecked")
	private void initKeywords(String keywordsFilepath) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(keywordsFilepath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			questionsKeywords = (HashMap<Integer, ArrayList<String>>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Problem while loading...");
		}
	}
	
	/**
	 * Save questions in a file : "questions.ser"
	 */
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
