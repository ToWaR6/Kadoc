package model;

public abstract class User {

	private String displayName;

	//CONSTRUCTOR
	public User(String displayName) {
		super();
		this.displayName = displayName;
	}

	//GETTER & SETTER
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
