import java.io.File;
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
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDOT;
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
		

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();
		long time = startTime;
		
		int minKeyword = 5;
		
		
		//---------------------------------GET QUESTIONS---------------------------------
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
		
		
		//---------------------------------PROCESS CLASSES---------------------------------
		
		MultiGraph graph = new MultiGraph("graph");
		String filePath = "graph"+File.separator+"graph-60.dgs";
		GraphGenerator<MultiGraph> graphGenerator = new GraphGenerator<MultiGraph>(0.6,graph);
				
		graph = graphGenerator.initializeWithSer("questions.ser", "questionsKeywords.ser")
				.generateGraph(true)
				.deleteLonelyNode()
				.getGraph();
		graph.write(filePath);
		

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
