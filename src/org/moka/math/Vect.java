/*Title:      mjbWorld
Copyright (c) 1998-2007 Martin John BakerThis program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.For information about the GNU General Public License see http://www.gnu.org/To discuss this program http://sourceforge.net/forum/forum.php?forum_id=122133
also see website http://www.euclideanspace.com/
 *//*
for theory see:
http://www.euclideanspace.com/maths/algebra/vectors/index.htm
 */
package org.moka.math;

import java.io.*; // for steamtokenizer
import java.lang.ref.*;
import java.util.*; // for StringTokenizer
   /* x3d definition<!ENTITY % SFVec3f "CDATA"> <!-- Vector3Float -->
 */

/** This class can represent a 3D vector. For instance a point in 3D space,    or
a relative position or movement.The class has methods to add, subtact, (cross and dot) multiply with other
vectors. also many other methods, including the ability to load and save
to from VRML and x3d */
public class Vect {

    /** VRML only supports float but allow override if higher resolution required    */
    public static boolean saveAsDouble = true;
    /** x coordinate */
    public double x;
    /** y coordinate */
    public double y;
    /** z coordinate */
    public double z;

    /** a constructor to set initial values of x,y and z coodinates
     * @param x1 value of x coordinate
     * @param y1 value of y coordinate
     * @param z1 value of z coordinate
     */
    public Vect(double x1, double y1, double z1) {
        x = x1;
        y = y1;
        z = z1;
    }

    public Vect(double[] p) {

        if (p.length<3) {
            x = 0;
            y = 0;
            z = 0;
        } else {
            x = p[0];
            y = p[1];
            z = p[2];
        }


    }

    /** a constructor to set initial values of x,y and z coodinates
     * @param x1 value of x coordinate
     * @param y1 value of y coordinate
     * @param z1 value of z coordinate
     */
    public Vect(float x1, float y1, float z1) {
        x = (double) x1;
        y = (double) y1;
        z = (double) z1;
    }

    /** copy constructor
     * @param in1 set values to save value in1
     * returns a new instace with values the same as in1
     */
    public Vect(Vect in1) {
        x = (in1 != null) ? in1.x : 0;
        y = (in1 != null) ? in1.y : 0;
        z = (in1 != null) ? in1.z : 0;
    }

    /** construct a vector with initial value zero. */
    public Vect() {
    }

