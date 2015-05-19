package api.data;


import java.util.*;

/**
 * Created by Dev on 28/04/2015.
 */
public class Travel {
    private int ID;
    private String owner;
    private String descrizione;
    private Coordinates destinazione;
    public List<User> getGuest() {
        return guest;
    }

    public Travel(int ID, String descrizione) {
        this.ID = ID;
        this.descrizione = descrizione;
    }

    public void setGuest(List<User> guest) {
        this.guest = guest;
    }

    private List<User> guest;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Coordinates getDestinazione() {
        return destinazione;
    }

    public void setDestinazione(Coordinates destinazione) {
        this.destinazione = destinazione;
    }

    public void setID(int ID){
        this.ID=ID;
    }

    public int getID(){
        return ID;
    }

    public Travel(String owner, String descrizione, Coordinates destinazione){
        guest = new ArrayList<User>();
        this.owner = owner;
        this.descrizione = descrizione;
        this.destinazione = destinazione;
    }
    public Travel() {
    }

    public Travel(String owner, Coordinates destinazione){
        guest = new ArrayList<User>();
        this.owner = owner;
        this.destinazione = destinazione;
    }
    public void addUser(String user,String pwd){
        guest.add(new User(user,pwd));
    }
}
