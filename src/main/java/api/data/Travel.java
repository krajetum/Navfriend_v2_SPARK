package api.data;


import java.util.List;

/**
 * Created by Dev on 28/04/2015.
 */
public class Travel {


    private String owner;
    private String descrizione;
    private Coordinates destinazione;
    private List<User> guest;

    public Travel(String owner, String descrizione, Coordinates destinazione){
        this.owner = owner;
        this.descrizione = descrizione;
        this.destinazione = destinazione;
    }

    public Travel(String owner, Coordinates destinazione){
        this.owner = owner;
        this.destinazione = destinazione;
    }
    public boolean addUser(User user){
        if(guest.contains(user)) {
            return false;
        }else{
            guest.add(user);
            return true;
        }
    }
}
