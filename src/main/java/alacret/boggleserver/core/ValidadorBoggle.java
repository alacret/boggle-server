package alacret.boggleserver.core;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

/**
 * 
 * @author narape
 */
public class ValidadorBoggle {
	private static final int TAM_MINIMO_PALABRA = 3;
	private static final int FILAS_TABLERO = 4;

	public static int countPointsByWords(List<String> words, char[][] cube) {
		int points = 0;
		for (String word : words) {
			if (word == null)
				continue;
			word = word.trim();
			if (word.length() > TAM_MINIMO_PALABRA)
				if (validar(cube, word)) {
					points += getPointsPerWord(word);
				}
		}
		return points;

	}

	public static int getPointsPerWord(String word) {
		int length = word.length();
		if (length < 5)
			return 1;
		else if (length == 5)
			return 2;
		else if (length == 6)
			return 3;
		else if (length == 7)
			return 5;
		else
			return 11;
	}

	public static boolean validar(char[][] tablero, String palabra) {
		assert palabra != null && palabra.length() >= TAM_MINIMO_PALABRA;
		assert tablero.length == FILAS_TABLERO
				&& tablero[0].length == FILAS_TABLERO;

		char inicial = palabra.charAt(0);
		String restoPalabra = palabra.substring(1);
		boolean[][] letrasUtilizadas = new boolean[FILAS_TABLERO][FILAS_TABLERO];

		for (int i = 0; i < FILAS_TABLERO; i++) {
			for (int j = 0; j < FILAS_TABLERO; j++) {
				if (tablero[i][j] == inicial) {
					letrasUtilizadas[i][j] = true;
					if (validar(tablero, letrasUtilizadas, restoPalabra, i, j)) {
						return true;
					} else {
						letrasUtilizadas[i][j] = false;
					}
				}
			}
		}
		return false;
	}

	private static boolean validar(char[][] tablero,
			boolean[][] letrasUtilizadas, String palabra, int xi, int xj) {
		if (palabra.isEmpty()) {
			return true;
		}

		char inicial = palabra.charAt(0);
		String restoPalabra = palabra.substring(1);
		for (int i = max(xi - 1, 0), ni = min(xi + 1, FILAS_TABLERO - 1); i <= ni; i++) {
			for (int j = max(xj - 1, 0), nj = min(xj + 1, FILAS_TABLERO - 1); j <= nj; j++) {
				char actual = tablero[i][j];
				boolean utilizado = letrasUtilizadas[i][j];
				if ((i != xi || j != xj) && actual == inicial && !utilizado) {
					letrasUtilizadas[i][j] = true;
					if (validar(tablero, letrasUtilizadas, restoPalabra, i, j)) {
						return true;
					} else {
						letrasUtilizadas[i][j] = false;
					}
				}
			}
		}

		return false;
	}

}
