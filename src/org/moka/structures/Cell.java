package org.moka.structures;

import java.io.Serializable;
import org.moka.common.Costants;
import org.jscience.mathematics.vector.*;
import org.moka.tools.MathTools;
import org.moka.tools.ArrayTools;


public class Cell implements Serializable {
	
	/**
	 * 
	 */
	
	double[][] cellBase =  new double[3][3];
	
	double[][] cellAU =  new double[3][3];
	double[][] cellAngstrom =  new double[3][3];
	double[][] cellReciprocal =  new double[3][3];
	
	int type = 0;
	double[] parameters = new double[6];
	
	
	String idCell = "";
	static String idSeparator = ":";
	
	//-------------------------------
	//	Inizializzatori
	//-------------------------------
	
	public  Cell (int _type, double[] _parameters, double[][] _cellManual) {
		
		this.type = _type;
		this.parameters = _parameters.clone();
		
		//CellBase data a amano nel caso type = 0
		if (type == 0) {
			this.cellBase = _cellManual.clone();
			updateAll();
		} else
		{ 
			if (genCellBase()) {
				updateAll();
			} else {
				cellBase=null;
			}
		}
		
	}
	
	public  Cell (int _type, double[] _parameters) {
		
		this.type = _type;
		this.parameters = _parameters.clone();
		
		if (genCellBase()) {
			updateAll();
		} else {
			cellBase=null;
		}
		
		
	}
	
	public  Cell (double[][] _cellAngtrom) {
		
		this.type = 0;
		cellBase = _cellAngtrom.clone();
		this.parameters[0]=Costants.cSPECIALPARAM;
		
		updateAll();
		
	}
	
	public Cell() {
		
		this.type = 1;
		this.parameters = new double[] {10.0,0.0,0.0,0.0,0.0,0.0};
		
		genCellBase();
		
		updateAll();
		
	}

	//-------------------------------
	//	CLEAR
	//-------------------------------
	
	public void clear() {
		
		this.type = 1;
		this.parameters = new double[] {10.0,0.0,0.0,0.0,0.0,0.0};
		
		genCellBase();
		
		updateAll();
		
	}
	
	public void genidCell() {
		
		idCell = "";
		idCell += String.format("%.4f",parameters[0]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[0][0]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[0][1]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[0][2]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[1][0]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[1][1]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[1][2]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[2][0]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[2][1]) + idSeparator;
		idCell += String.format("%.4f",cellAngstrom[2][2]) + idSeparator;
	}
	
	public double[] toCartesian(double[] value) {

		 double[] points = new double[] {(cellAngstrom[0][0]*value[0]+cellAngstrom[1][0]*value[1]+cellAngstrom[2][0]*value[2]),
								  		 (cellAngstrom[0][1]*value[0]+cellAngstrom[1][1]*value[1]+cellAngstrom[2][1]*value[2]),
								  		 (cellAngstrom[0][2]*value[0]+cellAngstrom[1][2]*value[1]+cellAngstrom[2][2]*value[2])};
		 return points;
		
	}
	
	//-------------------------------
	//	Generatori celle
	//-------------------------------
	
	public void updateAll() {
		
		genCellAU();
		genCellAngstrom();
		
		genReciprocal();
		
		genidCell();
	}
	
