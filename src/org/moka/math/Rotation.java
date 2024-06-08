package org.moka.math;

/*Title:      mjbWorld
Copyright (c) 1998-2007 Martin John BakerThis program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.For information about the GNU General Public License see http://www.gnu.org/To discuss this program http://sourceforge.net/forum/forum.php?forum_id=122133
also see website http://www.euclideanspace.com/
 */

import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Float64Vector;
import org.moka.tools.ArrayTools;
import org.moka.tools.MathTools;

   /* x3d definition<!ENTITY % SFRotation "CDATA"> <!-- Rotation -->
 */

/** a class to represent a rotation, internally the class may code the rotation    as an
/// axis angle:
/// http://www.euclideanspace.com/maths/geometry/rotations/axisAngle/index.htm
/// or a quaternion:
/// http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm
/// or as euler angles
/// http://www.euclideanspace.com/maths/geometry/rotations/euler/index.htm
 */
public class Rotation {

    /** defines the resolution at which the rotation will be saved to file */
    static boolean saveAsDouble = false;
    /** x element of axis angle or quaternion*/
    public double x;
    /** y element of axis angle or quaternion*/
    public double y;
    /** z element of axis angle or quaternion*/
    public double z;
    /** angle element of axis angle or w element of quaternion*/
    public double angle;
    /** VRML always uses axis-angle to represent rotations
     *but quaternions are more efficient for some applications
     * */
    public int coding = CODING_AXISANGLE;
    /** possible values for coding variable*/
    public static final int CODING_AXISANGLE = 0;
    public static final int CODING_QUATERNION = 1;
    public static final int CODING_EULER = 2;
    public static final int CODING_AXISANGLE_SAVEASQUAT = 3;
    public static final int CODING_QUATERNION_SAVEASQUAT = 4;
    public static final int CODING_EULER_SAVEASQUAT = 5;

    /**constructor which allows initial value to be suplied as axis angle
     * @param x1 x dimention of normalised axis
     * @param y1 y dimention of normalised axis
     * @param z1 z dimention of normalised axis
     * @param a1 angle
     */
    public Rotation(double x1, double y1, double z1, double radians) {

//        double r = Math.sqrt(x1*x1 + y1*y1 + z1*z1);
//        double sint2 = Math.sin(radians/2);
//        x = sint2*(x1/r);
//        y = sint2*(y1/r);
//        z = sint2*(z1/r);
//        angle = Math.cos(radians/2);
//
//        double r2 = Math.sqrt(x1*x1 + y1*y1 + z1*z1 + angle*angle);
//        x/=r2;
//        y/=r2;
//        z/=r2;
//        angle/=r2;

        x = x1;
        y = y1;
        z = z1;
        angle=radians;
    }

    public Rotation(double[] p, double radians) {

//        double x1,y1,z1;
//
//        if (p.length<3) {
//            x1 = 1;
//            y1 = 0;
//            z1 = 0;
//        } else {
//            x1 = p[0];
//            y1 = p[1];
//            z1 = p[2];
//        }
//
//        double r = Math.sqrt(x1*x1 + y1*y1 + z1*z1);
//        double sint2 = Math.sin(radians/2);
//        x = sint2*(x1/r);
//        y = sint2*(y1/r);
//        z = sint2*(z1/r);
//        angle = Math.cos(radians/2);
//
//        double r2 = Math.sqrt(x1*x1 + y1*y1 + z1*z1 + angle*angle);
//        x/=r2;
//        y/=r2;
//        z/=r2;
//        angle/=r2;

        x = p[0];
        y = p[1];
        z = p[2];
        angle=radians;
    }



    /** constructor which allows initial value to be suplied as axis angle,quaternion
     * or axis angle as defined by c1 whoes possible values are given by enum cde
     * @param x1 if quaternion or axis angle holds x dimention of normalised axis
     * @param y1 if quaternion or axis angle holds y dimention of normalised axis
     * @param z1 if quaternion or axis angle holds z dimention of normalised axis
     * @param a1 if quaternion holds w, if axis angle holds angle
     * @param c1 possible values are given by enum cde
     * */
    public Rotation(double x1, double y1, double z1, double a1, int c1) {
        x = x1;
        y = y1;
        z = z1;
        angle = a1;
        coding = c1;
    }

