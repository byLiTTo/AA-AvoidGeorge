package uhu.Juego03;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;

public class MolestoCerca extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {
		if (c.getMapa().getDistanciaLanzamiento() >= c.getMapa()
				.getMolestoCurrentDistanceFrom(c.getMapa().getAvatarCurrentPosition())) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}
}
