
package uhu;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

/**
 * Clase Agente encargada de ejecutar la Tabla Q generada en el entrenamiento.
 * 
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 *
 */
public class AgentPlayer extends AbstractPlayer {

	private Cerebro c;
	private int o = 0;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor publico con observacion del estado y con tiempo.
	 * 
	 * @param percepcion   Observacion del estado actual.
	 * @param elapsedTimer Temporizador para la creacion del controlador.
	 */
	public AgentPlayer(StateObservation percepcion, ElapsedCpuTimer elapsedTimer) {
		this.c = new Cerebro(percepcion);
	}

	// =============================================================================
	// METODOS
	// =============================================================================

	/**
	 * Devuelve una accion. Esta funcion es llamada en cada paso del juego para que
	 * devuelva una accion que debe realizar el jugador.
	 * 
	 * @param percepcion   Observacion del estado actual.
	 * @param elapsedTimer Temporizador cuando vence la accion devuelta.
	 * @return ACTIONS : Una accion para el estado actual.
	 */
	public ACTIONS act(StateObservation percepcion, ElapsedCpuTimer elapsedTimer) {

		c.percibe(percepcion);
		ACTIONS accion = c.pensar(percepcion);
		return accion;
	}

	@Override
	public void result(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
		c.writeTable("QTABLE.txt");
		c.saveTimer();
//		System.out.println("PUNTUACION: "+ c.getGR());
	}

}
