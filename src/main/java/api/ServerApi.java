package api;
import api.dao.ServerDAO;
import api.data.Coordinates;
import api.data.ResTravel;
import api.data.Travel;
import api.data.User;
import api.util.HttpStatus;
import api.util.JsonTransformer;
import api.util.RequestSuccess;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.*;

import static spark.Spark.*;

/**
 * Created by Andrea on 15/04/2015.
 */
public class ServerApi {
	private static Gson gson;
	private static ServerDAO DAO;

	public static void main(String[] args) {
		DAO = new ServerDAO();
		gson = new Gson();
		port(8182);

		put("/login", "application/json", (request, response) -> {
			System.out.println(request.body());

			User user = gson.fromJson(request.body(), User.class);

			HttpStatus status = DAO.checkUser(user);

			switch (status) {
				case SUCCESS:
					response.status(200);
					System.out.println("Client authorized");

					return new RequestSuccess(true, "Autorizzato");

				case UNAUTHORIZED:
					response.status(401);
					System.out.println("Client unauthorized");
					return new RequestSuccess(false, "Non Autorizzato");

				case SERVER_ERROR:
					response.status(500);
					System.out.println("Server Error");
					return new RequestSuccess(false, "Server Error");

				default:
					/**
					 *
					 * 	Unknown Server Error:
					 * 	Non Implemented
					 */
					response.status(500);
					return new RequestSuccess(false, "Unknown Server Error: Contact the Admin ");
			}

		}, new JsonTransformer());

		post("/newtravel", "application/json", (request, response) -> {

			System.out.println(request.body().toString());
			System.out.println(request.requestMethod());

			String usr = request.queryParams("user");
			System.out.println(usr);
			String descrizione = request.queryParams("descrizione");
			System.out.println(descrizione);
			Coordinates destinazione = gson.fromJson(request.queryParams("coordinates"), Coordinates.class);
			System.out.println(destinazione.getLatitude()+","+destinazione.getLongitude());

			Travel travel=new Travel(usr,descrizione,destinazione);

			ResTravel r = DAO.CreateTravel(travel);

			switch (r.getStatus()) {

				case SUCCESS:
					System.out.println("Creazione viaggio eseguita con successo! ");
					return travel;

				case SERVER_ERROR:
					System.out.println("Errore nella creazione del viaggio :(");
					return travel;

			}

			return null;
		}, new JsonTransformer());



		exception(IllegalArgumentException.class,(e, req, res) -> {
			res.status(400);
			res.body(gson.toJson(e));
		});
	}

}
