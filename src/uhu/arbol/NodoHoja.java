/**
 * 
 */
package uhu.arbol;

import uhu.Cerebro;
import static uhu.Constantes.*;

/**
 * @author LiTTo
 *
 */
public abstract class NodoHoja extends NodoDecision {

	private STATES state;

	public NodoHoja(STATES s) {
		this.state = s;
	}

	public STATES getState() {
		return this.state;
	}

	public void setState(STATES s) {
		this.state = s;
	}

	@Override
	public STATES decidir(Cerebro c) {
		return getState();
	}

}
