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
	
	public static final String[] elements = {VACIO,MURO,ENEMIGO};
	public static final String[] coord = {"n","s","e","o"};
	public static final String[] proximity = {"c","l"};
	public static final String defaultKey = "ddddddddd";

	// =============================================================================
	// OPCIONES DE METODOS
	// =============================================================================

	public enum Visualizaciones {
		APAGADA, ENCENDIDA
	}

	public enum STATES {
		lanzando_cigarro, huyendo_Earriba, huyendo_Eabajo, huyendo_Eizquierda, huyendo_Ederecha,
		huyendo_Eabajo_esquina0, huyendo_Ederecha_esquina0, huyendo_Eabajo_esquina1, huyendo_Eizquierda_esquina1,
		huyendo_Earriba_esquina2, huyendo_Ederecha_esquina2, huyendo_Earriba_esquina3, huyendo_Eizquierda_esquina3,
		huyendo_Eabajo_Barriba_Lizquierdo, huyendo_Eizquierda_Barriba_Lizquierdo, huyendo_Ederecha_Barriba_Lizquierdo,
		huyendo_Eabajo_Barriba_Lderecho, huyendo_Eizquierda_Barriba_Lderecho, huyendo_Ederecha_Barriba_Lderecho,
		huyendo_Earriba_Babajo_Lizquierdo, huyendo_Eizquierda_Babajo_Lizquierdo, huyendo_Ederecha_Babajo_Lizquierdo,
		huyendo_Earriba_Babajo_Lderecho, huyendo_Eizquierda_Babajo_Lderecho, huyendo_Ederecha_Babajo_Lderecho,
		huyendo_Earriba_Bizquierda_Lsuperior, huyendo_Eabajo_Bizquierda_Lsuperior,
		huyendo_Ederecha_Bizquierda_Lsuperior, huyendo_Earriba_Bizquierda_Linferior,
		huyendo_Eabajo_Bizquierda_Linferior, huyendo_Ederecha_Bizquierda_Linferior, huyendo_Earriba_Bderecha_Lsuperior,
		huyendo_Eabajo_Bderecha_Lsuperior, huyendo_Eizquierda_Bderecha_Lsuperior, huyendo_Earriba_Bderecha_Linferior,
		huyendo_Eabajo_Bderecha_Linferior, huyendo_Eizquierda_Bderecha_Linferior
	}

}