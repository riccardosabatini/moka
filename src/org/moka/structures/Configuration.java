/**
 * 
 */
package org.moka.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.moka.common.Costants;
import org.jscience.mathematics.vector.Float64Matrix;
import org.jscience.mathematics.vector.Float64Vector;
import org.moka.tools.ArrayTools;
import org.moka.tools.GeomTools;
import org.moka.tools.MathTools;

public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3631746515108503L;

    ArrayList<Atom> atoms = new ArrayList<Atom>();
	ArrayList<Drawable> drawList = new ArrayList<Drawable>();
	Kpoints kpoints;

	// Cella per configurazione
	Cell cell;

	// valore usato solo in vettore di configurazione
	//public int isNewCell = 0;

	int coordType = 0;
	int nAtoms = 0;

	AtomData data = new AtomData();
	String name = "";
	String notes = "";
	String drawSpecs = "";
	
	public Configuration() {

		this.nAtoms = 0;
		this.coordType = Costants.cALAT;
		this.cell = new Cell();

	}
	
	public Configuration(int _nAtoms) {

		for (int i=0; i<_nAtoms; i++){
			atoms.add(new Atom());
		}
		this.nAtoms = _nAtoms;
		this.coordType = Costants.cALAT;
		
		this.cell = new Cell();

	}


	public Configuration(ArrayList<double[]> _positions, int _nAtoms, int _type,
			ArrayList<String> _names) {

		atoms.clear();
		for (int i = 0; i < _nAtoms; i++) {
			atoms.add(new Atom(_positions.get(i), _names.get(i),
					Costants.emptyFlags.clone()));
		}

		this.nAtoms = _nAtoms;
		this.coordType = _type;

	}

	public Configuration(ArrayList<double[]> _positions, int _nAtoms, int _type,
			ArrayList<String> _names, ArrayList<int[]> _flags) {

		atoms.clear();
		for (int i = 0; i < _nAtoms; i++) {
			atoms.add(new Atom(_positions.get(i), _names.get(i), _flags
					.get(i)));
		}

		this.nAtoms = _nAtoms;
		this.coordType = _type;

	}

	public Configuration(double[][] _positions) {

		atoms.clear();
		this.nAtoms = _positions.length;
		
		for (int i = 0; i < nAtoms; i++) {
			atoms.add(new Atom(_positions[i], "H"));
		}

		this.coordType = Costants.cANGSTROM;

	}
	
	public String getAtomsID() {

		String out = "";
		for (int i = 0; i < nAtoms; i++) {
			out += getAtomElement(i);
		}

		return out;

		// MessageDigest m=MessageDigest.getInstance("MD5");
		// m.update(out.getBytes(),0,out.length());
		// return new BigInteger(1,m.digest()).toString(16);

	}

	
	// ---------------------------------------------
	// ADD DELETE
	// ---------------------------------------------

	public void addAtom() {
		
		atoms.add(nAtoms,new Atom());
		nAtoms+=1;
	}

	public void addAtom(String name, double[] pos) {
		
		atoms.add(nAtoms,new Atom(pos, name));
		nAtoms+=1;
		
	}
	
	public void addAtom(String name, String element, double[] pos) {
		
		atoms.add(nAtoms,new Atom(pos, name, element));
		nAtoms+=1;
		
	}

    public void addAtom(Atom _in) {

        atoms.add(_in);
        nAtoms+=1;
    }

	public void delAtom(int idAtom) {

		Atom.copyAtom(atoms.get(atoms.size()- 1), atoms.get(idAtom));
		// atoms.remove(nAtoms-1);
		atoms.remove(atoms.get(atoms.size()- 1));
		nAtoms = atoms.size();
		
	}

	public void delAtom(int[] idAtoms) {
		
		Arrays.sort(idAtoms);

		for (int i = idAtoms.length-1; i>=0 ; i--) {
			
			for (int j=idAtoms[i]; j<atoms.size()-1; j++){
				
				Atom.copyAtom(atoms.get(j+1), atoms.get(j));

			}
			atoms.remove(atoms.get(atoms.size()- 1));
		}
		// atoms.remove(nAtoms-1);

		nAtoms = atoms.size();
	}

    public void delAllAtoms() {

        atoms.clear();
        nAtoms = 0;

    }

    // ---------------------------------------------
	// CLEAR
	// ---------------------------------------------

	public void clear() {
		
		atoms.clear();
		drawList.clear();
		kpoints.clear();

		// Cella per configurazione resettata
		cell.clear();

		coordType = 0;
		nAtoms = 0;

		data.clear();
		name = "";
		notes = "";
		drawSpecs = "";
		
	}
	
    //---------------------------------------------
	// STATUS
	//---------------------------------------------
	
	public boolean hasAtoms() {
		
		if (this.getNAtoms()>0) return true;
		return false;
		
	}
	
	public boolean hasDrawables() {
		
		if (this.getNumDrawable()>0) return true;
		return false;
		
	}
	
	public boolean hasOptions() {
		
		if (this.getDrawSpecs()!=null || 
				!this.getDrawSpecs().equals("")) return true;
		return false;
		
	}
	
	// ---------------------------------------------
	// COPY
	// ---------------------------------------------

	public static void copyStructure(Configuration _source, Configuration _dest) {

		// Svuota i vettori
		if (_dest == null)
			_dest = new Configuration();

		if (_dest.atoms != null)
			_dest.atoms.clear();

		// Copia numero atomi e altri valori interi
		_dest.nAtoms = _source.nAtoms;
		_dest.coordType = _source.coordType;

		// Copia i dati
		_dest.name = _source.name;
		_dest.data.names = _source.data.names.clone();
		_dest.data.types = _source.data.types.clone();
		_dest.data.values = _source.data.values.clone();

		// Copia i punti k
		_dest.kpoints = _source.kpoints;
		
		// Copia le note		
		_dest.notes = _source.notes;
		_dest.drawSpecs = _source.drawSpecs;

		// Copia le posizioni di tutti gli atomi
		for (int i = 0; i < _source.nAtoms; i++) {
			_dest.atoms.add(new Atom(_source.atoms.get(i).position.clone(),
					_source.atoms.get(i).name,_source.atoms.get(i).element, _source.atoms.get(i).flags.clone()));
		}

		// Copia la cella
		if (_source.cell.type == 0) {
			_dest.cell = new Cell(_source.cell.type, _source.cell.parameters.clone(),
					_source.cell.cellBase.clone());
		} else {
			_dest.cell = new Cell(_source.cell.type, _source.cell.parameters.clone());
		}

		// Gli oggetti
		if (_dest.drawList!=null) _dest.drawList.clear();
			
		for (int i = 0; i < _source.getNumDrawable(); i++) {
			
			_dest.addDrawable( new Drawable(_source.getDrawable(i).getType(), 
											_source.getDrawable(i).getName(), 
											_source.getDrawable(i).getPoints(), 
											_source.getDrawable(i).getOffset(), 
											_source.getDrawable(i).getPercentual(), 
											_source.getDrawable(i).getColor(),
											_source.getDrawable(i).getAlpha(),
											_source.getDrawable(i).isActive()));
		}
	}

	// ---------------------------------------------
	//  POSITIONS
	// ---------------------------------------------

	public int getAtomFromPos(double[] pos) {

		for (int i = 0; i < nAtoms; i++) {
			
			double dx = Math.abs(atoms.get(i).getPos()[0] - pos[0]);
			double dy = Math.abs(atoms.get(i).getPos()[1] - pos[1]);
			double dz = Math.abs(atoms.get(i).getPos()[2] - pos[2]);

			if (dx < Costants.minDist && dy < Costants.minDist && dz < Costants.minDist) {
				return i;
			}
		}
		return -1;
	}

	public double[] getAtomPos(int idAtoms) {

		return atoms.get(idAtoms).position.clone();
	}

    public double[] getAtomPos(int idAtoms, int outCoordType) {

        double[] output = getAtomPos(idAtoms).clone();

		switch (getCoordType()) {
            case Costants.cCRYSTAL: output = cell.convertCrystalToAlat(output); break;
            case Costants.cANGSTROM: output = cell.convertAngstromToAlat(output); break;
		}

		switch (outCoordType) {
            case Costants.cCRYSTAL: output = cell.convertAlatToCrystal(output); break;
            case Costants.cANGSTROM: output = cell.convertAlatToAngstrom(output); break;
		}

		return output;
	}
    
    public double[][] getAtomPosAll() {
    	
    	return getAtomPosAll(this.getCoordType());
    	
    }
    
    public double[][] getAtomPosAll(int outCoordType) {

        double[][] output = new double[this.getNAtoms()][3];
        
        for (int i=0; i<this.getNAtoms(); i++) {
        	
        	output[i] = getAtomPos(i,outCoordType).clone();
        	
        }
        
		return output;
	}

    public double[] getAtomPosPBC(int idAtom) {

        return getAtomPosPBC(idAtom, getCoordType());

    }

    public double[] getAtomPosPBC(int idAtom, int outCoordType) {

        // Traforma il vettore in ALAT
        double[] in = getAtomPos(idAtom, Costants.cALAT);

        // Usa matrici e vettori per il calcolo
        double[][] cellBflip = MathTools.arrayFlip(cell.cellBase);

        Float64Matrix cellBaseM = Float64Matrix.valueOf(cellBflip);
        Float64Vector inV = Float64Vector.valueOf(in);
        Float64Vector sV = cellBaseM.inverse().times(inV);

        double[] s = ArrayTools.getArrayFromVector(sV);

        // Sposta gli atomi fuoiri dal box 0,1
        for (int i=0; i<s.length; i++) s[i] = MathTools.pbcPosition(s[i], 1);

        // Rispalma sulla cella
        sV = Float64Vector.valueOf(s);
        cellBaseM = Float64Matrix.valueOf(cellBflip);
        Float64Vector outV = cellBaseM.times(sV);

        double[] output = ArrayTools.getArrayFromVector(outV);

        // Converte nell'unita' voluta
        switch (outCoordType) {
            case Costants.cCRYSTAL: output = cell.convertAlatToCrystal(output); break;
            case Costants.cANGSTROM: output = cell.convertAlatToAngstrom(output); break;
		}

        return output;
    }

    public double[][] getAtomPosPBCAll() {
    	
    	return getAtomPosPBCAll(this.getCoordType());
    	
    }
    
    public double[][] getAtomPosPBCAll(int outCoordType) {

    	double[][] output = new double[this.getNAtoms()][3];
        
        for (int i=0; i<this.getNAtoms(); i++) {
        	
        	output[i] = getAtomPosPBC(i,outCoordType).clone();
        	
        }
        
		return output;
	}
    
    public void setAtomPos(int idAtom, double[] values) {

		atoms.get(idAtom).setPosition(values);
	}

	public void setAtomPos(int idAtom, int idCoord, double value) {

		atoms.get(idAtom).setPosition(idCoord, value);
	}

    // ---------------------------------------------
    // --- FLAGS
    // ---------------------------------------------

	public int[] getAtomsFlagged(int flagType) {

		int numFlagged = 0;

		for (int i = 0; i < nAtoms; i++) {
			if (atoms.get(i).flags[flagType] == 1) {
				numFlagged++;
			}
		}
		int[] output = new int[numFlagged];

		numFlagged = 0;
		for (int i = 0; i < nAtoms; i++) {
			if (atoms.get(i).flags[flagType] == 1) {
				output[numFlagged] = i;
				numFlagged++;
			}
		}

		return output;
	}

    public int[] getAtomsSelected() {

        return getAtomsFlagged(Costants.aSELECTED);
    }

	public int getAtomFlag(int idAtom, int flag) {
		return atoms.get(idAtom).getFlag(flag);
	}

	public void resetAtomFlags(){

		if (atoms==null) {
			return;
		}

		for (int i=0; i< nAtoms; i++) {
			atoms.get(i).flags = Costants.emptyFlags;
		}

	}

	public void clearFlags(int flagToClear){

		for (int i=0; i<nAtoms; i++) {
			atoms.get(i).flags[flagToClear] = 0;
		}

	}

    public void setAtomFlags(int idAtom, int[] values) {
		atoms.get(idAtom).setFlags(values);
	}

	public void setAtomFlag(int idAtom, int flag, int value) {
		atoms.get(idAtom).setFlag(flag, value);
	}

    //---------------------------------------------
	// SELECT
	//---------------------------------------------

	public boolean isAtomFlagged(int idAtom, int flag) {
		return atoms.get(idAtom).isFlagged(flag);
	}

	public void selectAtom(int idAtom) {

		atoms.get(idAtom).selectAtom();

	}

	public void unSelectAtom(int idAtom) {

		atoms.get(idAtom).unSelectAtom();

	}

	public boolean isAtomSelected(int idAtom) {

		return atoms.get(idAtom).isSelected();

	}

	public void blockAtom(int idAtom) {

		atoms.get(idAtom).blockAtom();

	}

	public void unBlockAtom(int idAtom) {

		atoms.get(idAtom).unBlokAtom();

	}

	public boolean isAtomBlocked(int idAtom) {

		return atoms.get(idAtom).isBlocked();

	}

    // ---------------------------------------------
    // --- OTEHRS
    // ---------------------------------------------

	public String getAtomName(int idAtom) {
		return atoms.get(idAtom).name;
	}
	
	public String[] getAtomNameAll() {
	
		String[] output = new String[this.getNAtoms()];
        
        for (int i=0; i<this.getNAtoms(); i++) {
        	
        	output[i] = getAtomName(i);
        	
        }
        
		return output;
		
	}
	
    public void setAtomName(int idAtom, String value) {

		atoms.get(idAtom).setName(value);

	}

	public String getAtomElement(int idAtom) {
		return atoms.get(idAtom).element;
	}

	public String[] getAtomElementAll() {
		
		String[] output = new String[this.getNAtoms()];
        
        for (int i=0; i<this.getNAtoms(); i++) {
        	
        	output[i] = getAtomElement(i);
        	
        }
        
		return output;
		
	}
	
	public void setAtomElement(int idAtom, String value) {

		atoms.get(idAtom).setElement(value);

	}

	// ---------------------------------------------
	// TRASLATE COORDINATES
	// ---------------------------------------------

	public void traslateAtomCoord(int idAtom, double[] vect) {

		atoms.get(idAtom).traslateAtomCoord(vect);

	}

	public void traslateAtomCoord(int[] idAtomList, double[] vect) {

		for (int i = 0; i < idAtomList.length; i++) {
			atoms.get(idAtomList[i]).traslateAtomCoord(vect);
		}

	}

	// ---------------------------------------------
	// ROTATE COORDINATES
	// ---------------------------------------------

	public void rotateAtomCoordAboutAxis(int[] atomsToMove, double[] rotAx, double[] center, double angle) {

//		double[] bar = new double[3];
//
//		for (int i = 0; i < atomsToMove.length; i++) {
//			bar[0] += atoms.get(atomsToMove[i]).position[0]
//					/ atomsToMove.length;
//			bar[1] += atoms.get(atomsToMove[i]).position[1]
//					/ atomsToMove.length;
//			bar[2] += atoms.get(atomsToMove[i]).position[2]
//					/ idAtomatomsToMoveList.length;
//		}

		for (int i = 0; i < atomsToMove.length; i++) {

            //setAtomPos(i, r1.getTransform(getAtomPos(i).clone()).toDouble()) ;
			//atoms.get(atomsToMove[i]).rotateAtomCoordAboutAxis(rotAx, center, angle);
            
            double[] newPos = GeomTools.getRotateAboutAxis(getAtomPos(atomsToMove[i]).clone(), rotAx, center, angle);
            setAtomPos(atomsToMove[i], newPos);
            
		}

	}

    public double[] getBaricenterSelected(int[] selAtoms) {

        return getBaricenterSelected(selAtoms, getCoordType());

    }

    public double[] getBaricenterSelected(int[] selAtoms, int coordTypeOut) {

        double[] bar = new double[3];

            for (int i=0; i<selAtoms.length; i++) {

                double[] coordAng = new double[3];

                if (coordTypeOut==-1) {
                    coordAng = getAtomPos(selAtoms[i]);
                } else {
                    coordAng = getAtomPos(selAtoms[i], coordTypeOut);
                }
                
                bar[0] += coordAng[0]/selAtoms.length;
                bar[1] += coordAng[1]/selAtoms.length;
                bar[2] += coordAng[2]/selAtoms.length;

            }

        return bar;

    }
	
	public void allineateCell(){

        allineateCell(0, 1);
		
	}
	
	public void allineateCell(int axes1, int axes2) {
		
		
		double[] angles = new double[] {0,0,0};
		
		//Allineamento primo asse con coordianta X
		if (cell.cellBase[axes1][0]!=0 && cell.cellBase[axes1][1]!=0) {
			
			angles[2] = cell.cellBase[axes1][1]/cell.cellBase[axes1][0];
			angles[2] = -Math.atan(angles[2])*(360/(2*Math.PI));
			
		} 
		
		if (cell.cellBase[axes1][0]!=0 && cell.cellBase[axes1][2]!=0) {
			angles[1] = cell.cellBase[axes1][2]/cell.cellBase[axes1][0];
			angles[1] = -Math.atan(angles[1])*(360/(2*Math.PI));
		
		}
		
		if (cell.cellBase[axes1][0]==0 && cell.cellBase[axes1][1]==0 ) {	//Asse lungo Z
			
			if (cell.cellBase[axes1][2]>0 ) angles[1]=90;
			if (cell.cellBase[axes1][2]<0 ) angles[1]=-90;
			
		}
				
		if (cell.cellBase[axes1][0]==0 && cell.cellBase[axes1][2]==0) { //Asse lungo Y
		
			if (cell.cellBase[axes1][1]>0 ) angles[2]=-90;	//Asse lungo Y positivo
			if (cell.cellBase[axes1][1]<0 ) angles[2]=90;
		
		}
		
//		Rotation r1 = new Rotation(1, 0, 0, angles[0]);
//        r1.combine(new Rotation(0,1,0,angles[1]));
//        r1.combine(new Rotation(0,0,1,angles[2]));

		for (int i = 0; i < 3; i++) {
            
			// cell.cellBase[i] = r1.getTransform(cell.cellBase[i]).toDouble();
            // MathTools.rotate(cell.cellBase[i], new double[] {0,0,0}, angles[0], angles[1], angles[2]);
            GeomTools.rotateAboutPoint(cell.cellBase[i], new double[] {0,0,0}, angles[0], angles[1], angles[2]);
        }
		
		cell.updateAll();
		
		for (int i = 0; i < nAtoms; i++) {

            //setAtomPos(i, r1.getTransform(getAtomPos(i).clone()).toDouble()) ;

            setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {1,0,0}, new double[] {0,0,0}, angles[0]));
			setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {0,1,0}, new double[] {0,0,0}, angles[1]));
            setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {0,0,1}, new double[] {0,0,0}, angles[2]));

            //atoms.get(i).rotateAtomCoordAboutAxis(new double[] {1,0,0}, new double[] {0,0,0}, angles[0]);
