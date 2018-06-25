import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import factory.QuestionFactory;
import model.Anonymous;
import model.Answer;
import model.Comment;
import model.PostLink;
import model.Question;
import model.Registered;
import model.User;
import model.Vote;

public class MAIN {

	public static void main(String[] args) throws SQLException {
		
		System.out.println("Begin");
		long startTime = System.nanoTime();
		
		HashMap<Integer, Question> questions = new HashMap<Integer, Question>();
		ArrayList<Vote> votes = new ArrayList<Vote>();
		ArrayList<PostLink> postLinks = new ArrayList<PostLink>();
		HashMap<Integer, User> users = new HashMap<Integer, User>();
		HashMap<Integer, Answer> answers = new HashMap<Integer, Answer>();
		ArrayList<Comment> comments = new ArrayList<Comment>();
//		ArrayList<Badge> badges = new ArrayList<Badge>();
		
		QuestionFactory qf = new QuestionFactory();
		ResultSet rs;
		
//		System.out.print("Badge : ");
		//get & construct all badges
//		rs = qf.getBadges();
//		while (rs.next()) { 
//			Badge b = new Badge(
//				rs.getInt("Id"), 
//				rs.getString("Name"),
//				rs.getDate("Date"), 
//				rs.getInt("Class"), 
//				rs.getBoolean("TagBased"),
//				rs.getInt("UserId")
//				);
//			badges.add(b);
//		}
//		System.out.println(badges.size());
		
		System.out.print("Users : ");
		//get & construct all users
		rs = qf.getUsers();
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
			//set badge
//			for (Badge badge : badges) {
//				if (badge.getUserId() == registeredUser.getId()) {
//					registeredUser.addBadge(badge);
//				}
//			}
			
			users.put(registeredUser.getId(), registeredUser); 
		}
		System.out.println(users.size());
		
		System.out.print("Comments : ");	
		//get & construct all users
		rs = qf.getComments();
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
		System.out.println(comments.size());
		
		System.out.print("Answers : ");	
		//get & construct all answers
		rs = qf.getAnswers();
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
		System.out.println(answers.size());
		
		System.out.print("Questions : ");
		//get & construct all questions
		rs = qf.getQuestions();		
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
		System.out.println(questions.size());
		
//		badges = null;
		comments = null;
		
		System.out.print("Votes : ");	
		//get & construct all votes
		rs = qf.getVotes();
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
		System.out.println(votes.size());
		
		answers = null;
		users = null;
		
		System.out.print("PostLinks : ");	
		//get & construct all postLinks
		rs = qf.getPostLinks();
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
		System.out.println(postLinks.size());
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println("-------------------------------------------------------");
		System.err.println("\n\nTermine en "+(float)(totalTime)/1000000000+" secondes");
		System.out.println(questions.size()+" questions sont dans 'questions'");
		System.out.println("-------------------------------------------------------");

	}

}
