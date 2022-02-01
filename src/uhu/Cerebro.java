/**
 * 
 */
package uhu;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

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
	private STATES currentState;
	private STATES lastState;

	private Nodo raiz;

	private ACTIONS lastAction;

	private double reward;
	private double globalReward;
//	private int racha = 1;
//	private Casilla nextToLastCasilla = null;

	// Arbol HUYENDO
	private EnemigoCerca enemigoCerca;
	private EnemigoAbajo enemigoAbajo;
	private EnemigoArriba enemigoArriba;
	private EnemigoIzquierda enemigoIzquierda;
	private EnemigoDerecha enemigoDerecha;

	private MuroArriba muroArriba;
	private MuroAbajo muroAbajo;
	private MuroIzquierda muroIzquierda;
	private MuroDerecha muroDerecha;

	// Arbol SIGUIENDO
	private CalmadoArriba sanoArriba;
	private CalmadoAbajo sanoAbajo;
	private CalmadoIzquierda sanoIzquierda;
	private CalmadoDerecha sanoDerecha;

	// Estados
	private Estado huye_arriba;
	private Estado huye_abajo;
	private Estado huye_izquierda;
	private Estado huye_derecha;

	private Estado siguiendo_calmado_arriba;
	private Estado siguiendo_calmado_abajo;
	private Estado siguiendo_calmado_izquierda;
	private Estado siguiendo_calmado_derecha;

	private Estado siguiendo_molesto_arriba;
	private Estado siguiendo_molesto_abajo;
	private Estado siguiendo_molesto_izquierda;
	private Estado siguiendo_molesto_derecha;

	private Estado lanzando_cigarro;

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

		this.currentState = STATES.SIGUIENDO_CALMADO_ARRIBA;
		this.lastState = STATES.SIGUIENDO_CALMADO_ARRIBA;

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
		this.mapa.actualiza(percepcion, Visualizaciones.ENCENDIDA);
	}

	/**
	 * Actualiza el estado
	 * 
	 * @param percepcion Percepcion del juego
	 */
	private void actualizaState(StateObservation percepcion) {
		this.lastState = this.currentState;
		this.currentState = this.raiz.decidir(this);
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
		System.out.println("\nEstado: " + this.currentState);

		return lastAction;
	}

	/**
	 * Entrena el agente para que aprenda a jugar a traves del algoritmo Q-learning
	 * 
	 * @param percepcion Percepcion del juego
	 * @return ACTIONS : devuelve una accion
	 */
	public ACTIONS entrenar(StateObservation percepcion) {
		double reward = getReward(lastState, lastAction, currentState);
		this.qlearning.update(lastState, lastAction, currentState, reward);
		this.lastAction = this.qlearning.nextAction(currentState);

		return lastAction;
	}

	/**
	 * Devuelve la recomensa total obtenida por el agente
	 * 
	 * @return double : devuelve la recompensa total
	 */
	public double getGR() {
		return this.globalReward;
	}

	// =============================================================================
	// AUXILIARES
	// =============================================================================

	public double calculaRotacion(Vector2d c) {
		Vector2d avatar = this.mapa.getCurrentAvatarPosition();
		double angulo = Math.atan2(avatar.y - c.y, avatar.x - c.x);
		return Math.toDegrees(angulo);
	}

	/**
	 * Calcula la recompensa para la ultima accion realizada
	 * 
	 * @param lastState    Estado anterior
	 * @param lastAction   ultima accion realizada
	 * @param currentState Estado actual
	 * @return double : devuelve la recompensa calculada
	 */
	private double getReward(STATES lastState, ACTIONS lastAction, STATES currentState) {
//		this.reward = -50;
//		Casilla currentCasilla = this.mapa.getAvatar();
//		Casilla lastCasilla = this.mapa.getLastAvatar();
//
//		switch (lastState) {
//		case ESTADO_4:
//		case ESTADO_8:
//		case ESTADO_12:
//			if (this.checkNorthMovement(lastCasilla.getY(), currentCasilla.getY()))
//				this.reward = 50;
//			break;
//		case ESTADO_2:
//		case ESTADO_6:
//		case ESTADO_10:
//		case ESTADO_13:
//			if (this.checkSouthMovement(lastCasilla.getY(), currentCasilla.getY()))
//				this.reward = 50;
//			break;
//		case ESTADO_1:
//		case ESTADO_5:
//		case ESTADO_9:
//			if (this.checkEastMovement(lastCasilla.getX(), currentCasilla.getX()))
//				this.reward = 50;
//			break;
//		case ESTADO_3:
//		case ESTADO_7:
//		case ESTADO_11:
//			if (this.checkWestMovement(lastCasilla.getX(), currentCasilla.getX()))
//				this.reward = 50;
//			break;
//		}
//
		return 0;
	}

	/**
	 * Comprueba si el agente se ha movido hacia el norte
	 * 
	 * @param a Posicion Y de la ultima casilla
	 * @param b Posicion Y de la casilla actual
	 * @return boolean : devuelve true si el agente se mueve hacia el norte. False
	 *         en caso contrario
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
	 * @param a Posicion Y de la ultima casilla
	 * @param b Posicion Y de la casilla actual
	 * @return booelan : devuelve true si el agente se mueve hacia el sur. False en
	 *         caso contrario
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
	 * @param a Posicion X de la ultima casilla
	 * @param b Posicion X de la casilla actual
	 * @return boolean : devuelve true si el agente se mueve hacia el este. False en
	 *         caso contrario
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
	 * @param a Posicion X de la ultima casilla
	 * @param b Posicion X de la casilla actual
	 * @return boolean : devuelve true si el agente se mueve hacia el oeste. False
	 *         en caso contrario
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
	 * @return ArrayList STATES : devuelve un ArrayList con los estados
	 */
	private ArrayList<STATES> getStates() {
		// CAMINODERECHA, CAMINOABAJO, CAMINOARRIBA, CAMINOATRAS
		return new ArrayList<STATES>(Arrays.asList(STATES.HUYENDO_ARRIBA, STATES.HUYENDO_ABAJO,
				STATES.HUYENDO_IZQUIERDA, STATES.HUYENDO_DERECHA,

				STATES.SIGUIENDO_CALMADO_ARRIBA, STATES.SIGUIENDO_CALMADO_ABAJO, STATES.SIGUIENDO_CALMADO_IZQUIERDA,
				STATES.SIGUIENDO_CALMADO_DERECHA,

				STATES.SIGUIENDO_MOLESTO_ARRIBA, STATES.SIGUIENDO_MOLESTO_ABAJO, STATES.SIGUIENDO_MOLESTO_IZQUIERDA,
				STATES.SIGUIENDO_MOLESTO_DERECHA,

				STATES.LANZANDO_CIGARRO));
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
	 * Crea el arbol de decision del agente
	 */
	private void generaArbol() {

		// INICIALIZACION DE PREGUNTAS ===================================
		// Arbol HUYENDO
		this.enemigoCerca = new EnemigoCerca();
		this.enemigoArriba = new EnemigoArriba();
		this.enemigoAbajo = new EnemigoAbajo();
		this.enemigoIzquierda = new EnemigoIzquierda();
		this.enemigoDerecha = new EnemigoDerecha();

		this.muroArriba = new MuroArriba();
		this.muroAbajo = new MuroAbajo();
		this.muroIzquierda = new MuroIzquierda();
		this.muroDerecha = new MuroDerecha();
		
		// INICIALIZACION DE ESTADOS =====================================
		// Estados
		this.huye_arriba = new Estado(STATES.HUYENDO_ARRIBA);
		this.huye_abajo = new Estado(STATES.HUYENDO_ABAJO);
		this.huye_izquierda = new Estado(STATES.HUYENDO_IZQUIERDA);
		this.huye_derecha = new Estado(STATES.HUYENDO_DERECHA);

		// ESTRUCTURA DE ARBOL ===========================================
		this.raiz = this.enemigoCerca;
		/* Hay Enemigo cerca? */
		/* .SI */this.enemigoCerca.setYes(this.enemigoArriba);
		/* ......Hay enemigo arriba? */
		/* .......SI */this.enemigoArriba.setYes(this.muroAbajo);
		/* ..........Hay muro Abajo? */
		/* ...........SI */this.muroAbajo.setYes(this.muroIzquierda);
		/* ................Hay muro izquierda */
		/* .................SI */this.muroIzquierda.setYes(this.muroDerecha);
		/* ......................Hay muro derecha */
		/* .......................SI */this.muroDerecha.setYes(quieto);
		/* .......................NO */this.muroDerecha.setNo(huye_derecha);
		/* .................NO */this.muroIzquierda.setNo(huye_izquierda);
		/* ...........NO */this.muroAbajo.setNo(huye_abajo);
		/* .......NO */this.enemigoArriba.setNo(this.enemigoAbajo);
		/* ..........Hay Enemigo abajo? */
		/* ...........SI */this.enemigoAbajo.setYes(huye_arriba);
		/* ...........NO */this.enemigoAbajo.setNo(this.enemigoIzquierda);
		/* ...............Hay Enemigo Izquierda? */
		/* ................SI */this.enemigoIzquierda.setYes(huye_derecha);
		/* ................NO */this.enemigoIzquierda.setNo(this.enemigoDerecha);
		/* ....................Hay Enemigo Derecha? */
		/* .....................SI */this.enemigoDerecha.setYes(huye_izquierda);
		/* .....................NO */this.enemigoDerecha.setNo(quieto);
		/* .NO */this.enemigoCerca.setNo(this.sanoArriba);
		/* ......Hay SANO ARRIBA? */
		/* .......SI */this.sanoArriba.setYes(huye_arriba);
		/* .......NO */this.sanoArriba.setNo(this.sanoAbajo);
		/* ............Hay sano abajo? */
		/* .............SI */this.sanoAbajo.setYes(huye_abajo);
		/* .............NO */this.sanoAbajo.setNo(this.sanoIzquierda);
		/* ..................Hay sano izquierda */
		/* ...................SI */this.sanoIzquierda.setYes(huye_izquierda);
		/* ...................NO */this.sanoIzquierda.setNo(this.sanoDerecha);
		/* ........................Hay sano derecha */
		/* .........................SI */this.sanoDerecha.setYes(huye_derecha);
		/* .........................NO */this.sanoDerecha.setNo(quieto);
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

	/**
	 * Guarda el valor de la variable epsilon de la formula del qlearning.
	 * 
	 * @param path : Ruta del directorio donde se guarda el valor.
	 */
	public void saveEpsilon(String path) {
		this.qlearning.saveEpsilon(path);
	}
}
