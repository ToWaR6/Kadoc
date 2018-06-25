import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import factory.QuestionFactory;
import model.Answer;
import model.Comment;
import model.Question;
import model.User;

public class MAIN {

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		
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
		
		FileInputStream fis = new FileInputStream("Questions.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		HashMap<Integer, Question> result = (HashMap<Integer, Question>) ois.readObject();
		ois.close();
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(totalTime)/1000000000+" secondes");
		System.out.println(result.size()+" questions sont dans 'questions'");
		System.out.println("-------------------------------------------------------");
		
		
		

	}

}
