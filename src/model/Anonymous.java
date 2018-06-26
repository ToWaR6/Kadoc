package model;

import java.io.Serializable;

public class Anonymous extends User implements Serializable {
	private static final long serialVersionUID = -8739464064101642901L;

	public Anonymous(String displayName) {
		super(displayName);
	}
	
}
