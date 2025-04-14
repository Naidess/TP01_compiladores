package analizadorlexico;

import java.io.*;
import java.util.*;

public class Lexer {
    private final BufferedReader reader;
    private String currentLine;
    private int lineNumber;

    public Lexer(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
        this.lineNumber = 1;
    }

    private List<Token> processLine(String line) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < line.length()) {
            char currentChar = line.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                i++;
                continue;
            }

            if (currentChar == '"') {
                String str = extractString(line, i);
                tokens.add(new Token(TokenType.LITERAL_CADENA, str));
                i += str.length() + 2;  // skip the quotes
            } else if (Character.isDigit(currentChar)) {
                String num = extractNumber(line, i);
                tokens.add(new Token(TokenType.LITERAL_NUM, num));
                i += num.length();
            } else if (Character.isLetter(currentChar)) {
                String word = extractWord(line, i);
                TokenType type = getKeywordType(word);
                if (type != TokenType.ERROR) {
                    tokens.add(new Token(type, word));
                    i += word.length();
                } else {
                    System.err.println("Error léxico en línea " + lineNumber + ": palabra no reconocida '" + word + "'");
                    break;
                }
            } else {
                TokenType type = getTokenType(currentChar);
                if (type != TokenType.ERROR) {
                    tokens.add(new Token(type, String.valueOf(currentChar)));
                    i++;
                } else {
                    System.err.println("Error léxico en línea " + lineNumber + ": carácter inválido '" + currentChar + "'");
                    break;
                }
            }
        }
        return tokens;
    }

    public void processFile() throws IOException {
        String line;
        int level = 0;
        while ((line = reader.readLine()) != null) {
            currentLine = line.trim();
            List<Token> tokens = processLine(currentLine);
            level = printTokens(tokens, level);  // Devuelve el nuevo nivel
            lineNumber++;
        }
    }
    
    private int printTokens(List<Token> tokens, int level) {
        StringBuilder output = new StringBuilder();
        boolean nuevaLinea = true;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Decrementar nivel si es cierre
            if (token.getType() == TokenType.R_LLAVE || token.getType() == TokenType.R_CORCHETE) {
                level--;
            }

            // Indentar si corresponde
            if (nuevaLinea) {
                output.append("\t".repeat(level));
                nuevaLinea = false;
            }

            // Manejo especial para cierre seguido de coma
            if ((token.getType() == TokenType.R_LLAVE || token.getType() == TokenType.R_CORCHETE)
                    && i + 1 < tokens.size()
                    && tokens.get(i + 1).getType() == TokenType.COMA) {
                output.append(token.getType().name()).append(" COMA\n");
                i++; // saltar la coma
                nuevaLinea = true;
                continue;
            }

            // Imprimir token
            switch (token.getType()) {
                case LITERAL_CADENA -> output.append("STRING");
                case LITERAL_NUM -> output.append("NUMBER");
                case PR_FALSE -> output.append("PR_FALSE");
                case PR_TRUE -> output.append("PR_TRUE");
                case PR_NULL -> output.append("PR_NULL");
                case DOS_PUNTOS -> output.append(" DOS_PUNTOS ");
                case COMA -> {
                    output.append(" COMA\n");
                    nuevaLinea = true;
                }
                case L_LLAVE, L_CORCHETE -> {
                    output.append(token.getType().name()).append("\n");
                    nuevaLinea = true;
                    level++;
                }
                case R_LLAVE, R_CORCHETE -> {
                    output.append(token.getType().name()).append("\n");
                    nuevaLinea = true;
                }
                default -> output.append(token.getType().name());
            }

            if (i + 1 < tokens.size()) {
                Token nextToken = tokens.get(i + 1);
                if ((nextToken.getType() == TokenType.R_LLAVE || nextToken.getType() == TokenType.R_CORCHETE)
                    && token.getType() != TokenType.COMA && token.getType() != TokenType.DOS_PUNTOS) {
                    output.append("\n");
                    nuevaLinea = true;
                }
            }
        }

        System.out.print(output.toString());
        return level;
    }

    private String extractString(String line, int startIndex) {
        int endIndex = line.indexOf('"', startIndex + 1);
        if (endIndex == -1) {
            return "";
        }
        return line.substring(startIndex + 1, endIndex);
    }

    private String extractNumber(String line, int startIndex) {
        int i = startIndex;
        while (i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.')) {
            i++;
        }
        return line.substring(startIndex, i);
    }
    
    private String extractWord(String line, int startIndex) {
        int i = startIndex;
        while (i < line.length() && Character.isLetter(line.charAt(i))) {
        i++;
        }
        return line.substring(startIndex, i);
    }

    private TokenType getKeywordType(String word) {
        return switch (word.toLowerCase()) {
            case "true" -> TokenType.PR_TRUE;
            case "false" -> TokenType.PR_FALSE;
            case "null" -> TokenType.PR_NULL;
            default -> TokenType.ERROR;
        };
    }
    
    private TokenType getTokenType(char currentChar) {
        return switch (currentChar) {
            case '{' -> TokenType.L_LLAVE;
            case '}' -> TokenType.R_LLAVE;
            case '[' -> TokenType.L_CORCHETE;
            case ']' -> TokenType.R_CORCHETE;
            case ':' -> TokenType.DOS_PUNTOS;
            case ',' -> TokenType.COMA;
            default -> TokenType.ERROR;
        };
    }
}
