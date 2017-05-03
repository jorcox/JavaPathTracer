package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.vecmath.Point3d;
import objects.Triangulo;
import scene.Camara;

public class ImportadorObj {

	public static ArrayList<Triangulo> leerFigura(String[] orden, Camara cam) throws FicheroDatosException {
		ArrayList<Triangulo> caras = new ArrayList<Triangulo>();
		ArrayList<Point3d> vertices = new ArrayList<Point3d>();
		vertices.add(null);
		double px = 0; double py = 0; double pz = 0;
		int cR = 0; int cG = 0; int cB = 0;
		int eR = 0; int eG = 0; int eB = 0;
		int rX = 0; int rY = 0; int rZ = 0;
		double escala = 1;
		double iRefl = 0;
		for (int i=2; i<orden.length; i++) {
			String[] partes = orden[i].split(":");
			String clave = partes[0];
			String valor = partes[1];
			switch(clave) {
			case "px":
				px = Double.parseDouble(valor);
				break;
			case "py":
				py = Double.parseDouble(valor);
				break;
			case "pz":
				pz = Double.parseDouble(valor);
				break;
			case "cR":
				cR = Integer.parseInt(valor);
				break;
			case "cG":
				cG = Integer.parseInt(valor);
				break;
			case "cB":
				cB = Integer.parseInt(valor);
				break;
			case "iRefl":
				iRefl = Double.parseDouble(valor);
				break;			
			case "eR":
				eR = Integer.parseInt(valor);
				break;
			case "eG":
				eG = Integer.parseInt(valor);
				break;
			case "eB":
				eB = Integer.parseInt(valor);
				break;
			case "rX":
				rX = Integer.parseInt(valor);
				break;
			case "rY":
				rY = Integer.parseInt(valor);
				break;
			case "rZ":
				rZ = Integer.parseInt(valor);
				break;
			case "escala":
				escala = Double.parseDouble(valor);
				break;
			default:
				throw new FicheroDatosException("Error de fichero");
			}
		}
		try {
			Scanner obj = new Scanner(new File(orden[1]));
			while (obj.hasNextLine()) {
				String v = obj.next();
				switch (v) {
				// vertices
				case "v":
					vertices.add(new Point3d(Float.parseFloat(obj.next()), Float.parseFloat(obj.next()),
							Float.parseFloat(obj.next())));
					break;
				// face
				case "f":
					int v1 = leerV(obj.next());
					int v2 = leerV(obj.next());
					int v3 = leerV(obj.next());
					Point3d p1 = new Point3d(vertices.get(v1));
					Point3d p2 = new Point3d(vertices.get(v2));
					Point3d p3 = new Point3d(vertices.get(v3));
					
					Transformacion aumento = Transformacion.getMatrizEscala(escala, escala, escala);
					Transformacion lejos = Transformacion.getMatrizTraslacion(px, py, pz);
					Transformacion camaraMundo = Transformacion.getMatrizCamaraMundo(cam);
					Transformacion giroX = Transformacion.getMatrizGiroX(rX);					
					Transformacion giroY = Transformacion.getMatrizGiroY(rY);
					Transformacion giroZ = Transformacion.getMatrizGiroZ(rZ);
					p1 = giroX.transformar(p1);
					p2 = giroX.transformar(p2);
					p3 = giroX.transformar(p3);	
					p1 = giroY.transformar(p1);
					p2 = giroY.transformar(p2);
					p3 = giroY.transformar(p3);
					p1 = giroZ.transformar(p1);
					p2 = giroZ.transformar(p2);
					p3 = giroZ.transformar(p3);					
					p1 = aumento.transformar(p1);
					p2 = aumento.transformar(p2);
					p3 = aumento.transformar(p3);
					p1 = lejos.transformar(p1);
					p2 = lejos.transformar(p2);
					p3 = lejos.transformar(p3);

					p1 = camaraMundo.transformar(p1);
					p2 = camaraMundo.transformar(p2);
					p3 = camaraMundo.transformar(p3);
					
					// Aqu� puedo cambiar las propiedades de un objeto complejo
					Triangulo t = new Triangulo(p1,p2,p3,new Color(cR,cG,cB),iRefl,1,2.417,new Color(eR,eG,eB));
					caras.add(t);
					break;
				}
				try {
					obj.nextLine();
				} catch (Exception e) {
				}
			}
			obj.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return caras;
	}

	private static int leerV(String v1) {
		String lv1[] = v1.split("/");

		return Integer.parseInt(lv1[0]);

	}
}