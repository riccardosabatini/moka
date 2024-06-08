/**
 * 
 */
package org.moka.tools;

/**
 * @author riki
 *
 */
public class MathTools {

	
	public static double sqr (double a) {return a*a;}
	public static double sqr (int a) {return a*a;}
	
	public static int permutationRepetitionCount(int n, int r){
		return (int)(Math.pow(n, r));
	}
	
	public static int permutationCount(int n, int r){
		
		return (int)(factorial(n)/factorial(n-r));
	}
	
	public static int combinationCount(int n, int k){
		
		return (int)(factorial(n)/(factorial(k)*factorial(n-k)));
	}
	
	public static int combinationRepetitionCount(int n, int k){
		
		return (int)(factorial(n+k-1)/(factorial(k)*factorial(n-1)));
	}
	
	public static double sinShift(double ang) {
		
		int rep = (int) (ang-(ang%(Math.PI/2)));
		double newAng = ang - rep*(Math.PI/2);
		
		if (rep%4==0) {
			return StrictMath.sin(newAng);
		} else if (rep%2==0) {
			return -StrictMath.sin(newAng);
		} else {
			return StrictMath.cos(newAng);
		}
	}
	
	public static double cosShift(double ang) {
		
		int rep = (int) (ang-(ang%(Math.PI/2)));
		double newAng = ang - rep*(Math.PI/2);
		
		if (rep%4==0) {
			return StrictMath.cos(newAng);
		} else if (rep%2==0) {
			return -StrictMath.cos(newAng);
		} else {
			return -StrictMath.sin(newAng);
		}
	}
	
	public static double[] rotate2(double[] point, double[] center, double alpha, double beta, double gamma){

		double a,b,c,d,e,f;
		
		double[] output = new double[3];
		
		output[0] = point[0] - center[0];
		output[1] = point[1] - center[1];
		output[2] = point[2] - center[2];

		a = cleanResidualEpsilon(StrictMath.cos(ConvertDegreesToRadians(alpha)));
		b = cleanResidualEpsilon(StrictMath.sin(ConvertDegreesToRadians(alpha)));

		c = cleanResidualEpsilon(StrictMath.cos(ConvertDegreesToRadians(beta)));
		d = cleanResidualEpsilon(StrictMath.sin(ConvertDegreesToRadians(beta)));

		e = cleanResidualEpsilon(StrictMath.cos(ConvertDegreesToRadians(gamma)));
		f = cleanResidualEpsilon(StrictMath.sin(ConvertDegreesToRadians(gamma)));

		double[][] array = {{c*e, -a*f+b*d*e, b*f+a*d*e}, 
				{c*f, a*e+b*d*f, -b*e+a*d*f},
				{-d, b*c, a*c}};

		output = arrayMultiplyw(array, output);

		output[0] = cleanResidualEpsilon(output[0] + center[0]);
		output[1] = cleanResidualEpsilon(output[1] + center[1]);
		output[2] = cleanResidualEpsilon(output[2] + center[2]);

		return output;
	}
	
	public static double ConvertDegreesToRadians (double degrees)
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
	
    public static long factorial(long n) {
        if      (n <  0) throw new RuntimeException("Underflow error in factorial");
        else if (n > 20) throw new RuntimeException("Overflow error in factorial");
        else if (n == 0) return 1;
        else             return n * factorial(n-1);
    }

    public static double getArea2D(double[] vec1, double[] vec2, int dim1, int dim2) {
    	
    	return Math.abs(vec1[dim1]*vec2[dim2] - vec1[dim2]*vec2[dim1]);
    	
    	
    }
 

    //--------------------------
	//	ARRAY distance
	//--------------------------

    public static double[][] arrayFlip (double[][] in) {

        double[][] out = new double[in[0].length][in.length];

		for (int i=0; i<in[0].length; i++){

            for (int j=0; j<in.length; j++){
                out[i][j] = in[j][i];
            }

		}

        return out;


    }
	//--------------------------
	//	ARRAY distance
	//--------------------------

