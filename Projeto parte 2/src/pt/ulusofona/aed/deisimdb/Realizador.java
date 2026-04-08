package pt.ulusofona.aed.deisimdb;

public class Realizador {
    int directorId;
    String directorName;
    int movieId;

    Realizador(int directorId, String directorName, int movieId) {
        this.directorId=directorId;
        this.directorName=directorName;
        this.movieId=movieId;
    }
    public Realizador(String nome) {
        this.directorName = nome;
    }
    public String toString() {
        return directorId + " | " + directorName + " | " + movieId;
    }
}
