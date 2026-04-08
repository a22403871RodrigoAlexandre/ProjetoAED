package pt.ulusofona.aed.deisimdb;

public class Ator {
    String gender;
    int actorId;
    String actorName;
    char actorGender;
    int movieId;

    Ator(int actorId, String actorName, char actorGender, int movieId ) {
        this.actorId=actorId;
        this.actorName=actorName;
        this.actorGender=actorGender;
        this.movieId=movieId;
    }
    public String toString() {
        if (actorId<1000) {

        }
        String gender = (actorGender == 'F') ? "Feminino" : "Masculino";
        return actorId + " | " + actorName + " | " + gender + " | " + movieId;
    }


}
