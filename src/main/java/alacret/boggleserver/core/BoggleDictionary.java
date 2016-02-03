package alacret.boggleserver.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BoggleDictionary {
	Set<String> words = new HashSet<>();

	public BoggleDictionary() {
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					BoggleDictionary.class.getResourceAsStream("/dict.txt")));

			String word = bf.readLine();
			while (!word.equals("#EXIT_TOKEN")) {
				words.add(word);
				word = bf.readLine();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isValid(String string) {
		return words.contains(string);

	}

}
