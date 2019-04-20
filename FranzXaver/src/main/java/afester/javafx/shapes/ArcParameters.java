package afester.javafx.shapes;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ArcParameters {

	private Point2D center;
	private double radius;
	private double startAngle;
	private double angle;
	private Color color;

	public ArcParameters(Point2D center, 
			double radius, double startAngle, double angle, Color col) {
		this.center = center;
		this.radius = radius;
		this.startAngle = startAngle;
		this.angle = angle;
		this.color = col;
	}

	public Point2D getCenter() {
		return center;
	}

	public double getRadius() {
		return radius;
	}

	public double getLength() {
		return angle;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return String.format("ArcParameters[center=%s, radius=%s, startAngle=%s, angle=%s]", center, radius, startAngle, angle);
	}
}
