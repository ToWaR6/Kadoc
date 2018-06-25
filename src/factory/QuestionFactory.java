package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import model.Anonymous;
import model.Answer;
import model.Comment;
import model.PostLink;
import model.Question;
import model.Registered;
import model.User;
import model.Vote;
/**
 * This class allows you to execute Query on your DB
 * Need My Sql Connector
 * @author Crauser
 *
 */
public class QuestionFactory {

	private Statement statement;
	
	public QuestionFactory() {
		super();
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db_stack?user=root&password=azertyuiop&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			this.statement = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Try install Sql Connector");
		}
	}
	
	public ResultSet query(String query) throws SQLException {
		return this.statement.executeQuery(query);
	}
	
	public ResultSet getUsers() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Users"
				);
	}
	
	public ResultSet getQuestions() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Posts " + 
				"Where PostTypeId = 1"
				);
	}
	
	public ResultSet getAnswers() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Posts " + 
				"Where PostTypeId = 2"
				);
	}

	public ResultSet getComments() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Comments"
				);
	}

	public ResultSet getBadges() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Badges"
				);
	}

	public ResultSet getVotes() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From Votes"
				);
	}

	public ResultSet getPostLinks() throws SQLException {
		return this.statement.executeQuery(
				"Select * " + 
				"From PostLinks"
				);
	}
	
	public HashMap<Integer, User> setUsers() throws SQLException {
		ResultSet rs = this.getUsers();
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		while (rs.next()) { 
			Registered registeredUser = new Registered(
				rs.getString("DisplayName"),
				rs.getInt("Id"),
				rs.getInt("Reputation"), 
				rs.getDate("CreationDate"), 
				rs.getDate("LastAccessDate"), 
				rs.getString("WebsiteUrl"), 
				rs.getString("Location"), 
				rs.getString("AboutMe"), 
				rs.getInt("Views"), 
				rs.getInt("Upvotes"), 
				rs.getInt("DownVotes"), 
				rs.getString("ProfileImageUrl"), 
				rs.getString("EmailHash"), 
				rs.getInt("Age"), 
				rs.getInt("AccountId")
				);
			
			users.put(registeredUser.getId(), registeredUser); 
		}
		return users;
	}
	
	public ArrayList<Comment> setComment() throws SQLException {
		ResultSet rs = this.getComments();
		ArrayList<Comment> comments = new ArrayList<Comment>();
		while (rs.next()) { 
			Comment c = new Comment(
				rs.getInt("Id"), 
				rs.getInt("Score"), 
				rs.getString("Text"), 
				rs.getDate("CreationDate"), 
				rs.getInt("PostId")
				);
			comments.add(c);
		}
		return comments;
	}
	
	public HashMap<Integer, Answer> setAnswers(HashMap<Integer, User> users, ArrayList<Comment> comments) throws SQLException {
		ResultSet rs = this.getAnswers();
		HashMap<Integer, Answer> answers = new HashMap<Integer, Answer>();
		while (rs.next()) {
			Answer a = new Answer(
				rs.getInt("Id"), 
				rs.getDate("CreationDate"), 
				rs.getDate("DeletionDate"), 
				rs.getInt("Score"), 
				rs.getString("Body"), 
				rs.getDate("LastEditDate"), 
				rs.getDate("ClosedDate"), 
				rs.getDate("CommunityOwnedDate"), 
				rs.getInt("OwnerUserId")==0 ? new Anonymous(rs.getString("OwnerDisplayName")) : users.get(rs.getInt("OwnerUserId")),
				rs.getInt("LastEditorUserId")==0 ? null : users.get(rs.getInt("LastEditorUserId")),
				rs.getInt("ParentId")
				);		
			//set comment
			for (Comment comment : comments) {
				if (comment.getPostId() == a.getId()) {
					a.addComment(comment);
				}
			}
			
			answers.put(a.getId(), a);
		}
		return answers;
	}
	
	public HashMap<Integer, Question> setQuestions(HashMap<Integer, User> users, ArrayList<Comment> comments, HashMap<Integer, Answer> answers) throws SQLException {
		ResultSet rs = this.getQuestions();
		HashMap<Integer, Question> questions = new HashMap<Integer, Question>();
		while (rs.next()) {
			Question q = new Question(
				rs.getInt("Id"),
				rs.getDate("CreationDate"),
				rs.getDate("DeletionDate"),
				rs.getInt("Score"),
				rs.getString("Body"), 
				rs.getDate("LastEditDate"),
				rs.getDate("ClosedDate"),
				rs.getDate("CommunityOwnedDate"),
				rs.getInt("OwnerUserId")==0 ? new Anonymous(rs.getString("OwnerDisplayName")) : users.get(rs.getInt("OwnerUserId")),  
				rs.getInt("LastEditorUserId")==0 ? null : users.get(rs.getInt("LastEditorUserId")),
				rs.getString("Title"),
				rs.getInt("FavoriteCount"),
				rs.getInt("ViewCount"),
				rs.getString("Tags").replaceAll("&lt;", "").split("&gt;")
				);
			
			//set answers
			int idSelectedAnswer = rs.getInt("AcceptedAnswerId");
			for (Answer answer : answers.values()) {
				if (answer.getParentId() == q.getId()) {
					q.addAnswer(answer);
					if (answer.getId() == idSelectedAnswer) {
						q.setSelectedAnswer(answer);
					}
				}
			}
			
			//set comments
			for (Comment comment : comments) {
				if (comment.getPostId() == q.getId()) {
					q.addComment(comment);
				}
			}
			
			questions.put(q.getId(), q);
		}
		return questions;
	}
	
	public ArrayList<Vote> setVotes(HashMap<Integer, User> users, HashMap<Integer, Answer> answers, HashMap<Integer, Question> questions) throws SQLException {
		ResultSet rs = this.getVotes();
		ArrayList<Vote> votes = new ArrayList<Vote>();
		while (rs.next()) { 
			Vote v = new Vote(
				rs.getInt("Id"),
				rs.getInt("VoteTypeId"), 
				rs.getDate("CreationDate"), 
				rs.getInt("BountyAmount"), 
				null, 
				users.get(rs.getInt("UserId"))
				);
			if (questions.get(rs.getInt("PostId")) != null) { //if the vote is on a question
				v.setPost(questions.get(rs.getInt("PostId")));
			} else if (answers.get(rs.getInt("PostId")) != null) { //if the vote is on an answer
				v.setPost(answers.get(rs.getInt("PostId")));
			}
			votes.add(v);
		}
		return votes;
	}
	
	public ArrayList<PostLink> setPostLinks(HashMap<Integer, Question> questions) throws SQLException {
		ResultSet rs = this.getPostLinks();
		ArrayList<PostLink> postLinks = new ArrayList<PostLink>();
		while (rs.next()) { 
			PostLink pl = new PostLink(
				rs.getInt("Id"), 
				rs.getDate("CreationDate"), 
				rs.getInt("LinkTypeId"), 
				questions.get(rs.getInt("PostId")), 
				questions.get(rs.getInt("RelatedPostId"))
				);
			postLinks.add(pl);
		}
		return postLinks;
	}
	
	public HashMap<Integer, Question> setQuestionsAuto() throws SQLException {
		HashMap<Integer, User> users = this.setUsers();
		ArrayList<Comment> comments = this.setComment();
		HashMap<Integer, Answer> answers = this.setAnswers(users, comments);
		return this.setQuestions(users, comments, answers);
	}
	
}


