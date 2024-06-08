package org.moka;

import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import org.moka.qeReader.qeParser;
import org.moka.structures.*;
import org.moka.tools.*;
import org.moka.tools.gui.*;

import org.moka.common.Costants;
import de.schlichtherle.io.*;


/**
 * @author riki
 *
 */
public class IO {
	
	public Moka app;

	ArchiveDetector mokaDetector = new DefaultArchiveDetector(
	        ArchiveDetector.NULL,
	        new Object[] {
	            "moka", new de.schlichtherle.io.archive.tar.TarGZipDriver(),
	        });
	
	public String[] importTypes = {"PWscf Output","PWscf Input","XYZ format", "XSF format"};
	
	public IO(Moka _app) {
		
		this.app = _app;
		
	}

	public int importFile(String fname, int type) {
		
		int confImported = 0;
		
		if (type==0) {
			
			try {
				//------------------
				// Apertura file OUT
				//------------------
			
			
				int readerArg = RadioDialog.showDialog(app.frame,null,"Select open mode","Mode", Costants.readList, Costants.readALL);
				
				
				ArrayList<Configuration> tempList = qeParser.readQEoutput(fname, readerArg, Costants.inputSIMPLE);
		
				checkNames(tempList);
				
				for (int j =0; j<tempList.size(); j++) {
					tempList.get(j).setNotes(fname+":"+(j+1));
					tempList.get(j).setName((new File(fname)).getName()+":"+(j+1));
					
					app.confDB.add(tempList.get(j));
					confImported++;
				}
			
			} catch (NumberFormatException e1) {
				return -1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				return -1;
			}
			
			return confImported;
			
		} else if(type==1) {
			
			try {
				//------------------
				// Apertura file IN
				//------------------
			
			
				int confs = 0;
				
				ArrayList<Configuration> tempList = qeParser.readQEinput(fname);
				
				checkNames(tempList);
				
				//Le entry neb sono lette come differenti configurazioni
				for (int j =0; j<tempList.size(); j++) {
					app.confDB.add(tempList.get(j));
					confImported++;
				}
			
			} catch (NumberFormatException e1) {
				return -1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				return -1;
			}
			
			return confImported;
			
		} else if(type==2) {
			
			try {
				//------------------
				// Apertura file XYZ
				//------------------
			
				Configuration tempList = qeParser.readXYZ(fname);
				
				tempList.setNotes(fname);
				app.confDB.add(tempList);
				confImported++;
			
			} catch (NumberFormatException e1) {
				return -1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				return -1;
			}
			
			return confImported;
			
		} else if(type==3) {
			
			try {
				//------------------
				// Apertura file AXSF
				//------------------
			
				ArrayList<Configuration> tempList = qeParser.readAXSFinput(fname);
				
				//Le entry neb sono lette come differenti configurazioni
				for (int j =0; j<tempList.size(); j++) {
					
					tempList.get(j).setNotes(fname+":"+(j+1));
					tempList.get(j).setName((new File(fname)).getName()+":"+(j+1));
					
					app.confDB.add(tempList.get(j));
					confImported++;
				}
				
			
			} catch (NumberFormatException e1) {
				return -1;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				return -1;
			}
			
			return confImported;
			
		}	else {
			
			return -1;
			
		}
		
	}

