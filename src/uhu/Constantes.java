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
		huyendo_Earriba, huyendo_Eabajo, huyendo_Eizquierda, huyendo_Ederecha,

		huyendo_Earriba_Babajo, huyendo_Eabajo_Barriba, huyendo_Eizquierda_Bderecha, huyendo_Ederecha_Bizquierda,

		huyendo_Eabajo_esquina0, huyendo_Ederecha_esquina0,

		huyendo_Eabajo_esquina1, huyendo_Eizquierda_esquina1,

		huyendo_Earriba_esquina2, huyendo_Ederecha_esquina2,

		huyendo_Earriba_esquina3, huyendo_Eizquierda_esquina3,

		lanzando_cigarro,
	}

}