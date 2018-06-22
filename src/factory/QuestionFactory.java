package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuestionFactory {

	private Statement statement;
	
	public QuestionFactory() {
		super();
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db_stack?user=root&password=azertyuiop&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
			this.statement = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
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
	
}


