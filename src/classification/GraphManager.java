package classification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

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
		for(int id : questionsKeywords.keySet()) {
			graph.addNode(Integer.toString(id));
		}
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
	 * This function return percentage of node in the same class of the model
	 * @param modelGraph
	 * @param testedGraph
	 * @return
	 */
	public static <T extends Graph> double checkSimilarity(T modelGraph, T testedGraph) {
		return 0.0;
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
