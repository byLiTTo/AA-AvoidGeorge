/**
 * 
 */
package uhu.grid;

import uhu.grid.Casilla;

/**
 * @author LiTTo
 *
 */
public class Heuristica {

	// =============================================================================
	// METODOS
	// =============================================================================

	/**
	 * Calcula la distancia euclidea, en este caso al ser bidimensional, se realiza
	 * con el teorema de Pitagora. Hace uso de las coordenadas x e y.
	 * 
	 * @param iniX Coordenada X del nodo de partida.
	 * @param iniY Coordenada Y del nodo de partida
	 * @param finX Coordenada X del nodo de destino.
	 * @param finY Coordenada Y del nodo de destino.
	 * @return Devuelve la distancia euclidea entre ambos puntos.
	 */
	public static float calcularDistancia(int iniX, int iniY, int finX, int finY) {
		float x = finX - iniX;
		float y = finY - iniY;

		return (float) Math.sqrt((x * x) + (y * y));
	}

	/**
	 * Calcula la distancia euclidea, en este caso al ser bidimensional, se realiza
	 * con el teorema de Pitagora. Usando objetos de tipo casilla.
	 * 
	 * @param ini Casilla de inicio.
	 * @param fin Casilla destino
	 * @return Distancia euclidea entre la casilla inicial y la casilla final.
	 */
	public static float calcularDistancia(Casilla ini, Casilla fin) {
		float x = fin.getX() - ini.getX();
		float y = fin.getY() - ini.getY();

		return (float) Math.sqrt((x * x) + (y * y));
	}
}