    /** static method to return sum of two vectors
     * @param a first vector to be added
     * @param b second vector to be added
     * @return the sum
     */
    public static Vect add(Vect a, Vect b) {
        return new Vect(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /** static method to return difference of two vectors
     * @param a first vector
     * @param b subract this vector
     * @return result
     */
    public static Vect sub(Vect a, Vect b) {
        return new Vect(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /** overrides base clone method
     * @return new instance with same value of this
     */
    public Vect clone() {
        return new Vect(this);
    }

    /** set the value of this instance to the value of other
     * this can be used to reuse an instance without the overheads of garbidge collection
     * @param other instace we want to use value of, if null then set to zero
     */
    public void copy(Vect other) {
        if (other == null) {
            x = y = z = 0;
            return;
        }
        x = other.x;
        y = other.y;
        z = other.z;
    }

    /** subtract other vector from this
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/index.htm
     * @param other vector to be subtracted from this
     */
    public Vect sub(Vect other) {
        
        Vect out = new Vect(this);
        if (other == null) {
            return out;
        }

        out.x -= other.x;
        out.y -= other.y;
        out.z -= other.z;
        return out;
    }

    /** add other vector to this
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/index.htm
     * @param other vector to be added to this
     */
    public Vect add(Vect other) {
        
        Vect out = new Vect(this);
        if (other == null) {
            return out;
        }

        out.x += other.x;
        out.y += other.y;
        out.z += other.z;

        return out;
    }

    /** inverts the direction of the vector */
    public Vect minus() {

        Vect out = new Vect(this);
        out.x = -x;
        out.y = -y;
        out.z = -z;

        return out;
    }

    /** return a vector pointing on the opposite direction to this without affecting    the
     * value of this instance
     * @return new instance with value= munus this
     */
    public Vect getMinus() {
        return new Vect(-x, -y, -z);
    }

    /** invert the vector
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/vecAlgebra/inverse/index.htm
     */
    public Vect invert() {

        Vect out = new Vect(this);

        double t2 = (x * x + y * y + z * z);
        out.x /= t2;
        out.y /= t2;
        out.z /= t2;

        return out;
    }

    /** convert this vector to unit length
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/normals/index.htm
     */
    public Vect normalize() {

        Vect out = new Vect(this);

        double t = Math.sqrt(x * x + y * y + z * z);
        out.x /= t;
        out.y /= t;
        out.z /= t;

        return out;
    }

    /** scale this vector equally in all directions
     * @param sc scale factor
     */
    public Vect scale(double sc) {

        Vect out = new Vect(this);

        out.x *= sc;
        out.y *= sc;
        out.z *= sc;

        return out;
    }

    /** scale this vector posibly different in x,y and z directions
     * @param other scale value
     */
    public Vect scale(Vect other) {

        Vect out = new Vect(this);

        out.x *= other.x;
        out.y *= other.y;
        out.z *= other.z;
        return out;
    }

    /** scale this vector by inverse of other
     * @param other scale value
     */
    public Vect scaleInv(Vect other) {

        Vect out = new Vect(this);

        out.x /= other.x;
        out.y /= other.y;
        out.z /= other.z;
        return out;
    }

    /** return square of distance from end of this vector to end of other
     * @param other calcules distance from this vector
     * @return square of distance from end of this vector to end of other
     */
    public double distanceSquared(Vect other) {
        double x1 = other.x - x;
        double y1 = other.y - y;
        double z1 = other.z - z;
        return x1 * x1 + y1 * y1 + z1 * z1;
    }

    /** cross product
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/index.htm
     * @param other vector to take cross product with
     */
    public Vect cross(Vect other) {

        Vect out = new Vect(this);

        double xh = y * other.z - other.y * z;
        double yh = z * other.x - other.z * x;
        double zh = x * other.y - other.x * y;
        out.x = xh;
        out.y = yh;
        out.z = zh;

        return out;
    }

    /** dot product
     * for theory see:
     * http://www.euclideanspace.com/maths/algebra/vectors/index.htm
     * @param other vector to take dot product with
     * @return dot product
     */
    public double dot(Vect other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    /** scalarMultiply
     * @param the scalar
     * @return dot product
     */
    public Vect scalarMultiply(double scalar) {

        Vect out = new Vect(this);
        out.x *= scalar;
        out.y *= scalar;
        out.z *= scalar;
        return out;
    }

    /** set the x value only without althering the other dimensions
     * @param v new value for x
     */
    public void setX(double v) {
        x = v;
    }

    /** set the y value only without althering the other dimensions
     * @param v new value for y
     */
    public void setY(double v) {
        y = v;
    }

    /** set the z value only without althering the other dimensions
     * @param v new value for z
     */
    public void setZ(double v) {
        z = v;
    }

    /** gets the value in the x dimension
     * @return the value in the x dimension
     */
    public double getx() {
        return x;
    }

    /** gets the value in the y dimension
     * @return the value in the y dimension
     */
    public double gety() {
        return y;
    }

    /** gets the value in the z dimension
     * @return the value in the z dimension
     */
    public double getz() {
        return z;
    }

    /** return a string which represents the value which can be used in source    code
     * @return a string representing the value of this
     */
    public String toStatic() {
        return "" + x + "f," + y + "f," + z + "f";
    }

    /** returns true if any dimension of other vector is greater than the same    dimension
     * of this
     * @param other vector to compare with this
     * @return true if greater
     */
    public boolean greaterThan(Vect other) {
        if (other.x > x) {
            return true;
        }
        if (other.y > y) {
            return true;
        }
        if (other.z > z) {
            return true;
        }
        return false;
    }

    /** returns true if any dimension of other vector is less than the same dimension
     * of this
     * @param other vector to compare with this
     * @return true if less
     */
    public boolean lessThan(Vect other) {
        if (other.x < x) {
            return true;
        }
        if (other.y < y) {
            return true;
        }
        if (other.z < z) {
            return true;
        }
        return false;
    }

    /** returns true if this vector has an equal value to other vector
     * @param other vector to compare with this
     * @return
     */
    public boolean equals(Vect other) {
        if (other == null) {
            return false;
        }
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }

    /** output as a string
     * @param format">mode values
     * 0 - output modified values
     * 1 - output original values
     * 2 - output attribute
     * 3 - output attribute in brackets
     * 4 - output with f prefix
     * @return string result
     */
    public String outstring(int format) {
        if (format == 3) {
            if (saveAsDouble) {
                return "(" + x + " " + y + " " + z + ")";
            } else {
                return "(" + new Float(x).toString() + " " +
                        new Float(y).toString() + " " +
                        new Float(z).toString() + ")";
            }
        } else if (format == 4) {
            return new Float(x).toString() + "f," +
                    new Float(y).toString() + "f," +
                    new Float(z).toString() + "f";
        } else {
            if (saveAsDouble) {
                return "" + x + " " + y + " " + z;
            } else {
                return new Float(x).toString() + " " +
                        new Float(y).toString() + " " +
                        new Float(z).toString();
            }
        }
    }

    /** calcultes new translation when combining translations
     *
     * Rt = Ra Rb
     * Ct = Cb
     * Tt = Ra (Cb + Tb - Ca) + Ca + Ta - Cb
     *
     * for theory:
     * http://www.euclideanspace.com/maths/geometry/rotations/rotationAndTranslation/nonMatrix/index.htm
     * @param ta Ta = translation of transform a in absolute coordinates
     * @param ra Ra = rotation function of transform a in absolute coordinates
     * @param ca Ca = centre of rotation of transform a in absolute coordinates
     * @param b Tb = translation of transform b in coordinates of transform a
     * @param cb Cb = centre of rotation of transform b in coordinates of transform    a
     * @return Tt total offset
     */
    public Vect rotationOffset(Vect ta, Rotation ra, Vect ca, Vect tb, Vect cb) {
        Vect result = new Vect(cb);
        result.add(tb);
        result.sub(ca);
        if (ra != null) {
            ra.transform(result);
        }
        result.add(ca);
        result.add(ta);
        result.sub(cb);
        return result;
    }

    /** use to combine bounds
     * if i=0 take minimum otherwise maximum
     * @param other">vector to combine with</param>
     * @param i">if i=0 take minimum otherwise maximum</param>
     * */
    public void bounds(Vect other, int i) {
        if (i == 0) { // take minimum
            if (other.x < x) {
                x = other.x;
            }
            if (other.y < y) {
                y = other.y;
            }
            if (other.z < z) {
                z = other.z;
            }
        } else { // take maximum
            if (other.x > x) {
                x = other.x;
            }
            if (other.y > y) {
                y = other.y;
            }
            if (other.z > z) {
                z = other.z;
            }
        }

    }

    public double[] toDouble() {

        double[] out = new double[3];
        out[0] = x;
        out[1] = y;
        out[2] = z;

        return out;

    }

 
}