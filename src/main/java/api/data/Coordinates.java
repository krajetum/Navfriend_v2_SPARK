package api.data;

import java.util.StringTokenizer;

/**
 * Created by Dev on 28/04/2015.
 */
public class Coordinates {

    private float longitude;
    private float latitude;

    public Coordinates(float longitude,float latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Coordinates(String coordinates){
        StringTokenizer s=new StringTokenizer(coordinates,",");
        latitude=Float.parseFloat(s.nextToken());
        longitude=Float.parseFloat(s.nextToken());
    }
}
