package uhu.Juego03;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;
import uhu.grid.Casilla;

public class MolestoDerecha extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {
		double grados = c.calculaRotacion(c.getMapa().getMolestoCurrentPosition());
		if (grados >= 135 && grados <= 180 || grados <= -135 && grados >= -180) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}
}
