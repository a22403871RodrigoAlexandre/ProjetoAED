package pt.ulusofona.aed.deisimdb;

public class ErrorResult extends Result {
    public ErrorResult(String errorMessage) {
        this.success = false;
        this.result = null;
        this.error = errorMessage;
    }
}
