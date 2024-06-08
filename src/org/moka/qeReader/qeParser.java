/**
 * 
 */
package org.moka.qeReader;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.moka.plugins.*;
import org.moka.structures.*;
import org.moka.tools.*;
import org.moka.common.*;

import de.schlichtherle.io.FileReader;

/**
 * @author riki
 *
 */
public class qeParser {

	public static Integer intAfterString(String _init, String all) {
		String[] nums = new String[] {"0","1","2","3","4","5","6","7","8","9"};
		
		int initPos = all.indexOf(_init); 
		if (initPos<0) return null;
		
		initPos+=+_init.length();
		
		int i=0;
		String doubleString = "";
		boolean charFound = false;
		
		do {
			
			boolean isNum = ArrayTools.isInArray(nums, (String)all.subSequence(initPos+i, initPos+i+1));
			boolean isAllowedSpace = all.subSequence(initPos+i, initPos+i+1).equals(" ") && doubleString.length()==0;
			
			if (isNum || isAllowedSpace) {
				
				if (isNum) doubleString+=all.subSequence(initPos+i, initPos+i+1);
				i++;
				
			} else {
				charFound = true;
			}
			
			
		} while (!charFound);
		
		if (doubleString.length()==0) return null;
		
		return Integer.parseInt(doubleString);
	}
	
	public static Double doubleAfterString(String _init, String all) {
		
		String[] nums = new String[] {"0","1","2","3","4","5","6","7","8","9"};
		
		int initPos = all.indexOf(_init)+_init.length(); 
		if (initPos<0) return null;
		
		int i=0;
		String doubleString = "";
		boolean charFound = false;
		
		do {
			
			boolean isNum = ArrayTools.isInArray(nums, (String)all.subSequence(initPos+i, initPos+i+1));
			boolean isAllowedSpace = all.subSequence(initPos+i, initPos+i+1).equals(" ") && doubleString.length()==0;
			boolean isSeparator = all.subSequence(initPos+i, initPos+i+1).equals(".") || all.subSequence(initPos+i, initPos+i+1).equals(",");
			
			if (isNum || isAllowedSpace || isSeparator) {
				
				if (isNum || isSeparator) doubleString+=all.subSequence(initPos+i, initPos+i+1);
				i++;
				
			} else {
				charFound = true;
			}
			
			
		} while (!charFound);
		
		if (doubleString.length()==0) return null;
		
		return Double.parseDouble(doubleString);
	}
	
	public static String stringInParanthesis(String _str, int numPar) {
		
		int lastParPos =  _str.indexOf("(");
		
		if (numPar>0) {
			boolean getOut = false;
			
			do {
				
				if (_str.indexOf("(", lastParPos+1)!=-1 && numPar>0){
					lastParPos = _str.indexOf("(", lastParPos+1);
					numPar--;
				} else {
					getOut=true;
				}
				
			} while(!getOut);
			
			if (numPar>0) return null;
		} 
		lastParPos++;
		
		boolean closingParFound = false;
		int i=0;
		String out = "";
		do {
			
			if (!_str.subSequence(lastParPos+i, lastParPos+i+1).equals(")")) {
				out+=_str.subSequence(lastParPos+i, lastParPos+i+1);
				i++;
			} else {
				closingParFound = true;
			}
			
		} while(!closingParFound);
		
		return out;
	}
	
	public static PjwfcConfiguration readFilePJWFC(String filename) throws IOException,NumberFormatException {

		ArrayList<PjwfcOrbital[]> atomWfc = new ArrayList<PjwfcOrbital[]>();
		PjwfcConfiguration output = new PjwfcConfiguration();
		
		String prefix = filename.substring(filename.lastIndexOf("/")+1, filename.length());
		prefix = prefix.substring(0, prefix.indexOf("."));
		//resetta tutto

		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		String thisDir = filename.substring(0, filename.lastIndexOf("/"));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		String atomName = "";
		int thisWfcNum = 0;
		int[] wfcNum = new int[100];
		int[] wfcType = new int[100];
		int nAtoms = 1;
		
		int index;
		do  {

			String[] words = line.replaceAll("\\t", " ").trim().split("\\s+");
			
			if ((index = ArrayTools.whereIsInArray(words, "state"))==0) {

				do {
					int thisAtom = intAfterString("atom", line);
					atomName = 	stringInParanthesis(line,0).trim();//words[5].substring(1, words[5].length());

					for (int i=0; i<wfcNum.length; i++){
						wfcNum[i] = 0;
					}
					
					ArrayList<PjwfcOrbital> temp = new ArrayList<PjwfcOrbital>();
					do{

						//raccoglie quante onde ci sono
						thisWfcNum = intAfterString("wfc", line);//Integer.parseInt(words[8]);
						
						if (wfcNum[thisWfcNum]==0) {
							
							//Collezziona che tipo di funzioni d'onda deve raccogliere (l=0)
							//sString[] parts = words[9].split("=");
							int type=intAfterString("(l=", line);//Integer.parseInt(parts[parts.length-1]);;
							
							temp.add(readAtomWfc( atomName, thisDir, prefix, thisAtom, thisWfcNum, type));
						}
						wfcNum[thisWfcNum]=1;
						
						//Passa alla riga dopo
						line = lnr.readLine();
						//words = line.replaceAll("\\t", " ").trim().split("\\s+");
						if (line.indexOf("(l=")<0) break;

					} while (intAfterString("atom", line)==thisAtom);
					
					
					atomWfc.add(temp.toArray(new PjwfcOrbital[temp.size()]));
					
					nAtoms++;

				} while (intAfterString("state #", line)!=null);
			
				output = new PjwfcConfiguration(atomWfc);
			}
			
			
			
			//LOWDIN charges
			
			if ((index = ArrayTools.whereIsInArray(words, "Lowdin"))==0) {
				
				int idAtom=-1;
				
				do {
					
					line = lnr.readLine();
					words = line.replaceAll("\\t", " ").replaceAll(",", " , ").trim().split("\\s+");
					
					
					
					if ((index = ArrayTools.whereIsInArray(words, "Atom"))==0 &&
							ArrayTools.whereIsInArray(words, "#")==index+1	) {
						
						idAtom++;
						
						double[] charge = new double[ArrayTools.countInArray(words, "=")];
						String[] orbName = new String[ArrayTools.countInArray(words, "=")];
						
						int numOrbitals = 0;
						for (int i=0; i<words.length; i++) {
							if (words[i].equals("=")) {
								
								orbName[numOrbitals] = words[i-1];
								charge[numOrbitals] = Double.parseDouble(words[i+1]);
								
								numOrbitals++;
								
							}
						}
						
						output.setOrbitalCharge(idAtom, charge, orbName);
						
					}
					
					if ((index = ArrayTools.whereIsInArray(words, "spin"))==0) {
						
						double[] charge = new double[ArrayTools.countInArray(words, "=")];
						String[] orbName = new String[ArrayTools.countInArray(words, "=")];
						
						int numOrbitals = 0;
						for (int i=0; i<words.length; i++) {
							if (words[i].equals("=")) {
								
								orbName[numOrbitals] = words[i-1];
								charge[numOrbitals] = Double.parseDouble(words[i+1]);
								
								numOrbitals++;
							}
						}
						
						if ((index = ArrayTools.whereIsInArray(words, "up"))>-1) {
							output.setOrbitalChargeSpin(idAtom, charge, orbName,0);
						} else {
							output.setOrbitalChargeSpin(idAtom, charge, orbName,1);
						}
						
					}
					
				}
				
				while (idAtom<(output.orbitals.size()-1));
				
			}
			
		} while (((line = lnr.readLine()) != null) );

		return output;
	}