    /** constructor to create sfrotation from euler angles.
     * @param heading rotation about z axis
     * @param attitude rotation about y axis
     * @param bank rotation about x axis
     */
    public Rotation(double heading, double attitude, double bank) {
        double c1 = Math.cos(heading / 2);
        double s1 = Math.sin(heading / 2);
        double c2 = Math.cos(attitude / 2);
        double s2 = Math.sin(attitude / 2);
        double c3 = Math.cos(bank / 2);
        double s3 = Math.sin(bank / 2);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;
        angle = c1c2 * c3 + s1s2 * s3;
        x = c1c2 * s3 - s1s2 * c3;
        y = c1 * s2 * c3 + s1 * c2 * s3;
        z = s1 * c2 * c3 - c1 * s2 * s3;
        coding = CODING_QUATERNION;
    }

    /** copy constructor
     * @param in1 class to copy
     * */
    public Rotation(Rotation in1) {
        x = (in1 != null) ? in1.x : 0;
        y = (in1 != null) ? in1.y : 0;
        z = (in1 != null) ? in1.z : 1;
        angle = (in1 != null) ? in1.angle : 0;
        coding = (in1 != null) ? in1.coding : CODING_AXISANGLE;
    }

    /** constructor
     * */
    public Rotation() {
    }

    /** calculates the effect of this rotation on a point
     * the new point is given by=q * P1 * q'
     * this version does not alter P1 but returns the result.
     *
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm
     * @param point">point to be transformed</param>
     * @return translated point</returns>
     */
    public Vect getTransform(Vect p1) {
        double wh = angle;
        double xh = x;
        double yh = y;
        double zh = z;
        if (coding == CODING_AXISANGLE) {
            double s = Math.sin(angle / 2);
            xh = x * s;
            yh = y * s;
            zh = z * s;
            wh = Math.cos(angle / 2);
        }
        Vect p2 = new Vect();
        p2.x = wh * wh * p1.x + 2 * yh * wh * p1.z - 2 * zh * wh * p1.y + xh * xh * p1.x + 2 * yh * xh * p1.y + 2 * zh * xh * p1.z - zh * zh * p1.x - yh * yh * p1.x;
        p2.y = 2 * xh * yh * p1.x + yh * yh * p1.y + 2 * zh * yh * p1.z + 2 * wh * zh * p1.x - zh * zh * p1.y + wh * wh * p1.y - 2 * xh * wh * p1.z - xh * xh * p1.y;
        p2.z = 2 * xh * zh * p1.x + 2 * yh * zh * p1.y + zh * zh * p1.z - 2 * wh * yh * p1.x - yh * yh * p1.z + 2 * wh * xh * p1.y - xh * xh * p1.z + wh * wh * p1.z;
        return p2;
    }

    public Vect getTransform(double[] d1) {

        Vect p1 = new Vect();
        p1.x = d1[0];
        p1.y = d1[1];
        p1.y = d1[2];

        return getTransform(p1);

    }
    /** calculates the effect of this rotation on a point
     * the new point is given by=q * P1 * q'
     * this version returns the result in p1
     *
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm
     * @param point point to be transformed</param>
     */
    public void transform(Vect p1) {
        double wh = angle;
        double xh = x;
        double yh = y;
        double zh = z;
        if (coding == CODING_AXISANGLE) {
            double s = Math.sin(angle / 2);
            xh = x * s;
            yh = y * s;
            zh = z * s;
            wh = Math.cos(angle / 2);
        }
        double resultx = wh * wh * p1.x + 2 * yh * wh * p1.z - 2 * zh * wh * p1.y + xh * xh * p1.x + 2 * yh * xh * p1.y + 2 * zh * xh * p1.z - zh * zh * p1.x - yh * yh * p1.x;
        double resulty = 2 * xh * yh * p1.x + yh * yh * p1.y + 2 * zh * yh * p1.z + 2 * wh * zh * p1.x - zh * zh * p1.y + wh * wh * p1.y - 2 * xh * wh * p1.z - xh * xh * p1.y;
        double resultz = 2 * xh * zh * p1.x + 2 * yh * zh * p1.y + zh * zh * p1.z - 2 * wh * yh * p1.x - yh * yh * p1.z + 2 * wh * xh * p1.y - xh * xh * p1.z + wh * wh * p1.z;
        p1.x = resultx;
        p1.y = resultx;
        p1.z = resultx;
    }

