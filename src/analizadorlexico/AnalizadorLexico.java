package analizadorlexico;
import java.io.IOException;

public class AnalizadorLexico {
    public static void main(String[] args) {
        // Verificar si se pasó un argumento para el archivo
        if (args.length != 1) {
            System.err.println("Debe proporcionar el archivo fuente como argumento.");
            return;
        }
        
        String archivoEntrada = args[0];
        try {
            Lexer lexer = new Lexer(archivoEntrada);
            lexer.processFile();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
