/**
 * 
 */
package org.moka.structures;
import java.io.Serializable;
import java.util.ArrayList;

import org.moka.tools.ArrayTools;
import org.moka.tools.CellTools;

import org.moka.common.Costants;

/**
 * @author riki
 *
 */
public class Kpoints implements Serializable{

	ArrayList<double[]> points = new ArrayList<double[]>();
	ArrayList<Double> weights = new ArrayList<Double>();
	int nPoints = 0;
	
	int kType = Costants.kTBIBA;
	String kNname = Costants.kTypesList[kType];
	String kGenerator = "";
		
	public Kpoints() {
		
		points.add(new double[] {0.0,0.0,0.0});
		weights.add(1.0);
		this.nPoints++;
		
	}
	
	public Kpoints(int num) {
		
		for (int i=0; i<num; i++){
			points.add(new double[] {0.0,0.0,0.0});
			weights.add(1.0);
			this.nPoints++;
		}
		
	}
	
	public Kpoints(double[][] _points) {
		
		for (int i=0; i<_points.length; i++) {
			points.add(_points[i]);
			weights.add(1.0);
		}
		
		nPoints = _points.length;
		
	}
	
	public Kpoints(double[][] _points, double[] _weights) {
		
		for (int i=0; i<_points.length; i++) {
			points.add(_points[i]);
			weights.add(_weights[i]);
		}
		
		nPoints = _points.length;
		
	}
	
	// ---------------------------------------------
	// CLEAR
	// ---------------------------------------------

	public void clear() {
		
		points.clear();
		weights.clear();
		nPoints = 0;
		
		kType = Costants.kTBIBA;
		kNname = Costants.kTypesList[kType];
		kGenerator = "";
		
	}
	
	//----------------------------
	//	ADD DELETE
	//----------------------------
	
	public void delKpoint(int index) {
		
		points.remove(index);
		weights.remove(index);
		nPoints--;
		
	}
	
	public void addKpoint(double[] newpoint) {
		
		points.add(newpoint);
		weights.add(1.0);
		nPoints++;
		
	}
	
	public void addKpoint(double[][] newpoints) {
		
		for (int i=0; i<newpoints.length; i++) {
			points.add(newpoints[i]);
			weights.add(1.0/newpoints.length);
			nPoints++;
		}
		
	}

	public void insertKpoint(double[] newpoint, int pos) {
		
		if (pos > nPoints-1) {
			addKpoint(newpoint);
			return;
		}
		
		addKpoint(new double[] {0,0,0});
		
		if (pos >= 0 && pos <  nPoints) {
			
			for (int i=nPoints-1; i>pos; i--) {
				setKpoint(i,points.get(i-1).clone());
			}
			
		}
		
		setKpoint(pos, newpoint);
		
	}
	
	//----------------------------
	//	Getters
	//----------------------------
	
	public double[] getKpoint(int index) {
		
		return points.get(index);
		
	}
	
	public double getKweight(int index) {
		
		return weights.get(index);
		
	}
	
	public double[][] toDoubleArray() {
		
		double[][] out = new double[nPoints][3];
		
		for (int i=0; i<nPoints; i++) {
			out[i][0] = points.get(i)[0];
			out[i][1] = points.get(i)[1];
			out[i][2] = points.get(i)[2];
		}
		
		return out;
		
	}
	
	//----------------------------
	//	Setters
	//----------------------------
	
	public void setKpoint(int index, double[] newpoint) {
		
		points.get(index)[0] = newpoint[0];
		points.get(index)[1] = newpoint[1];
		points.get(index)[2] = newpoint[2];
		
	}
	
	public void setWeigth(int index, double value) {
		
		weights.set(index, value);
		
	}
	
	public void setKvalue(int index, int pos, double value) {
		
		points.get(index)[pos] = value;
		
	}

	//----------------------------
	//	CONVERTER
	//----------------------------
		
	public void convert(int toCoord, Cell cell){
		
		if (kType==toCoord) {
			return;
		}
		
		for (int i =0; i<nPoints; i++) {
			
			double[] output = points.get(i).clone();
			
			switch (toCoord) {
				case Costants.kTBIBA: output = convertCrystalToAlat(output, cell); break;
				case Costants.kCRYSTAL: output = convertAlatToCrystal(output, cell); break;
			}
		
			points.set(i, output);
		}
		
		kType = toCoord;
	}
	

	public static double[] convertCrystalToAlat(double[] coord, Cell _cell) {
		
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += _cell.getReciprocalTranspose()[i][j]*coord[j];
			}
		}
		return point;
		
	}
	
	public static double[] convertAlatToCrystal(double[] coord, Cell _cell) {
		
		if (coord.length > 3) {
			return new double[] {0,0,0};
		}
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += _cell.getReciprocalTransposeInverse()[i][j]*coord[j];
			}
		}
		return point;
	}
		
	
	public void rescaleCellA() {
		
		
	}

	//----------------------------------
	//
	//----------------------------------
	
	public double[] spanKPath(boolean lastRepeated) {
		
		double[] out = new double[lastRepeated ? this.points.size()+1 : this.points.size()];
		double step = 0;
		
		out[0] = 0;
		for (int i=1; i<this.points.size(); i++) {
			
			double dx = (this.points.get(i)[0]-this.points.get(i-1)[0]);
			double dy = (this.points.get(i)[1]-this.points.get(i-1)[1]);
			double dz = (this.points.get(i)[2]-this.points.get(i-1)[2]);
			double drSq = dx*dx + dy*dy + dz*dz;
			double dr = Math.sqrt(drSq);
			
			step+=dr;
			out[i] = step;
			
		}
		
		if (lastRepeated) {
			
			int lastIndex = this.points.size()-1;
			double dx = (this.points.get(lastIndex)[0]-this.points.get(0)[0]);
			double dy = (this.points.get(lastIndex)[1]-this.points.get(0)[1]);
			double dz = (this.points.get(lastIndex)[2]-this.points.get(0)[2]);
			double drSq = dx*dx + dy*dy + dz*dz;
			double dr = Math.sqrt(drSq);
			out[this.points.size()] =  out[lastIndex] + dr;
		}
		
		return out;
		
		
	}
	
	public boolean repeatedPointExist() {
		
		boolean out = false;
		
		for (int i =0; i<nPoints-1; i++) {
			
			for (int j = i+1; j<nPoints; j++) {
				
				if (ArrayTools.isEqual(points.get(i), points.get(j))) out = true;
				
				
			}
		}
		
		return out;
	}

	/**
	 * @return the points
	 */
	public ArrayList<double[]> getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(ArrayList<double[]> points) {
		this.points = points;
	}

	/**
	 * @return the weights
	 */
	public ArrayList<Double> getWeights() {
		return weights;
	}

	/**
	 * @param weights the weights to set
	 */
	public void setWeights(ArrayList<Double> weights) {
		this.weights = weights;
	}

	/**
	 * @return the nPoints
	 */
	public int getNPoints() {
		return nPoints;
	}

	/**
	 * @param points the nPoints to set
	 */
	public void setNPoints(int points) {
		nPoints = points;
	}

	/**
	 * @return the kType
	 */
	public int getKType() {
		return kType;
	}

	/**
	 * @param type the kType to set
	 */
	public void setKType(int type) {
		kType = type;
	}

	/**
	 * @return the kGenerator
	 */
	public String getKGenerator() {
		return kGenerator;
	}

	/**
	 * @param generator the kGenerator to set
	 */
	public void setKGenerator(String generator) {
		kGenerator = generator;
	}
	
	//----------------------------
	//	GETTERS / SETTERS - BEAN
	//----------------------------

	
	
}
