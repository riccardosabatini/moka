/**
 * 
 */
package org.moka.plugins;

import java.util.ArrayList;

import org.moka.tools.AtomTools;


/**
 * @author riki
 *
 */
public class PjwfcConfiguration {
	
	public ArrayList<PjwfcOrbital[]> orbitals;
	
	public ArrayList<Double> atomCharge;
	public ArrayList<Double> atomChargeUp, atomChargeDown;
	
	public PjwfcConfiguration() {
		
		orbitals = new ArrayList<PjwfcOrbital[]>();
		
		atomCharge = new ArrayList<Double>();
		atomChargeUp = new ArrayList<Double>();
		atomChargeDown = new ArrayList<Double>();
	}
	
	public PjwfcConfiguration(ArrayList<PjwfcOrbital[]> _atomWfc) {
		
		this.orbitals = _atomWfc;
		
		atomCharge = new ArrayList<Double>();
		atomChargeUp = new ArrayList<Double>();
		atomChargeDown = new ArrayList<Double>();

	}
	
	//----------------------------------------------
	//	Set charges
	//----------------------------------------------
	
	public void setOrbitalCharge(int idAtom, double[] _charge, String[] _names) {

		atomCharge.add(idAtom, _charge[0]);
		
		for (int i=1; i<_charge.length; i++) {
			
			for (int j=0; j<orbitals.get(idAtom).length; j++) {
			
				if (orbitals.get(idAtom)[j].orbitalIndex == AtomTools.getPjwfcType(_names[i])) {
					orbitals.get(idAtom)[j].charge = _charge[i]; 
				}
			}
		}
		
	}
	
	public void setOrbitalChargeSpin(int idAtom, double[] _charge, String[] _names, int spin) {
	
		if (spin==0) {
			atomChargeUp.add(idAtom, _charge[0]);
		} else {
			atomChargeDown.add(idAtom, _charge[0]);
		}
		
		for (int i=1; i<_charge.length; i++) {
			
			for (int j=0; j<orbitals.get(idAtom).length; j++) {
			
				if (orbitals.get(idAtom)[j].orbitalIndex == AtomTools.getPjwfcType(_names[i])) {
					orbitals.get(idAtom)[j].chargeUpDown[spin] = _charge[i];  
				}
			}
		}
		
		
	}

	
	//----------------------------------------------
	//	Get orbitrals
	//----------------------------------------------
	
	public PjwfcOrbital[] getAtomAllOrbital(int idAtom) {

		return orbitals.get(idAtom);
	}
	
	public PjwfcOrbital getAtomOrbital(int idAtom, String orbital) {
		
		return getAtomOrbital(idAtom, AtomTools.getPjwfcType(orbital));
		
	}
	
	public PjwfcOrbital getAtomOrbital(int idAtom, int orbital) {
		
		PjwfcOrbital[] temp = getAtomAllOrbital(idAtom);
		
		for (int i=0; i<temp.length; i++) {
			if (temp[i].getOrbitalIndex()==orbital) {
				return temp[i];
			}
		}
		return null;
		
	}
	
	//----------------------------------------------
	//	Get charge
	//----------------------------------------------
		
	public Double getTotCharge(int idAtom) {
	
		if (atomCharge==null) return null;
		
		if (idAtom<atomCharge.size()) {
			return atomCharge.get(idAtom);
		} else {
			return null;
		}
	
	}
	
	public double getChargeSpin(int idAtom, int spin) {
		
		if (spin==0) {
			
			return atomChargeUp.get(idAtom);
			
		} else {
			
			return atomChargeDown.get(idAtom);
			
		}

	}
	
	
	
	public double[] getEnergy(int idAtom){
		
		return	getAtomAllOrbital(idAtom)[0].getEnergy();
		
	}
	

	
}
