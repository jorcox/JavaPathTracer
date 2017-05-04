package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import math.AlgebraLineal;
import objects.Esfera;
import objects.Objeto;
import scene.Camara;
import scene.DatosEscena;
import scene.Luz;
import scene.Pantalla;
import scene.Rayo;

public class Trazador {

	private static Point3d origen = new Point3d(0, 0, 0);
	private static ArrayList<Objeto> objetos = new ArrayList<Objeto>();
	private static Camara camara = null;
	private static ArrayList<Luz>  luces = null;
	private static double iAmbiental = 0.0;
	private static final int NUM_ANTIALIASING = 1;
	private static final String NOMBRE_IMG = "imagen";
	private static final String FORMATO_IMG = "png";
	private static final int RAYOS_POR_PIXEL = 128;

	public static void main(String[] args) {
		Pantalla pantalla = null;
		Color[][] pixels = null;

		/* Procesar fichero y crear objetos */
		if (args.length >= 1) {
			try {
				DatosEscena datos = TrazadorUtils.cargarObjetos(args[0]);
				if (datos == null) {
					System.out.println("Error en el fichero de datos");
					System.exit(1);
				}
				objetos = datos.getObjetos();
				iAmbiental = datos.getAmbiental();
				pantalla = datos.getPantalla();
				camara = datos.getCamara();
				luces = datos.getLuces();
				pixels = new Color[pantalla.getnC()][pantalla.getnR()];
				pantalla.calcularCoordenadasCamaraYMundo(camara);
				
			} catch(Exception e) {
				System.out.println("Error en la entrada de datos");
				System.exit(1);
			}		
		} else {
			System.out.println("Error: no ha introducido un fichero de datos");
			System.exit(1);
		}

		long t1 = System.currentTimeMillis();
		/*
		 * Por cada pixel de la pantalla se lanza un rayo
		 */
		double varU = pantalla.getVarU();
		double varV = pantalla.getVarV();
		double porcentaje = 0.0;
		for (int i = 0; i < pantalla.getnC(); i++) {
			porcentaje = (i*100.0)/pantalla.getnC();			
			System.out.println((i*100.0)/pantalla.getnC() + "%");
			for (int j = 0; j < pantalla.getnR(); j++) {
				
				Point3d pixel = pantalla.getPuntoCoordMundo(i,j);
				Color[] colores = new Color[RAYOS_POR_PIXEL];
				Random random = new Random();
				
				/*
				 * Traza varios rayos en el pixel para el antialiasing.
				 */
				for (int k=0; k<NUM_ANTIALIASING; k++) {
					double offsetX = random.nextDouble()*varU - varU/2;
					double offsetY = random.nextDouble()*varV - varV/2;
					Point3d nuevo = new Point3d(pixel.x + offsetX, pixel.y + offsetY, pixel.z);
					Rayo rayo = new Rayo(camara.getE(), nuevo);
					// Bucle principal
					for (int l = 0; l < RAYOS_POR_PIXEL; l++) {
						colores[l] = trazarRayo(rayo, 0, null, null, null, false, new Color(0,0,0), new Color(255,255,255));						
					}
				}
				Color colorFinal = Color.promedio(colores);

				/*
				 * Asignamos el valor del color del pixel [i,j] en su posicion
				 * correspondiente
				 */
				pixels[i][j] = colorFinal;
			}
		}

		try {
			Imagen.crearImagen(pantalla, pixels, NOMBRE_IMG, FORMATO_IMG);
		} catch (IOException e) {
			System.out.println("Error al crear la imagen.");
		}		
		long t2 = System.currentTimeMillis();
		System.out.println("Tiempo empleado: " + (t2-t1) + " ms");
	}

