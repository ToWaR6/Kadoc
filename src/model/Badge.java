package model;

import java.util.Date;

public class Badge {

	private int id;
	private String name;
	private Date date;
	private int classBadge;
	private boolean tagBased;
	
	//CONSTRUCTOR
	public Badge(int id, String name, Date date, int classBadge, boolean tagBased) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.classBadge = classBadge;
		this.tagBased = tagBased;
	}

	//GETTER & SETTER
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getClassBadge() {
		return classBadge;
	}

	public void setClassBadge(int classBadge) {
		this.classBadge = classBadge;
	}

	public boolean isTagBased() {
		return tagBased;
	}

	public void setTagBased(boolean tagBased) {
		this.tagBased = tagBased;
	}
	
	
}