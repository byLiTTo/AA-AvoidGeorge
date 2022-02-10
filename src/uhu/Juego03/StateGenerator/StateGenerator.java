package uhu.Juego03.StateGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import uhu.Constantes;
import javax.sql.StatementEventListener;

import uhu.Constantes;

/**
 * Crea la lista de estados a usar por el agente
 * @author Daniel
 *
 */
public class StateGenerator {

	/**
	 * Genera claves con el siguiente patron: Orientacion, proximidad_molesto, posicion_molesto, proximidad_enemigo, posicion_enemigo,
	 * bloque_arriba, bloque_abajo, bloque_izquierda, bloque_derecha
	 * @return Devuelve la lista de claves
	 */
	public ArrayList<String> generatorV1() {
		ArrayList<String> stateList = new ArrayList<>();
		ArrayList<String> listElements = new ArrayList<>();
		ArrayList<String> listProximity = new ArrayList<>();
		String aux;
		char auxChar;
		int num = 0, numRemoveGeorge = 0, numRemoveWall = 0, hashLength = 0;
		
		/*
		 * Disposicion de caracteres
		 * 1.- Orientacion -> coord[]
		 * 2.- Cercania del molesto mas cercano -> proximity[]
		 * 3.- Posicion del moslesto respecto al jugador -> coord[]
		 * 4.- Cercania del enemigo -> proximity[]
		 * 5.- Posicion del enemigo respecto al jugador -> coord[]
		 * Resto.- Combinaciones de elementos en el mapa
		 */
		
		// Primero creamos las combinaciones de coordenadas y proximidad
		for (int i = 0; i < Constantes.coord.length; i++) {
			for (int j = 0; j < Constantes.proximity.length; j++) {
				for (int k = 0; k < Constantes.coord.length; k++) {
					for (int l = 0; l < Constantes.proximity.length; l++) {
						for (int m = 0; m < Constantes.coord.length; m++) {
							aux = Constantes.coord[i] + Constantes.proximity[j] + Constantes.coord[k] + Constantes.proximity[l] 
									+ Constantes.coord[m];
							listProximity.add(aux);
						}
					}
				}
			}
		}
		
		// Ahora creamos todas las combinaciones de elementos en el mapa
		for (int i = 0; i < Constantes.elements.length; i++) {
			for (int j = 0; j < Constantes.elements.length; j++) {
				for (int k = 0; k < Constantes.elements.length; k++) {
					for (int l = 0; l < Constantes.elements.length; l++) {
						aux = Constantes.elements[i] + Constantes.elements[j] + Constantes.elements[k] + Constantes.elements[l];
						listElements.add(aux);
					}
				}
			}
		}
		
		hashLength = listProximity.get(0).length() + listElements.get(0).length();
		System.out.println("Longitud total de una clave: " + hashLength);
		
		for (int i = 0; i < listProximity.size(); i++) {
			for (int j = 0; j < listElements.size(); j++) {
				aux = listProximity.get(i) + listElements.get(j);
				stateList.add(aux);
				num++;
			}
		}
		
		System.out.println("Claves totales creadas: " + num);
		
		// Con todas las combinaciones ya creadas hay que descartar las que no sean necesarias
		// 1.- Quitar todos los estados donde el enemigo aparezca más de una vez
		int enemyCount = 0;
		
		for (int i = 0; i < stateList.size(); i++) {
			aux = stateList.get(i);
			enemyCount = 0;
			for (int j = 0; j < hashLength; j++) {
				auxChar = aux.charAt(j);
				if(auxChar == Constantes.ENEMIGO.charAt(0)) {
					enemyCount++;
					if(enemyCount == 2) {
						stateList.remove(i);
						i--;
						numRemoveGeorge++;
						break;
					}		
				}
			}
		}
		
		System.out.println("1.- Eliminacion de claves con George en varias posiciones: " + numRemoveGeorge);
		
		// 2.- Quitamos los estados en donde haya más de dos muros
		int wallCount = 0;
		
		for (int i = 0; i < stateList.size(); i++) {
			aux = stateList.get(i);
			wallCount = 0;
			for (int j = 0; j < hashLength; j++) {
				auxChar = aux.charAt(j);
				if(auxChar == Constantes.MURO.charAt(0)) {
					wallCount++;
					if(wallCount == 3) {
						stateList.remove(i);
						i--;
						numRemoveWall++;
						break;
					}		
				}
			}
		}
		
		// Estado por defecto, se llamará cuando se de una situacion imprevista
		stateList.add(Constantes.defaultKey);
		num++;
		
		System.out.println("2.- Eliminacion de claves con mas de 2 muros: " + numRemoveWall);
		num = num - numRemoveGeorge - numRemoveWall;
		System.out.println("CLAVES FILTRADAS: " + num);
		
		this.saveStates(stateList, "statesV1");
		
		// Ahora anadimos 
		return stateList;
	}

