package pl.fzymek.advancedrecyclerview.model;

/**
 * Created by Filip Zymek on 2015-06-08.
 */
public class User {

	String username;
	String firstname;
	String lastname;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public String toString() {
		return "User{" +
			"username='" + username + '\'' +
			", firstname='" + firstname + '\'' +
			", lastname='" + lastname + '\'' +
			'}';
	}
}
