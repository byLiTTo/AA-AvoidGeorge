package uhu.Juego03;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;

public class EnemigoCerca extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {
		if (c.getMapa().getDistanciaSeguridad() >= c.getMapa().getCurrentDistance()) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}
}