	public boolean genCellBase() {
		
		for (int i=0; i<cellBase.length; i++ ){
			for (int j=0; j<cellBase[i].length; j++ ){
				cellBase[i][j] = 0;
			}
		}
		
		
		if (type == 1) { // sc simple cubic
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = 0; cellBase[1][1] = 1; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = 1;
			
			return true;
			
		} else
		if (type == 2) { // fcc face centered cubic
			
			cellBase[0][0] = -0.5; cellBase[0][1] = 0; cellBase[0][2] = 0.5;
			cellBase[1][0] = 0; cellBase[1][1] = 0.5; cellBase[1][2] = 0.5;
			cellBase[2][0] = -0.5; cellBase[2][1] = 0.5; cellBase[2][2] = 0;
			
			return true;
			
		} else	
		if (type == 3) { // bcc body entered cubic
			
			cellBase[0][0] = 0.5; cellBase[0][1] = 0.5; cellBase[0][2] = 0.5;
			cellBase[1][0] = -0.5; cellBase[1][1] = 0.5; cellBase[1][2] = 0.5;
			cellBase[2][0] = -0.5; cellBase[2][1] = -0.5; cellBase[2][2] = 0.5;
			
			return true;
			
		} else
		if (type == 4) { // simple hexagonal and trigonal(p)
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = -0.5; cellBase[1][1] = Math.sqrt(3)/2; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = parameters[2];
			
			return true;
			
		} else
		if (type == 5) { // trigonal(r)
			
			if (parameters[3]<-1 || parameters[3]>=1) {
				return false;
			}
			
			double tx = Math.sqrt((1-parameters[3])/2);
			double ty = Math.sqrt((1-parameters[3])/6);
			double tz = Math.sqrt((1+2*parameters[3])/3);
			
			cellBase[0][0] = tx; cellBase[0][1] = -ty; cellBase[0][2] = tz;
			cellBase[1][0] = 0; cellBase[1][1] = 2*ty; cellBase[1][2] = tz;
			cellBase[2][0] = -tx; cellBase[2][1] = -ty; cellBase[2][2] = tz;
			
			return true;
			
		} else
		if (type == 6) { // simple tetragonal (p)
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = 0; cellBase[1][1] = 1; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = parameters[2];
			
			return true;
			
		} else
		if (type == 7) { // body centered tetragonal (i)
			
			cellBase[0][0] = 0.5; cellBase[0][1] = -0.5; cellBase[0][2] = parameters[2]/2;
			cellBase[1][0] = 0.5; cellBase[1][1] = 0.5; cellBase[1][2] = parameters[2]/2;
			cellBase[2][0] = -0.5; cellBase[2][1] = -0.5; cellBase[2][2] = parameters[2]/2;
			
			return true;
			
		} else
		if (type == 8) { // simple orthorhombic (p)
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = 0; cellBase[1][1] = parameters[1]; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = parameters[2];
			
			return true;
			
		} else
		if (type == 9) { // bco base centered orthorhombic
			
			cellBase[0][0] = 0.5; cellBase[0][1] = parameters[1]/2; cellBase[0][2] = 0;
			cellBase[1][0] = -0.5; cellBase[1][1] = parameters[1]/2; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = parameters[2];
			
			return true;
			
		} else
		if (type == 10) { // face centered orthorhombic
			
			cellBase[0][0] = 0.5; cellBase[0][1] = 0; cellBase[0][2] = parameters[2]/2;
			cellBase[1][0] = 0.5; cellBase[1][1] = parameters[1]/2; cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = parameters[1]/2; cellBase[2][2] = parameters[2]/2;
			
			return true;
			
		} else
		if (type == 11) { // body centered orthorhombic
			
			cellBase[0][0] = 0.5; cellBase[0][1] = parameters[1]/2; cellBase[0][2] = parameters[2]/2;
			cellBase[1][0] = -0.5; cellBase[1][1] = parameters[1]/2; cellBase[1][2] = parameters[2]/2;
			cellBase[2][0] = -0.5; cellBase[2][1] = -parameters[1]/2; cellBase[2][2] = parameters[2]/2;
			
			return true;
			
		} else
		if (type == 12) { // monoclinic (p)
			
			if (parameters[3]<-1 || parameters[3]>1) return false;
			
			double gamma = Math.acos(parameters[3]);
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = parameters[1]*Math.cos(gamma); cellBase[1][1] = parameters[1]*Math.sin(gamma); cellBase[1][2] = 0;
			cellBase[2][0] = 0; cellBase[2][1] = 0; cellBase[2][2] = parameters[2];
			
			return true;
			
		} else 
		if (type == 13) { // base centered monoclinic
			
			if (parameters[3]<-1 || parameters[3]>1) return false;
			
			double gamma = Math.acos(parameters[3]);
			
			cellBase[0][0] = 0.5; cellBase[0][1] = 0; cellBase[0][2] = -parameters[2]/2;
			cellBase[1][0] = parameters[1]*Math.cos(gamma); cellBase[1][1] = parameters[1]*Math.sin(gamma); cellBase[1][2] = 0;
			cellBase[2][0] = 0.5; cellBase[2][1] = 0; cellBase[2][2] = parameters[2]/2;
			
			return true;
			
		} else 
		if (type == 14) { // triclinic
			
			if (parameters[3]<-1 || parameters[3]>1 || 
					parameters[4]<-1 || parameters[4]>1 || 
					parameters[5]<-1 || parameters[5]>1) return false;
			
			double alpha = Math.acos(parameters[3]);
			double beta = Math.acos(parameters[4]);
			double gamma = Math.acos(parameters[5]);
			
			cellBase[0][0] = 1; cellBase[0][1] = 0; cellBase[0][2] = 0;
			cellBase[1][0] = parameters[1]*Math.cos(gamma); cellBase[1][1] = parameters[1]*Math.sin(gamma); cellBase[1][2] = 0;
			
			cellBase[2][0] = parameters[2]*Math.cos(beta); 
			cellBase[2][1] = parameters[2]*(Math.cos(alpha)-Math.cos(beta)*Math.cos(gamma))/(Math.sin(gamma)); 
			cellBase[2][2] = parameters[2]*Math.sqrt(1+2*Math.cos(alpha)*Math.cos(beta)*Math.cos(gamma) - (Math.cos(beta)*Math.cos(beta))-(Math.cos(alpha)*Math.cos(alpha))-(Math.cos(gamma)*Math.cos(gamma)))/(Math.sin(gamma));
			
			
			return true;
		}
		
		return false;
	}
	