	/**
	 * Metodo principal del trazador
	 * 
	 * @param rayoPrimario
	 * @param recursion
	 *            indica las veces que se ha llamado al metodo recursivamente
	 *            para un mismo pixel
	 * @param original
	 *            Indica el objeto desde el que parte el rayo para no tenerlo en
	 *            cuenta en el calculo de las colisiones
	 *
	 * @return el Color del pixel
	 *
	 */
	private static Color trazarRayo(Rayo rayoPrincipal, int recursion, Objeto objetoIgnorar, Objeto objetoActual, Objeto objetoIgnorarPrimera,
			boolean interno, Color luz, Color throughput) {
		rayoPrincipal.getD().normalize();
		
		ArrayList<Boolean> haceSombra = new ArrayList<Boolean>();
		/*
		 * Por cada objeto se calcula con cual intersecta primer ( i.e. el mas
		 * cercano)
		 */
		Objeto objetoCol = null;
		Point3d puntoColision = null;
		Point3d puntoColisionFinal = null;
		double distanciaMin = Double.MAX_VALUE;
		/*
		 * Bucle para comprobar con que objeto se colisiona
		 */
		for (int k = 0; k < objetos.size(); k++) {
			if (objetoIgnorar == null || !objetoIgnorar.equals(objetos.get(k))) {
				/*
				 * Si el objeto es complejo tendra una entrada y una salida
				 */
				if (objetos.get(k).esComplejo()) {
					Point3d[] puntos = objetos.get(k).interseccionCompleja(rayoPrincipal);
					try {
						puntoColision = puntos[0];
						/*
						 * Si es complejo pero interno ( el rayo esta actualmente dentro del objeto),
						 * puede haber una o dos colisiones, cogemos la mas lejana
						 */
						if (interno) {
							puntoColision = puntos[puntos.length - 1];
						}
					} catch (NullPointerException e) {
						puntoColision = null;
					}
				}
				/*
				 * Si el objeto no es complejo solo tendra una colision ( o
				 * ninguna )
				 */
				else {
					puntoColision = objetos.get(k).interseccion(rayoPrincipal);
				}
				/*
				 * Si ha habido colision se guarda la referencia
				 */
				if (puntoColision != null) {
					double distancia = puntoColision.distance(rayoPrincipal.getP0());
					if (distancia < distanciaMin) {
						distanciaMin = distancia;
						objetoCol = objetos.get(k);
						puntoColisionFinal = puntoColision;
					}
				}
			}
		}
		/*
		 * Caso en el que el rayo ha intersectado con algun objeto
		 */
		if (objetoCol != null) {	
			
			/*
			 * Se suma a la luz la emisión del objeto colisionado 
			 */
			luz = luz.suma(objetoCol.getEmision().aplicarIntensidad(throughput.getRed()/255.0, throughput.getGreen()/255.0, throughput.getBlue()/255.0));
			luz.normalizarColor();
			
			Color cl = new Color(luz);

			/*
			 * Ruleta rusa
			 */
			
			Random rand = new Random();
			
			
			double ep1 = rand.nextDouble();
			double ep2 = rand.nextDouble();
			double ep3 = rand.nextDouble();
			//ep1 = ep2+ep3;
			
			double theta;
			double phi;
			
			double alpha = 500;
			
			Vector3d wi;
			
			/*
			 * Cálculo de la componente difusa
			 */
			if(ep1 < objetoCol.getKd().media()/255.0) {

				theta = Math.acos(Math.sqrt(1-ep2));
				phi = 2*Math.PI*ep3;
				
				Vector3d u = new Vector3d(0,1,0);
				u.normalize();
				
				Vector3d n = objetoCol.getN(puntoColisionFinal);
				n.normalize();
				
				u.cross(n, u);
				u.normalize();
				
				Vector3d v = new Vector3d(u);
				v.cross(v, n);
				v.normalize();
				
				Vector3d wPrima = new Vector3d(Math.sin(theta)*Math.cos(phi), 
						Math.sin(theta)*Math.sin(phi), Math.cos(theta));
				Matrix3d T = new Matrix3d(u.x,v.x,n.x,
										  u.y,v.y,n.y,
										  u.z,v.z,n.z);
				
				wi = AlgebraLineal.multiplicar(wPrima, T);
				
				wi.normalize();
				
				if(objetoCol.getN(puntoColisionFinal).dot(wi) > 0){		
					throughput = throughput.aplicarIntensidad((objetoCol.getKd().getRed())/(objetoCol.getKd().media()),
							(objetoCol.getKd().getGreen())/(objetoCol.getKd().media()),
							(objetoCol.getKd().getBlue())/(objetoCol.getKd().media()));
					
					Rayo nuevoRayo = new Rayo(wi,puntoColisionFinal);
					
					Color nuevo;
					nuevo = trazarRayo(nuevoRayo, recursion+1, objetoCol, null, null, false, luz, throughput);				
					cl = cl.suma(nuevo);
				}			
			/*
			 * Cálculo de la componente especular
			 */
			} else if (ep1 >= objetoCol.getKd().media()/255.0 && ep1 < ((objetoCol.getIndiceReflexion()) +  objetoCol.getKd().media()/255.0)){
				double test = rand.nextDouble();
							
			    theta = Math.acos(Math.pow(ep2, 1/(alpha+1)));
			    phi = 2*Math.PI*ep3;
			     
			    Rayo rayoReflejado = new Rayo(
			            calcularReflejado(rayoPrincipal.getD(), objetoCol.getN(puntoColisionFinal)),
			            puntoColisionFinal);
			     
			    Vector3d u = new Vector3d(0,1,0);
			    u.normalize();
			    Vector3d n = rayoReflejado.getD();
			    n.normalize();
			    u.cross(n, u);
			    u.normalize();
			    Vector3d v = new Vector3d(u);
			    v.cross(v, n);
			    v.normalize();
			     

			    Vector3d wPrima = new Vector3d(Math.sin(theta)*Math.cos(phi), 
			            Math.sin(theta)*Math.sin(phi), Math.cos(theta));
			    Matrix3d T = new Matrix3d(u.x,v.x,n.x,
			                              u.y,v.y,n.y,
			                              u.z,v.z,n.z);
			     
			    wi = AlgebraLineal.multiplicar(wPrima, T);
			    wi.normalize();
			    
			    if(rand.nextDouble()<0.5){
				    if(objetoCol.getN(puntoColisionFinal).dot(wi) < 0)
						wi = rayoReflejado.getD();  
			    } else {
			    	wi = rayoReflejado.getD();  
			    }

			     
			    Color ks = objetoCol.getKs();             
			     
			    Vector3d normal = objetoCol.getN(puntoColisionFinal);
			    normal.normalize();
			    double cosPre = normal.dot(wi);
			    double cosTheeta = Math.abs(cosPre);
			    double sinTheeta = Math.sqrt(1 - Math.pow(cosTheeta, 2));
			    
				if(n.dot(wi)<0)
					wi.negate();
			     
			    double a = (ks.getBlue()*(alpha+2)*cosTheeta*sinTheeta);
			    double b = ((alpha+1)*ks.media()*Math.sin(theta));
			     
			    double c = a/b;
			     
			    throughput = throughput.aplicarIntensidad(c);
			     
			    Rayo nuevoRayo = new Rayo(wi,puntoColisionFinal);
			    Color nuevo = trazarRayo(nuevoRayo, recursion+1, objetoCol, null, null, false, luz, throughput);
			    cl = cl.suma(nuevo);    
			     
			} else {
			    return luz;
			}

			return cl;
		
		}
		/*
		 * Caso en el que el rayo no ha colisionado con nada
		 */
		else {
			return new Color(0, 0, 0);
		}
	}