    public static double getDistance(double[] vect1, double[] vect2) {
    	
    	int dim = vect1.length;
    	double out = 0.0;
    	
    	for (int i=0; i<dim; i++) {
    		out+= Math.pow(vect1[i]-vect2[i], 2);
    	}
    	
    	return Math.sqrt(out);
    	
    }
    
	//--------------------------
	//	ARRAY AVERAGE	/	SUM
	//--------------------------
	
	public static double arravAvg(double[] vect) {
		double avg = 0;
		
		for (int i=0; i<vect.length; i++){
			avg += vect[i]/vect.length;		
		}
		return avg;
	}
	
	public static double arraySum(double[] vect) {
		double sum = 0;
		
		for (int i=0; i<vect.length; i++){
			sum += vect[i];		
		}
		return sum;
	}
	
	public static int arraySum(int[] vect) {
		int sum = 0;
		
		for (int i=0; i<vect.length; i++){
			sum += vect[i];		
		}
		return sum;
	}
	
	//--------------------------
	//	ARRAY MULTIPLY	/	DIVIDE	/	ADD
	//--------------------------

	public static double[] arrayMultiply (double[] vect, double mult) {
		double[] temp = new double[vect.length];
		for (int i=0; i<vect.length; i++){
			temp[i] = mult*vect[i];		
		}
		return temp;
	}
	
	public static double[][] arrayMultiply (double[][] vect, double mult) {
		double[][] temp = new double[vect.length][vect[0].length];
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				temp[i][j] = mult*vect[i][j];
			}
		}
		return temp;
	}
	
	public static double[] arrayDivide(double[] vect, double div) {
		double[] temp = new double[vect.length];
		for (int i=0; i<vect.length; i++){
			temp[i] = vect[i]/div;		
		}
		return temp;
	}
	
	public static double[] arrayMultiplyw (double[][] matrix, double[] vect) {

        
		double[] temp = new double[vect.length];
		
		for (int i=0; i<matrix.length; i++){
			for (int j=0; j<matrix[i].length; j++){

                temp[i] += matrix[i][j]*vect[j];
			}
		}
		return temp;
	}

    public static double[] arrayMultiply (double[] vect, double[][] matrix) {

        if (vect.length!=matrix[0].length) return null;

		double[] temp = new double[vect.length];

		for (int i=0; i<matrix[0].length; i++){

            for (int j=0; j<matrix.length; j++){
                temp[i] += matrix[j][i]*vect[j];
            }
			
		}
		return temp;
	}

    public static double[] arrayMultiply (double[] vect1, double[] vect2) {


		double[] temp = new double[vect1.length];

		for (int i=0; i<vect1.length; i++){
                temp[i] += vect1[i]*vect2[i];
		}
		return temp;
	}
	
	
	public static double[] arrayAdd (double[] vect, double add) {
		double[] temp = new double[vect.length];
		for (int i=0; i<vect.length; i++){
			temp[i] = vect[i]+add;		
		}
		return temp;
	}

    public static double[] arraySubtract (double[] vect, double sub) {
		double[] temp = new double[vect.length];
		for (int i=0; i<vect.length; i++){
			temp[i] = vect[i]+sub;
		}
		return temp;
	}
	
	public static double[] arrayAdd (double[] vect, double[] add) {
		double[] temp = new double[vect.length];
		if (temp.length==add.length) {
			for (int i=0; i<vect.length; i++){
				temp[i] = vect[i]+add[i];		
			}
		}
		return temp;
	}

    public static double[] arraySubtract (double[] vect, double[] sub) {
		double[] temp = new double[vect.length];
		if (temp.length==sub.length) {
			for (int i=0; i<vect.length; i++){
				temp[i] = vect[i]-sub[i];
			}
		}
		return temp;
	}
 
    
    public static double pbcPosition(double pos, double length) {

		if (pos<length && pos>=0) return pos;

		if (pos >= length) {
			pos -= length;
		} else if( pos < 0) {
			pos += length;
		}

		return pbcPosition(pos, length);
	}

    public static double pbcSeparation(double dist, double length) {

        if (dist<0.5*length && dist >=-0.5*length) return dist;

        if (dist >= 0.5*length) {
			dist -= length;
		} else if( dist < -0.5*length) {
			dist += length;
		}

		return  pbcSeparation(dist, length);
	}


}
