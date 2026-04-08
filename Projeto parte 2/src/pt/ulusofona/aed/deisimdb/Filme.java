package pt.ulusofona.aed.deisimdb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Filme {
    int movieId;
    String movieName;
    double movieDuration;
    int movieBudget;
    String movieReleaseDate;
    int numeroatores;
    int numeroGenerosCinematograficos;
    int numeroRealizadores;
    int numeroAtoresMasculinos;
    int numeroAtoresFemininos;

    List<GeneroCinematografico> generos = new ArrayList<>();
    List<Realizador> realizadores = new ArrayList<>();
    List<Ator> atores = new ArrayList<>();

    // Construtor completo
    Filme(int movieId, String movieName, double movieDuration, int movieBudget,
          String movieReleaseDate, int numeroatores,
          int numeroGenerosCinematograficos, int numeroRealizadores,
          int numeroAtoresMasculinos, int numeroAtoresFemininos) {

        this.movieId = movieId;
        this.movieName = movieName;
        this.movieDuration = movieDuration;
        this.movieBudget = movieBudget;
        this.movieReleaseDate = movieReleaseDate;
        this.numeroatores = numeroatores;
        this.numeroGenerosCinematograficos = numeroGenerosCinematograficos;
        this.numeroRealizadores = numeroRealizadores;
        this.numeroAtoresMasculinos = numeroAtoresMasculinos;
        this.numeroAtoresFemininos = numeroAtoresFemininos;

        this.generos = new ArrayList<>();
        this.realizadores = new ArrayList<>();
    }

    // Construtor simplificado (opcional)
    Filme(int movieId, String movieName, double movieDuration, int movieBudget,
          String movieReleaseDate, int numeroatores) {

        this.movieId = movieId;
        this.movieName = movieName;
        this.movieDuration = movieDuration;
        this.movieBudget = movieBudget;
        this.movieReleaseDate = movieReleaseDate;
        this.numeroatores = numeroatores;

        this.generos = new ArrayList<>();
        this.realizadores = new ArrayList<>();
    }

    static String converterFormatoData(String data) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatoEntrada.parse(data);
            return formatoSaida.format(date);
        } catch (ParseException e) {
            return data;
        }
    }
    public int getAno() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(movieReleaseDate.trim());
            return date.getYear() + 1900;
        } catch (ParseException e) {
            return -1;
        }
    }

    public int getMes() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(movieReleaseDate.trim());
            return date.getMonth() + 1;
        } catch (ParseException e) {
            return -1;
        }
    }



    @Override
    public String toString() {
        String dataFormatada = converterFormatoData(movieReleaseDate);

        if (movieId > 1000) {
            return movieId + " | " + movieName + " | " + dataFormatada
                    + " | " + numeroGenerosCinematograficos
                    + " | " + numeroRealizadores
                    + " | " + numeroAtoresMasculinos
                    + " | " + numeroAtoresFemininos;
        } else {
            List<String> nomesGeneros = new ArrayList<>();
            for (GeneroCinematografico g : generos) {
                nomesGeneros.add(g.genreName);
            }
            Collections.sort(nomesGeneros);

            List<String> nomesRealizadores = new ArrayList<>();
            for (Realizador r : realizadores) {
                nomesRealizadores.add(r.directorName);
            }
            Collections.sort(nomesRealizadores);

            return movieId + " | " + movieName + " | " + dataFormatada
                    + " | " + String.join(",", nomesGeneros)
                    + " | " + String.join(",", nomesRealizadores)
                    + " | " + numeroAtoresMasculinos
                    + " | " + numeroAtoresFemininos;
        }
    }
    public String getMovieName() {
        return movieName;
    }

}
