package pt.ulusofona.aed.deisimdb;

import java.io.*;
import java.util.*;

public class Main {
    static ArrayList<Ator> atores = new ArrayList<>();
    static ArrayList<Filme> filmes = new ArrayList<>();
    static ArrayList<Realizador> diretores = new ArrayList<>();
    static ArrayList<GenresMovies> generos_filmes = new ArrayList<>();
    static ArrayList<InputInvalido> input_invalido = new ArrayList<>();
    static ArrayList<GeneroCinematografico> generoCinematograficos = new ArrayList<>();
    static ArrayList<MovieVotes> votos_filmes = new ArrayList<>();

    public static Map<Integer, Filme> filmesMap = new HashMap<>();
    public static Map<Integer, List<Ator>> atoresPorFilme = new HashMap<>();
    public static Map<Integer, List<Realizador>> realizadoresPorFilme = new HashMap<>();
    public static Map<Integer, List<GeneroCinematografico>> generosPorFilme = new HashMap<>();

    static boolean parseFiles(File folder) {
        filmes.clear();
        diretores.clear();
        input_invalido.clear();
        generoCinematograficos.clear();
        generos_filmes.clear();
        atores.clear();
        votos_filmes.clear();
        atoresPorFilme.clear();
        realizadoresPorFilme.clear();
        generosPorFilme.clear();
        filmesMap.clear();

        try {
            String[] files = {"movies.csv", "actors.csv", "directors.csv", "genres.csv", "genres_movies.csv", "movie_votes.csv"};
            for (String fileName : files) {
                File file = new File(folder, fileName);
                if (!file.exists()) {
                    System.err.println("Ficheiro não encontrado: " + fileName);
                    return false;
                }

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    br.readLine(); // cabeçalho
                    String line;
                    int contadorLinhasValidas = 0;
                    int contadorLinhasInvalidas = 0;
                    int primeiraLinhaErro = -1;
                    int linhaAtual = 1;

                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",", -1);
                        linhaAtual++;
                        try {
                            switch (fileName) {
                                case "actors.csv" -> {
                                    if (parts.length != 4) throw new Exception();
                                    Ator ator = new Ator(
                                            Integer.parseInt(parts[0].trim()),
                                            parts[1].trim(),
                                            parts[2].trim().charAt(0),
                                            Integer.parseInt(parts[3].trim()));
                                    atores.add(ator);
                                    contadorLinhasValidas++;
                                }
                                case "movies.csv" -> {
                                    if (parts.length != 5) throw new Exception();
                                    int movieId = Integer.parseInt(parts[0].trim());
                                    if (!filmesMap.containsKey(movieId)) {
                                        Filme filme = new Filme(movieId, parts[1].trim(),
                                                Double.parseDouble(parts[2].trim()),
                                                (int) Double.parseDouble(parts[3].trim()),
                                                parts[4].trim(), 0);
                                        filmes.add(filme);
                                        filmesMap.put(movieId, filme);
                                    }
                                    contadorLinhasValidas++;
                                }
                                case "directors.csv" -> {
                                    if (parts.length != 3) throw new Exception();
                                    Realizador r = new Realizador(
                                            Integer.parseInt(parts[0].trim()),
                                            parts[1].trim(),
                                            Integer.parseInt(parts[2].trim()));
                                    diretores.add(r);
                                    contadorLinhasValidas++;
                                }
                                case "genres.csv" -> {
                                    if (parts.length != 2) throw new Exception();
                                    generoCinematograficos.add(new GeneroCinematografico(
                                            Integer.parseInt(parts[0].trim()),
                                            parts[1].trim()));
                                    contadorLinhasValidas++;
                                }
                                case "genres_movies.csv" -> {
                                    if (parts.length != 2) throw new Exception();
                                    generos_filmes.add(new GenresMovies(
                                            Integer.parseInt(parts[0].trim()),
                                            Integer.parseInt(parts[1].trim())));
                                    contadorLinhasValidas++;
                                }
                                case "movie_votes.csv" -> {
                                    if (parts.length != 3) throw new Exception();
                                    votos_filmes.add(new MovieVotes(
                                            Integer.parseInt(parts[0].trim()),
                                            Double.parseDouble(parts[1].trim()),
                                            Integer.parseInt(parts[2].trim())));
                                    contadorLinhasValidas++;
                                }
                            }
                        } catch (Exception e) {
                            contadorLinhasInvalidas++;
                            if (primeiraLinhaErro == -1) primeiraLinhaErro = linhaAtual - 1;
                        }
                    }

                    input_invalido.add(new InputInvalido(fileName, contadorLinhasValidas, contadorLinhasInvalidas, primeiraLinhaErro));
                }
            }

            for (GenresMovies gm : generos_filmes) {
                Filme filme = filmesMap.get(gm.movieId);
                if (filme != null) {
                    for (GeneroCinematografico gc : generoCinematograficos) {
                        if (gc.genreId == gm.genreId) {
                            filme.generos.add(gc);
                            filme.numeroGenerosCinematograficos++;
                            break;
                        }
                    }
                }
            }

            for (Realizador r : diretores) {
                Filme filme = filmesMap.get(r.movieId);
                if (filme != null) {
                    filme.realizadores.add(r);
                    filme.numeroRealizadores++;
                }
            }

            for (Ator ator : atores) {
                Filme filme = filmesMap.get(ator.movieId);
                if (filme != null) {
                    filme.atores.add(ator);
                    if (ator.actorGender == 'M') filme.numeroAtoresMasculinos++;
                    else if (ator.actorGender == 'F') filme.numeroAtoresFemininos++;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler ficheiros: " + e.getMessage());
            return false;
        }

        return true;
    }


    public static ArrayList getObjects(TipoEntidade tipo) {
        switch (tipo) {
            case FILME:
                return filmes;
            case ATOR:
                return atores;
            case REALIZADOR:
                return diretores;
            case GENERO_CINEMATOGRAFICO:
                return generoCinematograficos;
            case INPUT_INVALIDO:
                return input_invalido;
            default:
                return new ArrayList<>();
        }
    }

    public static Result execute(String input) {
        if (input.isEmpty()) return new ErrorResult("Comando vazio.");

        String[] parts = input.split(" ", 2);
        String command = parts[0].toUpperCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "COUNT_MOVIES_MONTH_YEAR": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ");

                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: COUNT_MOVIES_MONTH_YEAR <month> <year>");
                }

                try {
                    int mes = Integer.parseInt(args[0]);
                    int ano = Integer.parseInt(args[1]);

                    long total = filmes.stream()
                            .filter(f -> f.getAno() == ano && f.getMes() == mes)
                            .count();

                    return new SuccessResult(String.valueOf(total));

                } catch (NumberFormatException e) {
                    return new ErrorResult("0");
                }
            }

            case "COUNT_MOVIES_DIRECTOR": {
                String nomeProcurado = arguments.trim();

                if (nomeProcurado.isEmpty()) {
                    return new ErrorResult("Sintaxe: COUNT_MOVIES_DIRECTOR <full-name>");
                }

                // Procurar quantos filmes o realizador realizou
                long total = diretores.stream()
                        .filter(d -> d.directorName.equalsIgnoreCase(nomeProcurado))
                        .map(d -> d.movieId)
                        .distinct()  // garante que só conta uma vez por filme
                        .count();

                return new SuccessResult(String.valueOf(total));
            }

            case "COUNT_ACTORS_IN_2_YEARS": {
                String[] args = arguments.trim().split(" ");

                if (args.length != 2) {
                    return new ErrorResult("Uso correto: COUNT_ACTORS_IN_2_YEARS <ano1> <ano2>");
                }

                try {
                    int ano1 = Integer.parseInt(args[0]);
                    int ano2 = Integer.parseInt(args[1]);

                    Set<Integer> atoresUnicos = new HashSet<>();

                    for (Filme f : filmes) {
                        int ano = f.getAno();
                        if (ano == ano1 || ano == ano2) {
                            for (Ator ator : Main.atores) {
                                if (ator.movieId == f.movieId) {
                                    atoresUnicos.add(ator.actorId);
                                }
                            }
                        }
                    }

                    return new SuccessResult(String.valueOf(atoresUnicos.size()));
                } catch (NumberFormatException e) {
                    return new ErrorResult("Os anos devem ser números inteiros.");
                }
            }


            case "COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ");

                if (args.length != 4) {
                    return new ErrorResult("Sintaxe: COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS <ano-inicio> <ano-fim> <min> <max>");
                }

                try {
                    int anoInicio = Integer.parseInt(args[0]);
                    int anoFim = Integer.parseInt(args[1]);
                    int minAtores = Integer.parseInt(args[2]);
                    int maxAtores = Integer.parseInt(args[3]);

                    long total = filmes.stream()
                            .filter(f -> {
                                int ano = f.getAno();
                                int totalAtores = f.numeroAtoresMasculinos + f.numeroAtoresFemininos;
                                return ano >= anoInicio && ano <= anoFim &&
                                        totalAtores >= minAtores && totalAtores <= maxAtores;
                            })
                            .count();

                    return new SuccessResult(String.valueOf(total));
                } catch (NumberFormatException e) {
                    return new ErrorResult("Os argumentos devem ser números inteiros.");
                }
            }


            case "GET_MOVIES_ACTOR_YEAR": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ", 2);

                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: GET_MOVIES_ACTOR_YEAR <ano> <nome-completo>");
                }

                try {
                    int ano = Integer.parseInt(args[0]);
                    String nomeAtor = args[1].trim().toLowerCase();

                    List<String> filmesDoAtor = new ArrayList<>();

                    for (Filme filme : filmes) {
                        if (filme.getAno() == ano) {
                            for (Ator ator : Main.atores) {
                                if (ator.movieId == filme.movieId &&
                                        ator.actorName.toLowerCase().equals(nomeAtor)) {
                                    filmesDoAtor.add(filme.movieName);
                                    break;
                                }
                            }
                        }
                    }

                    if (filmesDoAtor.isEmpty()) {
                        return new SuccessResult("No results");
                    }

                    return new SuccessResult(String.join("\n", filmesDoAtor));

                } catch (NumberFormatException e) {
                    return new ErrorResult("Ano inválido.");
                }
            }


            case "GET_MOVIES_WITH_ACTOR_CONTAINING": {
                String nomeProcurado = arguments.trim().toLowerCase();

                if (nomeProcurado.isEmpty()) {
                    return new ErrorResult("Sintaxe: GET_MOVIES_WITH_ACTOR_CONTAINING <texto>");
                }

                Set<Integer> filmesEncontrados = new HashSet<>();

                for (Ator ator : atores) {
                    if (ator.actorName.toLowerCase().contains(nomeProcurado)) {
                        filmesEncontrados.add(ator.movieId);
                    }
                }

                List<String> nomesFilmes = new ArrayList<>();
                for (Filme filme : filmes) {
                    if (filmesEncontrados.contains(filme.movieId)) {
                        nomesFilmes.add(filme.movieName);
                    }
                }

                if (nomesFilmes.isEmpty()) {
                    return new SuccessResult("No results");
                }

                nomesFilmes.sort(String::compareToIgnoreCase); // ← ordenação alfabética

                return new SuccessResult(String.join("\n", nomesFilmes));
            }


            case "GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING": {
                String termoProcurado = arguments.trim().toLowerCase();

                if (termoProcurado.isEmpty()) {
                    return new ErrorResult("Sintaxe: GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING <search-string>");
                }

                Map<Integer, Integer> contagemPorAno = new HashMap<>();

                for (Filme filme : filmes) {
                    if (filme.movieName.toLowerCase().contains(termoProcurado)) {
                        int ano = filme.getAno();
                        if (ano > 0) {
                            contagemPorAno.put(ano, contagemPorAno.getOrDefault(ano, 0) + 1);
                        }
                    }
                }

                List<Map.Entry<Integer, Integer>> topAnos = new ArrayList<>(contagemPorAno.entrySet());

                topAnos.sort((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue()); // por número de filmes desc
                    if (cmp == 0) {
                        return Integer.compare(a.getKey(), b.getKey()); // ano ascendente em caso de empate
                    }
                    return cmp;
                });

                List<String> resultado = new ArrayList<>();
                for (int i = 0; i < Math.min(4, topAnos.size()); i++) {
                    Map.Entry<Integer, Integer> entry = topAnos.get(i);
                    resultado.add(entry.getKey() + ":" + entry.getValue()); // ← aqui mudamos o formato
                }

                if (resultado.isEmpty()) {
                    return new SuccessResult("No results");
                }

                return new SuccessResult(String.join("\n", resultado)); // ← output limpo, linha a linha
            }


            case "GET_ACTORS_BY_DIRECTOR": {
                String[] args = arguments.trim().split(" ", 2);
                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: GET_ACTORS_BY_DIRECTOR <num> <full-name>");
                }

                int minimoFilmes;
                try {
                    minimoFilmes = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    return new ErrorResult("O número mínimo de filmes deve ser um número inteiro.");
                }

                String nomeRealizador = args[1].trim().toLowerCase();
                Map<String, Integer> contadorAtores = new HashMap<>();

                for (Filme f : filmes) {
                    boolean temRealizador = f.realizadores.stream()
                            .anyMatch(r -> r.directorName.equalsIgnoreCase(nomeRealizador));

                    if (temRealizador) {
                        for (Ator a : f.atores) {
                            contadorAtores.put(a.actorName,
                                    contadorAtores.getOrDefault(a.actorName, 0) + 1);
                        }
                    }
                }

                List<String> resultado = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : contadorAtores.entrySet()) {
                    if (entry.getValue() >= minimoFilmes) {
                        resultado.add(entry.getKey() + ":" + entry.getValue());
                    }
                }

                if (resultado.isEmpty()) {
                    return new SuccessResult("No results");
                }

                resultado.sort(String::compareToIgnoreCase); // ordem alfabética
                return new SuccessResult(String.join("\n", resultado));
            }


            case "TOP_MONTH_MOVIE_COUNT": {
                String anoStr = arguments.trim();
                int ano;
                try {
                    ano = Integer.parseInt(anoStr);
                } catch (NumberFormatException e) {
                    return new ErrorResult("O ano deve ser um número inteiro.");
                }

                Map<Integer, Integer> contagemPorMes = new HashMap<>();

                for (Filme f : filmes) {
                    if (f.getAno() == ano) {
                        int mes = f.getMes();
                        contagemPorMes.put(mes, contagemPorMes.getOrDefault(mes, 0) + 1);
                    }
                }

                if (contagemPorMes.isEmpty()) {
                    return new SuccessResult("Não há filmes lançados no ano " + ano + ".");
                }


                List<Map.Entry<Integer, Integer>> ordenado = new ArrayList<>(contagemPorMes.entrySet());
                ordenado.sort((a, b) -> {
                    if (!b.getValue().equals(a.getValue())) {
                        return b.getValue() - a.getValue(); // mais filmes primeiro
                    } else {
                        return a.getKey() - b.getKey(); // mês menor primeiro
                    }
                });

                List<String> resultado = new ArrayList<>();
                int limite = Math.min(3, ordenado.size());
                for (int i = 0; i < limite; i++) {
                    Map.Entry<Integer, Integer> entry = ordenado.get(i);
                    resultado.add(entry.getKey() + ":" + entry.getValue());
                }

                return new SuccessResult(String.join("\n", resultado));
            }


            case "TOP_VOTED_ACTORS": {
                String[] args = arguments.trim().split(" ");
                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: TOP_VOTED_ACTORS <num> <year>");
                }

                int num, year;
                try {
                    num = Integer.parseInt(args[0]);
                    year = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    return new ErrorResult("Argumentos inválidos. Usa: TOP_VOTED_ACTORS <num> <year>");
                }


                Map<Integer, Integer> votosPorFilme = new HashMap<>();
                for (MovieVotes v : votos_filmes) {
                    votosPorFilme.put(v.movieId, v.movieRatingCount);
                }


                Map<String, Integer> votosPorAtor = new HashMap<>();
                for (Ator ator : atores) {
                    Filme f = filmesMap.get(ator.movieId);
                    if (f != null && f.getAno() == year) {
                        int votos = votosPorFilme.getOrDefault(ator.movieId, 0);
                        votosPorAtor.put(ator.actorName,
                                votosPorAtor.getOrDefault(ator.actorName, 0) + votos);
                    }
                }

                // Ordenar os atores por votos (desc) e nome (asc)
                List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(votosPorAtor.entrySet());
                ordenado.sort((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());
                    return cmp != 0 ? cmp : a.getKey().compareToIgnoreCase(b.getKey());
                });

                // Construir resultado final
                List<String> resultado = new ArrayList<>();
                for (int i = 0; i < Math.min(num, ordenado.size()); i++) {
                    Map.Entry<String, Integer> entry = ordenado.get(i);
                    resultado.add(entry.getKey() + ":" + entry.getValue());
                }

                if (resultado.isEmpty()) {
                    return new SuccessResult("No results");
                }

                return new SuccessResult(String.join("\n", resultado));
            }




            case "TOP_MOVIES_WITH_MORE_GENDER": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ");

                if (args.length != 3) {
                    return new ErrorResult("Sintaxe: TOP_MOVIES_WITH_MORE_GENDER <num> <year> <gender>");
                }

                try {
                    int num = Integer.parseInt(args[0]);
                    int year = Integer.parseInt(args[1]);
                    char gender = args[2].trim().toUpperCase().charAt(0);

                    if (gender != 'M' && gender != 'F') {
                        return new ErrorResult("Género deve ser M ou F.");
                    }

                    Comparator<Filme> comparator = (f1, f2) -> {
                        int count1 = gender == 'M' ? f1.numeroAtoresMasculinos : f1.numeroAtoresFemininos;
                        int count2 = gender == 'M' ? f2.numeroAtoresMasculinos : f2.numeroAtoresFemininos;
                        return Integer.compare(count2, count1); // ordem decrescente
                    };

                    List<String> topFilmes = filmes.stream()
                            .filter(f -> f.getAno() == year)
                            .sorted(comparator)
                            .limit(num)
                            .map(f -> {
                                int count = gender == 'M' ? f.numeroAtoresMasculinos : f.numeroAtoresFemininos;
                                return f.movieName + ":" + count;
                            })
                            .toList();

                    if (topFilmes.isEmpty()) {
                        return new SuccessResult("No results");
                    }

                    return new SuccessResult(String.join("\n", topFilmes));

                } catch (Exception e) {
                    return new ErrorResult("Erro ao interpretar os argumentos.");
                }
            }


            case "TOP_MOVIES_WITH_GENDER_BIAS": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ");

                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: TOP_MOVIES_WITH_GENDER_BIAS <num> <year>");
                }

                try {
                    int num = Integer.parseInt(args[0]);
                    int year = Integer.parseInt(args[1]);

                    List<String> resultado = filmes.stream()
                            .filter(f -> f.getAno() == year && (f.numeroAtoresMasculinos + f.numeroAtoresFemininos) >= 11)
                            .map(f -> {
                                int total = f.numeroAtoresMasculinos + f.numeroAtoresFemininos;
                                int percentM = (int) Math.round((f.numeroAtoresMasculinos * 100.0) / total);
                                int percentF = 100 - percentM;
                                char generoPredominante = percentM >= percentF ? 'M' : 'F';
                                int percentagem = Math.max(percentM, percentF);
                                return new AbstractMap.SimpleEntry<>(f.movieName, generoPredominante + ":" + percentagem);
                            })
                            .sorted((a, b) -> {
                                // Extrair % para ordenar
                                int percentA = Integer.parseInt(a.getValue().split(":")[1].replace("%", ""));
                                int percentB = Integer.parseInt(b.getValue().split(":")[1].replace("%", ""));
                                if (percentB != percentA) {
                                    return Integer.compare(percentB, percentA); // maior % primeiro
                                } else {
                                    return a.getKey().compareToIgnoreCase(b.getKey()); // nome A-Z
                                }
                            })
                            .limit(num)
                            .map(entry -> entry.getKey() + ":" + entry.getValue())
                            .toList();

                    if (resultado.isEmpty()) {
                        return new SuccessResult("No results");
                    }

                    return new SuccessResult(String.join("\n", resultado));

                } catch (NumberFormatException e) {
                    return new ErrorResult("Argumentos inválidos. Usa: TOP_MOVIES_WITH_GENDER_BIAS <num> <year>");
                }
            }


            case "TOP_6_DIRECTORS_WITHIN_FAMILY": {
                String[] args = arguments.replace("<", "").replace(">", "").split(" ");
                if (args.length != 2) {
                    return new ErrorResult("Sintaxe: TOP_6_DIRECTORS_WITHIN_FAMILY <year-start> <year-end>");
                }

                try {
                    int startYear = Integer.parseInt(args[0]);
                    int endYear = Integer.parseInt(args[1]);

                    Map<String, Integer> colaboracoes = new HashMap<>();

                    for (Filme filme : filmes) {
                        int ano = filme.getAno();
                        if (ano >= startYear && ano <= endYear && filme.realizadores.size() >= 2) {
                            // Agrupar realizadores por apelido
                            Map<String, List<Realizador>> porApelido = new HashMap<>();
                            for (Realizador r : filme.realizadores) {
                                String[] partes = r.directorName.trim().split(" ");
                                String apelido = partes[partes.length - 1];
                                porApelido.putIfAbsent(apelido, new ArrayList<>());
                                porApelido.get(apelido).add(r);
                            }

                            // Para cada grupo com apelido igual e mais de um realizador, contar colaboração
                            for (List<Realizador> grupo : porApelido.values()) {
                                if (grupo.size() >= 2) {
                                    for (Realizador r : grupo) {
                                        colaboracoes.put(r.directorName,
                                                colaboracoes.getOrDefault(r.directorName, 0) + 1);
                                    }
                                }
                            }
                        }
                    }

                    if (colaboracoes.isEmpty()) {
                        return new SuccessResult("No results");
                    }

                    // Ordenar por nº colaborações desc, depois por nome asc
                    List<Map.Entry<String, Integer>> ordenado = new ArrayList<>(colaboracoes.entrySet());
                    ordenado.sort((a, b) -> {
                        int cmp = Integer.compare(b.getValue(), a.getValue());
                        return cmp != 0 ? cmp : a.getKey().compareToIgnoreCase(b.getKey());
                    });

                    List<String> resultado = new ArrayList<>();
                    for (int i = 0; i < Math.min(6, ordenado.size()); i++) {
                        Map.Entry<String, Integer> entry = ordenado.get(i);
                        resultado.add(entry.getKey() + ":" + entry.getValue());
                    }

                    return new SuccessResult(String.join("\n", resultado));
                } catch (NumberFormatException e) {
                    return new ErrorResult("Ano inválido.");
                }
            }


            case "INSERT_ACTOR": {
                String[] campos = arguments.split(";");

                if (campos.length != 4) {
                    return new ErrorResult("Sintaxe inválida. Usa: INSERT_ACTOR <id>;<name>;<gender>;<movie-id>");
                }

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String nome = campos[1].trim();
                    char genero = campos[2].trim().toUpperCase().charAt(0);
                    int movieId = Integer.parseInt(campos[3].trim());

                    if (genero != 'M' && genero != 'F') {
                        return new ErrorResult("Género inválido. Usa apenas 'M' ou 'F'.");
                    }

                    Filme filme = filmesMap.get(movieId);
                    if (filme == null) {
                        return new ErrorResult("Filme com ID " + movieId + " não encontrado.");
                    }

                    Ator novoAtor = new Ator(id, nome, genero, movieId);
                    atores.add(novoAtor);
                    filme.atores.add(novoAtor);

                    if (genero == 'M') {
                        filme.numeroAtoresMasculinos++;
                    } else {
                        filme.numeroAtoresFemininos++;
                    }

                    return new SuccessResult("OK");
                } catch (Exception e) {
                    return new ErrorResult("Erro ao inserir ator: " + e.getMessage());
                }
            }

            case "INSERT_DIRECTOR": {
                String[] campos = arguments.split(";");

                if (campos.length != 3) {
                    return new ErrorResult("Sintaxe inválida. Usa: INSERT_DIRECTOR <id>;<name>;<movie-id>");
                }

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String nome = campos[1].trim();
                    int movieId = Integer.parseInt(campos[2].trim());

                    Filme filme = filmesMap.get(movieId);
                    if (filme == null) {
                        return new ErrorResult("Filme com ID " + movieId + " não encontrado.");
                    }

                    Realizador novoRealizador = new Realizador(id, nome, movieId);
                    diretores.add(novoRealizador);
                    filme.realizadores.add(novoRealizador);
                    filme.numeroRealizadores++;

                    return new SuccessResult("OK");
                } catch (Exception e) {
                    return new ErrorResult("Erro ao inserir realizador: " + e.getMessage());
                }
            }


            case "DISTANCE_BETWEEN_ACTORS": {
                String[] nomes = arguments.split(",");
                if (nomes.length != 2) {
                    return new ErrorResult("Sintaxe inválida. Usa: DISTANCE_BETWEEN_ACTORS <actor-1>,<actor-2>");
                }

                String nome1 = nomes[0].trim();
                String nome2 = nomes[1].trim();

                if (nome1.equalsIgnoreCase(nome2)) {
                    return new SuccessResult("Distância entre atores: 0");
                }


                Map<String, Set<Integer>> atorParaFilmes = new HashMap<>();
                for (Ator ator : atores) {
                    atorParaFilmes.computeIfAbsent(ator.actorName, k -> new HashSet<>()).add(ator.movieId);
                }

                if (!atorParaFilmes.containsKey(nome1) || !atorParaFilmes.containsKey(nome2)) {
                    return new ErrorResult("Um ou ambos os atores não foram encontrados.");
                }

                Map<String, Set<String>> colaboracoes = new HashMap<>();
                for (Filme f : filmes) {
                    List<String> nomesAtores = new ArrayList<>();
                    for (Ator a : atores) {
                        if (a.movieId == f.movieId) {
                            nomesAtores.add(a.actorName);
                        }
                    }
                    for (String a1 : nomesAtores) {
                        for (String a2 : nomesAtores) {
                            if (!a1.equals(a2)) {
                                colaboracoes.computeIfAbsent(a1, k -> new HashSet<>()).add(a2);
                            }
                        }
                    }
                }

                Queue<String> queue = new LinkedList<>();
                Map<String, Integer> dist = new HashMap<>();

                queue.add(nome1);
                dist.put(nome1, 0);

                while (!queue.isEmpty()) {
                    String atual = queue.poll();
                    int d = dist.get(atual);

                    for (String vizinho : colaboracoes.getOrDefault(atual, Collections.emptySet())) {
                        if (!dist.containsKey(vizinho)) {
                            dist.put(vizinho, d + 1);
                            queue.add(vizinho);
                            if (vizinho.equalsIgnoreCase(nome2)) {
                                return new SuccessResult("Distância entre atores: " + (d + 1));
                            }
                        }
                    }
                }

                return new SuccessResult("Não existe ligação entre os dois atores.");
            }


            case "HELP":
                return new SuccessResult("Comandos disponíveis:\n" +
                        "COUNT_MOVIES_MONTH_YEAR <month> <year>\n" +
                        "COUNT_MOVIES_DIRECTOR <full-name>\n" +
                        "COUNT_ACTORS_IN_2_YEARS <year-1> <year-2>\n" +
                        "COUNT_MOVIES_BETWEEN_YEARS_WITH_N_ACTORS <year-start> <year-end> <min> <max>\n" +
                        "GET_MOVIES_ACTOR_YEAR <year> <full-name>\n" +
                        "GET_MOVIES_WITH_ACTOR_CONTAINING <name>\n" +
                        "GET_TOP_4_YEARS_WITH_MOVIES_CONTAINING <search-string>\n" +
                        "GET_ACTORS_BY_DIRECTOR <num> <full-name>\n" +
                        "TOP_MONTH_MOVIE_COUNT <year>\n" +
                        "TOP_VOTED_ACTORS <num> <year>\n" +
                        "TOP_MOVIES_WITH_MORE_GENDER <num> <year> <gender>\n" +
                        "TOP_MOVIES_WITH_GENDER_BIAS <num> <year>\n" +
                        "TOP_6_DIRECTORS_WITHIN_FAMILY <year-start> <year-end>\n" +
                        "INSERT_ACTOR <id>;<name>;<gender>;<movie-id>\n" +
                        "INSERT_DIRECTOR <id>;<name>;<movie-id>\n" +
                        "DISTANCE_BETWEEN_ACTORS <actor-1>,<actor-2>\n" +
                        "HELP\n" +
                        "QUIT");

            default:
                return new ErrorResult("Comando invalido");
        }
    }


    public static void main(String[] args) {
        System.out.println("Bem-vindo ao deisIMDB");
        long start = System.currentTimeMillis();
        boolean parseOk = parseFiles(new File("."));
        if (!parseOk) {
            System.out.println("Erro na leitura dos ficheiros");
            return;
        }
        long end = System.currentTimeMillis();
        System.out.println("Ficheiros lidos com sucesso em " + (end - start) + " ms");

        Result result = execute("HELP");
        System.out.println(result.result);
        Scanner in = new Scanner(System.in);

        String line;
        do {
            System.out.print("> ");
            line = in.nextLine(); // Read input at the start of the loop

            if (line != null && !line.equals("QUIT")) {
                start = System.currentTimeMillis();
                result = execute(line);
                end = System.currentTimeMillis();

                if (!result.success) {
                    System.out.println("Erro: " + result.error);
                } else {
                    System.out.println(result.result);
                    System.out.println("(demorou " + (end - start) + " ms)");
                }
            }


        } while (line != null && !line.equals("QUIT"));

    }
}