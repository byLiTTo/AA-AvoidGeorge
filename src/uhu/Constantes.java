/**
 * 
 */
package uhu;

/**
 * Clase donde se guardaran algunas constantes de uso comun para facilitar su
 * accesibilidad en las diferentes partes del proyecto.
 * 
 * @author LiTTo
 *
 */
public final class Constantes {

	// =============================================================================
	// VISUALIZAR MAPA
	// =============================================================================

	// POR DEFECTO-VACiO
	public static final String VACIO = ".";

	// AVATAR
	public static final String AVATAR = "@";
	public static final int avatar_cate = 0;
	public static final int avatar_tipo = 20;

	// INMOVIBLES
	public static final String MURO = "w";
	public static final int muro_cate = 4;
	public static final int muro_tipo = 0;

	// PORTALS

	// MOVIBLES

	// NPCs
	public static final String RELAJADO= "R";
	public static final int relax_cate = 3;
	public static final int relax_tipo = 6;
	
	public static final String ENFADADO= "E";
	public static final int angry_cate = 3;
	public static final int angry_tipo = 4;
	
	public static final String ENEMIGO= "#";
	public static final int enemy_cate = 3;
	public static final int enemy_tipo = 7;

	// =============================================================================
	// OPCIONES DE METODOS
	// =============================================================================

	public enum Visualizaciones {
		NADA, MAPA, BASICO, CAMELLOS, TODO
	}

	public enum SENTIDO {
		ORIENTE, OCCIDENTE,
	}

	public enum ORIENTACION {
		NORTE, SUR, ESTE, OESTE
	}

	public enum STATES {
		ESTADO_1, ESTADO_2, ESTADO_3, ESTADO_4, ESTADO_5, ESTADO_6, ESTADO_7, 
		ESTADO_8, ESTADO_9, ESTADO_10, ESTADO_11, ESTADO_12, ESTADO_13
	}

	public enum QUESTIONS {
		TIENES_MURO_ARRIBA, TIENES_MURO_ABAJO, TIENES_MURO_IZQUIERDA, TIENES_MURO_DERECHA, ORIENTACION_NORTE,
		ORIENTACION_SUR, ORIENTACION_ESTE, ORIENTACION_OESTE, TIENES_MURO_ALREDEDOR
	}
}
