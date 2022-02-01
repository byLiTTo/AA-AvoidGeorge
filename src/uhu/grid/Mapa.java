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

	private Observation calmadoCurrentObservation;
	private Observation calmadoLastObservation;

	private Observation molestoCurrentObservation;
	private Observation molestoLastObservation;

//	private double currentDistance;
//	private double lastDistance;

	private int numCalmados;
	private int numMolestos;

	private double dPeligro;
	private double dMolesto;

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

		this.calmadoCurrentObservation = new Observation();

		this.molestoCurrentObservation = new Observation();

		this.numCalmados = 0;
		this.numMolestos = 0;

		this.dPeligro = this.bloque * 2.5;
		this.dMolesto = this.bloque * 1.5;

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

	/**
	 * Devuelve la posicion del avatar en coordenadas x y del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el avatar en el tick actual.
	 */
	public Vector2d getCurrentAvatarPosition() {
		return this.percepcion.getAvatarPosition();
	}

	/**
	 * Devuelve la posicion del avatar en coordenadas x y del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el avatar en el tick anterior.
	 */
	public Vector2d getLastAvatarLastPosition() {
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

	/**
	 * Devuelve la posicion del enemigo en coordenadas x y del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el enemigo en el tick actual.
	 */
	public Vector2d getEnemyCurrentPosition() {
		return this.enemyCurrentObservation.position;
	}

	/**
	 * Devuelve la posicion del enemigo en coordenadas x y del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el enemigo en el tick anterior.
	 */
	public Vector2d getEnemyLastPosition() {
		return this.enemyLastObservation.position;
	}

	/**
	 * Devuelve la posicion del NPC calmado mas cercano al avatar en coordenadas x y
	 * del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el NPC en el tick actual.
	 */
	public Vector2d getCalmadoCurrentPosition() {
		return this.calmadoCurrentObservation.position;
	}

	/**
	 * Devuelve la posicion del NPC calmado mas cercano al avatar en coordenadas x y
	 * del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el NPC en el tick anterior.
	 */
	public Vector2d getCalmadoLastPosition() {
		return this.calmadoLastObservation.position;
	}

	/**
	 * Devuelve la posicion del NPC molesto mas lejano al enemigo en coordenadas x y
	 * del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el NPC en el tick actual.
	 */
	public Vector2d getMolestoCurrentPosition() {
		return this.molestoCurrentObservation.position;
	}

	/**
	 * Devuelve la posicion del NPC calmado mas lejano al enemigo en coordenadas x y
	 * del tablero.
	 * 
	 * @return Casilla : casilla donde se encuentra el NPC en el tick anterior.
	 */
	public Vector2d getMolestoLastPosition() {
		return this.molestoLastObservation.position;
	}

	/**
	 * Devuelve la distancia que hay entre el avatar y el enemigo en dicho instante.
	 * 
	 * @return double : distancia entre el avatar y el enemigo en el tick actual
	 */
	public double geyEnemyCurrentDistanceFrom(Vector2d position) {
		return this.enemyCurrentObservation.position.sqDist(position);
	}

	/**
	 * Devuelve la distancia que hay entre el avatar y el enemigo en el instante
	 * anterior.
	 * 
	 * @return double : distancia entre el avatar y el enemigo en el tick anterior.
	 */
	public double getEnemyLastDistanceFrom(Vector2d position) {
		return this.enemyLastObservation.position.sqDist(position);
	}

	/**
	 * Devuelve la distancia que hay entre el avatar y el enemigo en dicho instante.
	 * 
	 * @return double : distancia entre el avatar y el enemigo en el tick actual
	 */
	public double getMolestoCurrentDistanceFrom(Vector2d position) {
		return this.molestoCurrentObservation.position.sqDist(position);
	}

	/**
	 * Devuelve la distancia que hay entre el avatar y el enemigo en el instante
	 * anterior.
	 * 
	 * @return double : distancia entre el avatar y el enemigo en el tick anterior.
	 */
	public double getMolestoLastDistanceFrom(Vector2d position) {
		return this.molestoLastObservation.position.sqDist(position);
	}

	/**
	 * Devuelve el numero de NPCs con el estado calmado.
	 * 
	 * @return int : numero de NPCs calmados.
	 */
	public int getCalmados() {
		return this.numCalmados;
	}

	/**
	 * Devuelve el numero de NPCs con estado molesto.
	 * 
	 * @return int : numero de NPCs con estado molesto.
	 */
	public int getMolestos() {
		return this.numMolestos;
	}

	/**
	 * Devuelve la distancia de seguridad que posee el avatar para detectar al
	 * enemigo como cerca.
	 * 
	 * @return double : distancia a partir de la cual el avatar considera que tiene
	 *         cerca al enemigo.
	 */
	public double getDistanciaSeguridad() {
		return this.dPeligro;
	}

	/**
	 * Devuelve la distancia de seguridad que posee el avatar para detectar al
	 * enemigo como cerca.
	 * 
	 * @return double : distancia a partir de la cual el avatar considera que tiene
	 *         cerca al enemigo.
	 */
	public double getDistanciaLanzamiento() {
		return this.dMolesto;
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
		ArrayList<Observation>[] NPC = percepcion.getNPCPositions();
		double distCalmado = Double.MAX_VALUE;
		double distMolesto = Double.MIN_VALUE;
		if (NPC != null) {

			for (int i = 0; i < NPC.length; i++) {
				for (int j = 0; j < NPC[i].size(); j++) {

					int x = (int) NPC[i].get(j).position.x / this.bloque;
					int y = (int) NPC[i].get(j).position.y / this.bloque;

					switch (NPC[i].get(j).itype) {
					case Constantes.calmado_tipo:
						if (NPC[i].get(j).position.sqDist(percepcion.getAvatarPosition()) < distCalmado) {
							this.calmadoLastObservation = this.calmadoCurrentObservation;
							this.calmadoCurrentObservation = NPC[i].get(j);
							distCalmado = NPC[i].get(j).position.sqDist(percepcion.getAvatarPosition());
						}
						this.tablero.get(x).get(y).setEstado(Constantes.CALMADO);
						break;
					case Constantes.molesto_tipo:
						if (NPC[i].get(j).position.sqDist(percepcion.getAvatarPosition()) > distMolesto) {
							this.molestoLastObservation = this.molestoCurrentObservation;
							this.molestoCurrentObservation = NPC[i].get(j);
							distMolesto = NPC[i].get(j).position.sqDist(this.enemyCurrentObservation.position);
						}
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