	/**
	 * Calcula el rayo reflejado dados V y N
	 */
	private static Vector3d calcularReflejado(Vector3d v, Vector3d n) {
		// Código equivalente
		/*Vector3d V = new Vector3d(v);
		Vector3d N = new Vector3d(n);
		double aux = V.dot(N);
		N.scale(2 * aux);
		V.sub(N);
		return V;*/
		
		Vector3d V = new Vector3d(v);
		Vector3d N = new Vector3d(n);
		double aux = N.dot(V);
		N.scale(2 * aux);
		V = new Vector3d(N);
		V.sub(new Vector3d(v));
		if(n.dot(V)<0)
			V.negate();
		return V;
	}

	/**
	 * Calcula el rayo refractado dados I, N, el indice de refraccion de
	 * el objeto que desvia el rayo y el rayo en cuestion.
	 */
	private static Vector3d calcularRefractado(Vector3d i, Vector3d n, double indiceRefraccion, Rayo rayoPrincipal) {
		Vector3d N = new Vector3d(n);
		Vector3d I = new Vector3d(i);
		double nXi = N.dot(I);
		// nr*(N*I)-sqrt(1-nr^2*(1-(N*I)^2))
		double enRaiz = 1 - (Math.pow(indiceRefraccion, 2) * (1 - Math.pow(nXi, 2)));
		if (enRaiz >= 0) {
			double pO = (indiceRefraccion * nXi) - Math.sqrt(enRaiz);
			// ans*N
			N.scale(pO);
			// nr*I
			I.scale(indiceRefraccion);
			// ans*N-nr*I
			N.sub(I);
			return N;
		} else {
			return calcularReflejado(rayoPrincipal.getD(), N);
		}

	}

	private static Vector3d calcularReflejadoEspecular(Vector3d L, Vector3d n) {
		Vector3d nor = new Vector3d(n);
		double pO = nor.dot(L);
		nor.scale(2 * pO);
		nor.sub(L);
		nor.normalize();
		return nor;
	}
}