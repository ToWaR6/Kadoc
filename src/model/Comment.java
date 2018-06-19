package model;

import java.util.Date;

public class Comment  {

	private int id;
	private int score;
	private String text;
	private Date creationDate;
	
	
	//CONSTRUCTOR
	public Comment(int id, int score, String text, Date creationDate) {
		super();
		this.id = id;
		this.score = score;
		this.text = text;
		this.creationDate = creationDate;
	}

	//GETTER & SETTER
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	
}
