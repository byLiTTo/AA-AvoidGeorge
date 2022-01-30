package uhu.Juego03;

import static uhu.Constantes.MURO;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;
import uhu.grid.Casilla;

public class MuroIzquierda extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {

		Casilla avatar = c.getMapa().getCurrentAvatarPosition();
		Casilla abajo = c.getMapa().getNodo(avatar.getX() - 1, avatar.getY());

		if (abajo.getEstado().equals(MURO)) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}

}
