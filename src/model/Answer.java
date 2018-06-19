package model;

import java.util.Date;

public class Answer extends Post {

	public Answer(int id, Date creationDate, Date deletionDate, int score, String text, Date lastEditDate,
			Date closeDate, Date communityOwnedDate, User owner, User lastEditor) {
		super(id, creationDate, deletionDate, score, text, lastEditDate, closeDate, communityOwnedDate, owner, lastEditor);
	}

}
