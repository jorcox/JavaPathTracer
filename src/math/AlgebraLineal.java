package math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class AlgebraLineal {
	
	/**
	 * Dado un punto P y una matriz M, devuelve el punto P*M
	 */
	public static Point3d multiplicar(Point3d p, Matrix4d m) {
		double x = (p.x * m.m00) + (p.y * m.m10) + p.z * m.m20 + m.m30;
		double y = (p.x * m.m01) + (p.y * m.m11) + p.z * m.m21 + m.m31;
		double z = (p.x * m.m02) + (p.y * m.m12) + p.z * m.m22 + m.m32;
		double w = (p.x * m.m03) + (p.y * m.m13) + p.z * m.m23 + m.m33;
		if (w!=1) {
			return new Point3d(x/w, y/w, z/w);
		} else return new Point3d(x, y, z);
	}
	
	/**
	 * Dado un vector P y una matriz M, devuelve el punto P*M
	 */
	public static Vector3d multiplicar(Vector3d p, Matrix3d m) {
		double x = (p.x * m.m00) + (p.y * m.m01) + p.z * m.m02;
		double y = (p.x * m.m10) + (p.y * m.m11) + p.z * m.m12;
		double z = (p.x * m.m20) + (p.y * m.m21) + p.z * m.m22;
		return new Vector3d(x, y, z);
	}
}
