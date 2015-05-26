package api.dao;

import api.ServerApi;
import api.data.*;
import api.util.HttpStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 21/04/2015.
 */
public class ServerDAO {

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
    public HttpStatus checkUser(User user) {

        String sql1 = "SELECT COUNT(*) AS exist FROM utente WHERE email=?";
        String sql2 = "SELECT COUNT(*) AS exist FROM utente WHERE email=? AND  password=?";
        try (Connection connection = newConnection();
             PreparedStatement statement = connection.prepareStatement(sql1);
             PreparedStatement statement2 = connection.prepareStatement(sql2)) {

            statement.setString(1, user.getEmail());
            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    if (res.getInt("exist") == 1) {
                        statement2.setString(1, user.getEmail());
                        statement2.setString(2, user.getPwd());
                        try (ResultSet res2 = statement.executeQuery()) {
                            if (res2.next()) {
                                if (res2.getInt("exist") == 1) {
                                    return HttpStatus.SUCCESS;
                                } else {
                                    return HttpStatus.UNAUTHORIZED;
                                }
                            }
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

    public ResTravel createTravel(TrasferTravel travel) {
        Travel t = new Travel(travel.getUser().getEmail(), travel.getDescrizione(), travel.getDestinazione());
        boolean res = false;

        String sql = "INSERT INTO viaggio (destinazione,descrizione,proprietario) VALUES (?,?,?)";

        try (Connection connection = newConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, travel.getDestinazione().getLatitude() + "," + travel.getDestinazione().getLongitude());
            statement.setString(2, travel.getDescrizione());
            statement.setString(3, travel.getUser().getEmail());
            int exe = statement.executeUpdate();

            if (exe == 0) {
                res = true;
            }

            if (res) {
                return new ResTravel(HttpStatus.SERVER_ERROR, null);
            } else {
                String sql1 = "SELECT viaggio.codice_viaggio from viaggio,utente where viaggio.proprietario=utente.email and proprietario=?  order by data desc";
                String sql2 = "SELECT email FROM utente WHERE  codice_utente in (SELECT codice_amico FROM utente,amico WHERE utente.codice_utente=amico.codice_utente and email=?)";

                try (PreparedStatement statement2 = connection.prepareStatement(sql1);
                     PreparedStatement statement3 = connection.prepareStatement(sql2)){
                    statement2.setString(1, travel.getUser().getEmail());
                    try (ResultSet result = statement2.executeQuery()) {
                        result.next();
                        t.setID(result.getInt("codice_viaggio"));
                    }

                    statement3.setString(1, travel.getUser().getEmail());
                    try (ResultSet result = statement3.executeQuery()) {
                        while (result.next()) {
                            t.addUser(result.getString("email"), "");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return new ResTravel(HttpStatus.SUCCESS, t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpStatus addUsers(UserTravel userTravel) {
        List<User> utenti = userTravel.getTravel().getGuest();

        String sql = "update utente set codice_viaggio=? where email=?";

        try (Connection connection = newConnection();
             PreparedStatement statement = connection.prepareStatement(sql);) {

            System.out.println("ID VIAGGIO: " + userTravel.getTravel().getID());
            statement.setInt(1, userTravel.getTravel().getID());
            System.out.println("EMAIL UTENTE PROPRIETARIO: " + userTravel.getTravel().getOwner());
            statement.setString(2, userTravel.getTravel().getOwner());
            statement.executeUpdate();

            for (int i = 0; i < utenti.size(); i++) {
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
        String cod_usr="";

        String sql = "SELECT codice_utente FROM utente where email=?";

        try (Connection connection = newConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setString(1,user.getEmail());
            try (ResultSet res=statement.executeQuery()) {
                res.next();
                cod_usr = res.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nella ricerca del codice_utente");
            return HttpStatus.SERVER_ERROR;
        }

        sql = "INSERT INTO posizione (longitudine,latitudine,codice_viaggio,codice_utente) VALUES(?,?,?,?)";
        try (Connection connection = newConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setFloat(1, point.getLongitude());
            statement.setFloat(2, point.getLatitude());
            statement.setInt(3, travel.getID());
            statement.setString(4, cod_usr);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nell'inserimeto");
            return HttpStatus.SERVER_ERROR;
        }
        return HttpStatus.SUCCESS;
    }

    public Travel getTravelForUser(User user) {
        Travel t = null;

        String sql = "SELECT viaggio.codice_viaggio,viaggio.descrizione,viaggio.proprietario,viaggio.destinazione FROM viaggio,utente WHERE viaggio.codice_viaggio=utente.codice_viaggio AND utente.email=?";

        try (Connection connection = newConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getEmail());

            try (ResultSet s = statement.executeQuery()) {
                if (s.next()) {
                    if (s.getInt("codice_viaggio") > 0) {
                        t = new Travel();
                        t.setID(s.getInt("codice_viaggio"));
                        t.setDescrizione(s.getString("descrizione"));
                        t.setOwner(s.getString("proprietario"));
                        t.setDestinazione(new Coordinates(s.getString("destinazione")));
                    } else {
                        t = null;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nella ricerca del viaggio per l'utente");
            return null;
        }

        return t;
    }

    public List<trasferCoordinates> getCoordinates(Travel travel){
        List<trasferCoordinates> usersPos=null;

        String select =
                "SELECT email,latitudine,longitudine\n" +
                " FROM utente,viaggio,ultimaposizione p\n" +
                " WHERE utente.codice_viaggio=viaggio.codice_viaggio AND p.codice_utente=utente.codice_utente AND viaggio.codice_viaggio=p.codice_viaggio AND p.codice_viaggio=?";

        try (Connection connection = newConnection();
            PreparedStatement statement = connection.prepareStatement(select)) {

            statement.setInt(1, travel.getID());
            try (ResultSet res= statement.executeQuery()) {
                usersPos=new ArrayList<>();

                while(res.next()) {
                    usersPos.add(new trasferCoordinates(new Coordinates(res.getFloat(2), res.getFloat(3)), new User(res.getString(1), null), null));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("errore nel prendere le posizioni degli utenti del viaggio");
            return null;
        }
        return usersPos;
    }

    public HttpStatus TerminateTravel(User user) {
        List<trasferCoordinates> usersPos = null;

        try (Connection connection = newConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE utente SET codice_viaggio=null WHERE email=?")) {

            statement.setString(1, user.getEmail());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return HttpStatus.SERVER_ERROR;
        }
        return HttpStatus.SUCCESS;
    }

    private Connection newConnection() throws SQLException {
        return DriverManager.getConnection(
                ServerApi.getConfiguration().getDatabaseUrl(),
                ServerApi.getConfiguration().getDatabaseUser(),
                ServerApi.getConfiguration().getDatabasePassword());
    }
}
