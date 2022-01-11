/**
 * 
 */
package uhu.grid;

import java.util.ArrayList;

/**
 * @author LiTTo
 *
 */
public class Camino {

	// =============================================================================
	// VARIABLES
	// =============================================================================

	private ArrayList<Casilla> camino;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	public Camino() {
		this.camino = new ArrayList<Casilla>();
	}

	// =============================================================================
	// GETs y SETs
	// =============================================================================

	/**
	 * Devuelve la casilla de la posicion especificada.
	 * 
	 * @param index posicion de la casilla a devolver.
	 * @return la casilla correspondiente a la posicion especificada dentro del
	 *         camino.
	 */
	public Casilla get(int index) {
		return this.camino.get(index);
	}

	// =============================================================================
	// METODOS
	// =============================================================================

	/**
	 * Devuelve el numero de casillas en el camino.
	 * 
	 * @return el numero de casillas en el camino.
	 */
	public int size() {
		return this.camino.size();
	}

	/**
	 * Inserta una casilla espeficicada al final del camino.
	 * 
	 * @param n la casilla a insertar al final del camino.
	 */
	public void add(Casilla n) {
		this.camino.add(n);
	}

	/**
	 * Inserta una casilla especificada al principio del camino, en la posicion 0.
	 * PRECAUCION CON SU USO, ya que sobreescribe la primera posicion del camino y
	 * esto no ser interesante segun la formade usarlo.
	 * 
	 * @param n la casilla a insertar al principio del camino.
	 */
	public void addFront(Casilla n) {
		this.camino.add(0, n);
	}

	/**
	 * Devuelve verdadero si la casilla pertenece al camino y falso en caso
	 * contrario.
	 * 
	 * @param n la casilla a comprobar
	 * @return verdadero si se ha encontrado la casilla, falso si no.
	 */
	public boolean contains(Casilla n) {
		for (Casilla o : this.camino) {
			if ((o.getX() == n.getX()) && (o.getY() == n.getY())) {
				return true;
			}
		}
		return false;
	}

}
