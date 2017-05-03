package objects;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import core.Color;
import scene.Rayo;

/**
 * Clase que define al plano.
 */
public class Plano extends Objeto{
	
	private Point3d p;
	
	/**
	 * Metodo constructor a partir de un punto del plano, la normal y propiedades
	 * del objeto.
	 */
	public Plano (Point3d p, Vector3d n, Color kd, double reflex, double iRefrac, double cRefrac, Color emision) {
		super(kd, n, reflex, iRefrac, cRefrac, emision);
		this.p = p;
		super.setN(n);
	}

	/**
	 * Calcula la interseccion entre el plano y el rayo r. Si esta no
	 * existe, devuelve null. Solo se devuelven intersecciones con la cara
	 * delantera de un plano.
	 */
	public Point3d interseccion(Rayo r) {
		Vector3d d = r.getD();
		Point3d p = new Point3d(this.p);
		double inf = d.dot(super.getN(p));
		if (inf < 0){
			/*
			 * La normal del plano y el rayo van en sentidos inveros, entonces 
			 * intersectan
			 */
			p.sub(r.getP0());
			Vector3d vec = new Vector3d(p);
			double sup = vec.dot(super.getN(p));
			return r.getPunto(sup/inf);
		} else {
			/*
			 * El rayo no intersecta, se devuelve null ( inf == 0)
			 * o 
			 * El plano esta de espaldas al ojo, suponiendo que solo
			 * se ver por una cara, entonces este no se ve (inf > 0)
			 */
			return null;
		}		
	}
}
