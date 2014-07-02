package utilities;

public class Border {
	public int radiusSq;
	public int definiteSq;
	public double centerX;
	public double centerZ;
	public double radius;

	public Border(double X, double Z, double Radius) {
		centerX = X;
		centerZ = Z;
		radius = Radius;
		radiusSq = (int) (radius * radius);
		definiteSq = (int) Math.sqrt(0.5D * radiusSq);
	}

	public String toString() {
		return "X: " + centerX + " Z: " + centerZ + " Radius: "
				+ radius;
	}
}