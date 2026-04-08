package pt.ulusofona.aed.deisimdb;

import java.util.ArrayList;
import java.util.List;

public class SuccessResult extends Result {

    public SuccessResult(List<String> result) {
        this.success = true;
        this.result = String.valueOf(result);
        this.error = null;
    }
    public SuccessResult() {
        this.success = true;
        this.result = String.valueOf(new ArrayList<>());
        this.error = null;
    }
    public SuccessResult(String message) {
        this.success = true;
        this.result = message;
        this.error = null;
    }



}
