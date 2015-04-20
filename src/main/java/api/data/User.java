package api.data;

/**
 * Created by Lorenzo on 15/04/2015.
 */
public class User {

	private String email;
	private String pwd;

	public User(){

	}

	public User(String email,String pwd){
		this.email=email;
		this.pwd=pwd;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString(){
		return "email: "+email+", pwd: "+pwd;
	}
}