	public void checkNames(ArrayList<Configuration> _conf) {
		
		String[][] newName = new String[2][50];
		for (int i=0; i<2; i++) {
			for (int j=0; j<50; j++) {
				newName[i][j] = "";
			}
		}
		int newNamesFound = 0;
		
		for (int j =0; j<_conf.size(); j++) {
			
			for (int atom=0; atom<_conf.get(j).getNAtoms(); atom++) {
				//Controlla i nomi
				if (!ArrayTools.isInArray(app.status.atomsElements, _conf.get(j).getAtomName(atom))) {
					
					String newElement="";
					
					int subNum = ArrayTools.whereIsInArray(newName[0], _conf.get(j).getAtomName(atom));
					if (subNum>-1) {
						
						newElement = newName[1][subNum];
						
					} else {
						
						int elementId = ComboDialog.showDialog(app.frame, null, "Element for:"+_conf.get(j).getAtomName(atom), "Element for:"+_conf.get(j).getAtomName(atom), app.status.atomsElements, 0);
						newElement = app.status.atomsElements[elementId];
						
						newName[0][newNamesFound] = _conf.get(j).getAtomName(atom);
						newName[1][newNamesFound] = newElement; 
						newNamesFound++;
						
					}
					
					
					_conf.get(j).setAtomElement(atom, newElement);
				}
			}
			
		}
		
	}
	//-------------------------------
	//	WRITE MOKA
	//-------------------------------
	
//	public void writeMoka(String filename) throws IOException {
//		
//		new File(filename, mokaDetector).mkdir();
//		
//		File fileINFO = new File(filename+"/info.txt", mokaDetector);
//		FileOutputStream fosINFO = new FileOutputStream(fileINFO);
//		PrintStream printINFO = new PrintStream(fosINFO);
//		
//		printINFO.println(app.confDB.size());
//		fosINFO.close();
//		fileINFO.umount();
//		
//		for (int i=0; i<app.confDB.size(); i++) {
//			
//			//--IN
//			File fileIN = new File(filename+"/"+(i+1)+".in", mokaDetector);
//			FileOutputStream fosIN = new FileOutputStream(fileIN);
//			PrintStream printIN = new PrintStream(fosIN);
//			
//			writeSystem(printIN,i);
//			writeAtoms(printIN,i, false);
//			
//			if (app.confDB.get(i).getKpoints()!=null) {
//				writeKpoints(printIN,i);
//			}
//			
//			fosIN.close();
//			fileIN.umount();
//			
//			
//			//--DATA
//			File fileDATA = new File(filename+"/"+(i+1)+".data", mokaDetector);
//			FileOutputStream fosDATA = new FileOutputStream(fileDATA);
//			PrintStream printDATA = new PrintStream(fosDATA);
//			
//			writeData(printDATA,i);
//
//			fosDATA.close();
//			fileDATA.umount();
//			
//			//--NOTES
//			File fileNOTES = new File(filename+"/"+(i+1)+".notes", mokaDetector);
//			FileOutputStream fosNOTES = new FileOutputStream(fileNOTES);
//			PrintStream printNOTES = new PrintStream(fosNOTES);
//			
//			writeNotes(printNOTES,i);
//			
//			fosNOTES.close();
//			fileNOTES.umount();
//			
//		}
//		
//		//--IMMAGINE
//		File fileIMG = new File(filename+"/thumb.PNG", mokaDetector);
//		FileOutputStream fosIMG = new FileOutputStream(fileIMG);
//		writeImage(640, 480,fosIMG);
//		fosIMG.close();
//		fileIMG.umount();
//
//		
//	}
//	
	public void writeMoka(String filename) throws IOException {
		
		new File(filename, mokaDetector).mkdir();
		
		File fileINFO = new File(filename+"/info.txt", mokaDetector);
		FileOutputStream fosINFO = new FileOutputStream(fileINFO);
		PrintStream printINFO = new PrintStream(fosINFO);
		
		printINFO.println(app.confDB.size());
		fosINFO.close();
		fileINFO.umount();
		
		
//		XMLEncoder e = new XMLEncoder(System.out);
//        e.writeObject(app.confDB.get(0).getCell());
//        e.close();
        
		File fileConfXML = new File(filename+"/confDB.xml", mokaDetector);
        XMLEncoder encoder = new XMLEncoder( new BufferedOutputStream( new FileOutputStream(fileConfXML)));
        encoder.writeObject(app.confDB);
        encoder.close();
        fileConfXML.umount();
         
		//--IMMAGINE
		File fileIMG = new File(filename+"/thumb.PNG", mokaDetector);
		FileOutputStream fosIMG = new FileOutputStream(fileIMG);
		writeImage(640, 480,fosIMG);
		fosIMG.close();
		fileIMG.umount();

		
	}
	
	//-------------------------------
	//	READ MOKA
	//-------------------------------
	
//	public void readMoka(String filename, ArrayList<Configuration> out, boolean empty) throws IOException {
//		
//		if (empty) out.clear();
//		
//		File fileINFO = new File(filename+"/info.txt", mokaDetector);
//		FileReader readINFO = new FileReader(fileINFO);
//		
//		LineNumberReader lnr = new LineNumberReader(readINFO);
//		lnr.setLineNumber(0);
//		int totConf = Integer.valueOf(lnr.readLine());
//		readINFO.close();
//		fileINFO.umount();
//		
//		for (int i=0; i<totConf; i++) {
//
//			//--IN
//			File fileIN = new File(filename+"/"+(i+1)+".in", mokaDetector);
//			FileReader readIN = new FileReader(fileIN);
//			
//			ArrayList<Configuration> tempList = qeParser.readQEinput(readIN);
//			
//			for (int j =0; j<tempList.size(); j++) {
//				out.add(tempList.get(j));
//			}
//			readIN.close();
//			fileIN.umount();
//			
//			//--DATA
//			File fileDATA = new File(filename+"/"+(i+1)+".data", mokaDetector);
//			FileReader readDATA = new FileReader(fileDATA);
//						
//			out.get(out.size()-1).setData(readATOMSdata(readDATA));
//
//			readDATA.close();
//			fileDATA.umount();
//
//			//--NOTES
//			File fileNOTES = new File(filename+"/"+(i+1)+".notes", mokaDetector);
//			FileReader readNOTES = new FileReader(fileNOTES);
//			
//			out.get(out.size()-1).setNotes(readNotes(readNOTES));
//			readNOTES.close();
//			fileNOTES.umount();
//		
//	
//		}
//		
//	}
	
