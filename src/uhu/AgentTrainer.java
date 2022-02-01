
package uhu;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

/**
 * Clase agente encargada de ejecutar el entrenamiento de QLearing, es decir, la
 * tabla q se actualiza a medida que se vaya entrenando.
 * 
 * @author Carlos Garcia Silva
 * @author Daniel Perez Rodriguez
 *
 */
public class AgentTrainer extends AbstractPlayer {

	private Cerebro c;

	// =============================================================================
	// CONSTRUCTORES
	// =============================================================================

	/**
	 * Constructor publico con observacion del estado y con tiempo.
	 * 
	 * @param percepcion   Observacion del estado actual.
	 * @param elapsedTimer Temporizador para la creacion del controlador.
	 */
	public AgentTrainer(StateObservation percepcion, ElapsedCpuTimer elapsedTimer) {
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
	 * @return ACTIONS : accion a ejecutar en el tick actual.
	 */
	public ACTIONS act(StateObservation percepcion, ElapsedCpuTimer elapsedTimer) {

		c.percibe(percepcion);
		ACTIONS accion = c.entrenar(percepcion);
		try {
			Thread.sleep(260);
		} catch (InterruptedException e) {
			System.out.println("Error en sleep");
		}
		return accion;
	}

	@Override
	public void result(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
		c.writeTable("QTABLE.txt");
		c.saveTimer();
		c.saveEpsilon("Epsilon.csv");
//		System.out.println("PUNTUACION: "+ c.getGR());
	}

}
