package pt.ulusofona.aed.deisimdb;

public class GeneroCinematografico {
    int genreId;
    String genreName;

    GeneroCinematografico(int genreId, String genreName) {
        this.genreId=genreId;
        this.genreName=genreName;
    }
    public String toString() {
        return genreId + " | " + genreName;
    }
}
