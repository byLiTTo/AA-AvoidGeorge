package uhu.Juego03;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;
import uhu.grid.Casilla;

public class EnemigoIzquierda extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {
		double grados = c.calculaRotacion(c.getMapa().getCurrentEnemyPosition());
		if (grados >= 0 && grados <= 45 || grados <= 0 && grados >= -45) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}
}
