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

		this.currentState = STATES.LANZANDO_CIGARRO;
		this.lastState = STATES.LANZANDO_CIGARRO;

		this.lastAction = ACTIONS.ACTION_USE;

		System.out.println("N ESTADOS: " + getStates().size());
		System.out.println("N ACCIONES: " + getActions().size());

		this.qlearning = new QLearning(getStates(), getActions(), new String("QTABLE.txt"));

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
	private ArrayList<STATES> getStates() {
		return new ArrayList<STATES>(Arrays.asList(STATES.HUYENDO_ARRIBA, STATES.HUYENDO_ABAJO,
				STATES.HUYENDO_IZQUIERDA, STATES.HUYENDO_DERECHA,

//				STATES.SIGUIENDO_CALMADO_ARRIBA, STATES.SIGUIENDO_CALMADO_ABAJO, STATES.SIGUIENDO_CALMADO_IZQUIERDA,
//				STATES.SIGUIENDO_CALMADO_DERECHA,
//
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
	 * Calcula la recompensa para la ultima accion realizada
	 * 
	 * @param lastState    Estado anterior
	 * @param lastAction   ultima accion realizada
	 * @param currentState Estado actual
	 * @return double : devuelve la recompensa calculada
	 */
	private double getReward(STATES lastState, ACTIONS lastAction, STATES currentState) {

		switch (currentState) {
		case HUYENDO_ARRIBA:
		case HUYENDO_ABAJO:
		case HUYENDO_IZQUIERDA:
		case HUYENDO_DERECHA:
			if (this.mapa.getEnemyCurrentDistanceFrom(this.mapa.getCurrentAvatarPosition()) >= this.mapa
					.getEnemyLastDistanceFrom(this.mapa.getLastAvatarLastPosition())) {
				return 10;
			} else {
				return -5;
			}

		case SIGUIENDO_MOLESTO_ARRIBA:
		case SIGUIENDO_MOLESTO_ABAJO:
		case SIGUIENDO_MOLESTO_IZQUIERDA:
		case SIGUIENDO_MOLESTO_DERECHA:
			if (this.mapa.getMolestoCurrentDistanceFrom(this.mapa.getCurrentAvatarPosition()) < this.mapa
					.getMolestoLastDistanceFrom(this.mapa.getLastAvatarLastPosition())) {
				return 10;
			} else {
				return -5;
			}

		default:
			return 0;
		}

	}

	public double calculaRotacion(Vector2d c) {
		Vector2d avatar = this.mapa.getCurrentAvatarPosition();
		double angulo = Math.atan2(avatar.y - c.y, avatar.x - c.x);
		return Math.toDegrees(angulo);
	}

	// =============================================================================
	// COMPRUEBA ESTADO
	// =============================================================================
	private STATES compruebaEstado() {

		switch (hayEnemigoCerca()) {
		/* ENEMIGO ARRIBA */ case 0:
			return STATES.HUYENDO_ABAJO;
		/* ENEMIGO ABAJO */ case 1:
			return STATES.HUYENDO_ARRIBA;
		/* ENEMIGO IZQUIERDA */ case 2:
			return STATES.HUYENDO_DERECHA;
		/* ENEMIGO DERECHA */ case 3:
			return STATES.HUYENDO_IZQUIERDA;
		default:
			boolean hayMolestos = (this.mapa.getCurrentMolestos() >= 1) ? true : false;
			if (hayMolestos) {
				switch (hayMolestoCerca()) {
				default:
					switch (dondeEstaMolesto()) {
					/* HACIA ARRIBA */ case 0:
						return STATES.SIGUIENDO_MOLESTO_ARRIBA;
					/* HACIA ABAJO */ case 1:
						return STATES.SIGUIENDO_MOLESTO_ABAJO;
					/* HACIA IZQUIERDA */ case 2:
						return STATES.SIGUIENDO_MOLESTO_IZQUIERDA;
					/* HACIA DERECHA */ case 3:
						return STATES.SIGUIENDO_MOLESTO_DERECHA;
					default:
						return STATES.LANZANDO_CIGARRO;
					}
				}
			} else {
				return STATES.LANZANDO_CIGARRO;
			}
		}

	}

	private int hayEnemigoCerca() {
		if (this.mapa.getDistanciaPeligro() >= this.mapa
				.getEnemyCurrentDistanceFrom(this.mapa.getCurrentAvatarPosition())) {
			double grados = this.calculaRotacion(this.mapa.getEnemyCurrentPosition());
			if (grados > 45 && grados <= 135) {
				return 0;
			} else if (grados < -45 && grados >= -135) {
				return 1;
			} else if (grados >= 0 && grados <= 45 || grados <= 0 && grados >= -45) {
				return 2;
			} else {
				return 3;
			}

//			else if (grados >= 135 && grados <= 180 || grados <= -135 && grados >= -180) {
//				return 3;
//			}

		} else {
			return -1;
		}
	}

	private int hayMolestoCerca() {

		return 0;
	}

	private int dondeEstaMolesto() {
		double grados = this.calculaRotacion(this.mapa.getMolestoCurrentPosition());
		if (grados > 45 && grados <= 135) {
			return 0;
		} else if (grados < -45 && grados >= -135) {
			return 1;
		} else if (grados >= 0 && grados <= 45 || grados <= 0 && grados >= -45) {
			return 2;
		} else {
			return 3;
		}

//		else if (grados >= 135 && grados <= 180 || grados <= -135 && grados >= -180) {
//			return 3;
//		}
	}
}
