package scene;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class Camara {
	private Point3d e;
	private Vector3d g;
	private Vector3d up = new Vector3d(0,1,0);
	private Vector3d u;
	private Vector3d v;
	private Vector3d w;
	

	public Camara(Point3d e ,Vector3d g) {
		this.e = e;
		this.g = g;
		Vector3d aux = new Vector3d(g);
		aux.negate();
		aux.normalize();
		w = new Vector3d(aux);
		aux.cross(up, w);
		aux.normalize();
		u = new Vector3d(aux);
		aux.cross(w, u);
		v = new Vector3d(aux);
	}


	public Point3d getE() {
		return e;
	}
	
	public Vector3d getG() {
		return g;
	}

	public Vector3d getU() {
		return u;
	}

	public Vector3d getV() {
		return v;
	}

	public Vector3d getW() {
		return w;
	}	
	
}
