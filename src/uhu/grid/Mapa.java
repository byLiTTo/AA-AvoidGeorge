/**
 * 
 */
package uhu.grid;

import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import tools.Vector2d;
import uhu.Constantes;
import uhu.Constantes.Visualizaciones;

import static uhu.Constantes.*;

/**
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 * 
 */
public class Mapa {

	// =============================================================================
	// VARIABLES
	// =============================================================================

	private int ancho;
	private int alto;
	private int bloque;

	private StateObservation percepcion;

	private ArrayList<ArrayList<Casilla>> tablero;

	private Vector2d avatarCurrentPosition;
	private Vector2d avatarLastPosition;

	private Observation enemyCurrentObservation;
	private Observation enemyLastObservation;

	private ArrayList<Observation> calmadosCurrentObservation;
	private ArrayList<Observation> calmadosLastObservation;

	private ArrayList<Observation> molestosCurrentObservation;
	private ArrayList<Observation> molestosLastObservation;

	private double distanciaCerca;
	private double distanciaPeligro;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor con parametros.
	 * 
	 * @param ancho      : int
	 * @param alto       : int
	 * @param bloque     : int
	 * @param percepcion : StateObservation
	 */
	public Mapa(int ancho, int alto, int bloque, StateObservation percepcion) {
		this.ancho = ancho;
		this.alto = alto;
		this.bloque = bloque;

		this.percepcion = percepcion;

		this.tablero = new ArrayList<ArrayList<Casilla>>();

		this.avatarCurrentPosition = percepcion.getAvatarPosition();

		this.enemyCurrentObservation = new Observation();

		this.calmadosCurrentObservation = new ArrayList<Observation>();

		this.molestosCurrentObservation = new ArrayList<Observation>();

		this.distanciaCerca = this.bloque * 2.5;
		this.distanciaPeligro = this.bloque * 4.5;

		System.out.println(this.bloque);

		actualiza(percepcion, Visualizaciones.APAGADA);
	}

	// =============================================================================
	// GETs y SETs
	// =============================================================================

	/**
	 * Devuelve el ancho del mapa.
	 * 
	 * @return int : ancho del mapa.
	 */
	public int getAncho() {
		return this.ancho;
	}

	/**
	 * Devuelve el alto del mapa.
	 * 
	 * @return int : alto del mapa.
	 */
	public int getAlto() {
		return this.alto;
	}

	public int getBloque() {
		return this.bloque;
	}

	public Vector2d getAvatarCurrentPosition() {
		return this.percepcion.getAvatarPosition();
	}

	public Vector2d getAvatarLastPosition() {
		return this.avatarLastPosition;
	}

	/**
	 * Devuelve la orientacion del avatar. Si el juego ha terminado, no podemos
	 * garantiazar que la orientacion refleje la orientacion real del avatar (el
	 * avatar será destruido). Si el juego ha terminado, se devolverá Types,NIL.
	 * 
	 * @return Vector2d : orientacion del avatar, o Types.NIL si el juego ha
	 *         terminado.
	 */
	public Vector2d getOrientacionAvatar() {
		return this.percepcion.getAvatarOrientation();
	}

	public Vector2d getEnemyCurrentPosition() {
		return this.enemyCurrentObservation.position;
	}

	public Vector2d getEnemyLastPosition() {
		return this.enemyLastObservation.position;
	}

	public double getEnemyCurrentDistanceFrom(Vector2d position) {
		return this.enemyCurrentObservation.position.sqDist(position) / this.bloque;
	}

	public double getEnemyLastDistanceFrom(Vector2d position) {
		return this.enemyLastObservation.position.sqDist(position) / this.bloque;
	}

	public int getCalmadosCurrentSize() {
		return this.calmadosCurrentObservation.size();
	}

	public int getCalmadosLastSize() {
		return this.calmadosLastObservation.size();
	}

	public int getMolestosCurrentSize() {
		return this.molestosCurrentObservation.size();
	}

	public int getMolestosLastSize() {
		return this.molestosLastObservation.size();
	}

	public double getDistanciaCerca() {
		return this.distanciaCerca;
	}

	public double getDistanciaPeligro() {
		return this.distanciaPeligro;
	}

