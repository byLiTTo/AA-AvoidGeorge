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

		this.currentState = STATES.lanzando_cigarro;
		this.lastState = STATES.lanzando_cigarro;

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
		return new ArrayList<STATES>(Arrays.asList(STATES.huyendo_Earriba, STATES.huyendo_Eabajo,
				STATES.huyendo_Eizquierda, STATES.huyendo_Ederecha,

				STATES.huyendo_Earriba_Babajo, STATES.huyendo_Eabajo_Barriba, STATES.huyendo_Eizquierda_Bderecha,
				STATES.huyendo_Ederecha_Bizquierda,

				STATES.huyendo_Eabajo_esquina0, STATES.huyendo_Ederecha_esquina0,

				STATES.huyendo_Eabajo_esquina1, STATES.huyendo_Eizquierda_esquina1,

				STATES.huyendo_Earriba_esquina2, STATES.huyendo_Ederecha_esquina2,

				STATES.huyendo_Earriba_esquina3, STATES.huyendo_Eizquierda_esquina3,

				STATES.lanzando_cigarro));
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
		return 0;
	}

	public double calculaRotacion(Vector2d c) {
		Vector2d avatar = this.mapa.getAvatarCurrentPosition();
		double angulo = Math.atan2(avatar.y - c.y, avatar.x - c.x);
		return Math.toDegrees(angulo);
	}

	// =============================================================================
	// METODOS PARA PREGUNTAS
	// =============================================================================

	private int TengoEnemigoCerca() {
		if (this.mapa.getDistanciaCerca() >= this.mapa
				.getEnemyCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition())) {
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
		}
		return -1;
	}

	private int EstoyEnEsquina() {
		if (this.mapa.getNodo(1, 1).getEstado().equals(AVATAR)) {
			return 0;
		} else if (this.mapa.getNodo(this.mapa.getAncho() - 2, 1).getEstado().equals(AVATAR)) {
			return 1;
		} else if (this.mapa.getNodo(1, this.mapa.getAlto() - 2).getEstado().equals(AVATAR)) {
			return 2;
		} else if (this.mapa.getNodo(this.mapa.getAncho() - 2, this.mapa.getAlto() - 2).getEstado().equals(AVATAR)) {
			return 3;
		}
		return -1;
	}

	private int EstoyEnPeligro() {
		if (this.mapa.getDistanciaPeligro() >= this.mapa
				.getEnemyCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition())) {
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
		}
		return -1;
	}

	private int EstoyEnBorde() {
		int x = (int) this.mapa.getAvatarCurrentPosition().x / this.mapa.getBloque();
		int y = (int) this.mapa.getAvatarCurrentPosition().y / this.mapa.getBloque();

		if (y == 1) {
			return 0;
		} else if (y == this.mapa.getAlto() - 2) {
			return 1;
		} else if (x == 1) {
			return 2;
		} else if (x == this.mapa.getAncho() - 2) {
			return 3;
		}
		return -1;
	}

	// =============================================================================
	// COMPRUEBA ESTADO
	// =============================================================================

	private STATES compruebaEstado() {

		switch (TengoEnemigoCerca()) {
		case 0: // Earriba - Enemigo Arriba del avatar
			switch (EstoyEnEsquina()) {
			case 2: // esquina2 - Esquina Inferior Izquierda
				return STATES.huyendo_Earriba_esquina2;
			case 3: // esquina3 - Esquina Inferior Derecha
				return STATES.huyendo_Earriba_esquina3;
			default:
				switch (EstoyEnPeligro()) {
				case 0:
				case 1:
				case 2:
				case 3:
					switch (EstoyEnBorde()) {
					case 1:
						return STATES.huyendo_Earriba_Babajo;
					default:
						return STATES.huyendo_Earriba;
					}
				default:
					return STATES.lanzando_cigarro;
				}
			}
		case 1: // Eabajo - Enemigo Abajo del avatar
			switch (EstoyEnEsquina()) {
			case 0: // esquina0 - Esquina Superior Izquierda
				return STATES.huyendo_Eabajo_esquina0;
			case 1: // esquina0 - Esquina Superior Derecha
				return STATES.huyendo_Eabajo_esquina1;
			default:
				switch (EstoyEnPeligro()) {
				case 0:
				case 1:
				case 2:
				case 3:
					switch (EstoyEnBorde()) {
					case 0:
						return STATES.huyendo_Eabajo_Barriba;
					default:
						return STATES.huyendo_Eabajo;
					}
				default:
					return STATES.lanzando_cigarro;
				}
			}
		case 2: // Eizquierda - Enemigo Izquierda del avatar
			switch (EstoyEnEsquina()) {
			case 1: // esquina1 - Esquina Superior Derecha
				return STATES.huyendo_Eizquierda_esquina1;
			case 3: // esquina3 - Esquina Inferior Derecha
				return STATES.huyendo_Eizquierda_esquina3;
			default:
				switch (EstoyEnPeligro()) {
				case 0:
				case 1:
				case 2:
				case 3:
					switch (EstoyEnBorde()) {
					case 3:
						return STATES.huyendo_Eizquierda_Bderecha;
					default:
						return STATES.huyendo_Eizquierda;
					}
				default:
					return STATES.lanzando_cigarro;
				}
			}
		case 3: // Ederecha - Enemigo Derecha del avatar
			switch (EstoyEnEsquina()) {
			case 0: // esquina0 - Esquina Superior Izquierda
				return STATES.huyendo_Ederecha_esquina0;
			case 2: // esquina2 - Esquina Inferior Izquierda
				return STATES.huyendo_Ederecha_esquina2;
			default:
				switch (EstoyEnPeligro()) {
				case 0:
				case 1:
				case 2:
				case 3:
					switch (EstoyEnBorde()) {
					case 2:
						return STATES.huyendo_Ederecha_Bizquierda;
					default:
						return STATES.huyendo_Ederecha;
					}
				default:
					return STATES.lanzando_cigarro;
				}
			}
		default:
			return STATES.lanzando_cigarro;
		}

	}

//	private int hayEnemigoCerca() {
//		if (this.mapa.getDistanciaPeligro() >= this.mapa
//				.getEnemyCurrentDistanceFrom(this.mapa.getAvatarCurrentPosition())) {
//			double grados = this.calculaRotacion(this.mapa.getEnemyCurrentPosition());
//			if (grados > 45 && grados <= 135) {
//				return 0;
//			} else if (grados < -45 && grados >= -135) {
//				return 1;
//			} else if (grados >= 0 && grados <= 45 || grados <= 0 && grados >= -45) {
//				return 2;
//			} else {
//				return 3;
//			}
//
////			else if (grados >= 135 && grados <= 180 || grados <= -135 && grados >= -180) {
////				return 3;
////			}
//
//		} else {
//			return -1;
//		}
//	}
//
//	private int hayMolestoCerca() {
//
//		return 0;
//	}
//
//	private int dondeEstaMolesto() {
//		double grados = this.calculaRotacion(this.mapa.getMolestoCurrentPosition());
//		if (grados > 45 && grados <= 135) {
//			return 0;
//		} else if (grados < -45 && grados >= -135) {
//			return 1;
//		} else if (grados >= 0 && grados <= 45 || grados <= 0 && grados >= -45) {
//			return 2;
//		} else {
//			return 3;
//		}
//
////		else if (grados >= 135 && grados <= 180 || grados <= -135 && grados >= -180) {
////			return 3;
////		}
//	}
}
