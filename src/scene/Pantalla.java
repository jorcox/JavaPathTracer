package scene;

import javax.vecmath.Point3d;

import core.Transformacion;

public class Pantalla {
	private double l;
	private double t;
	private double f;
	private int nC;
	private int nR;
	private double varU;
	private double varV;
	private Point3d[][] coordCamara;
	private Point3d[][] coordMundo;

	public Pantalla(double l, double t, double f, int nC, int nR) {
		this.l = l;
		this.t = t;
		coordCamara = new Point3d[nC][nR];
		coordMundo = new Point3d[nC][nR];
		this.f = f;
		this.nC = nC;
		this.nR = nR;
		double L = 0;
		double R = 0;
		double T = 0;
		double B = 0;
		L = l/2;
		R = L;
		T = t/2;
		B = T;
		
		varU = (R-(-L))/(double)(nC-1);
		varV = (T-(-B))/(double)(nR-1);
	}
	
	public void calcularCoordenadasCamaraYMundo(Camara cam){
		int mColumnas = nC/2;
		int mFilas = nR/2;
		for(int i = 0; i < nC; i++){
			for(int j = 0; j < nR; j++){
				Point3d punto = new Point3d((i-mColumnas)*varU, (-j+mFilas)*varV, -f);
				coordCamara[i][j] = punto;
				Transformacion cameraToWorld = Transformacion.getMatrizCamaraMundo(cam);
				coordMundo[i][j] = cameraToWorld.transformar(punto);
			}
		}
	}
	
	public double getL() {
		return l;
	}

	public double getT() {
		return t;
	}

	public double getF() {
		return f;
	}

	public int getnC() {
		return nC;
	}

	public int getnR() {
		return nR;
	}
	
	public double getVarU() {
		return varU;
	}
	
	public double getVarV() {
		return varV;
	}

	public Point3d[][] getCoordCamara() {
		return coordCamara;
	}

	public Point3d[][] getCoordMundo() {
		return coordMundo;
	}
	
	public Point3d getPuntoCoordCamara(int i, int j) {
		return coordCamara[i][j];
	}
	
	public Point3d getPuntoCoordMundo(int i, int j) {
		return coordMundo[i][j];
	}
}
