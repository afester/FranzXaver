package afester.javafx.examples.shapes;

import javafx.geometry.Point2D;

public class VectorAngle {
	public static void main(String[] args) {
		final Point2D vec1 = new Point2D(1, 1);
		final Point2D vec2 = new Point2D(-1, 1);
		final Point2D vec3 = new Point2D(-1, -1);
		final Point2D vec4 = new Point2D(1, -1);
		final Point2D unitVec = new Point2D(-4, 2);

		System.err.printf("%s (%s)\n", vec1.angle(unitVec), vec1.getY());
		System.err.printf("%s (%s)\n", vec2.angle(unitVec), vec2.getY());
		System.err.printf("%s (%s)\n", vec3.angle(unitVec), vec3.getY());
		System.err.printf("%s (%s)\n", vec4.angle(unitVec), vec4.getY());
		System.err.printf("%s (%s)\n", unitVec.angle(vec1), vec1.getY());
		System.err.printf("%s (%s)\n", unitVec.angle(vec2), vec2.getY());
		System.err.printf("%s (%s)\n", unitVec.angle(vec3), vec3.getY());
		System.err.printf("%s (%s)\n", unitVec.angle(vec4), vec4.getY());
		
		System.err.printf("%s\n", Math.acos(-1.0));
		System.err.printf("%s\n", Math.acos(-0.8));
		System.err.printf("%s\n", Math.acos(-0.5));
		System.err.printf("%s\n", Math.acos(-0.2));
		System.err.printf("%s\n", Math.acos(0));
		System.err.printf("%s\n", Math.acos(0.2));
		System.err.printf("%s\n", Math.acos(0.5));
		System.err.printf("%s\n", Math.acos(0.8));
		System.err.printf("%s\n", Math.acos(1.0));

		System.err.println();
		System.err.printf("%s\n", Math.cos(Math.PI/2.0));
		System.err.printf("%s\n", Math.cos(Math.PI/2.0*3.0));
	}
}
