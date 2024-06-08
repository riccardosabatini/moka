
package org.moka.tools;

import java.util.ArrayList;

import org.moka.common.Costants;
import org.jscience.mathematics.vector.Vector;

/**
 * @author riki
 *
 */
public class ArrayTools {

	//--------------------------
	//	ARRAY RESIZER
	//--------------------------
	
	public static double[][] shortArray(double[][] input, int rows, int cols) {
		
		double[][] output = new double[rows][cols];
		for (int i=0; i<rows; i++){
			for (int j=0; j<cols; j++){
				output[i][j] = input[i][j];
			}		
		}
		return output;
	}
	
	public static double[][] rowsInColumns(double[][] input) {
		
		int rows = input.length;
		int cols = input[0].length;
		
		double[][] out = new double[cols][rows];
		
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				
				out[j][i] = input[i][j];
				
			}
		}
		return out;
		
	}
	//--------------------------
	//	ARRAY FILLER
	//--------------------------
	
	public static double[] arrayFill(double[] _in, double value){
		double[] out = new double[_in.length];
		for (int i=0; i<out.length; i++) {
			out[i] = value;
		}
		return out;
	}
	
	public static int[] arrayNewFill(int dim, int value) {
		int[] out = new int[dim];
		for (int i=0; i<out.length; i++){
			out[i] = value;
		}
		return out;
	}
	
	public static double[] arrayNewFill(int dim, double value) {
		double[] out = new double[dim];
		for (int i=0; i<out.length; i++){
			out[i] = value;
		}
		return out;
	}
	
	//--------------------------
	//	IS IN ARRAY (boolean)
	//--------------------------
	
	public static boolean isInArray(Double[] vect, Double value) {
		
		boolean isIn = false;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] ==value) isIn = true;		
		}
		
		return isIn;
	}
	
	public static boolean isInArray(int[] vect, int value) {
		
		boolean isIn = false;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] ==value) isIn = true;		
		}
		
		return isIn;
	}
	
	public static boolean isInArray(String[] vect, String value) {
		
		boolean isIn = false;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i].equals(value)) isIn = true;		
		}
		
		return isIn;
	}

    public static boolean isInArray(double[] vect, double value) {

		boolean isIn = false;

		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) isIn = true;
		}

		return isIn;
	}
	
    public static boolean isInArray(ArrayList<Object> vect, Object value) {

		boolean isIn = false;

		for (int i=0; i<vect.size(); i++){
			if (vect.get(i).equals(value)) isIn = true;
		}

		return isIn;

	}

    public static boolean isInArray(ArrayList<String> vect, String value) {

		boolean isIn = false;

		for (int i=0; i<vect.size(); i++){
			if (vect.get(i).equals(value)) isIn = true;
		}

		return isIn;

	}

    public static boolean isInArray(ArrayList<Integer> vect, int value) {

		boolean isIn = false;

		for (int i=0; i<vect.size(); i++){
			if (vect.get(i).equals(value)) isIn = true;
		}

		return isIn;

	}
	
	public static boolean isEqual(double[] one, double[] two) {
		
		boolean out = false;
		int equals=0;
		
		for (int i=0; i<one.length; i++) {
			if (Math.abs(one[i]-two[i])<Costants.minDist) {
				equals+=1;
			}
		}
		
		if (equals==one.length) {
			out = true;
		}
		
		return out;
	}
	
	//--------------------------
	//	WHERE IS IN ARRAY (int)
	//--------------------------

	public static int whereIsInArray(double[] vect, double value) {
		
		int id = -1;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) id = i;		
		}
		
		return id;
	}
	
	public static int whereIsInArray(Double[] vect, double value) {
		
		int id = -1;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) id = i;		
		}
		
		return id;
	}
	
	public static int whereIsInArray(int[] vect, int value) {
		
		int id = -1;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) id = i;		
		}
		
		return id;
	}
	
	public static int whereIsInArray(double[] vect, int value) { return whereIsInArray(vect, (double)value);}
	
	public static int whereIsInArray(String[] vect, String value) {
		
		int id = -1;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i].equals(value)) id = i;		
		}
		
		return id;
	}

    public static int whereIsInArray(ArrayList<String> vect, String value) {

		int id = -1;

		for (int i=0; i<vect.size(); i++){
			if (vect.get(i).equals(value)) id = i;
		}

		return id;
	}
	public static int[] whereIsAnyInArray(String[] vect, String[] values) {
		
		int id = -1;
		int pos = -1;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<values.length; j++){
				if (vect[i].equals(values[j])){
					pos = i;
					id = j;
					
				}
			}
		}
		
		return new int[] {pos,id};
	}
	
	//--------------------------
	//	IS LAST VALUE
	//--------------------------
	
	public static int isLastValue (double[] vect, double value) {
		
		int id = -1;
		int i=vect.length-1;
		boolean outValue = false;
		boolean inValue = false;
		do {
			if (vect[i] == value){
				if (id==-1) inValue = true;
				id = i;
			} else {
				if (id!=-1 && inValue) outValue = true;
			}
			
			
			i--;
		} while (i>=0 && !outValue);
		
		return id;
		
	}
	
	//--------------------------
	//	GET MAX	/	MIN
	//--------------------------
	
	public static double getSmallerPositive(double[][] vect) {
		// TODO Auto-generated method stub
		double smaller = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (vect[i][j] < smaller && vect[i][j]>0) smaller = vect[i][j];
			}
		}
		return smaller;
	}
	
	public static double getAverageOfPositive(double[][] vect) {
		// TODO Auto-generated method stub
		double sum=0;
		int summed = 0;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (vect[i][j] > 0){
					sum+= vect[i][j];
					summed++;
				}
			}
		}
		return sum/summed;
	}
	
	public static double getMax (double[] vect) {
		
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] > max) max = vect[i];		
		}
		return max;
	}
	
	public static double getMax (double[][] vect) {
		
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (vect[i][j] > max) max = vect[i][j];		
			}
		}
		return max;
	}
	
	public static double getAbsMax (double[][] vect) {
		
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (Math.abs(vect[i][j]) > max) max = Math.abs(vect[i][j]);		
			}
		}
		return max;
	}
	
	public static double getAbsMaxNotZero (double[][] vect) {
		
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (Math.abs(vect[i][j]) > max && Math.abs(vect[i][j])!=0) max = Math.abs(vect[i][j]);		
			}
		}
		return max;
	}
	
	public static double getMin (double[] vect) {
		
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] < min) min = vect[i];		
		}
		return min;
	}
	
	public static double getMin (double[][] vect) {
		
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (vect[i][j] < min) min = vect[i][j];
			}
		}
		return min;
	}
	
	public static double getAbsMin (double[][] vect) {
		
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (Math.abs(vect[i][j]) < min) min = Math.abs(vect[i][j]);
			}
		}
		return min;
	}
	
	public static double getAbsMinNotZero(double[][] vect) {
		
		double min = Double.POSITIVE_INFINITY;
		
		for (int i=0; i<vect.length; i++){
			for (int j=0; j<vect[i].length; j++){
				if (Math.abs(vect[i][j]) < min && Math.abs(vect[i][j])!=0) min = Math.abs(vect[i][j]);
			}
		}
		return min;
	}


	//--------------------------
	//	FINDERS
	//--------------------------
	
	public static int closeLeft(double[] vect, double value) {
		
		double diff = Double.MAX_VALUE;
		int index = -1;
		
		for (int i=0; i<vect.length; i++){
			if ((vect[i]<=value) && (Math.abs((vect[i]-value))<=diff)){
				diff = Math.abs(vect[i]-value);
				index = i;
			}
					
		}
		return index;
	}
	
	public static int closeRight(double[] vect, double value) {
		
		double diff = Double.MAX_VALUE;
		int index = -1;
		
		for (int i=vect.length-1; i>=0; i--){
			if ((vect[i]>=value) && (Math.abs((vect[i]-value))<=diff)){
				diff = Math.abs(value - vect[i]);
				index = i;
			}
					
		}
		return index;
	}

	//--------------------------
	//	APPEND
	//--------------------------
	
	public static String[] appendToArray(String[] _in, String _new) {
		
		String[] out = new String[_in.length+1];
		
		for (int i=0; i<_in.length; i++) {
			out[i] = _in[i];
		}
		out[out.length-1] = _new;
		return out;
	}
	//--------------------------
	//	COUNT
	//--------------------------
	
	public static int countInArray(Object[] vect, Object value) {
		
		
		int index = 0;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i].equals(value)) index++;
					
		}
		return index;
	}
	
	public static int countInArray(boolean[] vect, boolean value) {
		
		
		int index = 0;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) index++;
					
		}
		return index;
	}
	
	public static int countInArray(int[] vect, int value) {
		
		
		int index = 0;
		
		for (int i=0; i<vect.length; i++){
			if (vect[i] == value) index++;
					
		}
		return index;
	}
	
	//--------------------------
	//	INTERPOLATIONS
	//--------------------------
	
	public static double linInterp(double[] vectX,double[] vectY, double valueX) {
		
		int left = closeLeft(vectX, valueX);
		int right = closeRight(vectX, valueX);
		
		if (left==-1 || right==-1) {return -1;}
		else if (left==right) { return vectY[left];}
		else {
			double mid = (vectY[left]+vectY[right])/2;
			return mid;
		}
		
	}
	
	//--------------------------
	//	ARRAY PRINT
	//--------------------------
	public static String arrayToStringOne(double[] vect) {
		
		String out = "{";
		
		for (int i=0; i<vect.length; i++) {
			out+=vect[i]+",";
		}
		out=out.substring(0,out.length()-1)+"}";
		
		return out;
		
	}

    public static String arrayToStringOne(double[][] vect) {

		String out = "{";

		for (int i=0; i<vect.length; i++) {
			out+="{";
            for (int j=0; j<vect[i].length; j++) {
                out+=vect[i][j]+",";
            }   
            out+="},";
		}
		out=out.substring(0,out.length()-1)+"}";

		return out;

	}

    public static String arrayToStringTwo(double[] vect) {

		String out = "|";

		for (int i=0; i<vect.length; i++) {
			out+=vect[i]+" | ";
		}
		out=out.substring(0,out.length()-1);

		return out;

	}

    public static String arrayToStringTwo(double[][] vect) {

		String out = "";

		for (int i=0; i<vect.length; i++) {
			out+="|";
            for (int j=0; j<vect[i].length; j++) {
                out+=vect[i][j]+"|";
            }
            out+="|\n";
		}
		out=out.substring(0,out.length()-1);

		return out;

	}

    public static int[] arrayListToInt(ArrayList<Integer> a) {

        int[] out = new int[a.size()];

		for (int i=0; i<out.length; i++) {
			out[i] = a.get(i);
		}
		
		return out;

	}

    public static double[] arrayListToDouble(ArrayList<Double> a) {

        double[] out = new double[a.size()];

		for (int i=0; i<out.length; i++) {
			out[i] = a.get(i);
		}

		return out;

	}

    public static String[] arrayListToString(ArrayList<String> a) {

        String[] out = new String[a.size()];

		for (int i=0; i<out.length; i++) {
			out[i] = a.get(i);
		}

		return out;

	}

    public static void printVector (double[][] vect) {
		System.out.println("|--------------");
		for (int i=0; i<vect.length; i++) {
			for (int j=0; j<vect[0].length; j++) {
				System.out.print("| "+vect[i][j]+" ");
			}	
			System.out.print("| \n");
		}
		
		System.out.println("|--------------");
	}
	
	public static void printVector (double[] vect) {
		System.out.println("|--------------");
		for (int i=0; i<vect.length; i++) {
		
			System.out.print("| "+vect[i]+" ");
	
		}
		System.out.println("|");
		System.out.println("|--------------");
		
	}

    public static void printVector (int[] vect) {
		System.out.println("|--------------");
		for (int i=0; i<vect.length; i++) {

			System.out.print("| "+vect[i]+" ");

		}
		System.out.println("|");
		System.out.println("|--------------");

	}
	
	public static void printVector (String[] vect) {
		System.out.println("|--------------");
		for (int i=0; i<vect.length; i++) {
		
			System.out.print("| "+vect[i]+" ");
	
		}
		System.out.println("|");
		System.out.println("|--------------");
		
	}
	
	public static void printVector (Double[] vect) {
		System.out.println("|--------------");
		for (int i=0; i<vect.length; i++) {
		
			System.out.print("| "+vect[i]+" ");
	
		}
		System.out.println("|");
		System.out.println("|--------------");
		System.out.print("\n");
	}

	//-----------------
	//	PARSER
	//-----------------
	
	public static double[] parseFromString(String _in) {
	
		if (_in.trim().length()==0) {
			return new double[0];
		}
		
		String[] nums = _in.trim().subSequence(1, _in.length()-1).toString().split(" ");
		double[] dims = new double[nums.length];
		
		for (int i=0; i<dims.length; i++) {
			dims[i] = Double.parseDouble(nums[i]);
		}
		
		return dims;
		
	}

	/**
	 * @param celltemp
	 * @return
	 */
	
	//-----------------
	//	PRINTER
	//-----------------
	public static String elegantPrint(double[] _in) {
		String out = "[";
		
		for (int i=0; i<_in.length; i++) {
			
			out+=_in[i]+" ";
			
		}
		out += "]";
		return out;
	}
	
	public static String elegantPrint(int[] _in) {
		String out = "[";
		
		for (int i=0; i<_in.length; i++) {
			
			out+=_in[i]+" ";
			
		}
		out += "]";
		return out;
	}

    //-----------------
    //  CONVERTER
    //-----------------

    public static double[][] getArrayFromMatrix(org.jscience.mathematics.vector.Matrix m) {

        int rows = m.getNumberOfRows();
        int cols = m.getNumberOfRows();

        double[][] out = new double[rows][cols];

        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                out[i][j] = ((org.jscience.mathematics.number.Float64)m.get(i, j)).doubleValue();
            }
        }
        return out;

    }

    public static double[] getArrayFromVector(org.jscience.mathematics.vector.Vector m) {

        int all = m.getDimension();

        double[] out = new double[all];

        for (int i=0; i<all; i++) {
            out[i] = ((org.jscience.mathematics.number.Float64) m.get(i)).doubleValue();

        }
        return out;

    }
	
}
