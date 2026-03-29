package analizadorlexico;

import java.io.*;
import java.util.*;

public class Lexer {
    private final String contenido;
    private int lineNumber;

    public Lexer(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n"); //conservamos saltos de linea
        }
        reader.close();
        this.contenido = sb.toString();
        this.lineNumber = 1;
    }

    public void processFile() {
        List<Token> tokens = processLine(contenido);
        printTokens(tokens, 0);
    }

    private List<Token> processLine(String text) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;

        while (i < text.length()) {
            char currentChar = text.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                if (currentChar == '\n') lineNumber++;
                i++;
                continue;
            }

            if (currentChar == '"') {
                String str = extractString(text, i);
                tokens.add(new Token(TokenType.LITERAL_CADENA, str));
                i += str.length() + 2;
            } else if (Character.isDigit(currentChar)) {
                String num = extractNumber(text, i);
                tokens.add(new Token(TokenType.LITERAL_NUM, num));
                i += num.length();
            } else if (Character.isLetter(currentChar)) {
                String word = extractWord(text, i);
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

    private String extractString(String text, int startIndex) {
        int endIndex = text.indexOf('"', startIndex + 1);
        if (endIndex == -1) return "";
        return text.substring(startIndex + 1, endIndex);
    }

    private String extractNumber(String text, int startIndex) {
        int i = startIndex;
        while (i < text.length() && (Character.isDigit(text.charAt(i)) || text.charAt(i) == '.')) i++;
        return text.substring(startIndex, i);
    }

    private String extractWord(String text, int startIndex) {
        int i = startIndex;
        while (i < text.length() && Character.isLetter(text.charAt(i))) i++;
        return text.substring(startIndex, i);
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

	private int printTokens(List<Token> tokens, int level) {
		StringBuilder output = new StringBuilder();
		boolean nuevaLinea = true;

		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);

			switch (token.getType()) {
				//caso para cadenas que son las claves en el json
				case LITERAL_CADENA -> {
					if (nuevaLinea) {
						output.append("\t".repeat(level));
					}
					output.append("STRING");
					nuevaLinea = false;
					//si despues de la cadena viene dos_puntos, es una clave-valor
					if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.DOS_PUNTOS) {
						i++;
						output.append(" DOS_PUNTOS ");

						if (i + 1 < tokens.size()) {
							Token valor = tokens.get(i + 1);
							/*bandera que indica si es {} o [] para evitar procesar comas adicionales*/
							boolean esEstructuraCompleja = false;
							
							switch (valor.getType()) {
								//valores simples
								case LITERAL_CADENA -> {
									output.append("STRING");
									i++;
									esEstructuraCompleja = false;
								}
								case LITERAL_NUM -> {
									output.append("NUMBER");
									i++;
									esEstructuraCompleja = false;
								}
								case PR_TRUE -> {
									output.append("PR_TRUE");
									i++;
									esEstructuraCompleja = false;
								}
								case PR_FALSE -> {
									output.append("PR_FALSE");
									i++;
									esEstructuraCompleja = false;
								}
								case PR_NULL -> {
									output.append("PR_NULL");
									i++;
									esEstructuraCompleja = false;
								}
								//estructura compleja {} son los objetos
								case L_LLAVE -> {
									output.append("L_LLAVE\n");
									level++;
									esEstructuraCompleja = true;
									i++;
								}
								//estructura compleja [] array
								case L_CORCHETE -> {
									//verificar si es array vacio
									if (i + 2 < tokens.size() && tokens.get(i + 2).getType() == TokenType.R_CORCHETE) {
										output.append("L_CORCHETE R_CORCHETE");
										i += 2;
										esEstructuraCompleja = true;
									} else {
										output.append("L_CORCHETE\n");
										level++;
										esEstructuraCompleja = true;
										i++;
									}
								}
								
								default -> {
									output.append(valor.getType().name());
									i++;
									esEstructuraCompleja = false;
								}
							}
							//procesar coma y salto de linea solo si no es estructura compleja
							if (!esEstructuraCompleja) {
								if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.COMA) {
									output.append(" COMA ");
									i++;
								}
								output.append("\n");
								nuevaLinea = true;
							} else {
								nuevaLinea = true;
								//para arrays vacios, manejar la coma si existe
								if (valor.getType() == TokenType.L_CORCHETE && 
									i + 1 < tokens.size() && 
									tokens.get(i + 1).getType() == TokenType.COMA) {
									output.append(" COMA ");
									i++;
									output.append("\n");
								}
							}
						}
					}
				}
				//apertura de objeto {}
				case L_LLAVE -> {
					if (!nuevaLinea) output.append("\n");
					output.append("\t".repeat(level)).append("L_LLAVE\n");
					level++;
					nuevaLinea = true;
				}
				//apertura de array []
				case L_CORCHETE -> {
					if (!nuevaLinea) output.append("\n");
					output.append("\t".repeat(level)).append("L_CORCHETE");
					//verificar si es array vacio
					if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.R_CORCHETE) {
						output.append(" R_CORCHETE");
						i++;
						if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.COMA) {
							output.append(" COMA ");
							i++;
						}
						output.append("\n");
						nuevaLinea = true;
					} else {
						output.append("\n");
						level++;
						nuevaLinea = true;
					}
				}
				//cierre de objeto '}'
				case R_LLAVE -> {
					level--;
					output.append("\n");
					output.append("\t".repeat(level)).append("R_LLAVE");
					if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.COMA) {
						output.append(" COMA ");
						i++;
					}
					output.append("\n");
					nuevaLinea = true;
				}
				//cierre de array ']'
				case R_CORCHETE -> {
					level--;
					output.append("\n");
					output.append("\t".repeat(level)).append("R_CORCHETE");
					if (i + 1 < tokens.size() && tokens.get(i + 1).getType() == TokenType.COMA) {
						output.append(" COMA ");
						i++;
					}
					output.append("\n");
					nuevaLinea = true;
				}
				//token coma donde imprime espacios en ambos lados
				case COMA -> {
					output.append(" COMA ");
					nuevaLinea = false;
				}
				//numeros
				case LITERAL_NUM -> {
					if (nuevaLinea) output.append("\t".repeat(level));
					output.append("NUMBER");
					nuevaLinea = false;
				}
				//palabra reservada true
				case PR_TRUE -> {
					if (nuevaLinea) output.append("\t".repeat(level));
					output.append("PR_TRUE");
					nuevaLinea = false;
				}
				//palabra reservada false
				case PR_FALSE -> {
					if (nuevaLinea) output.append("\t".repeat(level));
					output.append("PR_FALSE");
					nuevaLinea = false;
				}
				//palabra reservada null
				case PR_NULL -> {
					if (nuevaLinea) output.append("\t".repeat(level));
					output.append("PR_NULL");
					nuevaLinea = false;
				}
				//caso por defecto para cualquier otro token
				default -> {
					if (nuevaLinea) output.append("\t".repeat(level));
					output.append(token.getType().name());
					nuevaLinea = false;
				}
			}
		}

		System.out.print(output.toString());
		return level;
	}
}