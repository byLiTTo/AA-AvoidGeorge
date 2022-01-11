/**
 * 
 */
package uhu.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types.ACTIONS;
import tools.Vector2d;

import static uhu.Constantes.*;

/**
 * @author LiTTo
 *
 */
public class Mapa {

	// =============================================================================
	// VARIABLES
	// =============================================================================

	private int ancho;
	private int alto;
	private int bloque;

	private ArrayList<ArrayList<Casilla>> tablero;

	private Casilla avatar;
	private Casilla avatarLastPosition;
	private int columnaPortal;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor con parametros.
	 * 
	 * @param ancho
	 * @param alto
	 * @param bloque
	 * @param grid
	 */
	public Mapa(int ancho, int alto, int bloque, StateObservation percepcion) {
		this.ancho = ancho;
		this.alto = alto;
		this.bloque = bloque;

		this.tablero = new ArrayList<ArrayList<Casilla>>();

		this.avatar = new Casilla();
		
		actualiza(percepcion, Visualizaciones.NADA);
	}

	// =============================================================================
	// GETs y SETs
	// =============================================================================

	/**
	 * Devuelve el ancho del mapa.
	 * 
	 * @return Ancho del mapa.
	 */
	public int getAncho() {
		return this.ancho;
	}

	/**
	 * Devuelve el alto del mapa.
	 * 
	 * @return Alto del mapa.
	 */
	public int getAlto() {
		return this.alto;
	}

	/**
	 * Devuelve la casilla donde se encuentra el avatar dentro del tablero.
	 * 
	 * @return Casilla superman.
	 */
	public Casilla getAvatar() {
		return this.avatar;
	}

	public Casilla getLastAvatar() {
		return this.avatarLastPosition;
	}

	public int getColumnaPortal() {
		return this.columnaPortal;
	}

	/**
	 * Devuelve el nodo que se encuentra en las coordenadas pasadas por parametro
	 * 
	 * @param x Coordenada X del nodo
	 * @param y Coordenada Y del nodo
	 * @return Casilla que se encuentra en la posicion X e Y del mapa
	 */
	public Casilla getNodo(int x, int y) {
		return this.tablero.get(x).get(y);
	}

	/**
	 * Cambia el estado de la casilla que se encuentra en la misma posicion que la
	 * casilla pasada por parametro.
	 * 
	 * @param n Casilla a la que se cambiara el estado.
	 */
	public void setNodo(Casilla n) {
		this.tablero.get(n.getX()).get(n.getY()).setEstado(n.getEstado());
	}

	// =============================================================================
	// METODOS
	// =============================================================================

	/**
	 * Actualiza las observaciones del mapa y vuelve a realizar todas las
	 * reasignaciones.
	 * 
	 * @param percepcion
	 * @param opcion     Opcion para mostrar por consola el mapa o solo cargar las
	 *                   percepciones necesarias para el funcionamiento
	 */
	public void actualiza(StateObservation percepcion, Visualizaciones opcion) {
		this.tablero.clear();
		generaCasillas();

		switch (opcion) {
		case NADA:
			setImmovablePositions(percepcion.getImmovablePositions());
			setPortalsPositions(percepcion.getPortalsPositions());
			setAvatarPosition(percepcion.getAvatarPosition());
			asignaVecinos();
			break;
		case BASICO:
			setImmovablePositions(percepcion.getImmovablePositions());
			setPortalsPositions(percepcion.getPortalsPositions());
			setAvatarPosition(percepcion.getAvatarPosition());
			asignaVecinos();
			visualiza();
			break;
		case MAPA:
			setImmovablePositions(percepcion.getImmovablePositions());
			setPortalsPositions(percepcion.getPortalsPositions());
			asignaVecinos();
			visualiza();
			break;
		case CAMELLOS:
			setMovablePositions(percepcion.getMovablePositions());
			setNPC(percepcion.getNPCPositions());
			asignaVecinos();
			visualiza();
			break;
		case TODO:
			setImmovablePositions(percepcion.getImmovablePositions());
			setPortalsPositions(percepcion.getPortalsPositions());
			setMovablePositions(percepcion.getMovablePositions());
			setNPC(percepcion.getNPCPositions());
			setAvatarPosition(percepcion.getAvatarPosition());
			asignaVecinos();
			visualiza();
			break;
		}

//		ACTIONS lastAction = percepcion.getAvatarLastAction();
//		switch (lastAction) {
//		case ACTION_UP:
//			this.avatarLastPosition.setY(this.avatar.getY() - 1);
//			break;
//		case ACTION_DOWN:
//			this.avatarLastPosition.setY(this.avatar.getY() + 1);
//			break;
//		case ACTION_LEFT:
//			this.avatarLastPosition.setX(this.avatar.getX() + 1);
//			break;
//		case ACTION_RIGHT:
//			this.avatarLastPosition.setX(this.avatar.getX() - 1);
//			break;
//		default:
//			this.avatarLastPosition = this.avatar;
//			break;
//		}

	}

