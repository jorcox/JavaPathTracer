package core;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import math.AlgebraLineal;
import scene.Camara;

/**
 * Clase que encapsula la funcionalidad de transformaciones
 * tridimensionales de los objetos.
 */
public class Transformacion {
	
	private Matrix4d m;
	
	/**
	 * Crea una transformacion a partir de la matriz de transformacion m.
	 */
	private Transformacion(Matrix4d m) {
		this.m = m;
	}
	
	/**
	 * Devuelve una transformacion de las coordenadas de la
	 * camara a las coordenadas del mundo.
	 */
	public static Transformacion getMatrizCamaraMundo(Camara cam) {
		Matrix4d m = new Matrix4d(cam.getU().x,cam.getU().y,cam.getU().z,0,
								cam.getV().x,cam.getV().y,cam.getV().z,0,
								cam.getW().x,cam.getW().y,cam.getW().z,0,
								cam.getE().x,cam.getE().y,cam.getE().z,1);
		return new Transformacion(m);
	}
	
	/**
	 * Devuelve una transformacion de traslacion de un objeto a unas coordenadas
	 * x, y, z.
	 */
	public static Transformacion getMatrizTraslacion(double x, double y, double z) {
		Matrix4d m = new Matrix4d(1, 0, 0, 0,
								0, 1, 0, 0,
								0, 0, 1, 0,
								x, y, z, 1);
		return new Transformacion(m);
	}
	
	/**
	 * Devuelve una transformacion de cambio de escala en las dimensiones x, y, z.
	 */
	public static Transformacion getMatrizEscala(double x, double y, double z) {
		Matrix4d m = new Matrix4d(x, 0, 0, 0,
								0, y, 0, 0,
								0, 0, z, 0,
								0, 0, 0, 1);
		return new Transformacion(m);
	}
	
	/**
	 * Aplica la matriz de transformacion a un punto p.
	 */
	public Point3d transformar(Point3d p) {
		Point3d res = AlgebraLineal.multiplicar(p, m);
		return res;
	}
	
	/**
	 * Aplica la matriz de transformacion a un vector v.
	 */
	public Vector3d transformar(Vector3d v) {
		Point3d res = AlgebraLineal.multiplicar(new Point3d(v), m);
		return new Vector3d(res);
	}
	
	/**
	 * Devuelve una transformacion de giro en las dimensiones i, j, k 
	 * (indicadas en grados).
	 */
	public static Transformacion getMatrizGiro(double i,double j,double k) {
		/* Transforma los grados en radianes */
		double iRadianes = (i * Math.PI) / 180;
		double jRadianes = (j * Math.PI) / 180;
		double kRadianes = (k * Math.PI) / 180;
		
		Matrix4d m = new Matrix4d(Math.cos(jRadianes)+Math.cos(kRadianes), Math.sin(kRadianes), -Math.sin(jRadianes), 0,
				-Math.sin(kRadianes), Math.cos(iRadianes)+Math.cos(kRadianes), Math.sin(iRadianes), 0,
				Math.sin(jRadianes), -Math.sin(jRadianes), Math.cos(iRadianes)+Math.cos(jRadianes), 0,
				0, 0, 0, 1);
		return new Transformacion(m);
	}

	/**
	 * Devuelve una transformacion de giro en la dimension i
	 * (indicada en grados).
	 */
	public static Transformacion getMatrizGiroX(double i) {
		double iRadianes =  (i * Math.PI) / 180;
		Matrix4d m = new Matrix4d(1, 0, 0, 0,
				0, Math.cos(iRadianes), Math.sin(iRadianes), 0,
				0, -Math.sin(iRadianes), Math.cos(iRadianes), 0,
				0, 0, 0, 1);
		return new Transformacion(m);
	}
	
	/**
	 * Devuelve una transformacion de giro en la dimension j 
	 * (indicada en grados).
	 */
	public static Transformacion getMatrizGiroY(double j) {
		double jRadianes =  (j * Math.PI) / 180;
		Matrix4d m = new Matrix4d(Math.cos(jRadianes), 0, -Math.sin(jRadianes), 0,
				0, 1, 0, 0,
				Math.sin(jRadianes), 0, Math.cos(jRadianes), 0,
				0, 0, 0, 1);
		return new Transformacion(m);
	}
	
	/**
	 * Devuelve una transformacion de giro en la dimension k
	 * (indicada en grados).
	 */
	public static Transformacion getMatrizGiroZ(double k) {
		double kRadianes =  (k * Math.PI) / 180;
		Matrix4d m = new Matrix4d(Math.cos(kRadianes), Math.sin(kRadianes), 0, 0,
				-Math.sin(kRadianes), Math.cos(kRadianes), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
		return new Transformacion(m);
	}
	
}
