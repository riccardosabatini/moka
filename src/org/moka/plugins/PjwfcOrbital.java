/**
 * 
 */
package org.moka.plugins;

import org.moka.tools.AtomTools;

/**
 * @author riki
 *
 */
public class PjwfcOrbital {
	
	double[][] wfcAll;
	double[] energy;
	double[] projection;
	double[][] projectionUpDown;
	
	public double charge;
	double[] chargeUpDown;
	
	String atomName;
	int orbitalIndex;
	//int id;
	boolean spin = false;
	
	public PjwfcOrbital(String _atomName, int _orbital, double[][] _wfc, boolean isSpin) {
		
		this.orbitalIndex = _orbital;
		this.atomName = _atomName;
		this.wfcAll = _wfc;
		this.spin = isSpin;
		this.chargeUpDown = new double[2];
		this.energy = wfcAll[0];
		
		projection = new double[energy.length];
		projectionUpDown = new double[2][energy.length];
		
		for (int i=0; i<energy.length; i++) {
			
			if (spin) {
				
				projection[i] = wfcAll[1][i]+wfcAll[2][i];
				
				projectionUpDown[0][i] = wfcAll[1][i];
				projectionUpDown[1][i] = wfcAll[2][i];
				
			} else {
				projection[i] = wfcAll[1][i];
			}
		}
		
	}
	
	//public void setId(int _id) { this.id = _id	;}
	

	//-----------------------------------
	//	Getters
	//-----------------------------------
	
	public String getAtomName() { return this.atomName;}
	
	public String getOrbitalName() { return AtomTools.getPjwfcType(orbitalIndex);}
	
	public int getOrbitalIndex() { return this.orbitalIndex;}
	
	//public int getId() { return id;}
	
	public double[] getEnergy() { return this.energy; }

	//-----------------------------------
	//	Projection
	//-----------------------------------
	
	public double[] getProjection() { return this.projection; }
	
	public double[] getProjectionUpDown(int which) { return this.projectionUpDown[which]; }

	//-----------------------------------
	//	Charge
	//-----------------------------------
	
	public double getCharge() { return charge; }
	
	public double getChargeSpin(int spin) { return chargeUpDown[spin]; }
	
	public boolean isSpin() { return this.spin; }
	
}