	public ArrayList<Configuration> readMoka(String filename) throws IOException {
		
		ArrayList<Configuration> out;
		
		File fileINFO = new File(filename+"/info.txt", mokaDetector);
		FileReader readINFO = new FileReader(fileINFO);
		
//		LineNumberReader lnr = new LineNumberReader(readINFO);
//		lnr.setLineNumber(0);
//		int totConf = Integer.valueOf(lnr.readLine());
		readINFO.close();
		fileINFO.umount();
		
		File fileConfXML = new File(filename+"/confDB.xml", mokaDetector);
		XMLDecoder e = new XMLDecoder( new BufferedInputStream( new FileInputStream(fileConfXML)));
		out = (ArrayList<Configuration>) e.readObject();
		e.close();
		
		return out;
		
	}
	
	public AtomData readATOMSdata(FileReader fileREAD) throws IOException {
		
		AtomData data = new AtomData();
		LineNumberReader lnr = new LineNumberReader(fileREAD);
		int linNum = 0;
		lnr.setLineNumber(linNum);
		
		//Legge e incrementa la riga
		String line = lnr.readLine();
		
		do  {
			if (line.equals("null")) {
				data.values[linNum] = data.dataNull(data.types[linNum]);
			} else {
				data.storeData(linNum, line);
			}
			
			linNum++;	
		} while ((line = lnr.readLine()) != null);
		
		
		return data;
		
		
	}
	
	private static String readNotes(FileReader fileREAD) throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(fileREAD);
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	

	//-------------------------------
	//	WRITERS
	//-------------------------------
	
	public void writeSystem(Object output, int confToPrint) {
		
		if (app.confDB.get(confToPrint).getCell()==null) return;
		
		ArrayList<String> allLines = new ArrayList<String>();
		int cellType = app.confDB.get(confToPrint).getCell().getType();
		int nat = app.confDB.get(confToPrint).getNAtoms();
		int ntyp = app.confDB.get(confToPrint).getTotAtomNames();
		
		//Cell types
		allLines.add("&system");
		allLines.add("nat="+nat+",");
		allLines.add("ntyp="+ntyp+",");
		
		allLines.add("ibrav="+cellType+",");
		
		//Cell parameters
		for (int i =0; i<Costants.paramtersEditablePerCellType[cellType].length; i++){
			if (Costants.paramtersEditablePerCellType[cellType][i]==1) {
				allLines.add("celldm("+(i+1)+")="+
						String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getCell().getParameters()[i])
						+",");
			}
		}
		allLines.add("");
		
