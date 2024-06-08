package org.moka;
import org.moka.structures.Module;
import org.moka.common.Costants;

import mathParser.EvaluationTree;
import mathParser.MathParser;
import mathParser.Variable;

/**
 * 
 */

/**
 * @author riki
 *
 */
public class Mathematics implements Module {

	MathParser parser = new MathParser();
	Variable xVariable, yVariable;
	EvaluationTree function;
	
	Moka app;
	
	public Mathematics(Moka _app) {
		
		this.app = _app; 
		
		xVariable = parser.getVariable("x");
		yVariable = parser.getVariable("y");
		function = parser.parse("y = 0");
		
		parser.addConstant("ry", 0.5291772);
		
	}
	
	public void fillParser() {
		
		if (app.workConf==null) {
			return;
		}
		
		for (int i=0; i< app.workConf.getNAtoms(); i++) {
			
			
			parser.addConstant("a"+(i+1)+"x", app.workConf.getAtomPos(i)[0]);
			parser.addConstant("a"+(i+1)+"y", app.workConf.getAtomPos(i)[1]);
			parser.addConstant("a"+(i+1)+"z", app.workConf.getAtomPos(i)[2]);
			
			double distance = Math.sqrt(app.workConf.getAtomPos(i)[0]*app.workConf.getAtomPos(i)[0] + app.workConf.getAtomPos(i)[1]*app.workConf.getAtomPos(i)[1] + app.workConf.getAtomPos(i)[2]*app.workConf.getAtomPos(i)[2]);
			parser.addConstant("a"+(i+1)+"-dist", distance);

		}
		
		if (app.workConf.getCell()==null) {
			return;
		}
		
		parser.addConstant("ca", app.workConf.getCell().getParameters()[0]);
		for (int i=0; i< 3; i++) {
			if (app.workConf.getCoordType() == Costants.cALAT) {
				parser.addConstant("c"+(i+1)+"x", app.workConf.getCell().getCellAU()[i][0]);
				parser.addConstant("c"+(i+1)+"y", app.workConf.getCell().getCellAU()[i][1]);
				parser.addConstant("c"+(i+1)+"z", app.workConf.getCell().getCellAU()[i][2]);
			} else if (app.workConf.getCoordType() == Costants.cANGSTROM) {
				parser.addConstant("c"+(i+1)+"x", app.workConf.getCell().getCellAngstrom()[i][0]);
				parser.addConstant("c"+(i+1)+"y", app.workConf.getCell().getCellAngstrom()[i][1]);
				parser.addConstant("c"+(i+1)+"z", app.workConf.getCell().getCellAngstrom()[i][2]);
			} else if (app.workConf.getCoordType() == Costants.cCRYSTAL) {
				if (i==0) {
					parser.addConstant("c"+(i+1)+"x", 1);
					parser.addConstant("c"+(i+1)+"y", 0);
					parser.addConstant("c"+(i+1)+"z", 0);	
				}
				if (i==1) {
					parser.addConstant("c"+(i+1)+"x", 0);
					parser.addConstant("c"+(i+1)+"y", 1);
					parser.addConstant("c"+(i+1)+"z", 0);
				}
				if (i==2) {
					parser.addConstant("c"+(i+1)+"x", 0);
					parser.addConstant("c"+(i+1)+"y", 0);
					parser.addConstant("c"+(i+1)+"z", 1);
				}
				
			}
			
		}
		
		
	}
	
	public double evaluateEq(String _in) {
		
		function = parser.parse("y = " + _in);
		return function.evaluate();
		
	}
	
	public void confChanged() {

		fillParser();
	}

	public void reset() {

		fillParser();
	}

	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		fillParser();
	}

	/* (non-Javadoc)
	 * @see MokaMOD#reciveMouseSelection(int)
	 */
	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	} 
	
}
