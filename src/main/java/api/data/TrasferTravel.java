package api.data;

/**
 * Created by Dev on 28/04/2015.
 */
public class TrasferTravel {

    private User user;
    private Travel travel;
    private String descrizione;

    public TrasferTravel(User user, Travel travel, String descrizione) {
        this.user = user;
        this.travel = travel;
        this.descrizione = descrizione;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
