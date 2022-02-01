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
	public static final String CALMADO = "C";
	public static final int calmado_cate = 3;
	public static final int calmado_tipo = 6;

	public static final String MOLESTO = "M";
	public static final int molesto_cate = 3;
	public static final int molesto_tipo = 4;

	public static final String ENEMIGO = "#";
	public static final int enemy_cate = 3;
	public static final int enemy_tipo = 7;

	// =============================================================================
	// OPCIONES DE METODOS
	// =============================================================================

	public enum Visualizaciones {
		APAGADA, ENCENDIDA
	}

	public enum STATES {
		HUYENDO_ARRIBA, HUYENDO_ABAJO, HUYENDO_IZQUIERDA, HUYENDO_DERECHA,

//		SIGUIENDO_CALMADO_ARRIBA, SIGUIENDO_CALMADO_ABAJO, SIGUIENDO_CALMADO_IZQUIERDA, SIGUIENDO_CALMADO_DERECHA,

		SIGUIENDO_MOLESTO_ARRIBA, SIGUIENDO_MOLESTO_ABAJO, SIGUIENDO_MOLESTO_IZQUIERDA, SIGUIENDO_MOLESTO_DERECHA,

		LANZANDO_CIGARRO
	}

}
