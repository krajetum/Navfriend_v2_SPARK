package api;
import api.data.User;
import api.util.JsonTransformer;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.*;

import static spark.Spark.*;

/**
 * Created by Lorenzo on 15/04/2015.
 */
public class ServerApi {
	private static Gson gson;
	public static void main(String[] args) {

		gson = new Gson();
		port(8182);
		post("/login", "application/json", (request, response) -> {
			System.out.println(request.queryParams("user"));
			System.out.println(request.queryParams("pwd"));
			User user = new User(request.queryParams("user"), request.queryParams("pwd"));

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Connection connection = null;
			PreparedStatement statement = null;
			try {
				connection = DriverManager.getConnection("jdbc:mysql://192.168.1.254:3306/navfriend", "navfriend", "navfriend");

				statement = connection.prepareStatement("SELECT COUNT(*) AS exist FROM utente WHERE email=?");
				statement.setString(1, user.getEmail());


				ResultSet res = statement.executeQuery();
				if (res.next()) {
					if (res.getInt("exist") == 1) {
						statement = connection.prepareStatement("SELECT COUNT(*) AS exist FROM utente WHERE email=? AND  password=?");
						statement.setString(1, user.getEmail());
						statement.setString(2, user.getPwd());
						res = statement.executeQuery();
						if (res.next()) {
							if (res.getInt("exist") == 1) {
								response.status(200);
								return "{auth:\"si\"}";
							} else {
								response.status(200);
								return "{auth:\"no\"}";
							}
						}
					}
				}


			} catch (SQLException e) {
				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
			} finally {
				if (connection != null)
					connection.close();
				if (statement != null)
					statement.close();
			}

			response.type("application/json");
			response.status(500);
			return null;
		}, new JsonTransformer());

		exception(IllegalArgumentException.class,(e, req, res) -> {
			res.status(400);
			res.body(gson.toJson(e));
		});
	}

}