//            atoms.get(i).rotateAtomCoordAboutAxis(new double[] {0,1,0}, new double[] {0,0,0}, angles[1]);
//            atoms.get(i).rotateAtomCoordAboutAxis(new double[] {0,0,1}, new double[] {0,0,0}, angles[2]);
		}
		
		//Rotazione secondo asse sul piano XY
		
		angles = new double[] {0,0,0};
		
		if (cell.cellBase[axes2][1]!=0 && cell.cellBase[axes2][2]!=0) {
			angles[0] = cell.cellBase[axes2][2]/cell.cellBase[axes2][1];
			angles[0] = -Math.atan(angles[0])*(360/(2*Math.PI));
		
		}
		
		if(cell.cellBase[axes2][2]==0) {	//Asse lungo Y
			
			if (cell.cellBase[axes2][1]<0 ) angles[0]=180;
		
		}
		
		if(cell.cellBase[axes2][1]==0) {	//Asse lungo Z
			
			if (cell.cellBase[axes2][2]>0 ) angles[0]=-90;
			if (cell.cellBase[axes2][2]<0 ) angles[0]=90;
		
		}

//        r1 = new Rotation(1, 0, 0, angles[0]);
//        r1.combine(new Rotation(0,1,0,angles[1]));
//        r1.combine(new Rotation(0,0,1,angles[2]));
		
		for (int i = 0; i < 3; i++) {
            GeomTools.rotateAboutPoint(cell.cellBase[i], new double[] {0,0,0}, angles[0], angles[1], angles[2]);

			//cell.cellBase[i] = r1.getTransform(cell.cellBase[i]).toDouble();
            //cell.cellBase[i] = MathTools.rotate(cell.cellBase[i], new double[] {0,0,0}, angles[0], angles[1], angles[2]);
			}
		cell.updateAll();
        
		for (int i = 0; i < nAtoms; i++) {

            //setAtomPos(i, r1.getTransform(getAtomPos(i).clone()).toDouble()) ;

            setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {1,0,0}, new double[] {0,0,0}, angles[0]));
			setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {0,1,0}, new double[] {0,0,0}, angles[1]));
            setAtomPos(i, GeomTools.getRotateAboutAxis( getAtomPos(i), new double[] {0,0,1}, new double[] {0,0,0}, angles[2]));
            
