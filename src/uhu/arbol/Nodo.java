/**
 * 
 */
package uhu.arbol;

import static uhu.Constantes.*;
import uhu.Cerebro;

/**
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 *
 */
public abstract interface Nodo {

	public STATES decidir(Cerebro c);

}