      /** override of clone method for this class
     * @return clone of this
     */
    public Object clone() {
        return new Rotation(this);
    }


    /** invert direction of rotation
     *
     */
    public void minus() {
        if (coding == CODING_AXISANGLE) {
            angle = -angle;
            return;
        }
        x = -x;
        y = -y;
        z = -z;
    }

    /** get a clone of the rotation
     * @return a new array with value of minus this
     */
    public Rotation getMinus() {
        if (coding == CODING_AXISANGLE) {
            return new Rotation(x, y, z, -angle, coding);
        }
        return new Rotation(-x, -y, -z, angle, coding);
    }

    /** set the axis of rotation
     * @param tx
     * @param ty
     * @param tz
     * */
    public void set(double tx, double ty, double tz) {
        angle = Math.sqrt(tx * tx + ty * ty + tz * tz);
        if (angle == 0) {
            x = 1;
            y = z = 0;
            return;
        }
        x = tx / angle;
        y = ty / angle;
        z = tz / angle;
    }

    /** set the values of this rotation
     * @param tx
     * @param ty
     * @param tz
     * @param tangle
     * */
    public void set(double tx, double ty, double tz, double tangle) {
        x = tx;
        y = ty;
        z = tz;
        angle = tangle;
    }

    /** returns axis in x dimention
     * @return axis in x dimention
     * */
    public double getTx() {
        return x * angle;
    }

    /** returns axis in y dimention
     * @return returns axis in y dimention
     * */
    public double getTy() {
        return y * angle;
    }

    /** returns axis in z dimention
     * @return returns axis in z dimention
     * */
    public double getTz() {
        return z * angle;
    }

    /** calculate total rotation by taking current rotation and then
     * apply rotation r
     *
     * if both angles are quaternions then this is a multiplication
     * @param r
     * */
    public void combine(Rotation r) {
        toQuaternion();
        if (r == null) {
            return;
        }
        double qax = x;
        double qay = y;
        double qaz = z;
        double qaw = angle;
        double qbx;
        double qby;
        double qbz;
        double qbw;
        if (r.coding == CODING_QUATERNION) {
            qbx = r.x;
            qby = r.y;
            qbz = r.z;
            qbw = r.angle;
        } else {
            double s = Math.sin(r.angle / 2);
            qbx = r.x * s;
            qby = r.y * s;
            qbz = r.z * s;
            qbw = Math.cos(r.angle / 2);
        }
        // now multiply the quaternions
        angle = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;
        x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
        y = qaw * qby - qax * qbz + qay * qbw + qaz * qbx;
        z = qaw * qbz + qax * qby - qay * qbx + qaz * qbw;
        coding = CODING_QUATERNION;
    }

