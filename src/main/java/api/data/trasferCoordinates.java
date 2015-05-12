package api.data;

/**
 * Created by Dev on 12/05/2015.
 */
public class trasferCoordinates {
    private Coordinates position;
    private User user;
    private Travel travel;

    public trasferCoordinates(Coordinates position, User user, Travel travel) {
        this.position = position;
        this.user = user;
        this.travel = travel;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
