package it.polimi.web.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.web.project.beans.User;

public class UserDao {

	private Connection con;

	public UserDao(Connection c) {
		this.con = c;
	}

	public User checkCredentials(String username, String password) throws SQLException {
		// preparo la query
		String query = "SELECT userID,name,surname FROM user where username = ? and password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			// eseguo la query
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("userID"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;

				}
			}
		}

	}

}
