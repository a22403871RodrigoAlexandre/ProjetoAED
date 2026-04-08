package pt.ulusofona.aed.deisimdb;

public class MovieVotes {
    int movieId;
    double movieRating;
    int movieRatingCount;

    MovieVotes(int movieId, double movieRating, int movieRatingCount) {
        this.movieId=movieId;
        this.movieRating=movieRating;
        this.movieRatingCount=movieRatingCount;
    }
    public String toString() {
        return movieId + " | " + movieRating + " | " + movieRatingCount;
    }
}
