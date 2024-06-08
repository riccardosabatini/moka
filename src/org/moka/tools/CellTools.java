/**
 * 
 */
package org.moka.tools;

import Jama.Matrix;

/**
 * @author riki
 *
 */
public class CellTools {
	public static boolean isInCorner(double[][] cell, double[] pos){
		
		double[][] corners = new double [8][];
		
		corners[0] = new double[] {0,0,0};
		
		corners[1] = new double[] {cell[0][0],cell[0][1],cell[0][2]};
		corners[2] = new double[] {cell[1][0],cell[1][1],cell[1][2]};
		corners[3] = new double[] {cell[2][0],cell[2][1],cell[2][2]};
		
		corners[4] = new double[] {cell[0][0]+cell[1][0],
								   cell[0][1]+cell[1][1],
								   cell[0][2]+cell[1][2]};
		
		corners[5] = new double[] {cell[0][0]+cell[2][0],
				  				   cell[0][1]+cell[2][1],
				  				   cell[0][2]+cell[2][2]};
		
		corners[6] = new double[] {cell[1][0]+cell[2][0],
								   cell[1][1]+cell[2][1],
								   cell[1][2]+cell[2][2]};
		
		corners[7] = new double[] {cell[0][0]+cell[1][0]+cell[2][0],
								   cell[0][1]+cell[1][1]+cell[2][1],
								   cell[0][2]+cell[1][2]+cell[2][2]};
		
		boolean isFound = false;
		for (int i=0; i<8; i++){
			if (corners[i].equals(pos)) { isFound = true;}
		}

		return isFound;
		
	}
	
	public static double[] toCrystalCoord(double[][] cell, double[] vect) {
		
		double[] res = new double[3];
		
		//System.out.println("crystal");
		Matrix cellDimM = new Matrix(cell);
		Matrix toPlaceM = new Matrix(vect, vect.length);				
		Matrix toPlaceMcellBase = cellDimM.inverse().transpose().times(toPlaceM); 
		
		double[] toPlaceCellBase = toPlaceMcellBase.getColumnPackedCopy();
		res[0]=toPlaceCellBase[0];
		res[1]=toPlaceCellBase[1];
		res[2]=toPlaceCellBase[2];
		
		return res;
	
	}
	
	public static double[] toCrystalCoord(double[][] cell, Double[] vect) {
		
		double[] temp = new double[vect.length];
		for (int i =0; i<temp.length; i++ ){
			temp[i] = vect[i];
		}
		return toCrystalCoord(cell, temp);
	}
	
	public static double[] toAlatCoordinate(double[][] cell, double[] vect) {
		
		double[] res = new double[3];
		
		//System.out.println("crystal");
		Matrix cellDimM = new Matrix(cell);
		Matrix toPlaceM = new Matrix(vect, vect.length);				
		Matrix toPlaceMalat = cellDimM.times(toPlaceM); 
		
		double[] toPlaceCellBase = toPlaceMalat.getColumnPackedCopy();
		res[0]=toPlaceCellBase[0];
		res[1]=toPlaceCellBase[1];
		res[2]=toPlaceCellBase[2];
		
		return res;
	
	}

	public static double[][] makeMonkhorstPackgrid (double[][] cell, int n1, int n2, int n3, double j1, double j2, double j3){
		
		int totPoints = n1*n2*n3;
		double[][] k = new double[totPoints][3];
		
		double[] g1 = cell[0];
		double[] g2 = cell[1];
		double[] g3 = cell[2];
		
		int kStep = 0;
		for (int i1=0; i1<n1; i1++){
			for (int i2=0; i2<n2; i2++){
				for (int i3=0; i3<n3; i3++){
					for (int coord = 0; coord<3; coord++) {
						
						k[kStep][coord]  = (i1+j1/2) * g1[coord]/n1 + (i2+j2/2) * g2[coord]/n2 + (i3+j3/2) * g3[coord]/n3;
						
					}
					kStep++;
				}
			}
		}
		
		return k;
	}
	
	public static double[] getCorner(int corner, double[][] _inCell) {
		
		double[][] angles = new double[8][3];
		
		angles[0] = new double[] {0,0,0};
		
		for (int i=0; i<3; i++) {
			angles[i+1] = new double[] {_inCell[i][0], _inCell[i][1], _inCell[i][2]};
		}
		
		angles[4] = new double[] {(_inCell[0][0]+_inCell[1][0]), (_inCell[0][1]+_inCell[1][1]), (_inCell[0][2]+_inCell[1][2])};
		angles[5] = new double[] {(_inCell[0][0]+_inCell[2][0]), (_inCell[0][1]+_inCell[2][1]), (_inCell[0][2]+_inCell[2][2])};
		
		
		angles[6] = new double[] {(_inCell[1][0]+_inCell[2][0]), (_inCell[1][1]+_inCell[2][1]), (_inCell[1][2]+_inCell[2][2])};
		
		double sumX = _inCell[0][0]+_inCell[1][0]+_inCell[2][0];
		double sumY = _inCell[0][1]+_inCell[1][1]+_inCell[2][1];
		double sumZ = _inCell[0][2]+_inCell[1][2]+_inCell[2][2];
		
		angles[7] = new double[] {sumX, sumY, sumZ}; 
		
		return angles[corner];
		
	}
	
}
