/**
 * 
 */
package org.moka.qeReader;

/**
 * @author riki
 *
 */
public class PWOneOutput {
	
	static private final double Ry = 0.5291772;
	
	public double aCell = 1, eFin, magnTot, eFermi;
	int kNum;
	public Double[][] cell = new Double[3][3];
	public Double[][] positions;// = new ArrayList<Double[]>();
	public String [] names;
	
	public int nAtoms = 1;
	
	PWOneOutput(int _natoms, 
				double _acell,
				double _en,
				Double[][] _cell,
				Double[][] _pos) {
		
		this.positions = _pos;
		this.eFin = _en;
		this.nAtoms = _natoms;
		this.aCell = _acell;
		this.cell = _cell;
				
		
	}
	
	public double getEtot() {return eFin;}
	public double magnEtot() {return magnTot;}
	
	public Double[][] getPositions() {return positions;}
	public Double[][] getCell() {return cell;}
	
}