	/**
	 * Genera claves con el siguiente patron: Orientacion, proximidad_molesto, posicion_molesto, proximidad_enemigo, posicion_enemigo
	 * @return Devuelve la lista de claves
	 */
	public ArrayList<String> generatorV2() {
		ArrayList<String> stateList = new ArrayList<>();
		ArrayList<String> listElements = new ArrayList<>();
		ArrayList<String> listProximity = new ArrayList<>();
		String aux;
		char auxChar;
		int num = 0, numRemoveGeorge = 0, numRemoveWall = 0, hashLength = 0;
		
		/*
		 * Disposicion de caracteres
		 * 1.- Orientacion -> coord[]
		 * 2.- Cercania del molesto mas cercano -> proximity[]
		 * 3.- Posicion del moslesto respecto al jugador -> coord[]
		 * 4.- Cercania del enemigo -> proximity[]
		 * 5.- Posicion del enemigo respecto al jugador -> coord[]
		 * Resto.- Combinaciones de elementos en el mapa
		 */
		
		// Primero creamos las combinaciones de coordenadas y proximidad
		for (int i = 0; i < Constantes.coord.length; i++) {
			for (int j = 0; j < Constantes.proximity.length; j++) {
				for (int k = 0; k < Constantes.coord.length; k++) {
					for (int l = 0; l < Constantes.proximity.length; l++) {
						for (int m = 0; m < Constantes.coord.length; m++) {
							aux = Constantes.coord[i] + Constantes.proximity[j] + Constantes.coord[k] + Constantes.proximity[l] 
									+ Constantes.coord[m];
							listProximity.add(aux);
						}
					}
				}
			}
		}
		
		hashLength = listProximity.get(0).length();
		System.out.println("Longitud total de una clave: " + hashLength);

		num = listProximity.size();
		System.out.println("Claves totales creadas: " + num);
		
		// Estado por defecto, se llamará cuando se de una situacion imprevista
		listProximity.add(Constantes.defaultKey);
		num++;
		
		System.out.println("CLAVES FILTRADAS: " + num);
		
		this.saveStates(listProximity, "statesV2");
		
		// Ahora anadimos 
		return listProximity;
	}

	/**
	 * Guarda una lista de claves en un fichero
	 * @param stateList Lista de claves a guardar
	 * @param name Nombre con el que se desea guardar el fichero
	 */
	private void saveStates(ArrayList<String> stateList, String name) {
		String path = "src/uhu/Juego03/StateGenerator/" + name;
		try {
			FileWriter fichero;

			fichero = new FileWriter(path);
			String fila = "";
			for (int i = 0; i < stateList.size(); i++) {
				fila = stateList.get(i);
				fichero.write(fila + "\n");
			}
			fichero.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		StateGenerator sg = new StateGenerator();
		ArrayList<String> stateList = sg.generatorV2();
//		for (int i = 0; i < stateList.size(); i++) {
//			System.out.println(stateList.get(i) + " - State " + i);
//		}
		
		
	}
}
