package pt.ulusofona.aed.deisimdb;

public class GenresMovies {
    int genreId;
    int movieId;

    GenresMovies(int genreId, int movieId) {
        this.genreId=genreId;
        this.movieId=movieId;
    }
    public String toString() {
        return genreId + " | " + movieId;
    }
}
