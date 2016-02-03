package alacret.boggleserver.core;

import java.util.Random;

final class CubeProvider {

	public static String getCubeAsString(char[][] cube) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				sb.append(cube[i][j]);
				sb.append(",");
			}
			sb = new StringBuilder(sb.substring(0, sb.length() - 1));
			sb.append(",,");
		}
		return sb.substring(0, sb.length() - 2);
	}

	public static char[][] getNewCube() {
		return calculateCube();
	}

	private static char[][] calculateCube() {
		char[][] cube = new char[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				cube[i][j] = getRandomLetter();
		return cube;
	}

	private static char getRandomLetter() {
		Random r = new Random();
		float f = r.nextFloat();
		if (f > 0 && f < 0.0625)
			return 'a';
		else if (f > 0.0625 && f < 0.0833333333)
			return 'b';
		else if (f > 0.0833333333 && f < 0.1041666667)
			return 'c';
		else if (f > 0.1041666667 && f < 0.1354166667)
			return 'd';
		else if (f > 0.1354166667 && f < 0.25)
			return 'e';
		else if (f > 0.25 && f < 0.2708333333)
			return 'f';
		else if (f > 0.2708333333 && f < 0.2916666667)
			return 'g';
		else if (f > 0.2916666667 && f < 0.34375)
			return 'h';
		else if (f > 0.34375 && f < 0.40625)
			return 'i';
		else if (f > 0.40625 && f < 0.4166666667)
			return 'j';
		else if (f > 0.4166666667 && f < 0.4270833333)
			return 'k';
		else if (f > 0.4270833333 && f < 0.46875)
			return 'l';
		else if (f > 0.46875 && f < 0.4895833333)
			return 'm';
		else if (f > 0.4895833333 && f < 0.5520833333)
			return 'n';
		else if (f > 0.5520833333 && f < 0.625)
			return 'o';
		else if (f > 0.625 && f < 0.6458333333)
			return 'p';
		else if (f > 0.6458333333 && f < 0.65625)
			return 'q';
		else if (f > 0.65625 && f < 0.7083333333)
			return 'r';
		else if (f > 0.7083333333 && f < 0.7708333333)
			return 's';
		else if (f > 0.7708333333 && f < 0.8645833333)
			return 't';
		else if (f > 0.8645833333 && f < 0.8958333333)
			return 'u';
		else if (f > 0.8958333333 && f < 0.9166666667)
			return 'v';
		else if (f > 0.9166666667 && f < 0.9479166667)
			return 'w';
		else if (f > 0.9479166667 && f < 0.9583333333)
			return 'x';
		else if (f > 0.9583333333 && f < 0.9895833333)
			return 'y';
		else
			return 'z';
	}
}
