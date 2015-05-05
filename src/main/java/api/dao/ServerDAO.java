package api.dao;

import api.data.*;
import api.util.HttpStatus;

import java.sql.*;
import java.util.List;

/**
 * Created by Dev on 21/04/2015.
 */
public class ServerDAO {


    private static String database = "jdbc:mysql://localhost:3306/navfriend";
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

    public ResTravel CreateTravel(TrasferTravel travel){
        Connection connection  = null;
        System.out.println(travel.getUser().getEmail()+","+travel.getDescrizione()+","+travel.getDestinazione());
        Travel t=new Travel(travel.getUser().getEmail(),travel.getDescrizione(),travel.getDestinazione());
        PreparedStatement statement = null;
        boolean res=false;

        try {
            connection = DriverManager.getConnection(database, username, password);
            statement = connection.prepareStatement("INSERT INTO viaggio (destinazione,descrizione,proprietario) VALUES (?,?,?)");
            statement.setString(1,travel.getDestinazione().getLatitude()+","+travel.getDestinazione().getLongitude());
            statement.setString(2,travel.getDescrizione());
            statement.setString(3,travel.getUser().getEmail());
            int exe = statement.executeUpdate();

            if(exe==0){
                res=true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(res)
            return new ResTravel(HttpStatus.SERVER_ERROR,null);
        else {

            try {
                statement=connection.prepareStatement("SELECT viaggio.codice_viaggio from viaggio,utente where viaggio.proprietario=utente.email and proprietario=?  order by data");
                statement.setString(1,travel.getUser().getEmail());
                ResultSet result = statement.executeQuery();
                result.next();
                t.setID(result.getInt("codice_viaggio"));

                statement=connection.prepareStatement("SELECT email FROM utente WHERE  codice_utente in (SELECT codice_amico FROM utente,amico WHERE utente.codice_utente=amico.codice_utente and email=?)");
                statement.setString(1, travel.getUser().getEmail());

                result = statement.executeQuery();
                while(result.next()){
                    System.out.println(result.getString("email"));
                    t.addUser(result.getString("email"), "");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new ResTravel(HttpStatus.SUCCESS,t);
        }
    }
    public HttpStatus AddUsers(UserTravel userTravel){
        Connection connection  = null;
        PreparedStatement statement = null;
        List<User> utenti=userTravel.getTravel().getGuest();


        try {
            connection = DriverManager.getConnection(database, username, password);
            statement = connection.prepareStatement("update utente set codice_viaggio=? where email=?");
            statement.setInt(1, userTravel.getTravel().getID());
            statement.setString(2, userTravel.getTravel().getOwner());
            statement.executeUpdate();

            for (int i = 0; i < utenti.size(); i++){
                statement = connection.prepareStatement("update utente set codice_viaggio=? where email=?");
                statement.setInt(1, userTravel.getTravel().getID());
                statement.setString(2, utenti.get(i).getEmail());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.SERVER_ERROR;
        }

        return HttpStatus.SUCCESS;
    }
}