	/**
	 * Devuelve el nodo que se encuentra en las coordenadas pasadas por parametro
	 * 
	 * @param x : Coordenada X del nodo
	 * @param y : Coordenada Y del nodo
	 * @return Casilla : casilla que se encuentra en la posicion X e Y del mapa
	 */
	public Casilla getNodo(int x, int y) {
		return this.tablero.get(x).get(y);
	}

	/**
	 * Cambia el estado de la casilla que se encuentra en la misma posicion que la
	 * casilla pasada por parametro.
	 * 
	 * @param n : Casilla a la que se cambiara el estado.
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
	 * @param opcion     : Opcion para mostrar por consola el mapa o solo cargar las
	 *                   percepciones necesarias para el funcionamiento
	 */
	public void actualiza(StateObservation percepcion, Visualizaciones opcion) {
		this.percepcion = percepcion;
		this.tablero.clear();
		generaCasillas();
		actualizaImmovable();
		actualizaAvatar();
		actualizaNPC();
		asignaVecinos();

		if (opcion == Visualizaciones.ENCENDIDA) {
			visualiza();
		}
	}

	/**
	 * Inicializa todas las casillas que forman el mapa, por defecto inicializamos
	 * todas como VACIO.
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
	 */
	private void actualizaImmovable() {
		ArrayList<Observation>[] inmovable = percepcion.getImmovablePositions();
		if (inmovable != null) {
			for (int i = 0; i < inmovable.length; i++) {
				for (int j = 0; j < inmovable[i].size(); j++) {
					if (inmovable[i].get(j).category == muro_cate && inmovable[i].get(j).itype == muro_tipo) {
						int x = (int) inmovable[i].get(j).position.x / this.bloque;
						int y = (int) inmovable[i].get(j).position.y / this.bloque;

						this.tablero.get(x).get(y).setEstado(MURO);
					}
				}
			}
		}
	}

	/**
	 * Actualiza la ultima posición del avatar, actualiza la posición actual del
	 * avatar y reasigna el estado de la casilla afectada.
	 */
	private void actualizaAvatar() {
		this.avatarLastPosition = this.avatarCurrentPosition;
		int x = (int) percepcion.getAvatarPosition().x / this.bloque;
		int y = (int) percepcion.getAvatarPosition().y / this.bloque;

		this.tablero.get(x).get(y).setEstado(AVATAR);
		this.avatarCurrentPosition = percepcion.getAvatarPosition();
	}

	/**
	 * Asigna a las casillas, donde se encuentra los NPCs detectados como
	 * observaciones, con su estado correspondiente
	 */
	private void actualizaNPC() {
		this.calmadosLastObservation = this.calmadosCurrentObservation;
		this.calmadosCurrentObservation.clear();

		this.molestosLastObservation = this.molestosCurrentObservation;
		this.molestosCurrentObservation.clear();

		ArrayList<Observation>[] NPC = percepcion.getNPCPositions();
		if (NPC != null) {

			for (int i = 0; i < NPC.length; i++) {
				for (int j = 0; j < NPC[i].size(); j++) {

					int x = (int) NPC[i].get(j).position.x / this.bloque;
					int y = (int) NPC[i].get(j).position.y / this.bloque;

					switch (NPC[i].get(j).itype) {
					case Constantes.calmado_tipo:
						this.calmadosCurrentObservation.add(NPC[i].get(j));
						this.tablero.get(x).get(y).setEstado(Constantes.CALMADO);
						break;
					case Constantes.molesto_tipo:
						this.molestosCurrentObservation.add(NPC[i].get(j));
						this.tablero.get(x).get(y).setEstado(Constantes.MOLESTO);
						break;
					case Constantes.enemy_tipo:
						this.enemyLastObservation = this.enemyCurrentObservation;
						this.enemyCurrentObservation = NPC[i].get(j);
						this.tablero.get(x).get(y).setEstado(Constantes.ENEMIGO);
						break;
					}
				}
			}
		}
	}

	/**
	 * Asigna los vecinos a cada nodo que forma el mapa, mediante la vecindad tipo
	 * 4, esto no hace posible realizar movimientos en diagonal.
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
