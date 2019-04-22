package afester.javafx.shapes;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class ArcFactory {

	private final static Point2D UNIT_VECTOR = new Point2D(1, 0);

	/**
	 * Creates the parameters (center, start angle, radius) for an Arc between two given
	 * points and the angle of the Arc.
	 * 
	 * @param p1	The start point for the Arc
	 * @param p2    The end point of the Arc 
	 * @param cAngle The angle or length of the Arc
	 * @return An ArcParameters object which contains all the required parameters
	 *         to setup an Arc which connects the given two points and which
	 *         spans the given angle.
	 */
	public static ArcParameters arcFromPointsAndAnglePos(Point2D p1, Point2D p2, double cAngle, Color col) {
		System.err.printf("IN: %s, %s, %s, %s\n", p1, p2, cAngle, col);

		// calculate the direction vector between the two points
		final Point2D dirVec = p2.subtract(p1);
		double beta = dirVec.angle(UNIT_VECTOR);
		if (dirVec.getY() > 0) {
			beta = 360 - beta;
		}

		// rotate the direction vector
		final double alpha = -Math.toRadians(cAngle / 2 - 90);

		// calculate the radius
		final double r = Math.abs(dirVec.magnitude() / Math.cos(alpha) / 2);

		final Point2D dirVec2 = new Point2D(Math.cos(alpha) * dirVec.getX() - Math.sin(alpha) * dirVec.getY(),
				                            Math.sin(alpha) * dirVec.getX() + Math.cos(alpha) * dirVec.getY());

		final Point2D dirVec2normal = dirVec2.normalize();

		final Point2D p5 = dirVec2normal.multiply(r);

		// set the new end point
		final Point2D centerPoint = p1.add(p5);

		final double startAngle = 90 - cAngle / 2 + beta;

		ArcParameters result = new ArcParameters(centerPoint, r, startAngle, cAngle, col);
		System.err.println(result);
		return result;
	}

	public static ArcParameters arcFromPointsAndAngleNeg(Point2D p1, Point2D p2, double cAngle, Color col) {
		System.err.printf("IN: %s, %s, %s, %s\n", p1, p2, cAngle, col);

    	// calculate the direction vector between the two points
		final Point2D dirVec = p1.subtract(p2);
		double beta = dirVec.angle(UNIT_VECTOR);
		if (dirVec.getY() > 0) {
			beta = 360 - beta;
		}

		// rotate the direction vector
        final double alpha = -Math.toRadians(-cAngle/2 - 90);

        // calculate the radius
        final double r = Math.abs(dirVec.magnitude() / Math.cos(alpha) / 2);

        final Point2D dirVec2 = new Point2D(Math.cos(alpha) * dirVec.getX() - Math.sin(alpha) * dirVec.getY(), 
                                            Math.sin(alpha) * dirVec.getX() + Math.cos(alpha) * dirVec.getY());

        final Point2D dirVec2normal = dirVec2.normalize();

        final Point2D p5 = dirVec2normal.multiply(r);

        // set the new end point
        final Point2D centerPoint = p2.add(p5);

        final double startAngle = 90 - cAngle/2 + beta;

        ArcParameters result = new ArcParameters(centerPoint, r, startAngle, cAngle, col);
        System.err.println(result);
        return result;
	}

	public static ArcParameters arcFromPointsAndAngle(Point2D p1, Point2D p2, double cAngle, Color col) {
		if (cAngle < 0) {
			return arcFromPointsAndAngleNeg(p1, p2, cAngle, col);
		} else {
			return arcFromPointsAndAnglePos(p1, p2, cAngle, col);
		}
	}
}
