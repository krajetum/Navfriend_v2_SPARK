package api.data;

import api.util.HttpStatus;

import java.util.List;

/**
 * Created by Dev on 28/04/2015.
 */
public class ResTravel {
    private HttpStatus status;
    private Travel travel;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public ResTravel(HttpStatus status, Travel travel) {

        this.travel = travel;
        this.status = status;
    }
}