	public void genCellAU( ) {
			
		cellAU = MathTools.arrayMultiply(cellBase, parameters[0]);	
			
	}
	
	public void genCellAngstrom( ) {
		
		cellAngstrom = MathTools.arrayMultiply(cellBase, parameters[0]*Costants.Ry);	
			
	}
	
	public void genReciprocal() {

		//cellBaseM = new Matrix(cellBase);
		//cellReciprocalM = cellBaseM.transpose().inverse();
		
		//--ok-- cellReciprocal = ((new Matrix(cellBase)).transpose().inverse()).getArray();
        cellReciprocal = ArrayTools.getArrayFromMatrix(Float64Matrix.valueOf(cellBase).transpose().inverse());
		
	}

	//-------------------------------
	//	TRASFORMAZIONI
	//-------------------------------
	
	public double[][] getReciprocalTranspose() {
		
		//--ok-- return (new Matrix(cellBase).transpose().inverse()).transpose().getArray();
        return ArrayTools.getArrayFromMatrix(Float64Matrix.valueOf(cellBase).inverse().transpose());

	}
	
	public double[][] getReciprocalTransposeInverse() {
		
		//--ok--return (new Matrix(cellBase).transpose().inverse()).transpose().inverse().getArray();

        return ArrayTools.getArrayFromMatrix( (Float64Matrix.valueOf(cellBase).transpose().inverse()).transpose().inverse());
		
	}

	public double[][] getCellBaseTranspose() {
		//-ok-return (new Matrix(cellBase)).transpose().getArray();
        return ArrayTools.getArrayFromMatrix(Float64Matrix.valueOf(cellBase).transpose());
	}
	
	public double[][] getCellBaseTransposeInverse() {
		//-ok-return (new Matrix(cellBase)).transpose().inverse().getArray();
        return ArrayTools.getArrayFromMatrix(Float64Matrix.valueOf(cellBase).transpose().inverse());
	}

    //-------------------------------
	//	TOOLS
	//-------------------------------

	public void makeSuperCell(int[] dims) {

		if (dims[0]<1) dims[0]=1;
		if (dims[1]<1) dims[1]=1;
		if (dims[2]<1) dims[2]=1;
		
		double[][] cellTemp =	new double[3][3];
		
		for (int i=0; i<3; i++){
			cellTemp[i][0] = dims[i]*this.cellBase[i][0];
			cellTemp[i][1] = dims[i]*this.cellBase[i][1];
			cellTemp[i][2] = dims[i]*this.cellBase[i][2];
		}
		
		this.cellBase = cellTemp.clone();
		
		//Trasforma tutto in una cella manuale
		this.type=0;
		
		updateAll();
		
	}

    public boolean isPointInCell(double[] in, int pointCoordType) {

        double[] inAlat = new double[in.length];

        switch (pointCoordType) {
            case Costants.cCRYSTAL: inAlat = convertCrystalToAlat(in); break;
            case Costants.cANGSTROM: inAlat = convertAngstromToAlat(in); break;
		}

        // Usa matrici e vettori per il calcolo
        double[][] cellBflip = MathTools.arrayFlip(cellBase);

        Float64Matrix cellBaseM = Float64Matrix.valueOf(cellBflip);
        Float64Vector sV = cellBaseM.inverse().times(Float64Vector.valueOf(inAlat));

        double[] s = ArrayTools.getArrayFromVector(sV);

        // Sposta gli atomi fuoiri dal box 0,1
        boolean isInCell = true;

        for (int i=0; i<s.length; i++) {
            if (s[i] < 0 || s[i] >1) isInCell = false;
        }

        return isInCell;

    }

    //-------------------------------
	//	CALCULATION
	//-------------------------------

   	public double[] getCenter() {

		double[] center = new double[3];

		center[0] = (cellBase[0][0]+cellBase[1][0]+cellBase[2][0])*parameters[0]/3;
		center[1] = (cellBase[0][1]+cellBase[1][1]+cellBase[2][1])*parameters[0]/3;
		center[2] = (cellBase[0][2]+cellBase[1][2]+cellBase[2][2])*parameters[0]/3;

		return center;
	}

	public double getDiag() {

		return Math.sqrt(getCenter()[0]*getCenter()[0]+getCenter()[1]*getCenter()[1] + getCenter()[2]*getCenter()[2])*2;
	}

