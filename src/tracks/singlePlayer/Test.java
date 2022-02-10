package tracks.singlePlayer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

	public static void main(String[] args) {

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";

		String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
		String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
		String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		String sampleMDPController = "uhu.AgentPlayer";
		String sampleMDPTrainer = "uhu.AgentTrainer";

		// Load available games
		String spGamesCollection = "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		// Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 3;
		int levelIdx = 5; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level = game.replace(gameName, gameName + "_lvl" + levelIdx);

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
		// + levelIdx + "_" + seed + ".txt";
		// where to record the actions
		// executed. null if not to save.

		// ==============================================================================================================
		// NUESTRO ENTRENAMIENTO
		// ==============================================================================================================

		visuals = true;
		String path = "src/uhu/mdp/resources/resultados.csv";
		int M = 5000; // Número de partidas
		String[] arrayResult = new String[M + 1];
		arrayResult[0] = "Victoria,Puntos,Ticks Norm\n";
		Double[] arrayTicks = new Double[M];
		for (int i = 0; i < M; i++) {
			if (i > M-M-1)
				visuals = false;

			levelIdx = getRandomNumber(0, 0);
			System.out.println("\nPartida actual: " + (i + 1) + " - Nivel: " + levelIdx);
			level = game.replace(gameName, gameName + "_lvl" + levelIdx);

			double[] resultado = ArcadeMachine.runOneGame(game, level, visuals, sampleMDPTrainer, recordActionsFile,
					seed, 0);
			// resultado[0] -> indica la victoria(1 o 0) - resultado[1] -> puntos -
			// resultado[2] -> ticks
			if(resultado[1] == -1) // Si nos mata George, nos quita un punto. De esta forma veremos un 0 en vez de un -1 en el resultado
				resultado[1]++;  
			arrayResult[i+1] = resultado[0] + "," + (resultado[1]) + "," + (resultado[2]) + "\n";
		}

		// Creamos fichero
		try {
			FileWriter myWriter = new FileWriter(path);
			for (int i = 0; i < M+1; i++)
				myWriter.write(arrayResult[i]);

			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		showNoVisitStateNumber("src/uhu/mdp/resources/QTABLE.txt");
		
		// ==============================================================================================================
		// NUESTRO TEST
		// ==============================================================================================================
//		Scanner sc = new Scanner(System.in);
//		String cadena = sc.nextLine();
		
		visuals = true;
		int numeroMapas = 5;
		double victorias[] = new double[numeroMapas];
		double puntos[] = new double[numeroMapas];
		double victoriasTotales = 0;
		double puntosTotales = 0;
		for (int i = 0; i < numeroMapas; i++) {
			levelIdx = i;
			level = game.replace(gameName, gameName + "_lvl" + levelIdx);
			double[] resultado = ArcadeMachine.runOneGame(game, level, visuals, sampleMDPController, recordActionsFile, seed, 0);
			//resultado[0] -> indica la victoria(1 o 0) - resultado[1] -> puntos - resultado[2] -> ticks
			victorias[i] = resultado[0];
			victoriasTotales += resultado[0];
			puntos[i] = resultado[1];
			puntosTotales += resultado[1];
		}
		
		System.out.println("Victorias Totales: " + victoriasTotales);
		System.out.println("Media de puntos: " + Double.toString(puntosTotales/numeroMapas));
		System.out.println("Porcentaje de victorias en test: " + ((victoriasTotales/(float)numeroMapas)*100) + "%");
		System.out.println("----Resultados por mapa----");
		for(int i=0;i<numeroMapas;i++) {
			System.out.println("Mapa_"+(i+1)+": " + Double.toString(victorias[i]) + " - Puntos: " + Double.toString(puntos[i]));
		}
		
		//----------------------------------------------------------------------------------------------------------
		
		// 1. This starts a game, in a level, played by a human.
//		ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
//		ArcadeMachine.runOneGame(game, level1, visuals, sampleMDPTrainer, recordActionsFile, seed, 0);
		
		// 3. This replays a game from an action file previously recorded
		// String readActionsFile = recordActionsFile;
		// ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		// 5. This plays N games, in the first L levels, M times each. Actions to file
		// optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}

	}
	
	private static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}
	
	private static void showNoVisitStateNumber(String path) {
		int totalStateNumber = 0,noVisitStateNumber = 0;
		try {
			FileReader fichero = new FileReader(path); // FileReader sierve para leer ficheros
			BufferedReader b = new BufferedReader(fichero); // BufferReader sirve para leer texto de una entrada de
															// caracteres
			String aux; // Variable donde guardar las lecturas de fichero de forma momentanea
			ArrayList<String> stringFichero = new ArrayList<>(); // Almacena cada linea del fichero
			String[] parts; // Para dividir Strings

			// Mientras pueda leer la siguiente linea, sigue leyendo, hace la asignaciÃ³n
			// dentro del if
			while ((aux = b.readLine()) != null) {
				stringFichero.add(aux);
			}

			fichero.close();
			totalStateNumber = stringFichero.size();
			for (int i = 0; i < stringFichero.size(); i++) {
				if(stringFichero.get(i).equals("0.0	0.0	0.0	0.0	0.0"))
					noVisitStateNumber++;
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		System.out.println("-----------------------------------------------------");
		System.out.println("No se han visitado " + noVisitStateNumber + " de un total de " + totalStateNumber);
		System.out.println("-----------------------------------------------------");
	}
}

