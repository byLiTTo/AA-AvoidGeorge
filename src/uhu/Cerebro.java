/**
 * 
 */
package uhu;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;
import uhu.Juego03.*;
import uhu.arbol.*;
import uhu.grid.*;
import uhu.mdp.QLearning;

import static uhu.Constantes.*;

/**
 * Clase cerebro que funciona como controlador. Se encarga de analizar el
 * entorno en el que se encuentra el agente y de devolver una accion que realiza
 * en cada tick.
 * 
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 */
public class Cerebro {

	// =============================================================================
	// VARIABLES
	// =============================================================================

	private Mapa mapa;
	private QLearning qlearning;
	private HashMap<String, Integer> hashmap;
	private int currentState;
	private int lastState;

	private ACTIONS lastAction;

	private double reward;
	private double globalReward;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor de la clase cerebro que crea un mapa y genera el arbol de
	 * decision.
	 * 
	 * @param percepcion observacion del estado actual.
	 * @param timer      tiempo actual
	 */
	public Cerebro(StateObservation percepcion) {
		Dimension dim = percepcion.getWorldDimension();
		int bloque = percepcion.getBlockSize();

		this.mapa = new Mapa(dim.width / bloque, dim.height / bloque, bloque, percepcion);

		this.loadHashMap("src/uhu/Juego03/StateGenerator/statesV2");
	
		this.currentState = 0;
		this.lastState = 0;

		this.lastAction = ACTIONS.ACTION_USE;

//		System.out.println("N ESTADOS: " + getStates().size());
//		System.out.println("N ACCIONES: " + getActions().size());

		this.qlearning = new QLearning(getStates(), getActions(), new String("/src/uhu/mdp/resources/QTABLE.txt"));

		this.reward = 0;
		this.globalReward = 0;
	}

	// =============================================================================
	// GETs Y SETs
	// =============================================================================

	/**
	 * Devuelve el mapa que tiene el cerebro en su memoria.
	 * 
	 * @return Mapa : devuelve el mapa generado.
	 */
	public Mapa getMapa() {
		return this.mapa;
	}

	/**
	 * Devuelve la ultima accion realizada
	 * 
	 * @return ACTIONS : Devuelve una accion
	 */
	public ACTIONS getLastAction() {
		return this.lastAction;
	}

	/**
	 * Devuelve el timer actual
	 * 
	 * @return int : devuelve el numero de ticks jugados por el agente
	 */
	public int getTimer() {
		return this.qlearning.getTimer();
	}

	// =============================================================================
	// METODOS
	// =============================================================================

	/**
	 * Analiza el mapa y actualiza el estado
	 * 
	 * @param percepcion Percepcion del juego
	 */
	public void percibe(StateObservation percepcion) {
		analizarMapa(percepcion);
		actualizaState(percepcion);
	}

	/**
	 * Analiza el mapa del juego
	 * 
	 * @param percepcion Observacion del estado actual.
	 */
	private void analizarMapa(StateObservation percepcion) {
		this.mapa.actualiza(percepcion, Visualizaciones.APAGADA);
	}

	/**
	 * Actualiza el estado
	 * 
	 * @param percepcion Percepcion del juego
	 */
	private void actualizaState(StateObservation percepcion) {
		this.lastState = this.currentState;
		this.currentState = this.compruebaEstado();
	}

	/**
	 * Recorre el arbol de decision y devuelve una accion a realizar.
	 * 
	 * @return ACTIONS : accion a realizar tras recorrer los nodos el arbol.
	 */
	public ACTIONS pensar(StateObservation percepcion) {
		this.lastAction = this.qlearning.nextOnlyOneBestAction(currentState);
//		
//		this.mapa.visualiza();
//		System.out.println("\nEstado: " + this.currentState);

		return lastAction;
	}

	/**
	 * Entrena el agente para que aprenda a jugar a traves del algoritmo Q-learning
	 * 
	 * @param percepcion Percepcion del juego
	 * @return ACTIONS : devuelve una accion
	 */
	public ACTIONS entrenar(StateObservation percepcion) {
		
		double reward = getReward(lastAction, this.mapa);
		this.qlearning.update(lastState, lastAction, currentState, reward);
		this.lastAction = this.qlearning.nextAction(currentState);
		System.out.println("Accion: " + this.lastAction.toString());
		
		
		return lastAction;
	}

	// =============================================================================
	// METODOS PARA LA TABLA Q
	// =============================================================================

