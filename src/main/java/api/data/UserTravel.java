package api.data;

/**
 * Created by Dev on 05/05/2015.
 */
public class UserTravel {
    private Travel travel;
    private User user;

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
}
