package scene;

import java.util.ArrayList;

import objects.Objeto;

public class DatosEscena {
	
	private Camara camara;
	private ArrayList<Luz> luces;
	private Pantalla pantalla;
	private ArrayList<Objeto> objetos;
	private double iAmbiental;
	
	public DatosEscena(Camara cam, ArrayList<Luz> luces, Pantalla p, ArrayList<Objeto> obj, double iAmb) {
		camara = cam;
		this.luces = luces;
		pantalla = p;
		objetos = obj;
		iAmbiental = iAmb;
	}

	public ArrayList<Objeto> getObjetos() {
		return objetos;
	}

	public double getAmbiental() {
		return iAmbiental;
	}

	public Camara getCamara() {
		return camara;
	}

	public ArrayList<Luz> getLuces() {
		return luces;
	}

	public Pantalla getPantalla() {
		return pantalla;
	}
}
