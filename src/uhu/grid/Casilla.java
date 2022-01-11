/**
 * 
 */
package uhu.grid;

import java.util.ArrayList;

/**
 * @author LiTTo
 *
 */
public class Casilla /* implements Comparable<Casilla> */ {

	// =============================================================================
	// VARIABLES
	// =============================================================================

	private int x;
	private int y;
	private String estado;

	private Casilla vecinoArriba;
	private Casilla vecinoAbajo;
	private Casilla vecinoIzquierda;
	private Casilla vecinoDerecha;

	private ArrayList<Casilla> vecinos;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor publico sin parametros.
	 */
	public Casilla() {
	}

	/**
	 * Constructor con parametros.
	 * 
	 * @param x      Coordenada X de la casilla.
	 * @param y      Coordenada Y de la casilla.
	 * @param estado Estado en el que se encuentra la casilla;
	 */
	public Casilla(int x, int y, String estado) {
		this.x = x;
		this.y = y;
		this.estado = estado;

		vecinos = new ArrayList<Casilla>();
	}

	// =============================================================================
	// GETs y SETs
	// =============================================================================

	/**
	 * 
	 * @return Coordenada X de la casilla.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Cambia el valor de la coordenada X de la casilla por el pasado por parametro.
	 * 
	 * @param x Valor de la coordena X por la que se va a cambiar.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 
	 * @return Coordenada Y de la casilla.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Cambia el valor de la coordenada Y de la casilla por el pasado por parametro.
	 * 
	 * @param x Valor de la coordena Y por la que se va a cambiar.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 
	 * @return Estado en el que se encuentra la casilla.
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * Cambia el estado en el que se encuentra la casilla por el pasado por
	 * parametro.
	 * 
	 * @param estado Valor del estado por el que se va a cambiar.
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * 
	 * @return Casilla situada arriba en el mapa.
	 */
	public Casilla getVecinoArriba() {
		return vecinoArriba;
	}

	/**
	 * Cambia el vecino de arriba del nodo en cuestion. Si el nodo ya es vecino, lo
	 * borra y lo inserta como superior.
	 * 
	 * @param vecinoArriba
	 */
	public void setVecinoArriba(Casilla vecinoArriba) {
		if (vecinos.contains(vecinoArriba)) {
			vecinos.remove(vecinoArriba);
		}
		vecinos.add(vecinoArriba);
		this.vecinoArriba = vecinoArriba;
	}

	/**
	 * 
	 * @return Casilla situada abajo en el mapa.
	 */
	public Casilla getVecinoAbajo() {
		return vecinoAbajo;
	}

	/**
	 * Cambia el vecino de abajo del nodo en cuestion. Si el nodo ya es vecino, lo
	 * borra y lo aade como inferior.
	 * 
	 * @param vecinoAbajo
	 */
	public void setVecinoAbajo(Casilla vecinoAbajo) {
		if (vecinos.contains(vecinoAbajo)) {
			vecinos.remove(vecinoAbajo);
		}
		vecinos.add(vecinoAbajo);
		this.vecinoAbajo = vecinoAbajo;
	}

	/**
	 * 
	 * @return Casilla situada a la izquierda en el mapa.
	 */
	public Casilla getVecinoIzquierda() {
		return vecinoIzquierda;
	}

	/**
	 * Cambia el vecino de la izquierda del nodo en cuestion. Si el nodo ya es
	 * vecino, lo borra y lo inserta como vecino izquierdo.
	 * 
	 * @param vecinoIzquierda
	 */
	public void setVecinoIzquierda(Casilla vecinoIzquierda) {
		if (vecinos.contains(vecinoIzquierda)) {
			vecinos.remove(vecinoIzquierda);
		}
		vecinos.add(vecinoIzquierda);
		this.vecinoIzquierda = vecinoIzquierda;
	}

	/**
	 * 
	 * @return Casilla situada a la derecha en el mapa.
	 */
	public Casilla getVecinoDerecha() {
		return vecinoDerecha;
	}

	/**
	 * Cambia el vecino de la derecha del nodo en cuestion. Si el nodo ya es vecino,
	 * lo borra y lo inserta como vecino derecho.
	 * 
	 * @param vecinoDerecha
	 */
	public void setVecinoDerecha(Casilla vecinoDerecha) {
		if (vecinos.contains(vecinoDerecha)) {
			vecinos.remove(vecinoDerecha);
		}
		vecinos.add(vecinoDerecha);
		this.vecinoDerecha = vecinoDerecha;
	}

	/**
	 * 
	 * @return Vector con todos los vecinos de la casilla actual. Formada por
	 *         vecindad de tipo 4. (Arriba, abajo, izquierda y derecha).
	 */
	public ArrayList<Casilla> getVecinos() {
		return this.vecinos;
	}

	/**
	 * Cambia el vector de vecinos de la casilla actual.
	 * 
	 * @param vecinos Vector de los nuevos vecinos de la casilla en cuesti�n.
	 */
	public void setVecinos(ArrayList<Casilla> vecinos) {
		this.vecinos = vecinos;
	}

	// =============================================================================
	// SOBRECARGAS
	// =============================================================================

	@Override
	public boolean equals(Object o) {
		return (((Casilla) o).getX() == this.x) && (((Casilla) o).getY() == this.y);
	}

}
