/**
 * 
 */
package org.moka.tools;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.AxisAngle4d;
import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Float64Vector;

/**
 * @author riki
 *
 */
public class GeomTools {
	
	public double[] setPlane(final double[] x, final double[] y, final double[] z)
	{
		/**
		 * Equation of a plane in a three-dimensional space (x,y,z) -> z = c + cx*x + cy*y
		 * 
		 * return 4 vector of	-cx*x -cy*y + z = c
		 */
		
		double c, cx, cy;

		final int N = x.length;
		if (N!=y.length || N!=z.length) {
			return null;
		}

		double sum_x  = 0;
		double sum_y  = 0;
		double sum_z  = 0;
		double sum_xx = 0;
		double sum_yy = 0;
		double sum_xy = 0;
		double sum_zx = 0;
		double sum_zy = 0;

		for (int i=0; i<N; i++) {
			final double xi = x[i];
			final double yi = y[i];
			final double zi = z[i];
			sum_x  += xi;
			sum_y  += yi;
			sum_z  += zi;
			sum_xx += xi*xi;
			sum_yy += yi*yi;
			sum_xy += xi*yi;
			sum_zx += zi*xi;
			sum_zy += zi*yi;
		}
		/*
		 *    ( sum_zx - sum_z*sum_x )  =  cx*(sum_xx - sum_x*sum_x) + cy*(sum_xy - sum_x*sum_y)
		 *    ( sum_zy - sum_z*sum_y )  =  cx*(sum_xy - sum_x*sum_y) + cy*(sum_yy - sum_y*sum_y)
		 */

		final double ZX = sum_zx - sum_z*sum_x/N;
		final double ZY = sum_zy - sum_z*sum_y/N;
		final double XX = sum_xx - sum_x*sum_x/N;
		final double XY = sum_xy - sum_x*sum_y/N;
		final double YY = sum_yy - sum_y*sum_y/N;
		final double den= (XY*XY - XX*YY);

		cy = (ZX*XY - ZY*XX) / den;
		cx = (ZY*XY - ZX*YY) / den;
		c  = (sum_z - (cx*sum_x + cy*sum_y)) / N;

		return new double[] {-cx,-cy,1,c};
	}

	//Altro modo
	public double[] setPlane(double[][] points){
		
		double[] x= new double[points.length];
		double[] y= new double[points.length];
		double[] z= new double[points.length];
		
		for (int i=0; i<points.length; i++) {
			x[i] = points[i][0];
			y[i] = points[i][1];
			z[i] = points[i][2];
		}
		
		return setPlane(x, y, z);	
	}
	
	public double[] setLine(final double x1, final double y1, final double x2, final double y2) {
        
		double slope, x0, y0;
		
		slope = (y2-y1)/(x2-x1);
        x0    = x2 - y2/slope;
        y0    = y2 - slope*x2;
        
        if (Double.isNaN(x0) && slope==0) {
            // Occurs for horizontal lines right on the x axis.
            x0 = Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(y0) && Double.isInfinite(slope)) {
            // Occurs for vertical lines right on the y axis.
            y0 = Double.POSITIVE_INFINITY;
        }
        
        return new double[] {slope, y0};
    }


    //---------------------------------------------
	//	ROTATE
	//---------------------------------------------

    public static double[] getRotateAboutAxis(double[] point, double[] rotAx, double[] center, double angle) {

        double[] out = point.clone();
        rotateAboutAxis(out, rotAx, center, angle);
        return out;

    }

	public static void rotateAboutAxis(double[] point, double[] rotAx, double[] center, double angle) {


        Point3d posP = new Point3d(point[0], point[1], point[2]);

        Transform3D trasl = new Transform3D();
        trasl.setTranslation(new Vector3d(-center[0],-center[1],-center[2]));
        trasl.transform(posP);

        Transform3D rot = new Transform3D();
        rot.setRotation(new AxisAngle4d(rotAx[0],rotAx[1],rotAx[2], angle));
        rot.transform(posP);

        trasl.invert();
        trasl.transform(posP);
		//position = MathTools.rotate(position, point, angles[0], angles[1], angles[2]);

        point[0] = posP.x;
        point[1] = posP.y;
        point[2] = posP.z;

	}

    public static double[] getRotateAboutPoint(double[] point, double[] center, double alpha, double beta, double gamma) {

        double[] out = point.clone();
        rotateAboutPoint(out, center, alpha, beta, gamma);
        return out;

    }

    public static void rotateAboutPoint(double[] point, double[] center, double alpha, double beta, double gamma){

		double a,b,c,d,e,f;

		double[] output = new double[3];

		output[0] = point[0] - center[0];
		output[1] = point[1] - center[1];
		output[2] = point[2] - center[2];

		a = cleanResidualEpsilon(StrictMath.cos(convertDegreesToRadians(alpha)));
		b = cleanResidualEpsilon(StrictMath.sin(convertDegreesToRadians(alpha)));

		c = cleanResidualEpsilon(StrictMath.cos(convertDegreesToRadians(beta)));
		d = cleanResidualEpsilon(StrictMath.sin(convertDegreesToRadians(beta)));

		e = cleanResidualEpsilon(StrictMath.cos(convertDegreesToRadians(gamma)));
		f = cleanResidualEpsilon(StrictMath.sin(convertDegreesToRadians(gamma)));

		double[][] m = {{c*e, -a*f+b*d*e, b*f+a*d*e},
				{c*f, a*e+b*d*f, -b*e+a*d*f},
				{-d, b*c, a*c}};

		//output = MathTools.arrayMultiplyw(m, output);
        output = ArrayTools.getArrayFromVector(Float64Matrix.valueOf(m).times(Float64Vector.valueOf(output)));


		output[0] = cleanResidualEpsilon(output[0] + center[0]);
		output[1] = cleanResidualEpsilon(output[1] + center[1]);
		output[2] = cleanResidualEpsilon(output[2] + center[2]);

		point[0] = output[0];
        point[1] = output[1];
        point[2] = output[2];
	}

    public static double convertDegreesToRadians (double degrees)
	{
		double radians = (Math.PI / 180) * degrees;
		return (radians);
	}


    public static double cleanResidualEpsilon(double value) {

		double limit = 1*Math.pow(10, -15);
		if (-limit<value && value<limit) {
			return 0;
		} else {
			return value;
		}
	}
	

}