	/**
	 * Guarda la tabla-Q del agente en un fichero
	 * 
	 * @param path Ruta donde guardar la tabla
	 */
	public void writeTable(String path) {
		qlearning.writeTable(path);
	}

	/**
	 * Lee la tabla-Q guardada en un fichero
	 * 
	 * @param path Ruta donde leer la tabla
	 */
	public void readTable(String path) {
		qlearning.readTable(path);
	}

	/**
	 * Guarda el valor del timer en un fichero
	 */
	public void saveTimer() {
		this.qlearning.saveTimer();
	}

	/**
	 * Guarda el valor de la variable epsilon de la formula del qlearning.
	 * 
	 * @param path : Ruta del directorio donde se guarda el valor.
	 */
	public void saveEpsilon(String path) {
		this.qlearning.saveEpsilon(path);
	}
	

	// =============================================================================
	// AUXILIARES
	// =============================================================================

	/**
	 * Devuelve los estados en los que se puede encontrar el agente
	 * 
	 * @return ArrayList STATES : devuelve un ArrayList con los estados
	 */
	private ArrayList<Integer> getStates() {
		ArrayList<Integer> auxList = new ArrayList();
		for (int i = 0; i < this.hashmap.size(); i++) {
			auxList.add(i);
		}
		
		return auxList;
	}

	/**
	 * Devuelve las acciones que puede realizar al agente
	 * 
	 * @return ArrayList ACTIONS : devuelve un ArrayList con las acciones
	 */
	private ArrayList<ACTIONS> getActions() {
		return new ArrayList<ACTIONS>(Arrays.asList(ACTIONS.ACTION_UP, ACTIONS.ACTION_DOWN, ACTIONS.ACTION_LEFT,
				ACTIONS.ACTION_RIGHT, ACTIONS.ACTION_USE));
	}

	/**
	 * Calcula la recompensa para la ultima accion realizada
	 * @param lastAction   ultima accion realizada
	 * @param m Almacena el estado actual y el anterior
	 * @return double : devuelve la recompensa calculada
	 */
	private double getReward(ACTIONS lastAction, Mapa m) {
		double reward = -20;
		
		// RECOMPENSAS
		// Recompensamos si tras utilizar el cigarro calmamos a un molesto
		if(lastAction.equals(ACTIONS.ACTION_USE) && (m.getCalmadosCurrentSize() > m.getCalmadosLastSize()))
			reward += 300;
		
		// Recompensamos si nos acercamos al molesto mas cercano
		if(m.getNearestMolestoCurrentDistanceFrom(m.getAvatarCurrentPosition()) 
				< m.getNearestMolestoLastDistanceFrom(m.getAvatarLastPosition()))
			reward += 20;
		else
			reward -= 20;
		// Recompensamos si estamos muy cerca del molesto mas cercano
		if(m.getNearestMolestoCurrentDistanceFrom(m.getAvatarCurrentPosition()) <= m.getDistanciaCerca())
			reward += 80;
		
		// Recompensamos si nos alejamos del enemigo
		if(m.getEnemyCurrentDistanceFrom(m.getAvatarCurrentPosition()) 
				> m.getEnemyLastDistanceFrom(m.getAvatarCurrentPosition()))
			reward += 40;
		
		// Recompensamos si huye cuando estaba cerca del enemigo
		if(m.getEnemyCurrentDistanceFrom(m.getAvatarCurrentPosition()) > m.getEnemyLastDistanceFrom(m.getAvatarLastPosition()) 
				&& m.getEnemyLastDistanceFrom(m.getAvatarLastPosition()) <= m.getDistanciaCerca())
			reward += 100;
		
		// CASTIGOS
		// Castigamos si tras realizar la ultima accion morimos
		if(m.isGameOverWith(lastAction)) 
			reward -= 500;
		
		System.out.println("Recompensa: " + reward);
//		System.out.println("Recompensa: " + reward);
		return reward;
	}

	public double calculaRotacion(Vector2d c) {
		Vector2d avatar = this.mapa.getAvatarCurrentPosition();
		double angulo = Math.atan2(avatar.y - c.y, avatar.x - c.x);
		return Math.toDegrees(angulo);
	}


	// =============================================================================
	// COMPRUEBA ESTADO
	// =============================================================================

