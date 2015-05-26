package api.dao;

import api.data.*;
import api.util.HttpStatus;

import java.sql.*;
import java.util.ArrayList;
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
                statement=connection.prepareStatement("SELECT viaggio.codice_viaggio from viaggio,utente where viaggio.proprietario=utente.email and proprietario=?  order by data desc");
                statement.setString(1,travel.getUser().getEmail());
                ResultSet result = statement.executeQuery();
                result.next();
                t.setID(result.getInt("codice_viaggio"));

                statement=connection.prepareStatement("SELECT email FROM utente WHERE  codice_utente in (SELECT codice_amico FROM utente,amico WHERE utente.codice_utente=amico.codice_utente and email=?)");
                statement.setString(1, travel.getUser().getEmail());

                result = statement.executeQuery();
                while(result.next()){
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
            System.out.println("ID VIAGGIO: "+userTravel.getTravel().getID());
            statement.setInt(1, userTravel.getTravel().getID());
            System.out.println("EMAIL UTENTE PROPRIETARIO: " + userTravel.getTravel().getOwner());
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

    public HttpStatus insertPosition(Coordinates point,Travel travel,User user){
        Connection connection=null;
        PreparedStatement statement=null;
        String cod_usr="";

        try {
            connection=DriverManager.getConnection(database,username,password);
            statement = connection.prepareStatement("SELECT codice_utente FROM utente where email=?");
            statement.setString(1,user.getEmail());
            ResultSet res=statement.executeQuery();
            res.next();
            cod_usr = res.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nella ricerca del codice_utente");
            return HttpStatus.SERVER_ERROR;
        }

        try {
            connection = DriverManager.getConnection(database, username, password);
            statement=connection.prepareStatement("INSERT INTO posizione (longitudine,latitudine,codice_viaggio,codice_utente) VALUES(?,?,?,?)");
            statement.setFloat(1,point.getLongitude());
            statement.setFloat(2,point.getLatitude());
            statement.setInt(3, travel.getID());
            statement.setString(4,cod_usr);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nell'inserimeto");
            return HttpStatus.SERVER_ERROR;
        }
        return HttpStatus.SUCCESS;
    }
    public Travel getTravelForUser(User user){
        Connection connection=null;
        PreparedStatement statement=null;
        Travel t=null;

        try {
            connection = DriverManager.getConnection(database, username, password);
            statement=connection.prepareStatement("SELECT viaggio.codice_viaggio,viaggio.descrizione,viaggio.proprietario,viaggio.destinazione FROM viaggio,utente WHERE viaggio.codice_viaggio=utente.codice_viaggio AND utente.email=?");
            statement.setString(1,user.getEmail());

            ResultSet s=statement.executeQuery();
            if(s.next()){
                if(s.getInt("codice_viaggio")>0){
                    t=new Travel();
                    t.setID(s.getInt("codice_viaggio"));
                    t.setDescrizione(s.getString("descrizione"));
                    t.setOwner(s.getString("proprietario"));
                    t.setDestinazione(new Coordinates(s.getString("destinazione")));
                }
                else{
                    t=null;
                }

            }

        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("errore nella ricerca del viaggio per l'utente");
            return null;
        }

        return t;
    }

    public List<trasferCoordinates> getCoordinates(Travel travel, User usr){
        //aggiunto parametro user per inviare tutte le posizioni tranne quella di chi lo ha richiesto
	Connection connection=null;
        PreparedStatement statement=null;
        List<trasferCoordinates> usersPos=null;

        try {
            connection = DriverManager.getConnection(database, username, password);
	    //modificata query. aggiunto "utente.email<>?""
            statement = connection.prepareStatement("SELECT email,latitudine,longitudine\n" +
                    " FROM utente,viaggio,ultimaposizione p\n" +
                    " WHERE utente.codice_viaggio=viaggio.codice_viaggio AND p.codice_utente=utente.codice_utente AND viaggio.codice_viaggio=p.codice_viaggio AND utente.email<>? AND p.codice_viaggio=?");
	    //settaggio indirizzo email nello statement
	    statement.setString(1,usr.getEmain())
            statement.setInt(2,travel.getID());
            ResultSet res= statement.executeQuery();

            usersPos=new ArrayList<trasferCoordinates>();

            while(res.next()) {
                usersPos.add(new trasferCoordinates(new Coordinates(res.getFloat(2), res.getFloat(3)), new User(res.getString(1), null), null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nel prendere le posizioni degli utenti del viaggio");
            return null;
        }
        return usersPos;
    }

    public HttpStatus TerminateTravel(User user) {
        Connection connection=null;
        PreparedStatement statement=null;
        List<trasferCoordinates> usersPos=null;

        try {
            connection=DriverManager.getConnection(database, username, password);
            statement=connection.prepareStatement("UPDATE utente SET codice_viaggio=null WHERE email=?");
            statement.setString(1,user.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.SERVER_ERROR;
        }
        return HttpStatus.SUCCESS;
    }
}
