package api.dao;

import api.data.ResTravel;
import api.data.Travel;
import api.data.User;
import api.util.HttpStatus;

import java.sql.*;

/**
 * Created by Dev on 21/04/2015.
 */
public class ServerDAO {


    private static String database = "jdbc:mysql://192.168.201.111:3306/navfriend";
    private static String username = "navfriend";
    private static String password = "navfriend";
//    private Connection connection;
//    private PreparedStatement statement;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ServerDAO(){

    }

    /**
     *  Method Used to check if the user is already registered on the server
     *
     *
     *  @param user contains user and pass
     *
     *  @return HttpStatus
     *  @see api.util.HttpStatus
     *
     *
     *  @since 1.0
     */
    public HttpStatus checkUser(User user){
        Connection connection  = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(database, username, password);
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
                            statement.close();
                            res.close();
                            connection.close();
                            return HttpStatus.SUCCESS;
                        } else {
                            statement.close();
                            res.close();
                            connection.close();
                            return HttpStatus.UNAUTHORIZED;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        return HttpStatus.SERVER_ERROR;
    }

    public ResTravel CreateTravel(Travel travel){
        Connection connection  = null;
        PreparedStatement statement = null;
        boolean res=false;

        try {
            connection = DriverManager.getConnection(database, username, password);
            statement = connection.prepareStatement("INSERT INTO viaggio (destinazione,descrizione) VALUES (?,?)");
            statement.setString(1,travel.getDestinazione().getLatitude()+","+travel.getDestinazione().getLongitude());
            statement.setString(2,travel.getDescrizione());
            int exe = statement.executeUpdate();

            if(exe!=0){
                res=true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(res)
            return new ResTravel(HttpStatus.SERVER_ERROR,null);
        else {

            try {
                statement=connection.prepareStatement("SELECT email " +
                        "FROM utente INNER JOIN amico AS utente.codice_utente=amico.codice_utente " +
                        "WHERE utente.codice_utente=" +
                        "SELECT codice_utente" +
                        "FROM utente" +
                        "WHERE email=?");
                statement.setString(1,travel.getOwner());

                ResultSet result = statement.executeQuery();
                while(result.next()){
                    travel.addUser(new User(result.getString(1),null));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new ResTravel(HttpStatus.SUCCESS,travel);
        }
    }
}
