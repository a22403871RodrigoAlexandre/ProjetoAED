package pt.ulusofona.aed.deisimdb;

import java.util.List;

public class Result {

    public boolean success;
    public String error;
    public String result;


    public String message() {
        return success ? result : error;
    }


}
