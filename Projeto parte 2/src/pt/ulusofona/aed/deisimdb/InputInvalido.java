package pt.ulusofona.aed.deisimdb;

public class InputInvalido {
    String fileName;
    int linesRead;
    int invalidLines;
    int firstInvalidLine;

    InputInvalido(String fileName, int linesRead, int invalidLines, int firstInvalidLine) {
        this.fileName = fileName;
        this.linesRead = linesRead;
        this.invalidLines = invalidLines;
        this.firstInvalidLine = firstInvalidLine;
    }
    public String toString() {
        return fileName + " | " + linesRead + " | " + invalidLines + " | " + firstInvalidLine;
    }

}
