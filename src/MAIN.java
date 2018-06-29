import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.xml.sax.SAXException;

import Classification.*;
import factory.QuestionFactory;
import model.Question;


public class MAIN {
	
	private static class TfIdf {
		private String term;
		private double score;
		public TfIdf(String term, double score) {
			super();
			this.term = term;
			this.score = score;
		}
		public String getTerm() {
			return term;
		}
		public void setTerm(String term) {
			this.term = term;
		}
		public double getScore() {
			return score;
		}
		public void setScore(double score) {
			this.score = score;
		}
		public String toString() {
			return this.term+" | "+this.score;
		}
	}
	
	public static ArrayList<String> extractKeyword(String[] keywords, String text) {
		ArrayList<String> listKeyword = new ArrayList<String>();
		for (String keyword : keywords) {
			if (keyword.length() > 2) {
				if (text.toLowerCase().contains(keyword)) {
					listKeyword.add(keyword);
				}
			}
		}
		return listKeyword;
	}
	
	public static void saveObjectToFile(String nameFile, Object object) throws IOException {
		FileOutputStream fos = new FileOutputStream(nameFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
	}
	
	public static Object loadObjectFromFile(String nameFile) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(nameFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object o = ois.readObject();
		ois.close();
		return o;
	}	
	

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();
		long time = startTime;
		
		int minKeyword = 5;
				
		
		QuestionFactory qf = new QuestionFactory();
		
//		System.out.print("Questions : ");
//		HashMap<Integer, Question> questions1 = qf.getAllSqlQuestions();
//		System.out.println(questions1.size());
//		saveObjectToFile("Questions.ser", questions1);

		
		//---------------------------------GET QUESTIONS---------------------------------
		HashMap<Integer, Question> questions = (HashMap<Integer, Question>) loadObjectFromFile("Questions.ser");
		System.err.println("Get questions : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
		time = System.nanoTime();
				
//		HashMap<Integer, ArrayList<String>> questionsKeyword = new HashMap<Integer, ArrayList<String>>(); 
		
		//---------------------------------GET TF-IDF (XML)---------------------------------
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.parse("tf-idf_pre.xml");
//		System.err.println("Parser xml : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();
		
		
		//---------------------------------GET KEYWORDS---------------------------------
//		NodeList nodeListTerm = doc.getElementsByTagName("name_term");
//		NodeList nodeListScore = doc.getElementsByTagName("score");
//		String[] keywords = new String[nodeListTerm.getLength()];
//		TfIdf[] terms = new TfIdf[nodeListTerm.getLength()];
//		for (int i=0; i < nodeListTerm.getLength(); i++) {
//			keywords[i] = nodeListTerm.item(i).getTextContent();
//			terms[i] = new TfIdf(nodeListTerm.item(i).getTextContent(), Double.parseDouble(nodeListScore.item(i).getTextContent()));
//		}
//		System.err.println("Keyword gen : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();

		
		//---------------------------------EXTRACT KEYWORDS FROM QUESTIONS---------------------------------
//		for (int id : questions.keySet()) {
//			questionsKeyword.put(id, extractKeyword(keywords, questions.get(id).getTitle()+questions.get(id).getBody()));
//		}
//		System.err.println("question keyword : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
//		time = System.nanoTime();
		
		
		//---------------------------------SELECTION QUESTION KEYWORD---------------------------------
//		HashMap<Integer, ArrayList<String>> tmpSer = new HashMap<Integer, ArrayList<String>>();
//		for (int id : questionsKeyword.keySet()) {
//			if (questionsKeyword.get(id).size() >= minKeyword) {
//				tmpSer.put(id, questionsKeyword.get(id));
//			}
//		}
		
		//---------------------------------SAVE INTO FILE---------------------------------
//		saveObjectToFile("questionsKeywords_pre.ser", tmpSer);
		
		//---------------------------------GET QUESTIONS KEYWORDS---------------------------------
		HashMap<Integer, ArrayList<String>> questionsKeywords = (HashMap<Integer, ArrayList<String>>) loadObjectFromFile("questionsKeywords_pre.ser");
		System.err.println("Get questionKeyword : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
		time = System.nanoTime();
		
		//---------------------------------PROCESS CLASSES---------------------------------
		
		SingleGraph graph = new SingleGraph("Graphes des questions transformées en liste de keywords");
		
		for (int id : questionsKeywords.keySet()) {
			graph.addNode(Integer.toString(id));
		}
		double rate = 0.6;
		SimilarityMeasure<ArrayList<String>> similarityMeasure = new TrivialSimiliratyMeasure(questionsKeywords, rate);
		int cpt = 0;
		int nbATraite = questionsKeywords.size();
		int onePercent = nbATraite/100;
		Question question;
		for (int id : questionsKeywords.keySet()) {
			question = questions.get(id);
			if (cpt%onePercent==0) {
				System.out.print(cpt/onePercent+"% \r");
			}
			int idMostSimilar = similarityMeasure.getMostSimilarQuestion(question);
			if (idMostSimilar != -1) {
				graph.addEdge(id+"."+idMostSimilar, Integer.toString(id), Integer.toString(idMostSimilar), true);
				
			}
			cpt++;
			if (cpt >= nbATraite) {
				break;
			}
		}
		for (int id : questionsKeywords.keySet()) {
			Node n = graph.getNode(Integer.toString(id));
			if(n.getDegree()==0)
				graph.removeNode(n.getIndex());
		}
		
		ConnectedComponents cc = new ConnectedComponents();
		cc.init(graph);
		System.out.println(cc.getConnectedComponentsCount() + " composante(s) connexe(s) | C:"+cc.getGiantComponent().size());
		
		graph.display();
		
		System.err.println("Graph Process : "+(float)(System.nanoTime() - time)/1000000000+" secondes");
		time = System.nanoTime();
		
		
		/*
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(System.nanoTime() - startTime)/1000000000+" secondes");
//		System.out.println(questions.size()+" questions sont dans 'questions'");
		System.out.println("-------------------------------------------------------");
		*/
		
		

	}

}
