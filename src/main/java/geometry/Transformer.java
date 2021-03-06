package geometry;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;


/**
 *
 * @author antonin
 *
 * Class for superposing sets of points.
 *
 */
public class Transformer {

	private SuperPositionQCP qcp = new SuperPositionQCP(false);
	private Point3d[] a;
	private Point3d[] b;

	public void set(Point3d[] a, Point3d[] b) {
		this.a = a;
		this.b = b;
		qcp.set(a, b);
		qcp.getRmsd();
	}

	public double getRmsd() {
		double r = qcp.getRmsd();
		return r;
	}

	public double getSumOfDifferences() {
		Matrix4d m = getMatrix();
		for (Point3d p : b) {
			m.transform(p);
		}
		Point3d[] c = transform();
		double sum = 0;
		for (int i = 0; i < c.length; i++) {
			double d = a[i].distance(b[i]);
			sum += d;
			//sum += 1 / (1 + d * d);
		}
		sum = c.length;
		return sum;
	}

	public double getMaxDifferences() {
		Matrix4d m = getMatrix();
		for (Point3d p : b) {
			m.transform(p);
		}
		Point3d[] c = transform();
		double max = 0;
		for (int i = 0; i < c.length; i++) {
			double d = a[i].distance(b[i]);
			if (max < d) {
				max = d;
			}
		}
		return max;
	}

	public Point3d[] transform() {
		return qcp.getTransformedCoordinates();
	}

	public Matrix4d getMatrix() {
		Matrix4d m = qcp.getTransformationMatrix();
		return m;
	}

	public Matrix3d getRotationMatrix() {
		return qcp.calcRotationMatrix();
	}

	public static void main(String[] args) {
		double angle1 = -0.2;
		double angle2 = 0.3;
		double[][] ac = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
		double[][] bc = {{Math.cos(angle1) + 10, Math.sin(angle1), 0},
		{-Math.sin(angle1) + 10, Math.cos(angle1), 0}, {0 + 10, 0, 1}};
		double[][] cc = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
		double[][] dc = {{Math.cos(angle2), Math.sin(angle2), 0}, {-Math.sin(angle2), Math.cos(angle2), 0},
		{0, 0, 1}};
		Transformer ta = new Transformer();
		ta.set(PointConversion.getPoints3d(ac), PointConversion.getPoints3d(bc));
		Transformer tb = new Transformer();
		tb.set(PointConversion.getPoints3d(cc), PointConversion.getPoints3d(dc));

		double a = ta.getRmsd();
		Matrix3d ma = ta.getRotationMatrix();
		double b = tb.getRmsd();
		Matrix3d mb = tb.getRotationMatrix();
		System.out.println(a + " " + b);
		// System.out.println(compareRotations(ma, mb));
	}
}