	public static PjwfcOrbital readAtomWfc(String name, String dir, String pref, int atom, int nWfc, int wfcType) throws IOException,NumberFormatException {

		int stepE = 0;
		boolean isSpin = false;
		
		String thisFile = dir+"/"+pref+".pdos_atm#"+atom+"("+name.trim()+")_wfc#"+nWfc+"("+AtomTools.getPjwfcType(wfcType)+")";
		LineNumberReader lnr = new LineNumberReader(new FileReader(thisFile));
		lnr.setLineNumber(0);
		String line;

		//trova se ha spin o meno
		int numCols=-1;
		do {
			line = lnr.readLine();
			
			if (!line.substring(0,1).equals("#")){

				String[] cols = line.replaceAll("\\t", " ").trim().split("\\s+");
				
				
				int degFictious = AtomTools.getProjwfcDegeneracy(wfcType) == 0 ?  1 : AtomTools.getProjwfcDegeneracy(wfcType);
				
				int colsWithSpin = 1+(1+degFictious)*2;
				int colsNoSpin = 1+(1+degFictious);
				
				if (cols.length==colsWithSpin) {
					isSpin = true;
					numCols = colsWithSpin;
				} else {
					isSpin = false;
					numCols = colsNoSpin;
				}
			}
			

		} while (numCols==-1);
		
		stepE=0;
		double[][] superLong = new double[numCols][10000];

		do {
			if (!line.substring(0,1).equals("#")){

				String[] cols = line.replaceAll("\\t", " ").trim().split("\\s+");

				for (int nextCol = 0; nextCol<numCols; nextCol++) {
					superLong[nextCol][stepE] = Double.parseDouble(cols[nextCol]);
				}
				stepE++;	
			}

		} while (((line = lnr.readLine()) != null) );

		double toSend[][] = ArrayTools.shortArray(superLong, numCols, stepE); 

		PjwfcOrbital out = new PjwfcOrbital(name, wfcType, toSend, isSpin);
		//out.setId(atom);
		return out;

	}

	public static PhononConfiguration[] readPHall(File file, int totKpoints) throws IOException,NumberFormatException {

		PhononConfiguration[] output;
		ArrayList<PhononConfiguration> allK = new ArrayList<PhononConfiguration>();
		int nextK = 0;

		String filename = file.toString();
		String prefix = filename.substring(filename.lastIndexOf("/")+1, filename.length());
		prefix = prefix.substring(0, prefix.indexOf("."));
		String thisDir = filename.substring(0, filename.lastIndexOf("/"));

		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		for (int i=0; i<totKpoints; i++) {

			String nextFile = thisDir+"/"+prefix+".k"+(i+1)+".phG.out";

			allK.add(nextK, readPH(new File(nextFile)) );
			nextK++;

		}

		output = new PhononConfiguration[nextK];

		for (int i =0; i<nextK; i++) {
			output[i] = allK.get(i);
		}

		return output;
	}

	public static PhononConfiguration readPH(File file) throws IOException,NumberFormatException {

		ArrayList<Double> allOmega = new ArrayList<Double>();
		int maxNomega = 1;

		String filename = file.toString();
		String prefix = filename.substring(filename.lastIndexOf("/")+1, filename.length());
		prefix = prefix.substring(0, prefix.indexOf("."));

		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		do  {

			String[] words = line.replaceAll("\\t", " ").split("\\s+");

			if (words.length > 1) {

				//Becco il paametro di cella
				if (words[1].equals("omega(") && words[5].equals("[THz]")){

					maxNomega = 0;
					do {

						allOmega.add(maxNomega, Double.parseDouble(words[7]));
						maxNomega++;
						line = lnr.readLine();
						words = line.replaceAll("\\t", " ").split("\\s+");

					} while (words[1].equals("omega("));
				}

			}

		} while ((line = lnr.readLine()) != null);

		PhononConfiguration output =  new PhononConfiguration(allOmega,
				maxNomega+1);

		return output;
	}

//	public static KLIST readKList(File file) throws IOException,NumberFormatException {
//
//		ArrayList<Double[]> allK = new ArrayList<Double[]>();
//		ArrayList<String> allKnames = new ArrayList<String>();
//		int numK = 0;
//
//		String filename = file.toString();
//		String prefix = filename.substring(filename.lastIndexOf("/")+1, filename.length());
//		prefix = prefix.substring(0, prefix.indexOf("."));
//
//		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
//		lnr.setLineNumber(0);
//		String line = lnr.readLine();
//
//
//		do  {
//
//			String[] words = line.replaceAll("\\t", " ").split("\\s+");
//
//			if (words.length > 1) {
//
//				Double tempK[] = new Double[3];
//				tempK[0] = Double.parseDouble(words[0]);
//				tempK[1] = Double.parseDouble(words[1]);
//				tempK[2] = Double.parseDouble(words[2]);
//				allK.add(numK, tempK);
//
//				if (words.length > 3) {
//					allKnames.add(numK, words[3]);
//				} else {
//					allKnames.add(numK, "");
//				}
//
//				numK++;
//			}
//
//		} while ((line = lnr.readLine()) != null);
//
//		KLIST output =  new KLIST(allK,
//				allKnames);
//
//		return output;
//	}

