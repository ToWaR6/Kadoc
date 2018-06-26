import java.io.IOException;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import factory.QuestionFactory;


public class MAIN {

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();		

		QuestionFactory qf = new QuestionFactory();
		/*
		System.out.print("Users : ");
		HashMap<Integer, User> users = qf.setUsers();
		System.out.println(users.size());
		
		System.out.print("Comments : ");	
		ArrayList<Comment> comments = qf.setComment();
		System.out.println(comments.size());
		
		System.out.print("Answers : ");	
		HashMap<Integer, Answer> answers = qf.setAnswers(users, comments);		
		System.out.println(answers.size());
		
		System.out.print("Questions : ");
		HashMap<Integer, Question> questions = qf.setQuestions(users, comments, answers);
		System.out.println(questions.size());
		
		comments = null;
		
//		System.out.print("Votes : ");	
//		ArrayList<Vote> votes = qf.setVotes(users, answers, questions);
//		System.out.println(votes.size());
		
		answers = null;
		users = null;
		
//		System.out.print("PostLinks : ");	
//		ArrayList<PostLink> postLinks = qf.setPostLinks(questions);
//		System.out.println(postLinks.size());
		
		FileOutputStream fos = new FileOutputStream("Questions.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(questions);
		oos.close();
		*/
		
//		FileInputStream fis = new FileInputStream("Questions.ser");
//		ObjectInputStream ois = new ObjectInputStream(fis);
//		HashMap<Integer, Question> result = (HashMap<Integer, Question>) ois.readObject();
//		ois.close();
		
		String[] keywords = new String[1200];
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse("tf-idf.xml");
		
		NodeList nodeList = doc.getElementsByTagName("name_term");
		int cpt = 0;
		for (int i=0; i < nodeList.getLength(); i++) {
			System.out.println(nodeList.item(i).getTextContent());
			cpt++;
		}
		System.out.println("cpt "+cpt);
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(totalTime)/1000000000+" secondes");
//		System.out.println(result.size()+" questions sont dans 'questions'");
		System.out.println("-------------------------------------------------------");
		
		
		

	}

}
