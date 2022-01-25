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
import java.util.Scanner;

import core.game.StateObservation;
import ontology.Types.ACTIONS;
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
	private STATES currentState;
	private STATES lastState;

	private Nodo raiz;

	private ACTIONS lastAction;
	private ORIENTACION orientacion;

	private double reward;
	private double globalReward;
	private int racha = 1;
	private Casilla nextToLastCasilla = null;

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

//		if (this.mapa.getAvatar().getX() - this.mapa.getColumnaPortal() < 0) {
//			this.lastAction = ACTIONS.ACTION_DOWN;
//			this.currentState = STATES.ESTADO_2;
//			this.lastState = STATES.ESTADO_2;
//			this.orientacion = ORIENTACION.SUR;
//
//		} else {
//			this.lastAction = ACTIONS.ACTION_UP;
//			this.currentState = STATES.ESTADO_8;
//			this.lastState = STATES.ESTADO_8;
//			this.orientacion = ORIENTACION.NORTE;
//		}

		this.qlearning = new QLearning(getStates(), getActions(), new String("QTABLE.txt"));

		this.reward = 0;
		this.globalReward = 0;

		generaArbol();
	}

	// =============================================================================
	// GETs Y SETs
	// =============================================================================

	/**
	 * Devuelve el mapa que tiene el cerebro en su memoria.
	 * 
	 * @return Mapa generado.
	 */
	public Mapa getMapa() {
		return this.mapa;
	}

	/**
	 * Devuelve la �ltima acci�n realizada
	 * 
	 * @return Devuelve una acci�n
	 */
	public ACTIONS getLastAction() {
		return this.lastAction;
	}

	/**
	 * Devuelve la orientaci�n del agente
	 * 
	 * @return Devuelve una orientaci�n
	 */
	public ORIENTACION getOrientacion() {
		return this.orientacion;
	}

	/**
	 * Devuelve el timer actual
	 * 
	 * @return Devuelve el n�mero de ticks jugados por el agente
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
	 * @param percepcion Percepci�n del juego
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
		this.mapa.actualiza(percepcion, Visualizaciones.ENCENDIDA);
	}

	/**
	 * Actualiza el estado
	 * 
	 * @param percepcion Percepci�n del juego
	 */
	private void actualizaState(StateObservation percepcion) {
//		this.lastState = this.currentState;
//		this.currentState = this.raiz.decidir(this);
	}

	/**
	 * Recorre el arbol de decision y devuelve una accion a realizar.
	 * 
	 * @return Accion a realizar tras recorrer los nodos el arbol.
	 */
	public ACTIONS pensar(StateObservation percepcion) {
//		this.lastAction = this.qlearning.nextOnlyOneBestAction(currentState);
//		
//		switch(this.lastAction) {
//		case ACTION_UP:
//			this.orientacion = ORIENTACION.NORTE;
//			break;
//		case ACTION_DOWN:
//			this.orientacion = ORIENTACION.SUR;
//			break;		
//		case ACTION_RIGHT:
//			this.orientacion = ORIENTACION.ESTE;
//			break;		
//		case ACTION_LEFT:
//			this.orientacion = ORIENTACION.OESTE;
//			break;		
//		}
//		
//		System.out.println("Orientacion: " + this.orientacion);
//		
//		this.mapa.visualiza();
//		System.out.println("\nEstado: " + this.currentState);

		return null;
	}

	/**
	 * Entrena el agente para que aprenda a jugar a trav�s del algoritmo Q-learning
	 * 
	 * @param percepcion Percepci�n del juego
	 * @return Devuelve una acci�n
	 */
	public ACTIONS entrenar(StateObservation percepcion) {
		double reward = getReward(lastState, lastAction, currentState);
		this.qlearning.update(lastState, lastAction, currentState, reward);
		this.lastAction = this.qlearning.nextAction(currentState);

		switch (this.lastAction) {
		case ACTION_UP:
			this.orientacion = ORIENTACION.NORTE;
			break;
		case ACTION_DOWN:
			this.orientacion = ORIENTACION.SUR;
			break;
		case ACTION_RIGHT:
			this.orientacion = ORIENTACION.ESTE;
			break;
		case ACTION_LEFT:
			this.orientacion = ORIENTACION.OESTE;
			break;
		}

		return lastAction;
	}

	/**
	 * Devuelve la recomensa total obtenida por el agente
	 * 
	 * @return Devuelve la recompensa total
	 */
	public double getGR() {
		return this.globalReward;
	}

	// =============================================================================
	// AUXILIARES
	// =============================================================================

	/**
	 * Calcula la recompensa para la �ltima acci�n realizada
	 * 
	 * @param lastState    Estado anterior
	 * @param lastAction   �ltima acci�n realizada
	 * @param currentState Estado actual
	 * @return Devuelve la recompensa calculada
	 */
	private double getReward(STATES lastState, ACTIONS lastAction, STATES currentState) {
		this.reward = -50;
		Casilla currentCasilla = this.mapa.getAvatar();
		Casilla lastCasilla = this.mapa.getLastAvatar();

		switch (lastState) {
		case ESTADO_4:
		case ESTADO_8:
		case ESTADO_12:
			if (this.checkNorthMovement(lastCasilla.getY(), currentCasilla.getY()))
				this.reward = 50;
			break;
		case ESTADO_2:
		case ESTADO_6:
		case ESTADO_10:
		case ESTADO_13:
			if (this.checkSouthMovement(lastCasilla.getY(), currentCasilla.getY()))
				this.reward = 50;
			break;
		case ESTADO_1:
		case ESTADO_5:
		case ESTADO_9:
			if (this.checkEastMovement(lastCasilla.getX(), currentCasilla.getX()))
				this.reward = 50;
			break;
		case ESTADO_3:
		case ESTADO_7:
		case ESTADO_11:
			if (this.checkWestMovement(lastCasilla.getX(), currentCasilla.getX()))
				this.reward = 50;
			break;
		}

		return reward;
	}

	/**
	 * Comprueba si el agente se ha movido hacia el norte
	 * 
	 * @param a Posici�n Y de la �ltima casilla
	 * @param b Posici�n Y de la casilla actual
	 * @return Devuelve true si el agente se mueve hacia el norte. False en caso
	 *         contrario
	 */
	private boolean checkNorthMovement(int a, int b) {
		if ((a - b) > 0)
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si el agente se ha movido hacia el sur
	 * 
	 * @param a Posici�n Y de la �ltima casilla
	 * @param b Posici�n Y de la casilla actual
	 * @return Devuelve true si el agente se mueve hacia el sur. False en caso
	 *         contrario
	 */
	private boolean checkSouthMovement(int a, int b) {
		if ((a - b) < 0)
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si el agente se ha movido hacia el este
	 * 
	 * @param a Posici�n X de la �ltima casilla
	 * @param b Posici�n X de la casilla actual
	 * @return Devuelve true si el agente se mueve hacia el este. False en caso
	 *         contrario
	 */
	private boolean checkEastMovement(int a, int b) {
		if ((b - a) > 0)
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si el agente se ha movido hacia el oeste
	 * 
	 * @param a Posici�n X de la �ltima casilla
	 * @param b Posici�n X de la casilla actual
	 * @return Devuelve true si el agente se mueve hacia el oeste. False en caso
	 *         contrario
	 */
	private boolean checkWestMovement(int a, int b) {
		if ((b - a) < 0)
			return true;
		else
			return false;
	}

	/**
	 * Devuelve los estados en los que se puede encontrar el agente
	 * 
	 * @return Devuelve un ArrayList con los estados
	 */
	private ArrayList<STATES> getStates() {
		// CAMINODERECHA, CAMINOABAJO, CAMINOARRIBA, CAMINOATRAS
		return new ArrayList<STATES>(Arrays.asList(STATES.ESTADO_1, STATES.ESTADO_2, STATES.ESTADO_3, STATES.ESTADO_4,
				STATES.ESTADO_5, STATES.ESTADO_6, STATES.ESTADO_7, STATES.ESTADO_8, STATES.ESTADO_9, STATES.ESTADO_10,
				STATES.ESTADO_11, STATES.ESTADO_12, STATES.ESTADO_13));
	}

	/**
	 * Devuelve las acciones que puede realizar al agente
	 * 
	 * @return Devuelve un ArrayList con las acciones
	 */
	private ArrayList<ACTIONS> getActions() {
		return new ArrayList<ACTIONS>(
				Arrays.asList(ACTIONS.ACTION_UP, ACTIONS.ACTION_DOWN, ACTIONS.ACTION_LEFT, ACTIONS.ACTION_RIGHT));
	}

	/**
	 * Crea el arbol de decisi�n del agente
	 */
	private void generaArbol() {

	}

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

	public void saveEpsilon(String path) {
		this.qlearning.saveEpsilon(path);
	}
}
