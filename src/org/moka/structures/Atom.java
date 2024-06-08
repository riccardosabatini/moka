/**
 * 
 */
package org.moka.structures;

import java.io.Serializable;

import org.moka.common.Costants;

/**
 * @author riki
 *
 */
public class Atom implements Serializable {
	
	double[] position = new double[3];
	int[] flags = new int[6];
	String name = "";
	String element = "";
	
	public Atom(){
		
		this.position = new double[] {0,0,0};
		this.flags = new int[] {0,0,0,0,0,0};
		this.name = "H";
		this.element = "H";
		
	}
	
	public Atom(double[] _pos, String _name){
		
		this.position = _pos;
		this.name = _name;
		this.element = _name;
		this.flags = new int[] {0,0,0,0,0,0};
		
	}
	
	public Atom(double[] _pos, String _name, String _element){
		
		this.position = _pos;
		this.name = _name;
		this.element = _element;
		this.flags = new int[] {0,0,0,0,0,0};
		
	}
	
	public Atom(double[] _pos, String _name, int[] _flags){
		
		this.position = _pos;
		this.name = _name;
		this.element = _name;
		this.element = _name;
		this.flags = _flags;
		
	}
	
	public Atom(double[] _pos, String _name, String _element, int[] _flags){
		
		this.position = _pos;
		this.name = _name;
		this.element = _name;
		this.element = _element;
		this.flags = _flags;
		
	}
	
	
	public static void copyAtom(Atom _source, Atom _dest){
		//Svuota i vettori
		if (_dest == null) _dest = new Atom();
		
		_dest.position = _source.position;
		_dest.name = _source.name;
		_dest.element = _source.element;
		
	}
	
	//---------------------------------------------
	//	GETTER
	//---------------------------------------------

	public double[] getPos() {
		return this.position;
	}
	
	//---------------------------------------------
	//	TRASLATE
	//---------------------------------------------

	
	public void traslateAtomCoord(double[] vect) {
		
		position[0] += vect[0];
		position[1] += vect[1];
		position[2] += vect[2];
		
	}
		
//	//---------------------------------------------
//	//	ROTATE
//	//---------------------------------------------
//
//	public void rotateAtomCoordAboutAxis(double[] rotAx, double[] center, double angle) {
//
//
//        Point3d posP = new Point3d(position[0], position[1], position[2]);
//
//        Transform3D trasl = new Transform3D();
//        trasl.setTranslation(new Vector3d(-center[0],-center[1],-center[2]));
//        trasl.transform(posP);
//
//        Transform3D rot = new Transform3D();
//        rot.setRotation(new AxisAngle4d(rotAx[0],rotAx[1],rotAx[2], angle));
//        rot.transform(posP);
//
//        trasl.invert();
//        trasl.transform(posP);
//		//position = MathTools.rotate(position, point, angles[0], angles[1], angles[2]);
//        position = new double[] {posP.x, posP.y, posP.z};
//
//	}
	
	//---------------------------------------------
	//	SETTERS
	//---------------------------------------------

	
	public void setPosition(double[] values) {
		
		position = values;
		
	}
	
	public double[] getPosition() {
		return position;
	}

	public int[] getFlags() {
		return flags;
	}

	public String getName() {
		return name;
	}

	public String getElement() {
		return element;
	}

	public void setPosition(int idCoord, double value) {
		
		position[idCoord] = value;
		
	}
	
	public void setName(String value) {
		name = value;
	}
	
	public void setElement( String value) {
		element = value;
	}
	
	public void setFlags(int[] values) {
		flags = values;
	}
	
	public void setFlag(int flag, int value) {
		flags[flag] = value;
	}
	
	public boolean isFlagged(int flag) {
		if (this.flags[flag]==1) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getFlag(int flag) {
		return this.flags[flag];
	}
	
	//-----SELECT
	
	public void selectAtom() {
		this.flags[Costants.aSELECTED] = 1;
	}
	
	public void unSelectAtom() {
		this.flags[Costants.aSELECTED] = 0;
	}
	
	public boolean isSelected() {
		if (flags[Costants.aSELECTED]==1) {
			return true;
		} else {
			return false;
		}
	}

	//------BLOCK
	
	public void blockAtom() {
		flags[Costants.aBLOCKED] = 1;
	}
	
	public void unBlokAtom() {
		flags[Costants.aBLOCKED] = 0;
	}
	
	public boolean isBlocked() {
		if (flags[Costants.aBLOCKED]==1) {
			return true;
		} else {
			return false;
		}
	}
	
}