	/**
	 * Inicializa todas las casillas que forman el mapa, por defecto inicializamos
	 * todas como cielo.
	 * 
	 * @param grid Tabla con todos los obetos observados en el mapa.
	 */
	public void generaCasillas() {
		for (int x = 0; x < this.ancho; x++) {
			this.tablero.add(new ArrayList<Casilla>());
			for (int y = 0; y < this.alto; y++) {
				this.tablero.get(x).add(new Casilla(x, y, VACIO));
			}
		}
	}

	/**
	 * Asigna a las casillas, donde se encuentran los objetos inmovibles, su estado
	 * correspondiente.
	 * 
	 * @param percepcion Observacion del estado actual.
	 */
	private void setImmovablePositions(ArrayList<Observation>[] inmovable) {
		if (inmovable != null) {
			for (int i = 0; i < inmovable.length; i++) {
				for (int j = 0; j < inmovable[i].size(); j++) {
					int x = (int) Math.ceil(inmovable[i].get(j).position.x / this.bloque);
					int y = (int) Math.ceil(inmovable[i].get(j).position.y / this.bloque);

//					switch (inmovable[i].get(j).itype) {
//					case Constantes.muro_tipo:
//						this.tablero.get(x).get(y).setEstado(Constantes.MURO);
//						break;
//					default:
//						this.tablero.get(x).get(y).setEstado(Constantes.VACIO);
//						break;
//					}

					this.tablero.get(x).get(y).setEstado(MURO);

				}
			}
		}
	}

	private void setPortalsPositions(ArrayList<Observation>[] portals) {
		if (portals != null) {
			for (int i = 0; i < portals.length; i++) {
				for (int j = 0; j < portals[i].size(); j++) {
					int x = (int) portals[i].get(j).position.x / this.bloque;
					int y = (int) portals[i].get(j).position.y / this.bloque;

//					switch (portals[i].get(j).itype) {
//					case Constantes.meta_tipo:
//						this.tablero.get(x).get(y).setEstado(Constantes.META);
//						break;
//					default:
//						this.tablero.get(x).get(y).setEstado(Constantes.VACIO);
//						break;
//					}

					this.tablero.get(x).get(y).setEstado("NAH");
					this.columnaPortal = x;
				}
			}
		}
	}

