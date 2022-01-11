package uhu.mdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.event.IIOReadWarningListener;

import ontology.Types.ACTIONS;
import static uhu.Constantes.*;

/**
 * Clase que nos permitará implementar la técnica q-learning de aprendizaje por refuerzo
 * 
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 */
public class QLearning {

	private double alpha; // Porcentaje de aprendizaje en cada tick
	private double gamma = 0.5; // Porcentaje para tener en cuenta la siguiente acción con mejor resultado. (Mejora las acciones a largo plazo)
	private double epsilon; // Porcentaje de acciones aleatorias
	private double epsilonInicial = -1;
	private double defaultVarInit = 0.9;
	private int timer;

	private double reward;

	private ArrayList<STATES> states;
	private ArrayList<ACTIONS> actions;
	private double[][] qTable;
	
	private String pathTimer;
	

	/**
	 * Constructors de la clase
	 * @param states Posibles estados que puede tener el agente. Representa las filas de la tabla-Q
	 * @param actions Posibles acciones que puede tener el agente. Representa las filas de la tabla-Q
	 * @param path Ruta donde guardar los datos de la tabla-Q una vez que el agente termine de jugar
	 */
	public QLearning(ArrayList<STATES> states, ArrayList<ACTIONS> actions, String path) {
		this.states = states;
		this.actions = actions;

		this.qTable = new double[states.size()][actions.size()];
		
		// Para guardar timer
		this.pathTimer = "timer.txt";
		File file = new File(this.pathTimer);
		if (!file.exists()) {
			this.initTimer();
		} else {
			this.loadTimer();
		}
		
		// Para guardar Tabla-Q
		file = new File(path);
		if (!file.exists()) {
			initTable();
		} else {
			readTable(path);
		}

	}
	
	/**
	 * Devuelve el valor de la variable timer
	 * @return Devuelve el número de ticks totales almacenados
	 */
	public int getTimer() {
		return this.timer;
	}

	/**
	 * Actualiza la tabla-Q 
	 * @param lastState Último estado en el que ha estado el agente
	 * @param lastAction Última acción realizada por el agente
	 * @param currentState Estado actual en el que se encuentra el agente
	 * @param reward Recompensa obtenida por el agente en la última acción
	 * @return Devuelve la siguiente acción a realizar
	 */
	public ACTIONS update(STATES lastState, ACTIONS lastAction, STATES currentState, double reward) {
		this.reward = reward;
		double sample = reward + gamma * getMaxQValue(currentState);
		double newQValue = (1 - alpha) * getQValue(lastState, lastAction) + alpha * sample;

		setQValue(lastState, lastAction, newQValue);
		updateVar();

		return nextAction(currentState);
	}

	/**
	 * Crea la tabla-Q
	 */
	private void initTable() {
		for (int i = 0; i < states.size(); i++) {
			for (int j = 0; j < actions.size(); j++) {
				this.qTable[i][j] = 0;
			}
		}
	}

	/**
	 * Devuelve un Q-valor
	 * @param s Estado a seleccionar en la tabla
	 * @param a Acción a seleccionar en la tabla
	 * @return Devuelve el Q-valor asigando al par estado/acción
	 */
	public double getQValue(STATES s, ACTIONS a) {
		int i = states.indexOf(s);
		int j = actions.indexOf(a);

		return this.qTable[i][j];
	}

	/**
	 * Fija el Q-valor deseado para un par estado/acción
	 * @param s Estado a seleccionar en la tabla
	 * @param a Acción a seleccionar en la tabla
	 * @param qValue Q-valor a guardar
	 */
	public void setQValue(STATES s, ACTIONS a, double qValue) {
		int i = states.indexOf(s);
		int j = actions.indexOf(a);

		this.qTable[i][j] = qValue;
	}

	/**
	 * Devuelve el mejor Q-valor asignado a un estado
	 * @param state Estado a seleccionar
	 * @return Devuelve un Q-valor
	 */
	public double getMaxQValue(STATES state) {
		int i = states.indexOf(state);

		double maxVal = Double.MIN_VALUE;

		for (int j = 0; j < actions.size(); j++) {
			if (qTable[i][j] > maxVal) {
				maxVal = qTable[i][j];
			}
		}

		return maxVal;
	}

	

	/**
	 * Devuelve una acción aleatoria
	 * @return Devuelve una acción
	 */
	public ACTIONS getRandomAction() {
		Random rd = new Random(System.currentTimeMillis());
		int action = rd.nextInt(actions.size());

		return actions.get(action);
	}

	/**
	 * Actualiza las variables alpha y epsilon en función del timer(ticks). A medida que aumente el timer, las acciones serán menos
	 * aleatorias y aprenderá menos con cada acción
	 */
	private void updateVar() {
		double k = 5000; 
		this.alpha = (this.defaultVarInit * k / (k + timer));
		this.epsilon = ((this.defaultVarInit) * k / (k + timer));
		if(this.epsilonInicial == -1)
			this.epsilonInicial = this.epsilon;
		
		System.out.println("epsilon: " + this.epsilon);
		timer++;
	}

