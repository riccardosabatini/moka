/**
 * 
 */
package org.moka.plugins;

import java.util.ArrayList;
import java.util.Arrays;

import org.moka.tools.ArrayTools;

/**
 * @author riki
 *
 */
public class MD {
	
	static private final double Ry = 0.5291772;
	
	ArrayList<Double> time;// = new ArrayList<Double>();
	ArrayList<Double> temp;// = new ArrayList<Double>();
	ArrayList<Double> ekin;// = new ArrayList<Double>();
	ArrayList<Double> ekinpot;// = new ArrayList<Double>();
	ArrayList<Double> etot;// = new ArrayList<Double>();
	ArrayList<String> names;// = new ArrayList<String>();
	ArrayList<Double[]> positions;// = new ArrayList<Double[]>();
	Double[][] cell = new Double[3][3];
	
	
	public int nAtoms = 1, steps = -1, numRDFgot=0;;
	public double aCell = 1, dr;
	public int[] topTenPos = new int[10];
	public double[] RDFAccum, rdtAll;
	public double[][] rdtEach;
	
	public MD(ArrayList<Double> _time, 
			ArrayList<Double>_temp, 
			ArrayList<Double> _ekin, 
			ArrayList<Double> _ekinpot,
			ArrayList<Double> _etot,
			ArrayList<String> _names,
			ArrayList<Double[]> _pos,
			int _natoms, double _acell, Double[][] _cell, int _steps) {
		
		this.time = _time;
		this.temp = _temp;
		this.ekin = _ekin;
		this.ekinpot = _ekinpot;
		this.etot = _etot;
		this.names = _names;
		this.positions = _pos;
		
		this.steps = _steps;
		this.nAtoms = _natoms;
		this.aCell = _acell;
		this.cell = _cell;
				
		makeTopTen(etot);
		
	}
	
	public void makeTopTen(ArrayList<Double> ref) {
		
		double oldEn = 0;
		
		
		for (int i=0; i<steps; i++) {
			
			double d = ref.get(i);
			int j=0;
			int toShift = 10;
			boolean out = false;
			
			do {
				if (Math.abs(d)>=Math.abs(etot.get(this.topTenPos[j]))) {
					toShift = j;
					out = true; }
				j++;
			} while (j<10 && out == false);
			
			for (int k = 9; k>toShift; k--) {
				this.topTenPos[k]=this.topTenPos[k-1];
			}
			if (toShift<10) {
				this.topTenPos[toShift]=i;	
			}
			
			oldEn = d;
		}	
		
	}
	
	public void makeRDF(int divs) {
		

		double maxx = cell[0][0]+cell[1][0]+cell[2][0];
		double maxy = cell[0][1]+cell[1][1]+cell[2][1];
		double maxz = cell[0][2]+cell[1][2]+cell[2][2];


		this.dr = (Math.sqrt(maxx*maxx + maxy*maxy + maxz*maxz)*aCell*Ry)/(double)divs;
		this.RDFAccum = new double[divs];
		
		this.numRDFgot=0;
		
		for (int k = 0; k< steps; k++){
			for (int i=0; i<nAtoms-1; i++){
				for (int j=i+1; j<nAtoms; j++){
			
					double dx = positions.get(k+i)[0] - positions.get(j)[0];
					double dy = positions.get(k+i)[1] - positions.get(j)[1];
					double dz = positions.get(k+i)[2] - positions.get(j)[2];
					
					double r2 =(dx*dx + dy*dy + dz*dz);
					double r = Math.sqrt(r2)*aCell*Ry;
					
					int bin = (int) (r/dr);
	
					this.RDFAccum[bin] ++;
				}
			}
			this.numRDFgot++;
		}
	}

	public void makeRdt() {

		rdtEach = new double[nAtoms][steps];
		rdtAll = new double[steps*nAtoms];
		
		
		for (int i=0; i<steps; i++){
			
			
			for (int j=0; j<nAtoms; j++){
				
				Double[] oldPos = new Double[3];
				
				if (i>0){
					oldPos = ((Double [])positions.get((i-1)*nAtoms + j));
				} else {
					oldPos = ((Double [])positions.get(i*nAtoms +j));
				}
				Double[] newPos = new Double[3]; 
				newPos =	(Double [])positions.get(i*nAtoms + j);
				
				double R = Math.sqrt(Math.pow((newPos[0]),2) + Math.pow((newPos[1]),2) + Math.pow((newPos[2]),2));
				double Rdtau = Math.sqrt(Math.pow((newPos[0]-oldPos[0]),2) + Math.pow((newPos[1]-oldPos[1]),2) + Math.pow((newPos[2]-oldPos[2]),2));
				
				rdtAll[i*nAtoms+j] = Rdtau*aCell*Ry;
				rdtEach[j][i] = Rdtau*aCell*Ry;
				
			}			
		}
		
	}
	
	public ArrayList<Double> getTime() {return time;}
	public ArrayList<Double> getTemp() {return temp;}
	public ArrayList<Double> getEkin() {return ekin;}
	public ArrayList<Double> getEkinpot() {return ekinpot;}
	public ArrayList<Double> getEtot() {return etot;}
	public ArrayList<String> getNames() {return names;}
	public ArrayList<Double[]> getPositions() {return positions;}
	
}