//			atoms.get(i).rotateAtomCoordAboutAxis(new double[] {1,0,0}, new double[] {0,0,0}, angles[0]);
//            atoms.get(i).rotateAtomCoordAboutAxis(new double[] {0,1,0}, new double[] {0,0,0}, angles[1]);
//            atoms.get(i).rotateAtomCoordAboutAxis(new double[] {0,0,1}, new double[] {0,0,0}, angles[2]);
		}
		
		
	}

	// ---------------------------------------------
	// PBC REFOLD
	// ---------------------------------------------

    public void refoldAtoms(int[] list) {

        for (int i=0; i<list.length; i++) {

            double[] refPos = getAtomPosPBC(list[i]);
            setAtomPos(list[i],refPos);
        }


    }

	// ---------------------------------------------
	// SUPERCELL
	// ---------------------------------------------

	public void addSuperCellAtoms(int[] dims) {

		if (dims[0] < 1)
			dims[0] = 1;
		if (dims[1] < 1)
			dims[1] = 1;
		if (dims[2] < 1)
			dims[2] = 1;

		for (int ia = 0; ia < dims[0]; ia++) {

			for (int ib = 0; ib < dims[1]; ib++) {

				for (int ic = 0; ic < dims[2]; ic++) {

					for (int iatom = 0; iatom < nAtoms; iatom++) {

						if (ia != 0 || ib != 0 || ic != 0) {

							double[] thisPos = getAtomPos(iatom, Costants.cANGSTROM);
                            
							String thisName = atoms.get(iatom).name;
							String thisElement = atoms.get(iatom).element;
							
							thisPos[0] = thisPos[0] + ia
									* cell.cellAngstrom[0][0] + ib
									* cell.cellAngstrom[1][0] + ic
									* cell.cellAngstrom[2][0];
							thisPos[1] = thisPos[1] + ia
									* cell.cellAngstrom[0][1] + ib
									* cell.cellAngstrom[1][1] + ic
									* cell.cellAngstrom[2][1];
							thisPos[2] = thisPos[2] + ia
									* cell.cellAngstrom[0][2] + ib
									* cell.cellAngstrom[1][2] + ic
									* cell.cellAngstrom[2][2];

							thisPos = cell.convertVector(Costants.cANGSTROM, coordType, thisPos);

							atoms.add(new Atom(thisPos, thisName, thisElement));
						}
					}
				}
			}
		}
		nAtoms = nAtoms * (dims[0] * dims[1] * dims[2]);
	}

	// ---------------------------------------------
	// CONVERTERS
	// ---------------------------------------------
	
	public void rescaleCellA(double value) {
		
		
		//Place in safe coordinates
		int thisTypePos = coordType;
		
		int thisTypeK = -1;
		if (kpoints!=null){
			thisTypeK = kpoints.kType;
			convertKpoints(Costants.kCRYSTAL);
			
		}
		
		convertPositions(Costants.cANGSTROM);
		
		double[] newParam = cell.parameters.clone();
		
		double scaleFactor = newParam[0]/value;
		newParam = MathTools.arrayMultiply(newParam, scaleFactor);
		newParam[0] = value;
		
		double[][] newCellBase = cell.cellBase.clone();
		newCellBase = MathTools.arrayMultiply(newCellBase, scaleFactor);
		
		
		this.cell = new Cell(0,newParam,newCellBase);

		//Resotre original coordinates
		convertPositions(thisTypePos);
		if (kpoints!=null) convertKpoints(thisTypeK);
	}
	
	public void convertPositions(int toConvert) {
		
		if (coordType == toConvert) {
			return;
		} else {
			
			for (int i =0; i<nAtoms; i++) {
				
				double[] in = getAtomPos(i);
//				in[0] = app.workConf.positions.get(i)[0];
//				in[1] = app.workConf.positions.get(i)[1];
//				in[2] = app.workConf.positions.get(i)[2];
				
				if (coordType == Costants.cALAT && toConvert == Costants.cCRYSTAL) {
					in = cell.convertAlatToCrystal(in);
				}
				if (coordType == Costants.cALAT && toConvert == Costants.cANGSTROM) {
					in = cell.convertAlatToAngstrom(in);
				}
				if (coordType == Costants.cCRYSTAL && toConvert == Costants.cALAT) {
					in = cell.convertCrystalToAlat(in);
				}
				if (coordType == Costants.cCRYSTAL && toConvert == Costants.cANGSTROM) {
					in = cell.convertCrystalToAngstrom(in);
				}
				if (coordType == Costants.cANGSTROM && toConvert == Costants.cALAT) {
					in = cell.convertAngstromToAlat(in);
				}
				if (coordType == Costants.cANGSTROM && toConvert == Costants.cCRYSTAL) {
					in = cell.convertAngstromToCrystal(in);
				}
				
			double[] out = new double[3];
			out[0] = in[0];
			out[1] = in[1];
			out[2] = in[2];
			
			setAtomPos(i, out);
			
			}
			coordType = toConvert;
		}
		
		
	}
	
	public void convertKpoints(int toCoord) {
		
		kpoints.convert(toCoord, cell);
		
	}


	// ---------------------------------------------
	// DRAWABLES
	// ---------------------------------------------

	public void addDrawable (Drawable _in) {
		
		drawList.add(_in);
		
	}
	
	public void delDrawable (int _id) {
		
		drawList.remove(_id);
		
	}
	
	public Drawable getDrawable (int _id) {
		
		return drawList.get(_id);
		
	}
	
	public int getNumDrawable () {
		if (drawList==null) return 0;
		return drawList.size();
		
	}
	
	public Drawable getLastDrawable () {
		
		return drawList.get(drawList.size()-1);
		
	}
	
	public void setDrawableVisible(int _id, boolean _status) {
		
		drawList.get(_id).setActive(_status);
		
	}
	
	public Boolean isDrawableVisible(int _id) {
			
		return drawList.get(_id).isActive();
		
	}
	

	// ---------------------------------------------
	// CUSTOM GET / SET
	// ---------------------------------------------
	
	public int getTotAtomNames() {

		ArrayList<String> tempNames = new ArrayList<String>();

		int ntyp = 1;
		tempNames.add(atoms.get(0).name);
		for (int i = 1; i < nAtoms; i++) {
			if (!ArrayTools.isInArray(tempNames, atoms.get(i).name)) {
				tempNames.add(atoms.get(i).name);
				ntyp++;
			}
		}

		return ntyp;
	}

    //----------------------------------------------
    // CALCULATIONS
    //----------------------------------------------

    public double getAtomsDistance2(int at1, int at2) {

        return getAtomsDistance2(at1,at2, getCoordType());

    }

    public double getAtomsDistance2(int at1, int at2, int coordType) {

        double[] pos1 = getAtomPos(at1,coordType);
        double[] pos2 = getAtomPos(at2,coordType);

        return Math.sqrt( Math.pow(pos1[0]-pos2[0], 2) +
                          Math.pow(pos1[1]-pos2[1], 2) +
                          Math.pow(pos1[2]-pos2[2], 2));

    }

    public double getAtomsDistancePBC(int at1, int at2) {

        return getAtomsDistancePBC(at1,at2, getCoordType());
        
    }

    public double getAtomsDistancePBC(int at1, int at2, int coordType) {

         // Usa matrici e vettori per il calcolo
        double[][] cellBflip = MathTools.arrayFlip(cell.cellBase);
        Float64Matrix cellBaseM = Float64Matrix.valueOf(cellBflip);
        
        Float64Vector v1 = Float64Vector.valueOf(getAtomPos(at1, Costants.cALAT));
        Float64Vector v2 = Float64Vector.valueOf(getAtomPos(at2, Costants.cALAT));

        Float64Vector s1 = cellBaseM.inverse().times(v1);
        Float64Vector s2 = cellBaseM.inverse().times(v2);

        double[] diff = ArrayTools.getArrayFromVector(s1.minus(s2));

        // Sposta gli atomi fuoiri dal box 0,1
        for (int i=0; i<diff.length; i++) diff[i] = MathTools.pbcSeparation(diff[i],1);

        Float64Vector outV = cellBaseM.times(Float64Vector.valueOf(diff));

        double[] output = ArrayTools.getArrayFromVector(outV);

        // Converte nell'unita' voluta
        switch (coordType) {
            case Costants.cCRYSTAL: output = cell.convertAlatToCrystal(output); break;
            case Costants.cANGSTROM: output = cell.convertAlatToAngstrom(output); break;
		}

        return Math.sqrt( output[0]*output[0] + output[1]*output[1] + output[2]*output[2]);

    }

    public int[] getAtomsInShell(int at1, int numShells, double sensitivity) {

        ArrayList<Integer> found = new ArrayList<Integer>();

        double[] shells = new double[numShells];
        for (int i=0; i<shells.length; i++) {
            shells[i] = -1;
        }


        //
        // Find the closer atoms in the distance
        //
        for (int nDist=0; nDist<shells.length; nDist++) {

            double minDist = Double.MAX_VALUE;

            // Trova la nuova shell
            for (int i=0; i<nAtoms; i++) {

                if (i!= at1) {

                    double atDist = getAtomsDistancePBC(at1, i);

                    boolean disRead = false;
                    for (int k=0; k<shells.length; k++) {
                        if (Math.abs(shells[k]-atDist) < sensitivity) disRead = true;
                    }

                    if (atDist< minDist && !disRead){
                        shells[nDist] = atDist;
                        minDist = atDist;
                    }

                }
            }

            //Carica tutti gli atomi di quella shell

            for (int i=0; i<nAtoms; i++) {
                if (i!= at1) {

                    double atDist = getAtomsDistancePBC(at1, i);

                    if ( Math.abs(atDist-shells[nDist]) < sensitivity && !ArrayTools.isInArray(found, i)) {
                        
                        found.add(i);
                        
                    }

                }
            }

        }

        int[] out = new int[found.size()];
        for (int i=0; i<found.size(); i++) out[i] = found.get(i);

        return out;

    }

    public int[] getAtomsInRadius(int at1, String[] names, double[] radius) {

        ArrayList<Integer> found = new ArrayList<Integer>();

        for (int i=0; i<nAtoms; i++) {

            if (at1!=i) {

                double atDist = getAtomsDistancePBC(at1, i, Costants.cANGSTROM);

                int id1 = ArrayTools.whereIsInArray(names,getAtomElement(at1));
                double r1 = radius[id1];

                int id2 = ArrayTools.whereIsInArray(names,getAtomElement(i));
                double r2 = radius[id2];

                if (atDist<=(r1+r2) && !ArrayTools.isInArray(found, i)) {
                    found.add(i);
                }
            }
            
        }

        int[] out = new int[found.size()];
        for (int i=0; i<found.size(); i++) out[i] = found.get(i);

        return out;

    }

	// ---------------------------------------------
	// GETTERS / SETTERS
	// ---------------------------------------------

	public String getDrawSpecs() {
		return this.drawSpecs;
	}
	
	public void setDrawSpecs(String _in) {
		
		this.drawSpecs = _in;
		
	}

	public void setName(String _name) {
		
		this.name = _name;
		
	}
	
	public String getName() {
		return this.name;
	}

	public ArrayList<Atom> getAtoms() {
		return atoms;
	}

	public void setAtoms(ArrayList<Atom> atoms) {
		this.atoms = atoms;
	}

	public ArrayList<Drawable> getDrawList() {
		return drawList;
	}

	public void setDrawList(ArrayList<Drawable> drawList) {
		this.drawList = drawList;
	}

	public Kpoints getKpoints() {
		return kpoints;
	}

	public void setKpoints(Kpoints kpoints) {
		this.kpoints = kpoints;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public int getCoordType() {
		return coordType;
	}

	public void setCoordType(int coordType) {
		this.coordType = coordType;
	}

	public int getNAtoms() {
		return nAtoms;
	}

	public void setNAtoms(int atoms) {
		nAtoms = atoms;
	}

	public AtomData getData() {
		return data;
	}

	public void setData(AtomData data) {
		this.data = data;
	}

	public String getNotes() {
		return this.notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	

}