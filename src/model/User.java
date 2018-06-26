package model;

import java.io.Serializable;

public abstract class User implements Serializable {
	private static final long serialVersionUID = 7084472843361678784L;
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
