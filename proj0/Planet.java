public class Planet {
	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;

	private static final double G = 6.67e-11;

	public Planet(double xxPos, double yyPos, double xxVel,
					double yyVel, double mass, String imgFileName) {
		this.xxPos = xxPos;
		this.yyPos = yyPos;
		this.xxVel = xxVel;
		this.yyVel = yyVel;
		this.mass = mass;
		this.imgFileName = imgFileName;
	}

	public Planet(Planet b) {
		xxPos = b.xxPos;
		yyPos = b.yyPos;
		xxVel = b.xxVel;
		yyVel = b.yyVel;
		mass = b.mass;
		imgFileName = b.imgFileName;
	}

	public double calcDistance(Planet p) {
		double dx = p.xxPos - this.xxPos;
		double dy = p.yyPos - this.yyPos;
		double r2 = Math.pow(dx, 2) + Math.pow(dy, 2);
		return Math.sqrt(r2);
	}

	public double calcForceExertedBy(Planet p) {
		return G*p.mass*this.mass/Math.pow(this.calcDistance(p), 2);
	}

	public double calcForceExertedByX(Planet p) {
		double dx = p.xxPos - this.xxPos;
		return this.calcForceExertedBy(p) * dx / this.calcDistance(p);
	}

	public double calcForceExertedByY(Planet p) {
		double dy = p.yyPos - this.yyPos;
		return this.calcForceExertedBy(p) * dy / this.calcDistance(p);
	}

	public double calcNetForceExertedByX(Planet[] allPlanets) {
		double sumX = 0;
		for (Planet p : allPlanets ) {
			if (!this.equals(p)) {
				sumX = sumX + this.calcForceExertedByX(p);
			}
		}
		return sumX;
	}

	public double calcNetForceExertedByY(Planet[] allPlanets) {
		double sumY = 0;
		for (Planet p : allPlanets ) {
			if (!this.equals(p)) {
				sumY = sumY + this.calcForceExertedByY(p);
			}
		}
		return sumY;
	}

	public void update(double dt, double fX, double fY) {
		double ax = fX / this.mass;
		double ay = fY / this.mass;
		xxVel = xxVel + dt * ax;
		yyVel = yyVel + dt * ay;
		xxPos = xxPos + dt * xxVel;
		yyPos = yyPos + dt * yyVel;
	}

	public void draw() {
		StdDraw.picture(xxPos, yyPos, "./images/" + imgFileName);
	}
}