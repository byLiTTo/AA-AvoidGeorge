package uhu.Juego03;

import static uhu.Constantes.MURO;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;
import uhu.grid.Casilla;

public class MuroArriba extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {

		Casilla avatar = c.getMapa().getAvatarCurrentPosition();
		Casilla abajo = c.getMapa().getNodo(avatar.getX(), avatar.getY() - 1);

		if (abajo.getEstado().equals(MURO)) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}

}