    public double getAxLengthAU(int _ax){

        return Math.sqrt(Math.pow(cellAU[_ax][0],2) + Math.pow(cellAU[_ax][1],2) + Math.pow(cellAU[_ax][2],2));

    }

    public double getAxLengthAngstrom(int _ax){

        return Math.sqrt(Math.pow(cellAngstrom[_ax][0],2) +
                            Math.pow(cellAngstrom[_ax][1],2) +
                            Math.pow(cellAngstrom[_ax][2],2));

    }

    public double getAxAngleWithXYZ(int _ax, int _xyz) {

        return Math.acos(cellAngstrom[_ax][_xyz]/getAxLengthAngstrom(_ax));

    }

    //-------------------------------
    //  CONVERTERS
    //-------------------------------
    
    public double[] convertVector(int inCoord, int outCoord, double[] coord) {

        double[] output = coord.clone();

		switch (inCoord) {
            case Costants.cCRYSTAL: output = convertCrystalToAlat(output); break;
            case Costants.cANGSTROM: output = convertAngstromToAlat(output); break;
		}

		switch (outCoord) {
            case Costants.cCRYSTAL: output = convertAlatToCrystal(output); break;
            case Costants.cANGSTROM: output = convertAlatToAngstrom(output); break;
		}

		return output;
    }

	public double[] convertAlatToCrystal(double[] coord) {

		if (coord.length > 3) {
			return new double[] {0,0,0};
		}
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += getCellBaseTransposeInverse()[i][j]*coord[j];
			}
		}
		return point;
	}

	public double[] convertAlatToAngstrom(double[] coord) {

		double[] out = coord;
		for (int i=0; i<3; i++) {

			out[i] *= getParameters()[0]*Costants.Ry;

		}
		return out;
	}

	public double[] convertCrystalToAlat(double[] coord) {

		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += getCellBaseTranspose()[i][j]*coord[j];
			}
		}
		return point;

	}

	public double[] convertCrystalToAngstrom(double[] coord) {

		double[] point = convertCrystalToAlat(coord);
		return convertAlatToAngstrom(point);

	}

	public double[] convertAngstromToAlat(double[] coord) {

		double[] out = coord;

		for (int i=0; i<3; i++) {
			out[i] /= getParameters()[0]*Costants.Ry;
		}
		return out;

	}

	public double[] convertAngstromToCrystal(double[] coord) {

		double[] point = convertAngstromToAlat(coord);
		return convertAlatToCrystal(point);

	}

    public double[] convertKpoint(int toCoord, double[] coord){

		double[] output = coord.clone();

		switch (toCoord) {
		case Costants.kTBIBA: output = convertKpointCrystalToAlat(output); break;
		case Costants.kCRYSTAL: output = convertKpointAlatToCrystal(output); break;
		}

		return output;
	}

	public double[] convertKpointCrystalToAlat(double[] coord) {

		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += getReciprocalTranspose()[i][j]*coord[j];
			}
		}
		return point;

	}

	public double[] convertKpointAlatToCrystal(double[] coord) {

		if (coord.length > 3) {
			return new double[] {0,0,0};
		}
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += getReciprocalTransposeInverse()[i][j]*coord[j];
			}
		}
		return point;
	}

	//-------------------------------
	//	NEEDED FOR SAVING STAUS
	//-------------------------------
	
	/**
	 * @return the cellBase
	 */
	public double[][] getCellBase() {
		return cellBase;
	}

	/**
	 * @param cellBase the cellBase to set
	 */
	public void setCellBase(double[][] cellBase) {
		this.cellBase = cellBase;
	}

	/**
	 * @return the cellAU
	 */
	public double[][] getCellAU() {
		return cellAU;
	}

	/**
	 * @param cellAU the cellAU to set
	 */
	public void setCellAU(double[][] cellAU) {
		this.cellAU = cellAU;
	}

	/**
	 * @return the cellAngstrom
	 */
	public double[][] getCellAngstrom() {
		return cellAngstrom;
	}

	/**
	 * @param cellAngstrom the cellAngstrom to set
	 */
	public void setCellAngstrom(double[][] cellAngstrom) {
		this.cellAngstrom = cellAngstrom;
	}

	/**
	 * @return the cellReciprocal
	 */
	public double[][] getCellReciprocal() {
		return cellReciprocal;
	}

	/**
	 * @param cellReciprocal the cellReciprocal to set
	 */
	public void setCellReciprocal(double[][] cellReciprocal) {
		this.cellReciprocal = cellReciprocal;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the parameters
	 */
	public double[] getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(double[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the idCell
	 */
	public String getIdCell() {
		return idCell;
	}

	/**
	 * @param idCell the idCell to set
	 */
	public void setIdCell(String idCell) {
		this.idCell = idCell;
	}


	
	
}