	/**
	 * Asigna a las casillas, donde se encuentran los objetos moviles, su estado
	 * correspondiente.
	 * 
	 * @param percepcion Observacion del estado actual.
	 */
	private void setMovablePositions(ArrayList<Observation>[] movable) {
		if (movable != null) {

			for (int i = 0; i < movable.length; i++) {
				for (int j = 0; j < movable[i].size(); j++) {
//					int x = (int) Math.ceil(movable[i].get(j).position.x / this.bloque);
//					int y = (int) Math.ceil(movable[i].get(j).position.y / this.bloque);

					int x = (int) movable[i].get(j).position.x / this.bloque;
					int y = (int) movable[i].get(j).position.y / this.bloque;

//					switch (movable[i].get(j).itype) {
//					case Constantes.cdr_tipo:
//						this.tablero.get(x).get(y).setEstado(Constantes.CDR);
//						break;
//					case Constantes.cdm_tipo:
//						this.tablero.get(x).get(y).setEstado(Constantes.CDM);
//						break;
//					case Constantes.cdl_tipo:
//						this.tablero.get(x).get(y).setEstado(Constantes.CDL);
//						break;
//					default:
////						this.tablero.get(x).get(y).setEstado(Constantes.VACIO);
//						this.tablero.get(x).get(y).setEstado(Integer.toString(movable[i].get(j).category));
//						break;
//					}

//					this.tablero.get(x).get(y).setEstado(Integer.toString(movable[i].get(j).itype));
					this.tablero.get(x).get(y).setEstado(Integer.toString(movable[i].get(j).category));
				}
			}
		}

	}

	private void setNPC(ArrayList<Observation>[] NPC) {
		if (NPC != null) {

			for (int i = 0; i < NPC.length; i++) {
				for (int j = 0; j < NPC[i].size(); j++) {

					int x = (int) NPC[i].get(j).position.x / this.bloque;
					int y = (int) NPC[i].get(j).position.y / this.bloque;

//					this.tablero.get(x).get(y).setEstado(CAMELLO);
//					this.tablero.get(x).get(y).setEstado(Integer.toString(NPC[i].get(j).itype));
					this.tablero.get(x).get(y).setEstado(Integer.toString(NPC[i].get(j).category));
				}
			}
		}
	}

	private void initAvatarPosition(Vector2d pos) {
		int x = (int) Math.ceil(pos.x / this.bloque);
		int y = (int) Math.ceil(pos.y / this.bloque);

		this.tablero.get(x).get(y).setEstado(AVATAR);
		this.avatar = this.tablero.get(x).get(y);
	}

	/**
	 * Asigna la posicion del avatar dentro del tablero y guarda la casilla en la
	 * variable superman.
	 * 
	 * @param percepcion Observaci�n del estado actual.
	 */
	private void setAvatarPosition(Vector2d pos) {
		this.avatarLastPosition = this.avatar;
		int x = (int) Math.ceil(pos.x / this.bloque);
		int y = (int) Math.ceil(pos.y / this.bloque);

		this.tablero.get(x).get(y).setEstado(AVATAR);
		this.avatar = this.tablero.get(x).get(y);
	}

	/**
	 * Asigna los vecinosa cada nodo que forma el mapa, mediante la vecindad tipo 4,
	 * esto no hace posible realizar movimientos en diagonal.
	 */
	public void asignaVecinos() {
		for (int x = 0; x < this.ancho; x++) {
			for (int y = 0; y < this.alto; y++) {

				// Si no es borde superior
				if (y != 0) {
					this.tablero.get(x).get(y).setVecinoArriba(this.tablero.get(x).get(y - 1));
				}

				// Si no es borde inferior
				if (y != alto - 1) {
					this.tablero.get(x).get(y).setVecinoAbajo(this.tablero.get(x).get(y + 1));
				}

				// Si no es borde izquierdo
				if (x != 0) {
					this.tablero.get(x).get(y).setVecinoIzquierda(this.tablero.get(x - 1).get(y));
				}

				// Si no es borde derecho
				if (x != ancho - 1) {
					this.tablero.get(x).get(y).setVecinoDerecha(this.tablero.get(x + 1).get(y));
				}
			}
		}
	}

	/**
	 * Visualiza en la consola el mapa, representando el tipo de estado de cada
	 * casilla, es decir, su contenido.
	 */
	public void visualiza() {
		System.out.println();

		for (int i = 0; i < this.alto; i++) {
			for (int j = 0; j < this.ancho; j++) {
				System.out.print(this.tablero.get(j).get(i).getEstado() + " ");
			}
			System.out.println();
		}

		System.out.println();

	}

}
