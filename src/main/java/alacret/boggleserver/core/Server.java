package alacret.boggleserver.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

final public class Server {
	private static final int MINIMUM_NUMBERS_PLAYERS = 1;
	private static final int TIME_TO_WAIT_FOR_PLAYERS = 10;
	private static List<Player> players = new ArrayList<>();
	private static List<GameClient> gamers = new ArrayList<>();
	private static List<GameClient> doneGamers = new ArrayList<>();
	private static boolean isAGameOn = false;
	private static int rounds;
	private static Map<Integer, char[][]> cubes = new HashMap<Integer, char[][]>();
	private static final int DEFAULT_ROUNDS = 2;
	private static Thread timer;
	private static BoggleDictionary dictionary = new BoggleDictionary();

	private static List<String> allRoundWords = new ArrayList<>();
	private static CountDownLatch countDown;

	public static void main(String[] args) {

		try {
			rounds = Integer.valueOf(args[0]);
		} catch (Exception e) {
			rounds = DEFAULT_ROUNDS;
		}

		for (int i = 0; i < rounds; i++)
			cubes.put(i + 1, CubeProvider.getNewCube());

		try {
			ServerSocket server = new ServerSocket(8000);
			System.out.println("Startting boggle server...");
			while (true) {
				Socket accept = server.accept();
				new Request(accept).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	final private static synchronized boolean addPlayer(Socket socket,
			String playerName) {
		if (isAGameOn)
			return false;
		players.add(new Player(socket, playerName));
		if (players.size() >= MINIMUM_NUMBERS_PLAYERS)
			resetTimer();
		return true;
	}

	private static void resetTimer() {
		if (timer != null)
			timer.interrupt();

		timer = new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(TIME_TO_WAIT_FOR_PLAYERS * 1000);
					System.out.println("10 seconds time out rechead");
					isAGameOn = true;
					startGame();

				} catch (InterruptedException e) {
					System.out.println("another player");
				}

			}
		};
		timer.start();

	}

	private static char[][] getCube(int currentRound) {
		return cubes.get(currentRound).clone();
	}

	private static void startGame() {
		System.out.println("starting the game");
		isAGameOn = true;
		countDown = new CountDownLatch(players.size());

		for (Player player : players) {
			GameClient gameClient = new GameClient(player);
			gamers.add(gameClient);
			gameClient.start();
		}
	}

	private static void calculateWinner(GameClient gameClient) {
		System.out.println("calculating the winner...");
		synchronized (Server.class) {
			doneGamers.add(gameClient);
			if (doneGamers.size() == gamers.size()) {
				Collections.sort(gamers);
				notifiyResults();
			}
		}
	}

	private static int calculatePoints(List<String> validWords, int points,
			GameClient gameClient) {
		boolean tempFlag = true;
		System.out.println("calculating the round points...");

		countDown.countDown();

		synchronized (Server.class) {
			allRoundWords.addAll(validWords);
		}

		try {
			countDown.await();
			for (String word : validWords)
				if (allRoundWords.contains(word))
					points -= ValidadorBoggle.getPointsPerWord(word);
		} catch (InterruptedException e) {
		}

		if (tempFlag) {
			synchronized (Server.class) {
				if (tempFlag) {
					countDown = new CountDownLatch(players.size());
					tempFlag = false;
				}
			}
		}

		return points;

	}

	private static void notifiyResults() {

		StringBuilder results = new StringBuilder();
		int i = 1;
		for (GameClient gamer : gamers)
			results.append(i++ + ". " + gamer.getPlayerName() + " ("
					+ gamer.getTotalPoints() + " pts),");
		String realResults = results.substring(0, results.length() - 1);

		for (GameClient gamer : gamers) {
			try {
				Socket socket = gamer.getSocket();
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println("results:" + realResults);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Shutting down the server...");
		System.exit(0);

	}

	final private static class GameClient extends Thread implements
			Comparable<GameClient> {
		private Player player;
		private char[][] cube;
		private int totalPoints = 0;
		private int currentRound;

		public GameClient(Player player) {
			this.player = player;
			this.cube = getCube(rounds);
			this.currentRound = rounds;
			initRound();
		}

		private void initRound() {
			try {
				Socket socket = player.getSocket();
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				pw.println("cube:" + CubeProvider.getCubeAsString(cube));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void resetCube(char[][] newCube) {
			this.cube = newCube;
			initRound();
		}

		@Override
		public void run() {
			try {
				Socket socket = player.getSocket();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				while (true) {
					currentRound--;
					String command = br.readLine();
					if (command.split(":").length > 1) {
						System.out.println("these are the words:"
								+ command.split(":")[1]);
						String[] words = command.split(":")[1].split(",");
						List<String> validWords = gameWordsToValidWords(words);
						int points = ValidadorBoggle.countPointsByWords(
								validWords, cube);

						points = calculatePoints(validWords, points, this);

						totalPoints += points;
					}
					if (currentRound == 0) {
						calculateWinner(this);
						return;
					}
					System.out.println("starting another round");
					resetCube(getCube(currentRound));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public String getPlayerName() {
			return player.getName();
		}

		@Override
		public int compareTo(GameClient anotherGamer) {
			if (totalPoints < anotherGamer.getTotalPoints())
				return -1;
			else if (totalPoints > anotherGamer.getTotalPoints())
				return 1;
			else
				return 0;
		}

		public int getTotalPoints() {
			return totalPoints;
		}

		public Socket getSocket() {
			return player.getSocket();
		}

	}

	final static List<String> gameWordsToValidWords(String[] words) {
		List<String> validWords = new ArrayList<>();
		for (int i = 0; i < words.length; i++)
			if (dictionary.isValid(words[i]))
				validWords.add(words[i]);
		return validWords;
	}

	final private static class Request extends Thread {
		private Socket socket;

		public Request(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
				String command = br.readLine();

				System.out.println("The client (" + socket.getInetAddress()
						+ ":" + socket.getPort() + ") send: " + command);

				if (command.split(":")[0].equals("connect"))
					if (addPlayer(socket, command.split(":")[1]))
						pw.println("ok");
					else
						pw.println("busy");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	final private static class Player {
		private Socket socket;
		private String name;

		public Player(Socket socket, String name) {
			this.socket = socket;
			this.name = name;
		}

		public Socket getSocket() {
			return socket;
		}

		public String getName() {
			return name;
		}

	}
}