	/**
	 * Comprueba el estado en el que se encuentra el agente
	 * @return Devuelve el numero asignado que tiene el agente
	 */
	private int compruebaEstado() {
		String clave = "", aux;
		double auxDouble;
		Vector2d auxVector;
		/*
		 * Disposicion de caracteres
		 * 1.- Orientacion -> coord[]
		 * 2.- Cercania del molesto mas cercano -> proximity[]
		 * 3.- Posicion del moslesto respecto al jugador -> coord[]
		 * 4.- Cercania del enemigo -> proximity[]
		 * 5.- Posicion del enemigo respecto al jugador -> coord[]
		 * Resto.- Combinaciones de elementos en el mapa
		 */
		
		// 1.- Orientacion
		clave += this.vectorToCoords(this.mapa.getOrientacionAvatar());
		
		// 2.- Cercania del molesto mas cercano
		auxDouble = this.mapa.getNearestMolestoCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition());
		
		// Comprobamos que haya NPCs molestos. Sino, cogemos un NPC calmado
		if(auxDouble != -1) {
			auxDouble = this.mapa.getFirstCalmadoCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition());
//			System.out.println("No hay molesto");
		}
		if(auxDouble<this.mapa.getDistanciaCerca())
			aux = Constantes.proximity[0]; // Cerca
		else 
			aux = Constantes.proximity[1]; // Lejos
		
		clave += aux;
		
		// 3.- Posicion del molesto respecto al jugador
		auxVector = this.mapa.getNearestMolestoCurrentPosition();
		if(auxVector == null)
			auxVector = this.mapa.getFirstCalmadoCurrentPosition();
		clave += this.vectorToCoords(auxVector);
		
		// 4.- Cercania del enemigo
		auxDouble = this.mapa.getEnemyCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition());
		if(auxDouble<this.mapa.getDistanciaCerca())
			aux = Constantes.proximity[0]; // Cerca
		else 
			aux = Constantes.proximity[1]; // Lejos
		
		clave += aux;
		
		// 5.- Posicion del enemigo respecto al jugador
		auxVector = this.mapa.getEnemyCurrentPosition();
		clave += this.vectorToCoords(auxVector);
		
		// Resto.- Combinaciones de elementos en el mapa
//		clave += this.mapa.getValuesFromAvatarNeighbors();
		
		System.out.println("Clave generada: " + clave + " - Valor: " + this.hashmap.get(clave));
		
		if(this.hashmap.containsKey(clave))
			return this.hashmap.get(clave);
		else
			return this.hashmap.get(Constantes.defaultKey);
	}
	
	private String vectorToCoords(Vector2d v) {
		String aux = "";
		
		if(v.x == 0.0 && v.y == 0.0)
			aux = Constantes.coord[0]; // Norte
		else 
			if(v.x == 0.0 && v.y == 1.0)
				aux = Constantes.coord[1]; // Sur
			else
				if(v.x == 1.0 && v.y == 0.0)
					aux = Constantes.coord[3]; // Oeste
				else
					aux = Constantes.coord[2]; // Este

		
		return aux;
	}
	
	/**
	 * Carga la lista de claves en el hashmap de la clase
	 * @param path Ruta donde se encuentra la lista de claves
	 */
	private void loadHashMap(String path) {
		this.hashmap = new HashMap<>();
		ArrayList<String> auxList = this.readHashTable(path);
		for (int i = 0; i < auxList.size(); i++) {
			this.hashmap.put(auxList.get(i), i);
		}
	}
	
	/**
	 * Lee un fichero statesV 
	 * @param path Ruta donde se encuentra la lista de claves 
	 * @return Lista de claves leidas
	 */
	private ArrayList<String> readHashTable(String path) {
		ArrayList<String> auxList = new ArrayList();
		try {
			FileReader fichero = new FileReader(path); // FileReader sierve para leer ficheros
			BufferedReader b = new BufferedReader(fichero); // BufferReader sirve para leer texto de una entrada de
															// caracteres
			String aux; // Variable donde guardar las lecturas de fichero de forma momentanea
			ArrayList<String> stringFichero = new ArrayList<>(); // Almacena cada linea del fichero
			String[] parts; // Para dividir Strings

			// Mientras pueda leer la siguiente linea, sigue leyendo, hace la asignacion
			// dentro del if
			while ((aux = b.readLine()) != null) {
				stringFichero.add(aux);
			}

			fichero.close();
			for (int i = 0; i < stringFichero.size(); i++) {
				auxList.add(stringFichero.get(i));
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
		return auxList;
	}
}
