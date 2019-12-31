public class NBody {
	public static double readRadius(String str) {
		In in = new In(str);
		double count = in.readInt();
		double radius = in.readDouble();
		return radius;
	}

	public static Planet[] readPlanets(String str) {
		In in = new In(str);
		int count = in.readInt();
		double radius = in.readDouble();
		Planet[] planets = new Planet[count];
		for (int i = 0; i < count; i++) {
			planets[i] = new Planet(in.readDouble(), in.readDouble(), in.readDouble(),
									 in.readDouble(), in.readDouble(), in.readString());
		}
		return planets;
	}

	public static void main(String[] args) {
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]);
		String filename = args[2];

		double radius = readRadius(filename);
		Planet[] planets = readPlanets(filename);

		double t = 0;
		while(t < T) {
			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];
			for (int i = 0; i < planets.length; i++) {
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);
				planets[i].update(dt, xForces[i], yForces[i] );
			}
			String imageToDraw = "./images/starfield.jpg";
			StdDraw.setScale(-radius, radius);
			StdDraw.enableDoubleBuffering();
			StdDraw.clear();
			StdDraw.picture(0, 75, imageToDraw);
			for (int i = 0; i < planets.length;i++) {
				planets[i].draw();
			}
			StdDraw.show();
			StdDraw.pause(10);
			t += dt;
		}

		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
		    StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
		                  planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
		                  planets[i].yyVel, planets[i].mass, planets[i].imgFileName);   
		}
	}
}