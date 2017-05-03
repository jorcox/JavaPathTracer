package objects;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import core.Color;
import scene.Rayo;

/**
 * Objeto esfera de la escena, definido por su radio y la posicion
 * de su centro.
 */
public class Esfera extends Objeto {
	
	double radio;
	Point3d centro;
	
	/**
	 * Constructor de la esfera.
	 */
	public Esfera(double radio, Point3d centro, Color kd, double reflex, 
			double iRefrac, double cRefrac, Color emision) {
		super(kd, reflex, iRefrac, cRefrac, true, emision);
		this.radio = radio;
		this.centro = centro;
	}

	public double getRadio() {
		return radio;
	}
	
	public void setRadio(double radio) {
		this.radio = radio;
	}
	
	public Point3d getCentro() {
		return centro;
	}
	
	public void setCentro(Point3d centro) {
		this.centro = centro;
	}

	/**
	 * Calcula la interseccion entre la esfera y el rayo r. Si no hay interseccion,
	 * devuelve null. Si existe 1 interseccion, devuelve dicho punto. Si hay 2
	 * intersecciones, devuelve el punto mas cercano (el que se ve).
	 */
	public Point3d interseccion(Rayo r) {				
		/* Calcula a, b y c de la ecuacion de interseccion */
		Vector3d r1 = r.getD();
		double a = r1.dot(r.getD());
		a = Math.pow(r1.x,2)+Math.pow(r1.y,2)+Math.pow(r1.z,2);
		Point3d p0 = r.getP0();
		Vector3d ca = new Vector3d();
		ca.sub(p0, centro);
		double b = r1.dot(ca);
		double c = ca.dot(ca);
		c = c - Math.pow(radio, 2);
		
		/* Calcula el discriminante para decidir numero de soluciones */
		double d = Math.pow(b, 2) - a*c;
		if (d<0) {
			/* No hay interseccion */
			return null;
		} else if (d==0) {
			/* Solo hay una interseccion (el rayo es tangente a la esfera) */
			double lambda = (-2*b + Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			return r.getPunto(lambda);
		} else {
			/* Hay dos intersecciones (el rayo corta a la esfera) */
			double lambda1 = (-2*b + Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			double lambda2 = (-2*b - Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			
			/* Comprueba que interseccion es visible */
			if (lambda1<0 && lambda2<0) {
				return null;
			} else if (lambda1>0 && lambda2<0) {
				return r.getPunto(lambda1);
			} else if (lambda1>lambda2 && lambda2>0) {
				return r.getPunto(lambda2);
			} else if (lambda1<0 && lambda2>0) {
				return r.getPunto(lambda2);
			} else {
				return r.getPunto(lambda1);
			}
		}
 	}
	
	/**
	 * Calcula las intersecciones entre la esfera y el rayo r. Si no hay interseccion,
	 * devuelve null. En caso contrario, devuelve todas las existentes.
	 */
	public Point3d[] interseccionCompleja(Rayo r) {				
		/* Calcula a, b y c de la ecuacion de interseccion */
		Vector3d r1 = r.getD();
		double a = r1.dot(r.getD());
		a=Math.pow(r1.x,2)+Math.pow(r1.y,2)+Math.pow(r1.z,2);
		Point3d p0 = r.getP0();
		Vector3d ca = new Vector3d();
		ca.sub(p0, centro);
		double b = r1.dot(ca);
		double c = ca.dot(ca);
		c = c - Math.pow(radio, 2);
		
		/* Calcula el discriminante para decidir numero de soluciones */
		double d = Math.pow(b, 2) - a*c;
		if (d<0) {
			/* No hay interseccion */
			return null;
		} else if (d==0) {
			/* Solo hay una interseccion (el rayo es tangente a la esfera) */
			double lambda = (-2*b + Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			return new Point3d[] {r.getPunto(lambda)};
		} else {
			/* Hay dos intersecciones (el rayo corta a la esfera) */
			double lambda1 = (-2*b + Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			double lambda2 = (-2*b - Math.sqrt(4*Math.pow(b, 2) - 4*a*c)) / (double) (2*a);
			
			/* Comprueba cuantas son visibles */
			if (lambda1<0 && lambda2<0) {
				return null;
			} else if (lambda1>0 && lambda2<0) {
				return new Point3d[] {r.getPunto(lambda1)};
			} else if (lambda1>lambda2 && lambda2>0) {
				return new Point3d[] {r.getPunto(lambda2),r.getPunto(lambda1)};
			} else if (lambda1<0 && lambda2>0) {
				return new Point3d[] {r.getPunto(lambda2)};
			} else {
				return new Point3d[] {r.getPunto(lambda1)};
			}
		}
 	}
	
	/**
	 * Devuelve la normal de la esfera en un punto determinado.
	 */
	public Vector3d getN(Point3d punto) {
		Vector3d n = new Vector3d();
		n.sub(punto, centro);
		return n;
	}

}