	/**
	 * Devuelve la acción a realizar en el estado actual
	 * @param currentState Estado actual
	 * @return Devuelve una acción
	 */
	public ACTIONS nextAction(STATES currentState) {
		Random rd = new Random(System.currentTimeMillis());
		double randomNumber = Math.abs(rd.nextDouble());

		if (randomNumber > epsilon) {
			return getBestAction(currentState);
		} else {
			return getRandomAction();
		}
	}
	
	/**
	 * Devuelve la mejor acción a realizar a partir de un estado. En caso de que existan dos acciones con el mismo Q-valor,
	 * se elegirá una de las dos al azar 
	 * @param state Estado a seleccionar
	 * @return Devuelve una acción
	 */
	public ACTIONS getBestAction(STATES state) {
		int i = states.indexOf(state);

		ArrayList<Integer> candidatos = new ArrayList<Integer>();

		double maxVal = -Double.MAX_VALUE;
		int indexAction = 0;

		for (int j = 0; j < actions.size(); j++) {
			if (qTable[i][j] > maxVal) {
				maxVal = qTable[i][j];
				indexAction = j;
				candidatos.clear();
				candidatos.add(indexAction);
			} else if (qTable[i][j] == maxVal) {
				indexAction = j;
				candidatos.add(indexAction);
			}
		}

		Random rd = new Random(System.currentTimeMillis());
		int seleccion = rd.nextInt(candidatos.size());

		return actions.get(candidatos.get(seleccion));
	}

	/**
	 * Devuelve la mejor acción a realizar a partir de un estado. En caso de que existan dos acciones con el mismo Q-valor,
	 * se elegirá la primera encontrada en la tabbla
	 * @param currentState Estado a seleccionar
	 * @return Devuelve una acción
	 */
	public ACTIONS nextOnlyOneBestAction(STATES currentState) {
		int i = states.indexOf(currentState);

		double maxVal = -Double.MAX_VALUE;
		int indexAction = 0;

		for (int j = 0; j < actions.size(); j++) {
			if (qTable[i][j] > maxVal) {
				maxVal = qTable[i][j];
				indexAction = j;
			}
		}
		return actions.get(indexAction);
	}

	/**
	 * Guarda la tabla-Q en un fichero
	 * @param path Ruta donde guardar la tabla
	 */
	public void writeTable(String path) {
		try {
			FileWriter fichero;
			
			fichero = new FileWriter(path);
			String fila = "";
			for (int i = 0; i < states.size(); i++) {
				fila = "";
				for (int j = 0; j < actions.size(); j++) {
					if (j != 0)
						fila += "," + qTable[i][j];
					else
						fila += qTable[i][j];
				}
				fichero.write(fila + "\n");
			}
			fichero.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Lee la tabla-Q guardada en un fichero
	 * @param path Ruta donde leer la tabla
	 */
	public void readTable(String path) {
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
			for (int i = 0; i < stringFichero.size(); i++) {
				aux = stringFichero.get(i);
				parts = aux.split(",");

				for (int j = 0; j < parts.length; j++) {
					qTable[i][j] = Double.parseDouble(parts[j]);
				}
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Muesta la tabla-Q por pantalla
	 */
	public void printTable() {
		for (int i = 0; i < states.size(); i++) {
			System.out.println();
			for (int j = 0; j < actions.size(); j++) {
				System.out.print(qTable[i][j] + " , ");
			}
		}
		System.out.println();
	}
	
	/**
	 * Inicia el timer y crea el fichero para almacenarlo
	 */
	private void initTimer() {
		this.timer = 0;
		this.saveTimer();
	}
	
	/**
	 * Guarda la variable timer en un fichero 
	 */
	public void saveTimer() {
		try {
			FileWriter fichero;
			fichero = new FileWriter(this.pathTimer);
			String fila = "";
			fichero.write(Integer.toString(timer) + "\n");
			fichero.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Lee el timer de un fichero
	 */
	private void loadTimer() {
		int t = -1;
		try {
			FileReader fichero = new FileReader(this.pathTimer); // FileReader sierve para leer ficheros
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
			for (int i = 0; i < stringFichero.size(); i++) {
				aux = stringFichero.get(i);
				t = Integer.parseInt(aux);
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		this.timer = t;
	}
	
	public void saveEpsilon(String path) {
		try {
			FileWriter fichero;
			File file = new File(path);
			if (!file.exists()) {
				fichero = new FileWriter(path);
				fichero.write(Double.toString(this.epsilonInicial) + "\n");
				fichero.close();
			} else {
				fichero = new FileWriter(path, true);
				fichero.write(Double.toString(this.epsilonInicial) + "\n");
				fichero.close();
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}
