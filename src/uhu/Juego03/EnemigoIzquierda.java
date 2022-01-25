package uhu.Juego03;

import uhu.Cerebro;
import uhu.Constantes.STATES;
import uhu.arbol.NodoLogico;
import uhu.grid.Casilla;

public class EnemigoIzquierda extends NodoLogico {
	@Override
	public STATES decidir(Cerebro c) {
		Casilla avatar = c.getMapa().getCurrentAvatarPosition();
		Casilla enemigo = c.getMapa().getCurrentEnemyPosition();
		if (avatar.getX() == enemigo.getX() && avatar.getY() - 1 == enemigo.getY()) {
			this.setValor(true);
		} else {
			this.setValor(false);
		}
		return super.decidir(c);
	}
}
