package factory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import model.PostLink;
import model.Question;

public class PostLinksFactory {
	private RemoteFetcher remoteFetcher;
	public PostLinksFactory() {
		remoteFetcher = new RemoteFetcher();
	}
	
	public ArrayList<PostLink> getAllPostLinks(HashMap<Integer, Question> questions) throws SQLException {
		ArrayList<PostLink> postLinks = new ArrayList<PostLink>();
		ResultSet resultSet = remoteFetcher.fetchAllPostLinks();
		PostLink postlink;
		while (resultSet.next()) { 
			postlink = new PostLink(
				resultSet.getInt("Id"), 
				resultSet.getDate("CreationDate"), 
				resultSet.getInt("LinkTypeId"), 
				questions.get(resultSet.getInt("PostId")), 
				questions.get(resultSet.getInt("RelatedPostId"))
				);
			postLinks.add(postlink);
		}
		return postLinks;
	}
}