    /** combine a rotation expressed as euler angle with current rotation.
     * first convert both values to quaternoins then combine and convert back to
     * axis angle. Theory about these conversions shown here:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/index.htm
     * @param heading angle about x axis
     * @param attitude angle about y axis
     * @param bank angle about z axis
     * */
    public void combine(double heading, double attitude, double bank) {
        // first calculate quaternion qb from heading, attitude and bank
        double c1 = Math.cos(heading / 2);
        double s1 = Math.sin(heading / 2);
        double c2 = Math.cos(attitude / 2);
        double s2 = Math.sin(attitude / 2);
        double c3 = Math.cos(bank / 2);
        double s3 = Math.sin(bank / 2);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;
        double qbw = c1c2 * c3 + s1s2 * s3;
        double qbx = c1c2 * s3 - s1s2 * c3;
        double qby = c1 * s2 * c3 + s1 * c2 * s3;
        double qbz = s1 * c2 * c3 - c1 * s2 * s3;
        // then convert axis-angle to quaternion if required
        toQuaternion();
        double qax = x;
        double qay = y;
        double qaz = z;
        double qaw = angle;
        // now multiply the quaternions
        angle = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;
        x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
        y = qaw * qby - qax * qbz + qay * qbw + qaz * qbx;
        z = qaw * qbz + qax * qby - qay * qbx + qaz * qbw;
        coding = CODING_QUATERNION;
    }

    public Vect getTransformAroundPoint(Vect P, Vect A) {

        double[][] m = new double[4][4];

        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1.0 - c;
        m[0][0] = c + x * x * t;
        m[1][1] = c + y * y * t;
        m[2][2] = c + z * z * t;


        double tmp1 = x * y * t;
        double tmp2 = z * s;
        m[1][0] = tmp1 + tmp2;
        m[0][1] = tmp1 - tmp2;


        tmp1 = x * z * t;
        tmp2 = y * s;
        m[2][0] = tmp1 - tmp2;
        m[0][2] = tmp1 + tmp2;

        tmp1 = y * z * t;
        tmp2 = x * s;
        m[2][1] = tmp1 + tmp2;
        m[1][2] = tmp1 - tmp2;


        double a1, a2, a3;
        if (A == null) {
            a1 = a2 = a3 = 0;
        } else {
            a1 = A.x;
            a2 = A.y;
            a3 = A.z;
        }

        m[0][3] = a1 - a1 * m[0][0] - a2 * m[0][1] - a3 * m[0][2];
        m[1][3] = a2 - a1 * m[1][0] - a2 * m[1][1] - a3 * m[1][2];
        m[2][3] = a3 - a1 * m[2][0] - a2 * m[2][1] - a3 * m[2][2];
        
        m[3][0] = 0.0;
        m[3][1] = 0.0;
        m[3][2] = 0.0;
        
        m[3][3] = 1.0;

        double[] point = new double[4];
        point[0] = P.x;
        point[1] = P.y;
        point[2] = P.z;
        point[3] = 1.0;

        // --- ok --- double[] out = MathTools.arrayMultiply(m, point);

        double[] out = ArrayTools.getArrayFromVector(
                            Float64Matrix.valueOf(m).times(Float64Vector.valueOf(point))
                            );

        return new Vect(out[0],out[1],out[2]);


    }


    /** if this rotation is not already coded as axis angle then convert it to    axis angle */
    public void toAxisAngle() {
        if (coding == CODING_AXISANGLE) {
            return;
        }
        double s = Math.sqrt(1 - angle * angle);
        if (Math.abs(s) < 0.001) {
            s = 1;
        }
        angle = 2 * Math.acos(angle);
        x = x / s;
        y = y / s;
        z = z / s;
    }

    /** if this rotation is not already coded as quaternion then convert it to quaternion    */
    public void toQuaternion() {
        if (coding == CODING_QUATERNION) {
            return;
        }
        double s = Math.sin(angle / 2);
        x = x * s;
        y = y * s;
        z = z * s;
        angle = Math.cos(angle / 2);
    }

  

    /** convert x,y,z,angle to string between brackets */
    public String toString() {
        return "(" + x + "," +
                y + "," +
                z + "," +
                angle + ")";
    }/// call openGL mglRotated
    /// <param name="axo"></param>
   /*public void render3d(AxmjboglCtl axo){
    if (coding==(int)cde.CODING_AXISANGLE) {
    axo.mglRotated(angle * 180 / Math.PI,x,y,z);
    return;
    }
    double s = Math.Sqrt(1-angle*angle);
    if (Math.Abs(s) < 0.001) s=1;
    axo.mglRotated(Math.Acos(angle) * 360 / Math.PI,x / s,y / s,z / s);
    }*/

  
}