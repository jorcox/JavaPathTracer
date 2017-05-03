package scene;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Clase que representa al rayo de luz.
 */
public class Rayo {
	
	Point3d p0;
	Point3d p1;
	Vector3d d;
	
	/**
	 * Crea un rayo a partir de la recta que surge de unir 2 puntos.
	 */
	public Rayo (Point3d p0, Point3d p1) {
		this.p0 = new Point3d(p0);
		this.p1 = new Point3d(p1);
		d = new Vector3d();
		d.sub(p1, p0);
	}
	
	/**
	 * Crea un rayo a partir de un punto y un vector direccion.
	 */
	public Rayo(Vector3d vector, Point3d punto) {
		d = new Vector3d(vector);
		p0 = new Point3d(punto);
	}

	public Point3d getP0() {
		return p0;
	}

	public Point3d getP1() {
		return p1;
	}

	public Vector3d getD() {
		return d;
	}

	/**
	 * Dada la ecuacion parametrica del rayo, devuelve el punto 
	 * para un parametro lambda determinado.
	 */
	public Point3d getPunto(double lambda) {		
		Vector3d D = new Vector3d(d);
		D.scale(lambda, D);
		Point3d p = new Point3d(p0);
		p.add(D);
		return p;
	}
	
	/**
	 * Devuelve el angulo en grados entre el rayo y un vector v.
	 */
	public double getAngulo(Vector3d v) {
		Vector3d a = new Vector3d(d);
		double radianes = a.angle(v);
		return Math.toDegrees(radianes);
	}

	public void setD(Vector3d d) {
		this.d = d;
	}	
	
}
