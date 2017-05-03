package scene;

import javax.vecmath.Point3d;

public class Luz {
	private Point3d punto;
	private double brillo;
	
	public Luz(Point3d punto, double brillo) {
		this.punto = punto;
		this.brillo = brillo;
	}

	public Point3d getPunto() {
		return punto;
	}

	public double getBrillo() {
		return brillo;
	}
	
	
}
