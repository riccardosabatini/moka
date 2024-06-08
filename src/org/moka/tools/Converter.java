package org.moka.tools;

import org.moka.common.Costants;
import org.moka.structures.Cell;

public class Converter {

	//-------------------------------
	//	Convertitori
	//-------------------------------
	
	public static double[] convert(int fromCoord, int toCoord, double[] coord, Cell cell){
		
		double[] output = coord.clone();
		
		switch (fromCoord) {
		case Costants.cCRYSTAL: output = convertCrystalToAlat(output, cell); break;
		case Costants.cANGSTROM: output = convertAngstromToAlat(output, cell); break;
		}
		
		switch (toCoord) {
		case Costants.cCRYSTAL: output = convertAlatToCrystal(output, cell); break;
		case Costants.cANGSTROM: output = convertAlatToAngstrom(output, cell); break;
		}
		
		return output;
	}
	
	//--------------------
	//	ALAT ->
	//--------------------
	
	//Converte coordinaet alat a crystal
	public static double[] convertAlatToCrystal(double[] coord, Cell cell) {
		
		if (coord.length > 3) {
			return new double[] {0,0,0};
		}
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += cell.getCellBaseTransposeInverse()[i][j]*coord[j];
			}
		}
		return point;
	}
	
	//Converte da alat a angstrom
	public static double[] convertAlatToAngstrom(double[] coord, Cell cell) {
		
		double[] out = coord;
		for (int i=0; i<3; i++) {
			
			out[i] *= cell.getParameters()[0]*Costants.Ry;
			
		}
		return out;
	}
	
	//--------------------
	//	CRYSTAL ->
	//--------------------
	
	//Converte da crystal coordinate ad alat
	public static double[] convertCrystalToAlat(double[] coord, Cell cell) {
		
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += cell.getCellBaseTranspose()[i][j]*coord[j];
			}
		}
		return point;
		
	}
	
	public static double[] convertCrystalToAngstrom(double[] coord, Cell cell) {
		
		double[] point = convertCrystalToAlat(coord, cell);
		return convertAlatToAngstrom(point, cell);
		
	}

	//--------------------
	//	ANGSTROM ->
	//--------------------
	
	public static double[] convertAngstromToAlat(double[] coord, Cell cell) {
		
		double[] out = coord;
		
		for (int i=0; i<3; i++) {
			out[i] /= cell.getParameters()[0]*Costants.Ry;
		}
		return out;
		
	}

	public static double[] convertAngstromToCrystal(double[] coord, Cell cell) {
		
		double[] point = convertAngstromToAlat(coord,cell);
		return convertAlatToCrystal(point,cell);
		
	}

	
	public static double[] convertKpoint(int toCoord, double[] coord, Cell cell){
		
		double[] output = coord.clone();
		
		switch (toCoord) {
		case Costants.kTBIBA: output = convertKpointCrystalToAlat(output, cell); break;
		case Costants.kCRYSTAL: output = convertKpointAlatToCrystal(output, cell); break;
		}
		
		return output;
	}
	

	public static double[] convertKpointCrystalToAlat(double[] coord, Cell _cell) {
		
		double[] point = new double[3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				point[i] += _cell.getReciprocalTranspose()[i][j]*coord[j];
			}
		}
		return point;
		
	}
	
	public static double[] convertKpointAlatToCrystal(double[] coord, Cell _cell) {
		
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
	
	
}
