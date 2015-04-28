package api.util;

/**
 * Created by Dev on 21/04/2015.
 */
public enum HttpStatus {

    SUCCESS (200),
    UNAUTHORIZED (401),
    SERVER_ERROR (500);

    private int value;

    HttpStatus(int value){
        this.value = value;
    }
}