		//Manual cell
		if (cellType == 0) {
			allLines.add("CELL_PARAMETERS");
			
			for (int j=0; j<3; j++) {
				String line = "";
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getCell().getCellBase()[j][0]);
				line+=Costants.separatorSPACE;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getCell().getCellBase()[j][1]);
				line+=Costants.separatorSPACE;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getCell().getCellBase()[j][2]);
				
				allLines.add(line);
			}
			
		}
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
		
	public void writeAtoms(Object output, int confToPrint, boolean onlySelected) {
		
		ArrayList<String> allLines = new ArrayList<String>();
		int coordType = app.confDB.get(confToPrint).getCoordType();
		
		String title = "ATOMIC_POSITIONS";
		
		//Legge il tipo di coordinate
		if (coordType == Costants.cALAT) {
			title += " (alat)";
		} else if (coordType == Costants.cANGSTROM) {
			title += " (angstrom)";
		} else if (coordType == Costants.cCRYSTAL) {
			title += " (crystal)";
		} 
		allLines.add(title);
		allLines.add("");
		
		//Positions
		for (int i=0; i<app.confDB.get(confToPrint).getNAtoms(); i++) {
		
			if (!onlySelected || ( onlySelected && app.confDB.get(confToPrint).isAtomSelected(i) )  ) {
				String line = "";
				line += app.confDB.get(confToPrint).getAtomName(i);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getAtomPos(i)[0]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getAtomPos(i)[1]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getAtomPos(i)[2]);
				
				//if (app.confDB.get(confToPrint).isAtomBlocked(i)) {
				if (app.confDB.get(confToPrint).isAtomSelected(i)) {
					line +=Costants.separatorTAB+"! -- selected";
				}
				
				allLines.add(line);
			}
		}
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
	
	public void writeXYZ(Object output, int confToPrint, boolean onlySelected) {
		
		ArrayList<String> allLines = new ArrayList<String>();

		allLines.add(app.confDB.get(confToPrint).getNAtoms()+"");
		allLines.add(app.confDB.get(confToPrint).getName());
		
		//Positions
		for (int i=0; i<app.confDB.get(confToPrint).getNAtoms(); i++) {
		
			if (!onlySelected || ( onlySelected && app.confDB.get(confToPrint).isAtomSelected(i) )  ) {
				
				double[] pos = app.confDB.get(confToPrint).getAtomPos(i, Costants.cANGSTROM);
				String line = "";
				line += app.confDB.get(confToPrint).getAtomName(i);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,pos[0]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,pos[1]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,pos[2]);
				allLines.add(line);
			}
		}
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
	
	public void writeKpoints(Object output, int confToPrint) {
		
		ArrayList<String> allLines = new ArrayList<String>();
		int KcoordType = app.confDB.get(confToPrint).getKpoints().getKType();
		
		String title = "K_POINTS";
		
		//Legge il tipo di coordinate
		if (KcoordType == Costants.kCRYSTAL ) {
			title += " crystal";
		} else if (KcoordType == Costants.kTBIBA &&  !app.confDB.get(confToPrint).getKpoints().getKGenerator().equals("")) {
			title += " {automatic}";
		}
		
		allLines.add(title);
		
		if (((KcoordType == Costants.kTBIBA ) && (app.confDB.get(confToPrint).getKpoints().getKGenerator().equals("")))|| (KcoordType == Costants.kCRYSTAL )) { 
			
			allLines.add(app.confDB.get(confToPrint).getKpoints().getNPoints()+"");
			
			for (int i=0; i<app.confDB.get(confToPrint).getKpoints().getNPoints(); i++) {
			
			
				String line = "";
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getKpoints().getKpoint(i)[0]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getKpoints().getKpoint(i)[1]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getKpoints().getKpoint(i)[2]);
				line+=Costants.separatorTAB;
				line += String.format(Costants.decimalPlace8,app.confDB.get(confToPrint).getKpoints().getKweight(i));
				line+=Costants.separatorTAB;
				
				allLines.add(line);
			
			}
			
		} else if (KcoordType == Costants.kTBIBA &&  !app.confDB.get(confToPrint).getKpoints().getKGenerator().equals("")) {
			allLines.add(app.confDB.get(confToPrint).getKpoints().getKGenerator());
		}
			
		
		//Positions
		
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
	
	public void writeData(Object output, int confToPrint) {
		
		ArrayList<String> allLines = new ArrayList<String>();
		
		//Datas
		for (int i =0; i<app.confDB.get(confToPrint).getData().values.length; i++){

			allLines.add((String) app.confDB.get(confToPrint).getData().printData(i));
			
		}
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
	
	public void writeNotes(Object output, int confToPrint) {
		
		
		//Print all the lines in the output
		
		if (output.getClass()==PrintStream.class) {
			((PrintStream)output).print(app.confDB.get(confToPrint).getNotes());
		} else if (output.getClass()==JTextArea.class) {
			((JTextArea)output).append(app.confDB.get(confToPrint).getNotes());
		}
	

	}
	
	public void writeImage(int width, int height, FileOutputStream out) throws IOException {
		
		BufferedImage bi = app.viewer.getImage(width, height);
		ImageIO.write(bi, "PNG", out);
		
	}
	
	public void writeArray(Object output, double[][] vect) {
		
		ArrayList<String> allLines = new ArrayList<String>();
		
		for (int i=0; i<vect.length; i++) {
			
			String line = "";
			for (int j=0; j<vect[i].length; j++) {
		
			line += String.format(Costants.decimalPlace8,vect[i][j]);
			line+=Costants.separatorTAB;
			
			}
			allLines.add(line);
		}
		
		//Print all the lines in the output
		for (int i=0; i<allLines.size(); i++){
			if (output.getClass()==PrintStream.class) {
				((PrintStream)output).println(allLines.get(i));
			} else if (output.getClass()==JTextArea.class) {
				((JTextArea)output).append(allLines.get(i)+Costants.newline);
			}
		}

	}
	
	public void writeSystem(Object output) {
		writeSystem(output, app.thisConf);
	}
	
	public void writeAtoms(Object output, boolean onlySelected) {
		writeAtoms(output, app.thisConf, onlySelected);
	}
	
	public void writeXYZ(Object output, boolean onlySelected) {
		writeXYZ(output, app.thisConf, onlySelected);
	}
	
	public void writeKpoints(Object output) {
		writeKpoints(output, app.thisConf);
	}


	//-------------------------------
	//	PARSERS - PARTS
	//-------------------------------
	
	
	public Integer parseInteger(String _num) {
		
		Pattern pattern = Pattern.compile("^(([+-]))?([0-9])+$");
		Matcher matcher =  pattern.matcher(_num);
		if (!matcher.find()) return null;
		
		return Integer.parseInt(matcher.group(0));
		
	}
	
	public Double parseDouble(String _num) {
		
		Pattern pattern = Pattern.compile("^(([+-]))?([0-9])+((\\.([0-9])+)|(\\,([0-9])+))?([dD])?");
		Matcher matcher =  pattern.matcher(_num);
		if (!matcher.find()) return null;
		
		String toParse = matcher.group(0);
		if (toParse.toLowerCase().indexOf("d")!=-1) {
			toParse = toParse.substring(0,toParse.toLowerCase().indexOf("d"));
		}
		
		return Double.parseDouble(toParse);
		
	}
	
	public Double parseStringWithMath(String _num) {
				
		try {
			return app.math.evaluateEq(_num);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public int[] parseIntArray(String _arr) throws NumberFormatException {
		
		Pattern pattern = Pattern.compile("(([0-9])+(\\ )+)+([0-9])+");
		Matcher matcher = pattern.matcher(_arr.trim());
		if (!matcher.find()) return null;
		
		String toParse = matcher.group(0);
		
		String[] parts = toParse.trim().split("\\s+");
		if (parts==null) return null;
		
		int numArgs = parts.length;
		int[] arrInt = new int[numArgs];
		
		for (int i=0; i<numArgs; i++) {
			
			if (parseInteger(parts[i])==null) return null;
			arrInt[i] = parseInteger(parts[i]);
			
		}
		
		return arrInt;
		
	}
	
	public double[] parseDoubleArray(String _arr) throws NumberFormatException {
		
		Pattern pattern = Pattern.compile("(([0-9])+((\\.([0-9])+)|(\\,([0-9])+))?([dD])?(\\ )+)+(([0-9])+((\\.([0-9])+)|(\\,([0-9])+))?([dD])?)");
		Matcher matcher = pattern.matcher(_arr.trim());
		if (!matcher.find()) return null;
		
		String toParse = matcher.group(0);
				
		String[] parts = toParse.trim().split("\\s+");
		if (parts==null) return null;
		
		int numArgs = parts.length;
		double[] arrInt = new double[numArgs];
		
		for (int i=0; i<numArgs; i++) {
			
			if (parseDouble(parts[i])==null) return null;
			arrInt[i] = parseDouble(parts[i]);
			
		}
		
		return arrInt;
		
	}
	
	public double[] parseDoubleArrayWithMath(String _arr) throws NumberFormatException {
		
		String[] lParethesis = new String[] {"[","{","("};
		String[] rParethesis = new String[] {"]","}",")"};
		
		int lStart = 0;
		for (int i =0; i<lParethesis.length; i++) {
			int parPos = _arr.trim().indexOf(lParethesis[i]); 
			if (parPos>=lStart && parPos!=-1) {
				lStart = parPos+1;
			}
		}
		
		
		int rStart = _arr.trim().length();
		for (int i =0; i<rParethesis.length; i++) {
			int parPos = _arr.trim().lastIndexOf(rParethesis[i]); 
			if (parPos<rStart && parPos!=-1) {
				rStart = parPos;
			}
		}
		
		String[] parts = _arr.trim().substring(lStart, rStart).trim().split("\\s+");
		if (parts==null) return null;
		
		int numArgs = parts.length;
		double[] arrInt = new double[numArgs];
		
		for (int i=0; i<numArgs; i++) {
			arrInt[i] = parseStringWithMath(parts[i]);
		}
		
		return arrInt;
		
	}
}