	//-----------------------------------------------------------------
	//	CELLA
	//-----------------------------------------------------------------
	public static Cell readOutTextCELL(String[] text) throws IOException,NumberFormatException {

		final int inputSTART = 1;
		final int inputRELAX = 2;
		final int inputSTARTin =3;

		double[][] getCell = new double[3][3];
		double[] getParameters = new double[6];

		//resetta tutto
		Boolean gotCell = false;
		int index;

		int inputType = -1;
		int cellType = -1;

		for (int i=0; i<text.length; i++) {

			String[] words = text[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");


			if ((index = ArrayTools.whereIsInArray(words, "bravais-lattice"))!=-1){

				inputType = inputSTART;
				cellType = Integer.valueOf(words[index+3]);

			}

			if ((index = ArrayTools.whereIsInArray(words, "ibrav"))!=-1)	{

				inputType = inputSTARTin;
				cellType = Integer.valueOf(words[index+2]);

			}

			//Leggo i dati della cella
			if ((index = ArrayTools.whereIsInArray(words, "a(1)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell && inputType == inputSTART)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[0][0] = aX;
				getCell[0][1] = aY;
				getCell[0][2] = aZ;

			}

			if ((index = ArrayTools.whereIsInArray(words, "a(2)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell && inputType == inputSTART)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[1][0] = aX;
				getCell[1][1] = aY;
				getCell[1][2] = aZ;

			}
			if ((index = ArrayTools.whereIsInArray(words, "a(3)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell && inputType == inputSTART)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[2][0] = aX;
				getCell[2][1] = aY;
				getCell[2][2] = aZ;

				gotCell = true;
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(1)"))!=-1 && 
					inputType == inputSTART)	{

				double p1 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p2 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p3 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[0] = p1;
				getParameters[1] = p2;
				getParameters[2] = p3;
			}

			if ((index = ArrayTools.whereIsInArray(words, "celldm(4)"))!=-1 && 
					inputType == inputSTART)	{

				double p4 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p5 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p6 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[3] = p4;
				getParameters[4] = p5;
				getParameters[5] = p6;
			}


			if ((index = ArrayTools.whereIsInArray(words, "CELL_PARAMETERS"))!=-1 &&
					inputType != inputSTART)	{

				int steps = 0;
				do {
					i++;
					words = text[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					if (words.length>1) {
						getCell[steps][0] = Double.valueOf(words[0].trim()).doubleValue();
						getCell[steps][1] = Double.valueOf(words[1].trim()).doubleValue();
						getCell[steps][2] = Double.valueOf(words[2].trim()).doubleValue();
						steps++;
					}

				} while(steps<3);
			}

			if ((index = ArrayTools.whereIsInArray(words, "celldm(1)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[0] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(2)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[1] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(3)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[2] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(4)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[3] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(5)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[4] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(6)"))!=-1 && 
					inputType != inputSTART)	{					
				getParameters[5] = Double.valueOf(words[index+2].trim()).doubleValue();
			}


		} 

		Cell output =  new Cell(cellType,getParameters,getCell);
		return output;
	}

	public static Cell readInFileCELL(File file) throws IOException,NumberFormatException {
		
		double[][] getCell = new double[3][3];
		double[] getParameters = new double[6];

		//resetta tutto
		Boolean gotCellType = false;
		int index;

		int cellType = -1;

		LineNumberReader lnr = new LineNumberReader(new FileReader(file.getAbsolutePath()));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		do  {
			String[] words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
			
			if ((index = ArrayTools.whereIsInArray(words, "ibrav"))!=-1){

				cellType = Integer.valueOf(words[index+2]);
				gotCellType=true;
			}
			
			
			if ((index = ArrayTools.whereIsInArray(words, "celldm(1)"))!=-1)	{					
				getParameters[0] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(2)"))!=-1)	{					
				getParameters[1] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(3)"))!=-1)	{					
				getParameters[2] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(4)"))!=-1)	{					
				getParameters[3] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(5)"))!=-1)	{					
				getParameters[4] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			if ((index = ArrayTools.whereIsInArray(words, "celldm(6)"))!=-1)	{					
				getParameters[5] = Double.valueOf(words[index+2].trim()).doubleValue();
			}
			
			if ((index = ArrayTools.whereIsInArray(words, "CELL_PARAMETERS"))!=-1 
					&& cellType == 0 && gotCellType)	{

				int steps = 0;
				do {
					line = lnr.readLine();
					words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");

					if (words.length>1) {
						getCell[steps][0] = Double.valueOf(words[0].trim()).doubleValue();
						getCell[steps][1] = Double.valueOf(words[1].trim()).doubleValue();
						getCell[steps][2] = Double.valueOf(words[2].trim()).doubleValue();
						steps++;
					}

				} while(steps<3);
			}
			
			
		}	while ((line = lnr.readLine()) != null);

		Cell output;
		
		if (cellType == 0) {
			output =  new Cell(cellType, getParameters,getCell);	
		} else {
			output =  new Cell(cellType, getParameters);
		}
		
		return output;
	}
	
	public static Cell readOutFileCELL(File file) throws IOException,NumberFormatException {

		double[][] getCell = new double[3][3];
		double[] getParameters = new double[6];

		//resetta tutto
		Boolean gotCell = false;
		int index;

		int inputType = -1;
		int cellType = -1;

		LineNumberReader lnr = new LineNumberReader(new FileReader(file.getAbsolutePath()));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		do  {
			String[] words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
			
			if ((index = ArrayTools.whereIsInArray(words, "bravais-lattice"))!=-1){

				cellType = Integer.valueOf(words[index+3]);

			}

			//Leggo i dati della cella
			if ((index = ArrayTools.whereIsInArray(words, "a(1)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[0][0] = aX;
				getCell[0][1] = aY;
				getCell[0][2] = aZ;

			}

			if ((index = ArrayTools.whereIsInArray(words, "a(2)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[1][0] = aX;
				getCell[1][1] = aY;
				getCell[1][2] = aZ;

			}
			if ((index = ArrayTools.whereIsInArray(words, "a(3)"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1 && 
					!gotCell)	{

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[2][0] = aX;
				getCell[2][1] = aY;
				getCell[2][2] = aZ;

				gotCell = true;
			}
			
			if ((index = ArrayTools.whereIsInArray(words, "celldm(1)"))!=-1)	{

				double p1 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p2 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p3 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[0] = p1;
				getParameters[1] = p2;
				getParameters[2] = p3;
			}

			if ((index = ArrayTools.whereIsInArray(words, "celldm(4)"))!=-1)	{

				double p4 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p5 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p6 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[3] = p4;
				getParameters[4] = p5;
				getParameters[5] = p6;
			}

		} while ((line = lnr.readLine()) != null);

		Cell output;
		
		if (cellType == 0) {
			output =  new Cell(cellType, getParameters,getCell);	
		} else {
			output =  new Cell(cellType, getParameters);
		}
		
		return output;
	}
	
	//-----------------------------------------------------------------
	//	IN structure
	//-----------------------------------------------------------------
	
	public static ArrayList<Configuration> readQEinput(String file) throws IOException,NumberFormatException {
		return readQEinput(new FileReader(file));
	}
	
	public static ArrayList<Configuration> readQEinput(FileReader fileREAD) throws IOException,NumberFormatException {
		
		ArrayList<Configuration> output = new ArrayList<Configuration>();
		
		//resetta dati cella
		Cell cellTemp = new Cell();
		double[][] getCell = new double[3][3];
		double[] getParameters = new double[6];

		int cellType = -1;

		//resetta dati posizione atomi
		int nAtoms = 0;
		boolean isNeb = false;
		int coordType = -1;
		
		boolean gotKmp = false;
		int n1=0,n2=0,n3=0,j1=0,j2=0,j3=0;
		double[][] kPoints = new double[2][2];
		Kpoints kTemp = new Kpoints(0);
		
		//Utilizzati per loop e configurazione
		int index = 0;
		int numConf = 0;
		
		ArrayList<String[]> allFile = new ArrayList<String[]>();
		
		//fa partire la lettura del file
		LineNumberReader lnr = new LineNumberReader(fileREAD);
		int linNum = 0;
		lnr.setLineNumber(linNum);
		
		//Legge e incrementa la riga
		String line = lnr.readLine();
		linNum++;
		
		
		Pattern emptyLine = Pattern.compile("^$");
		do  {
			String[] parts = line.trim().replaceAll(",", "\n").split("\n");
						
			for (int i =0; i<parts.length; i++) {
				
				if (parts[i].indexOf("!")>-1) {
					parts[i] = parts[i].substring(0,parts[i].indexOf("!"));
				}
				
				if (!emptyLine.matcher(parts[i]).matches()) {
					allFile.add(parts[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+"));
				}
			}
		
			
		} while ((line = lnr.readLine()) != null);
		
		//----------------------
		//	Legge la cella
		//----------------------
		//Legge il tipo
		cellType = Integer.valueOf(getStringAfterEqual(allFile,"ibrav",0));
		
		//Legge i parametri		
		for (int i=1; i<=6; i++){
			String paramName = "celldm("+i+")";
			if (getLineWithString(allFile, paramName, 0)!=-1){
				getParameters[i-1] = Double.valueOf(getStringAfterEqual(allFile,paramName,0));
			} else {
				getParameters[i-1] = 0.0;
			}
		}
		
		//Legge la matrice
		if (cellType==0) {
			int cellLine = getLineWithString(allFile, "CELL_PARAMETERS",0);
			cellLine++;
			for (int i=0; i<3; i++) {
				getCell[i] = getDoubleVect(allFile, cellLine+i, 3, 0);
			}
		}
		
		if (cellType==0) {
			cellTemp = new Cell(cellType, getParameters, getCell);
		} else {
			cellTemp = new Cell(cellType, getParameters);
		}
		
		
		
		
		//----------------------
		//	Legge gli atomi
		//----------------------
		//Legge il tipo
		nAtoms = Integer.valueOf(getStringAfterEqual(allFile,"nat",0));
		
		int[] allPositions;
		
		if (getLineWithString(allFile,"first_image",0) !=-1) {
			//----------------------
			//	CASO con NEB
			//----------------------
			int[] intNum = getTotalLinesWithString(allFile, "intermediate_image",0);
			int lineFirst = getLineWithString(allFile,"first_image",0);
			int lineLast = getLineWithString(allFile,"last_image",0);
			allPositions = new int[intNum.length +2];
			
			allPositions[0] = lineFirst;
			for (int i =0; i<intNum.length; i++) {
				allPositions[i+1] = intNum[i];
			}
			allPositions[allPositions.length-1] = lineLast;
			
		} else {
			//----------------------
			//	CASO semplice
			//----------------------
			
			allPositions =  new int[1];
			allPositions[0] = getLineWithString(allFile,"ATOMIC_POSITIONS",0);
		}
		
		//Becco il tipo di coordinata
		if (allFile.get(getLineWithString(allFile,"ATOMIC_POSITIONS",0)).length>1) {

            for (int i=1; i< allFile.get(getLineWithString(allFile,"ATOMIC_POSITIONS",0)).length; i++) {

                String coordString = allFile.get(getLineWithString(allFile,"ATOMIC_POSITIONS",0))[i];
                //Legge che tipo di coordinate usa il file
                if (coordString.toLowerCase().indexOf("alat")>-1) {
                    coordType = Costants.cALAT;
                } else if (coordString.toLowerCase().indexOf("crystal")>-1) {
                    coordType = Costants.cCRYSTAL;
                } else if (coordString.toLowerCase().indexOf("angstrom")>-1) {
                    coordType = Costants.cANGSTROM;
                }
            }

		} else {
			coordType = Costants.cALAT;
		}
		
		
		for (int thisPos=0; thisPos<allPositions.length; thisPos++) {

			int lineAtoms = allPositions[thisPos];
			
			
			ArrayList<double[]> positions = new ArrayList<double[]>();
			ArrayList<String> names = new ArrayList<String>();
			
			lineAtoms++;
			for (int i=0; i<nAtoms; i++) {
				
				positions.add(getDoubleVect(allFile, lineAtoms+i, 3, 1));
				names.add(allFile.get(lineAtoms+i)[0]);
			}
			
			output.add(new Configuration(positions,nAtoms,coordType, names));
			numConf++;	
		}
		
		
		//----------------------
		//	Legge i punti K
		//----------------------
		int kLine=-1;
		if (( kLine = getLineWithString(allFile, "K_POINTS",0)) >-1 ){;
			gotKmp = true;
			if ( (isInLine(allFile, kLine, "automatic")>-1) || (isInLine(allFile, kLine, "tbiba")>-1)) {
				
				if (allFile.get(kLine+1).length==6) {
					n1 = Integer.parseInt(allFile.get(kLine+1)[0]);
					n2 = Integer.parseInt(allFile.get(kLine+1)[1]);
					n3 = Integer.parseInt(allFile.get(kLine+1)[2]);
					
					j1 = Integer.parseInt(allFile.get(kLine+1)[3]);
					j2 = Integer.parseInt(allFile.get(kLine+1)[4]);
					j3 = Integer.parseInt(allFile.get(kLine+1)[5]);
					
				}
				
				
				kTemp = new Kpoints(CellTools.makeMonkhorstPackgrid(cellTemp.getCellReciprocal(), n1, n2, n3, j1, j2, j3));
				kTemp.setKType(Costants.kTBIBA);
				kTemp.setKGenerator(n1+" "+n2+" "+n3+" "+j1+" "+j2+" "+j3+"");
			}
			
			else if (isInLine(allFile, kLine, "crystal")>-1) {
				
				int numK = Integer.parseInt(allFile.get(kLine+1)[0]);
				double[][] kImport = new double[numK][3];
				double[] wImport = new double[numK];
				
				for (int i=0; i<numK; i++) {
					
					kImport[i][0] = Double.parseDouble(allFile.get(kLine+2+i)[0]);
					kImport[i][1] = Double.parseDouble(allFile.get(kLine+2+i)[1]);
					kImport[i][2] = Double.parseDouble(allFile.get(kLine+2+i)[2]);
					wImport[0] = Double.parseDouble(allFile.get(kLine+2+i)[3]);
					
				}
				
				kTemp = new Kpoints(kImport,wImport);
				kTemp.setKType(Costants.kCRYSTAL);
				
			}
			else if (isInLine(allFile, kLine, "gamma")>-1) {
				
			}
			else {
				
				int numK = Integer.parseInt(allFile.get(kLine+1)[0]);
				double[][] kImport = new double[numK][3];
				double[] wImport = new double[numK];
				for (int i=0; i<numK; i++) {
					
					kImport[i][0] = Double.parseDouble(allFile.get(kLine+2+i)[0]);
					kImport[i][1] = Double.parseDouble(allFile.get(kLine+2+i)[1]);
					kImport[i][2] = Double.parseDouble(allFile.get(kLine+2+i)[2]);
					wImport[0] = Double.parseDouble(allFile.get(kLine+2+i)[3]);
					
				}
				
				kTemp = new Kpoints(kImport,wImport);
			}
			
		}
		
		
		for (int i=0; i<numConf; i++) {
			//Aggiungo la cella a tutte le configurazioni
			output.get(i).setCell(cellTemp);
			if (gotKmp) {
				output.get(i).setKpoints(kTemp);
			}
			
		}
		
		return output;
		
	}
	
	public static int[] getTotalLinesWithString(ArrayList<String[]> vect, String value, int skipLines ) {
	
		ArrayList<Integer> temp = new ArrayList<Integer>();
		
		for (int i =0; i<vect.size(); i++) {
			
			for (int j=0; j<vect.get(i).length; j++) {
				if (vect.get(skipLines+i)[j].toLowerCase().equals(value.toLowerCase())) {
					temp.add(skipLines+i);
				}
			}
		}
		
		int[] out = new int[temp.size()];
		for (int i =0; i<out.length; i++) {
			out[i] = temp.get(i);
		}
		
		return out;
	}
	
	public static int getLineWithString(ArrayList<String[]> vect, String value, int skipLines ) {
		
		for (int i =0; i<vect.size(); i++) {
			
			for (int j=0; j<vect.get(i).length; j++) {
				if (vect.get(skipLines+i)[j].toLowerCase().equals(value.toLowerCase())) {
					return skipLines+i;
				}
			}
		}
		return -1;
	}
	
	
	
	public static int isInLine(ArrayList<String[]> vect, int line, String value) {
		
		String[] parts = vect.get(line);
		int out = -1;
		
		for (int i=0; i<parts.length; i++) {
			if (parts[i].toLowerCase().indexOf(value.toLowerCase())>-1) {
				out = i;
			}
		}
		
		return out;
		
	}
	
	
	public static String getStringAfterEqual(ArrayList<String[]> vect, int lineNum) {
		
		String[] line = vect.get(lineNum);
		
		int index = -1;
		if ((index = ArrayTools.whereIsInArray(line, "=")) != -1) {
			return line[index+1];
		}
	
		return "";
	}
	

	public static String getStringAfterEqual(ArrayList<String[]> vect, String value, int skipLines) {
		
		int line = getLineWithString(vect, value, skipLines);
		if (line!=-1) return getStringAfterEqual(vect, line);
		return "";
		
	}
	
	public static String getStringAfterSpace(ArrayList<String[]> vect, String value, int skipLines) {
		
		int line = getLineWithString(vect, value, skipLines);
		
		if (line!=-1){
			int index = -1;
			if ((index = ArrayTools.whereIsInArray(vect.get(line), value)) != -1) {
				return vect.get(line)[index+1];
			}
		}
		return "";
		
	}
	
	public static double[] getDoubleVect(ArrayList<String[]> vect, int lineNum, int size, int spaces) {
		
		double[] out = new double[size];
		
		for (int i=0; i<size; i++) {
			out[i] = Double.valueOf(vect.get(lineNum)[spaces+i]);
		}
		return out;
	}
	
	//-----------------------------------------------------------------
	//	OUT structure
	//-----------------------------------------------------------------
	
	public static Configuration readOutTextATOMS(String[] text) throws IOException,NumberFormatException {

		ArrayList<double[]> positions = new ArrayList<double[]>();
		ArrayList<String> names = new ArrayList<String>();
		
		int nAtoms = 0;

		//resetta tutto
		int coordType = -1;
		int index = 0;

		for (int i=0; i<text.length; i++) {

			String[] words = text[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");


			if ((index = ArrayTools.whereIsInArray(words, "ATOMIC_POSITIONS"))!=-1){
				
				if (words.length>=index+2) {
					
					if (words[index+1].equals("(alat)")) {
						coordType = Costants.cALAT;
					} else if (words[index+1].equals("(crystal)")) {
						coordType = Costants.cCRYSTAL;
					} else if (words[index+1].equals("(angstrom)")) {
						coordType = Costants.cANGSTROM;
					}
				} else {
					coordType = Costants.cALAT;
				}
				


				do {
					i++;
					words = text[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					if (words.length>3) {
						double coord[] = new double[3];
						coord[0] = Double.valueOf(words[1].trim()).doubleValue();
						coord[1] = Double.valueOf(words[2].trim()).doubleValue();
						coord[2] = Double.valueOf(words[3].trim()).doubleValue();

						positions.add(nAtoms, coord);
						names.add(nAtoms, words[0].trim());
						nAtoms++;
					}

				} while(i<text.length-1);
			}
		} 

		Configuration output =  new Configuration(positions,nAtoms,coordType,names);
		
		return output;
	}
	
	public static ArrayList<Configuration> readQEoutput(String file, int readArg, int inputType) throws IOException,NumberFormatException {
		
		return readQEoutput(new FileReader(file), readArg, inputType);
	}
	
	public static ArrayList<Configuration> readQEoutput(FileReader fileREAD, int readArg, int inputType) throws IOException,NumberFormatException {
		
		ArrayList<Configuration> output = new ArrayList<Configuration>();
		double[][] kpoints = new double[1][1]; int nKpoints = 0; double[] kWeights = new double[1];
		
		//resetta dati cella
		double[][] getCell = new double[3][3];
		double[] getParameters = new double[6];

		int cellType = -1;

		//resetta dati posizione atomi
		int nAtoms = 0;
		int coordType = -1;
		boolean gotNatom = false, keepReading = true, gotInitialConf = false, kToAdd = false;
		
		//Utilizzati per loop e configurazione
		int index = 0;
		int numConf = 0;
		
		//fa partire la lettura del file
		LineNumberReader lnr = new LineNumberReader(fileREAD);
		int linNum = 0;
		lnr.setLineNumber(linNum);
		
		//Legge e incrementa la riga
		String line = lnr.readLine();
		linNum++;
		
		do  {
			String[] words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");


			//----------------------------------
			//	
			//	LEGGO I DATI INIZIALI
			//
			//----------------------------------

			//----------------------------------
			//	Numero di atomi atoms/cell
			//----------------------------------				
			if (	(index = ArrayTools.whereIsInArray(words, "number"))!=-1
				 && (index = ArrayTools.whereIsInArray(words, "atoms/cell"))!=-1
				 && !gotNatom){

				nAtoms = (int)Double.valueOf(words[index+2].trim()).doubleValue();
				gotNatom = true;

				//System.out.println("NATOMS = " + nAtoms);

			}

			//----------------------------------
			//	
			//	LEGGO I DATI DELLA CELLA (sono sempre prima delle posizione degli atomi)
			//
			//----------------------------------

			//----------------------------------
			//	Input cella iniziale
			//----------------------------------		

			if ((index = ArrayTools.whereIsInArray(words, "bravais-lattice"))!=-1){

				cellType = Integer.valueOf(words[index+3]);

			}
			
			//Leggo i vettori della cella
			if (	(	(index = ArrayTools.whereIsInArray(words, "a(1)"))!=-1 || 
						(index = ArrayTools.whereIsInArray(words, "a(2)"))!=-1 || 
						(index = ArrayTools.whereIsInArray(words, "a(3)"))!=-1
					) &&
					(index = ArrayTools.whereIsInArray(words, "="))!=-1)	{

				int vectNum = Integer.valueOf(words[index-1].trim().substring(2,3));

				double aX = Double.valueOf(words[index+2].trim()).doubleValue();
				double aY = Double.valueOf(words[index+3].trim()).doubleValue();
				double aZ = Double.valueOf(words[index+4].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getCell[vectNum-1][0] = aX;
				getCell[vectNum-1][1] = aY;
				getCell[vectNum-1][2] = aZ;

			}
			
			//Leggo i parametri
			if ((index = ArrayTools.whereIsInArray(words, "celldm(1)"))!=-1)	{

				double p1 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p2 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p3 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[0] = p1;
				getParameters[1] = p2;
				getParameters[2] = p3;
				
				line = lnr.readLine();
				words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
				index = ArrayTools.whereIsInArray(words, "celldm(4)");
				
				double p4 = Double.valueOf(words[index+2].trim()).doubleValue();
				double p5 = Double.valueOf(words[index+5].trim()).doubleValue();
				double p6 = Double.valueOf(words[index+8].trim()).doubleValue();
				//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);

				//Salvo nella lista
				getParameters[3] = p4;
				getParameters[4] = p5;
				getParameters[5] = p6;

			}
			
			//----------------------------------
			//	Input cella generico
			//----------------------------------	
			if ((index = ArrayTools.whereIsInArray(words, "CELL_PARAMETERS"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "(alat)"))!=-1)	{
				
				for (int i=0; i<3; i++) {
					line = lnr.readLine();
					words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					
					double aX = Double.valueOf(words[0].trim()).doubleValue();
					double aY = Double.valueOf(words[1].trim()).doubleValue();
					double aZ = Double.valueOf(words[2].trim()).doubleValue();
					
					getCell[i][0] = aX;
					getCell[i][1] = aY;
					getCell[i][2] = aZ;
				}
			}
			

			//----------------------------------
			//	
			//	LEGGE POSIZIONI ATOMI
			//
			//----------------------------------
			
			//----------------------------------
			//	Legge la posizione iniziale
			//----------------------------------		
			if ((index = ArrayTools.whereIsInArray(words, "Cartesian"))!=-1 
					&& (index = ArrayTools.whereIsInArray(words, "axes"))!=-1 
					&& gotNatom 
					&& !gotInitialConf){
				
				ArrayList<double[]> positions = new ArrayList<double[]>();
				ArrayList<String> names = new ArrayList<String>();
				
				//Vado a ccapo due righe
				line = lnr.readLine();
				line = lnr.readLine();
			
				for (int i=0; i< nAtoms; i++) {
					
					line = lnr.readLine();
					words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					
					if (words.length>3) {
						
						double coord[] = new double[3];
						coord[0] = Double.valueOf(words[words.length-4].trim()).doubleValue();
						coord[1] = Double.valueOf(words[words.length-3].trim()).doubleValue();
						coord[2] = Double.valueOf(words[words.length-2].trim()).doubleValue();

						positions.add(i, coord);
						names.add(i, words[1].trim());
					
					}
					
				}
				
				//---------------------------------------------
				//
				//	LETTA LA POSIZIONE CREA UNA CONFIGURAZIONE
				//
				//---------------------------------------------
				output.add(new Configuration(positions, nAtoms, 0, names));
				numConf++;
				
				//Aggiunge la cella attuale
				if (cellType==0) {
					output.get(numConf-1).setCell(new Cell(cellType, getParameters, getCell));
				} else {
					output.get(numConf-1).setCell(new Cell(cellType, getParameters));
				}
				
				
				//Setta i valori per le iterazioni dopo
				if (readArg == Costants.readFIRST) keepReading = false;
				gotInitialConf = true;
			}
			
			
			//----------------------------------
			//	Legge i punti K
			//----------------------------------
			
			if ((index = ArrayTools.whereIsInArray(words, "number"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "k"))!=-1 &&
					(index = ArrayTools.whereIsInArray(words, "points"))!=-1) {
			
				nKpoints = Integer.parseInt(words[index+2]);
				kpoints = new double[nKpoints][3];
				kWeights = new double[nKpoints];
				
				//Vado a ccapo una
				line = lnr.readLine();
				
				for (int i=0; i< nKpoints; i++) {
					
					line = lnr.readLine();
					words = line.replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					
					if (words.length>3) {
						
						
						kpoints[i][0] = Double.valueOf(words[words.length-7].trim()).doubleValue();
						kpoints[i][1] = Double.valueOf(words[words.length-6].trim()).doubleValue();
						kpoints[i][2] = Double.valueOf(words[words.length-5].substring(0,words[words.length-6].length()-1).trim()).doubleValue();
						
						kWeights[i] = Double.valueOf(words[words.length-1].trim()).doubleValue(); 
											
					}
				}
				if (gotInitialConf){
					output.get(numConf-1).setKpoints(new Kpoints(kpoints,kWeights));
				}
				kToAdd = true;
			}
			
			
			//----------------------------------
			//	Legge la posizione intermedia ATOMIC_POSITIONS
			//----------------------------------
			if ((index = ArrayTools.whereIsInArray(words, "ATOMIC_POSITIONS"))!=-1 
					&& gotNatom 
					&& keepReading){

				ArrayList<double[]> positions = new ArrayList<double[]>();
				ArrayList<String> names = new ArrayList<String>();
				

				//Legge che tipo di coordinate usa il file
				if (words[index+1].equals("(alat)")) {
					coordType = Costants.cALAT;
				} else if (words[index+1].equals("(crystal)")) {
					coordType = Costants.cCRYSTAL;
				} else if (words[index+1].equals("(angstrom)")) {
					coordType = Costants.cANGSTROM;
				}


				//Legge la posizione degli atomi fino a nAtmos letto prima
				for (int i=0; i< nAtoms; i++) {

					line = lnr.readLine();
					words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");

					if (words.length>3) {
						double coord[] = new double[3];
						coord[0] = Double.valueOf(words[1].trim()).doubleValue();
						coord[1] = Double.valueOf(words[2].trim()).doubleValue();
						coord[2] = Double.valueOf(words[3].trim()).doubleValue();

						positions.add(i, coord);
						names.add(i, words[0].trim());
					
					}

				}
			
				//---------------------------------------------
				//
				//	LETTA LA POSIZIONE CREA UNA CONFIGURAZIONE
				//
				//---------------------------------------------
				
				if (readArg == Costants.readLAST) {
					if (output!=null) {
						if (output.size()>0) {
							output.set(0,new Configuration(positions,nAtoms,coordType, names));
							numConf=1;
						} else {
							output.add(0,new Configuration(positions,nAtoms,coordType, names));
							numConf=1;
						}
					}
					
				} else {
					output.add(new Configuration(positions,nAtoms,coordType, names));
					numConf++;
				}
				
				
				//Aggiunge la cella attuale
				if (cellType==0) {
					output.get(numConf-1).setCell(new Cell(cellType, getParameters.clone(),getCell.clone()));
				} else {
					output.get(numConf-1).setCell(new Cell(cellType, getParameters.clone()));
				}
				
				if (kToAdd) {
					output.get(numConf-1).setKpoints(new Kpoints(kpoints,kWeights));
				}
				
			}
			
			//------------------------------------------
			//	LEGGE ENERGY / DATI
			//------------------------------------------
			if (keepReading) {
				//Energia
				if ((index = ArrayTools.whereIsInArray(words, "!"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "total"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "energy"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("eTot", Double.valueOf(words[index+2].trim())); //eTot
	
				}
	
				//Magnetizzazione
				if ((index = ArrayTools.whereIsInArray(words, "total"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "magnetization"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("magnTot", Double.valueOf(words[index+2].trim()));
	
				}
				
				//Fermi energy
				if ((index = ArrayTools.whereIsInArray(words, "Fermi"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "energy"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("eFermi", Double.valueOf(words[index+2].trim()));
				}
			     
				//Total force
				if ((index = ArrayTools.whereIsInArray(words, "Total"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "force"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("forceTot", Double.valueOf(words[index+2].trim()));
	
				}
				
				//Dynamics
				if ((index = ArrayTools.whereIsInArray(words, "Entering"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "Dynamics:"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					line = lnr.readLine();
					words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
					
					if ((index = ArrayTools.whereIsInArray(words, "time"))!=-1) {
						
						output.get(numConf-1).getData().storeDataByName("timeStep", Double.valueOf(words[index+2].trim()));
						
					}
	
				}
				
				//Energia Cinetica
				if ((index = ArrayTools.whereIsInArray(words, "kinetic"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "energy"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("eKin", Double.valueOf(words[index+3].trim()));
					
				}
				
				//Energia cinetica e totale
				if ((index = ArrayTools.whereIsInArray(words, "Ekin"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "Etot"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "(const)"))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("eKineTot", Double.valueOf(words[index+2].trim()));
					
				}
				
				//Temperature
	//			if ((index = ArrayTools.whereIsInArray(words, "Thermalization:"))!=-1
	//					&& gotNatom 
	//					&& keepReading ) {
	//
	//				String raw = words[index+2].replace("(", "").replace(")", "").replace("K", "");
	//				
	//				double d = Double.valueOf(raw).doubleValue();
	//				output.get(numConf-1).temp = d;
	//
	//			}
				
				//Temperatura 2
				if ((index = ArrayTools.whereIsInArray(words, "temperature"))!=-1
						&& (index = ArrayTools.whereIsInArray(words, "="))!=-1
						&& gotNatom 
						&& keepReading ) {
	
					output.get(numConf-1).getData().storeDataByName("temp", Double.valueOf(words[index+1].trim()));
					
				}
			
			}
			
		} while ((line = lnr.readLine()) != null);

		//Se il modo di lettura e' last allora aggiunge solo l'ultima che e'
		//	ancora carica in memoria

		
		return output;
	}

	//-----------------------------------------------------------------
	//	BANDS file
	//-----------------------------------------------------------------

	public static Object readBANDS(String file, int type) throws IOException,NumberFormatException {
			
			Kpoints kTemp = new Kpoints(0);
			double[][] bands = new double[1][1];
			
			//Utilizzati per loop e configurazione
			int nPoints = 0;
			int nBands = 0;
			
			ArrayList<String[]> allFile = new ArrayList<String[]>();
			
			//fa partire la lettura del file
			FileReader fileREAD = new FileReader(file);
			LineNumberReader lnr = new LineNumberReader(fileREAD);
			int linNum = 0;
			lnr.setLineNumber(linNum);
			
			//Legge e incrementa la riga
			String line = lnr.readLine();
			linNum++;
			
			
			
			do  {
				String[] parts = line.trim().replaceAll(",", "\n").split("\n");
							
				for (int i =0; i<parts.length; i++) {
					if (parts[i].indexOf("!")>-1) {
						parts[i] = parts[i].substring(0,parts[i].indexOf("!"));
					}
					if (parts[i].length()>1) {
						allFile.add(parts[i].trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+"));
					}
				}
			
			} while ((line = lnr.readLine()) != null);
			
			nPoints = Integer.valueOf(getStringAfterEqual(allFile,"nks",0));
			nBands = Integer.valueOf(getStringAfterEqual(allFile,"nbnd",0));
			
			bands = new double[nPoints][nBands];
			
			int lineDone = 2;
			for (int i=0; i<nPoints; i++) {
				
				double[] thisK = new double[3];
				thisK[0] = Double.parseDouble(allFile.get(lineDone)[0]);
				thisK[1] = Double.parseDouble(allFile.get(lineDone)[1]);
				thisK[2] = Double.parseDouble(allFile.get(lineDone)[2]);
				kTemp.addKpoint(thisK);
				
				lineDone++;
				
				int bandDone = 0;
				while (bandDone < nBands) {
					for (int j=0; j<allFile.get(lineDone).length; j++) {
						bands[i][bandDone] = Double.parseDouble(allFile.get(lineDone)[j]);
						bandDone++;
					}
					lineDone++;
				}
				
				
			}
			
			if (type==0) {
				return bands;
			} else {
				return kTemp;
			}
	}
	
	//-----------------------------------------------------------------
	//	XYZ
	//-----------------------------------------------------------------
	public static Configuration readXYZ(String file) throws IOException,NumberFormatException {
		
		FileReader fileREAD = new FileReader(file);
		
		Configuration output;
		
		
		//resetta dati posizione atomi
		int nAtoms = 0;
		
		//fa partire la lettura del file
		LineNumberReader lnr = new LineNumberReader(fileREAD);
		int linNum = 0;
		lnr.setLineNumber(linNum);
		
		//Legge e incrementa la riga
		String line = lnr.readLine();
		linNum++;
		
		//leggo il numero di atomi
		String[] words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
		nAtoms = (int)Double.valueOf(words[0].trim()).doubleValue(); 
		line = lnr.readLine();
		linNum++;
		
		//leggo il commento
		String comment = line.trim();
		line = lnr.readLine();
		linNum++;
		
		ArrayList<double[]> positions = new ArrayList<double[]>();
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i=0; i<nAtoms; i++){
			
			words = line.trim().replaceAll("\\t", " ").replaceAll(",", " , ").replaceAll("=", " = ").split("\\s+");
			
			if (words.length>3) {
				
				
				double coord[] = new double[3];
				coord[0] = Double.valueOf(words[1].trim()).doubleValue();
				coord[1] = Double.valueOf(words[2].trim()).doubleValue();
				coord[2] = Double.valueOf(words[3].trim()).doubleValue();
	
				positions.add(i, coord);
				names.add(i, words[0].trim());
		
			}
			
			line = lnr.readLine();
		}
		
		output = new Configuration(positions, nAtoms, Costants.cANGSTROM, names);
		output.setNotes(comment);		
		output.setCell(new Cell(1, new double[] {1.0,0.0,0.0,0.0,0.0,0.0}));				
		return output;
		
	}
	

	//-----------------------------------------------------------------
	//	ASXF
	//-----------------------------------------------------------------

	public static ArrayList<Configuration> readAXSFinput(String file) throws IOException,NumberFormatException {
		return readAXSFinput(new FileReader(file));
	}
	
	public static ArrayList<Configuration> readAXSFinput(FileReader fileREAD) throws IOException,NumberFormatException {
		
		ArrayList<Configuration> output = new ArrayList<Configuration>();
		
		//resetta dati cella
		double[][] getCell = new double[3][3];

		
		//fa partire la lettura del file
		ArrayList<String[]> allFile = new ArrayList<String[]>();
		LineNumberReader lnr = new LineNumberReader(fileREAD);
		int linNum = 0;
		lnr.setLineNumber(linNum);
		
		//Legge e incrementa la riga
		String line = lnr.readLine();
		linNum++;
		
		
		//Toglie i commenti
		Pattern emptyLine = Pattern.compile("^$");
		do  {
				
			if (line.trim().indexOf("#")==-1) {
				
				if (!emptyLine.matcher(line.trim()).matches()) {
					allFile.add(line.trim().replaceAll("\\t", " ").replaceAll("-", " -").split("\\s+"));
				}
			}
		} while ((line = lnr.readLine()) != null);
		
		
		//Legge il tipo
		if (getLineWithString(allFile,"ANIMSTEPS",0)> -1){
			
			int animSteps = Integer.valueOf(getStringAfterSpace(allFile,"ANIMSTEPS",0));
			
			boolean cellAnimated = false;
			String[] firstCell = allFile.get(getLineWithString(allFile,"PRIMVEC",0));
			if (firstCell.length>1) {
				cellAnimated = true;
			}
			
			int lastLine = 0;
			if (!cellAnimated) {
				
				lastLine = getLineWithString(allFile, "PRIMVEC",0);
				lastLine++;
				for (int i=0; i<3; i++) {
					getCell[i] = getDoubleVect(allFile, lastLine+i, 3, 0);
				}
			}
			
			
			for (int i=0; i<animSteps; i++) {
			
				if (cellAnimated) {
					lastLine = getLineWithString(allFile, "PRIMVEC",lastLine);
					lastLine++;
					for (int c=0; c<3; c++) {
						getCell[i] = getDoubleVect(allFile, lastLine+c, 3, 0);
					}
				}
				
				
				lastLine = getLineWithString(allFile, "PRIMCOORD",lastLine);
				lastLine++;
				int nat = Integer.valueOf(allFile.get(lastLine)[0]);
				
				ArrayList<double[]> positions = new ArrayList<double[]>();
				ArrayList<String> names = new ArrayList<String>();
				
				for (int thisPos=0; thisPos<nat; thisPos++) {
					
					
					lastLine++;
					
					positions.add(getDoubleVect(allFile, lastLine, 3, 1));
					names.add(allFile.get(lastLine)[0]);
					
				}
				
				output.add(new Configuration(positions,nat,Costants.cANGSTROM, names));
				output.get(i).setCell(new Cell(getCell));
			}
			
		} else {
			
			int lastLine = 0;
						
			lastLine = getLineWithString(allFile, "PRIMVEC",0);
			lastLine++;
			for (int i=0; i<3; i++) {
				getCell[i] = getDoubleVect(allFile, lastLine+i, 3, 0);
			}
		
			lastLine = getLineWithString(allFile, "PRIMCOORD",lastLine);
			lastLine++;
			int nat = Integer.valueOf(allFile.get(lastLine)[0]);
			
			ArrayList<double[]> positions = new ArrayList<double[]>();
			ArrayList<String> names = new ArrayList<String>();
			
			for (int thisPos=0; thisPos<nat; thisPos++) {
				
				
				lastLine++;
				
				positions.add(getDoubleVect(allFile, lastLine, 3, 1));
				names.add(allFile.get(lastLine)[0]);
				
			}
			
			output.add(new Configuration(positions,nat,Costants.cANGSTROM, names));
			output.get(0).setCell(new Cell(getCell));
			
		}
		
		return output;
		
	}
	
}
