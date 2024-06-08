/**
 * 
 */
package org.moka.plugins;

import java.util.ArrayList;

/**
 * @author riki
 *
 */
public class PhononConfiguration {
	
	
	public ArrayList<Double> omega;// = new ArrayList<Double>();
	
	public int maxNomega = 1;
	
	public PhononConfiguration(ArrayList<Double> _omega, 
			int _max) {
		
		this.omega = _omega;
		
		this.maxNomega = _max;
		
			
	}
	
	
		
	public ArrayList<Double> getOmega() {return omega;}
	
	public int getNomegaMax() {return maxNomega;}
	
	public int size() {return omega.size();}
	
}
