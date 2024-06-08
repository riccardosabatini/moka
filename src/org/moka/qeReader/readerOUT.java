package org.moka.qeReader;

import java.io.*;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.*;

public class readerOUT extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6178060109747238173L;
	/**
	 * @param args
	 * @throws IOException 
	 */
	//Per histogramma
	Double[][] getCell = new Double[3][3];
	ArrayList<Double[]> getPositions = new ArrayList<Double[]>();
	ArrayList<String> getType = new ArrayList<String>();
		
	// Per PWSCF input
	Boolean gotNatom = false;
	Boolean gotA = false;
	Boolean gotData = false;
	Boolean gotCell = false;
	int nAtoms = 1;
	int aCell = 1;
	int steps = -1;
	static private final double Ry = 0.5291772;
	
	double[][] rdtEach, rEach;
	double[] rdtAll, rAll;
	
	//Per interfaccia
	static private final String newline = "\n";
    JButton openButton, mirrorButton, shiftButton;
    JTextArea log;
    JFileChooser fc;
    JTextField jtfText1, jtfUneditableText;
    
	
	public readerOUT() {
		super(new BorderLayout());
		
		//Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(35,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        //Crea file choser
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        Box topBoxB = Box.createHorizontalBox();
        
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);
        
        shiftButton = new JButton("Shift atoms");
        shiftButton.addActionListener(this);
        
        mirrorButton = new JButton("Mirror atoms");
        mirrorButton.addActionListener(this);
        
        topBoxB.add(openButton);
        topBoxB.add(shiftButton);
        topBoxB.add(mirrorButton);
        
        add(topBoxB, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.SOUTH);
        
        

	}
	
	public void actionPerformed(ActionEvent e) {

        //Handle open button action.
        if (e.getSource() == openButton) {
            
            if (fc.showOpenDialog(readerOUT.this) == JFileChooser.APPROVE_OPTION) {
            	
            File file = fc.getSelectedFile();
            log.append("Opening: " + file.getName() + "." + newline);
            
        	try {
        		//
        		//	Bottone read
        		//
				this.readFile(file.getAbsolutePath());
				log.append("Numero atomi: " + nAtoms + newline);
				log.append("Dimensione cella: " + aCell + newline);
				
				for (int i=0; i<nAtoms; i++) {
					log.append("Atomo ("+i+")-  " + getType.get(i) + "\t"+getPositions.get(i)[0]+"\t"+getPositions.get(i)[1]+"\t"+getPositions.get(i)[2]+"\n");
				}
			
			} catch (NumberFormatException e1) {
				log.append("Errore formato numero." + newline);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				log.append("Errore lettura file." + newline);
			}
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());

        //Handle save button action.
        } else if (e.getSource() == shiftButton) {
        	
        	String inputXYZ = getStrFormUser("Dammil il vettore di shift");
    		String[] atomXYZ = inputXYZ.replaceAll("\\t", " ").split("\\s+");
    		
    		double vectX = Double.valueOf(atomXYZ[0]);
    		double vectY = Double.valueOf(atomXYZ[1]);
    		double vectZ = Double.valueOf(atomXYZ[2]);
    		
    		log.setText("");
    		log.append("------Atomi shiftati-------\n");
    		for (int i=0; i<nAtoms; i++) {
    			
    			double newX = (double) getPositions.get(i)[0]+vectX;
				double newY = (double) getPositions.get(i)[1]+vectY;
				double newZ = (double) getPositions.get(i)[2]+vectZ;
    			
				log.append(getType.get(i) + "\t");
				log.append(String.format("%.7f",newX)+"\t");
				log.append(String.format("%.7f",newY)+"\t");
				log.append(String.format("%.7f",newZ)+"\t");
				log.append("\n");
			}
        
        } else if (e.getSource() == mirrorButton) {
        	
        	String inputXYZ = getStrFormUser("Dammil il vettore di mirror");
    		String[] atomXYZ = inputXYZ.replaceAll("\\t", " ").split("\\s+");
    		
    		double vectX = Double.parseDouble(atomXYZ[0]);
    		double vectY = Double.parseDouble(atomXYZ[1]);
    		double vectZ = Double.parseDouble(atomXYZ[2]);
    		
    		
    		
    		log.setText("");
    		log.append("------Atomi mirrorati-------"+vectX+"/"+vectY+"/"+vectZ+"\n");
    		for (int i=0; i<nAtoms; i++) {
    			
    			double diffX=0.0, diffY=0.0,diffZ=0.0;
    			
    			double oldX = (double) getPositions.get(i)[0];
				double oldY = (double) getPositions.get(i)[1];
				double oldZ = (double) getPositions.get(i)[2];
    			
				double newX = oldX;
				double newY = oldY;
				double newZ = oldZ;
				
    			if (vectX > 0){
    				diffX = (oldX - vectX);
    				newX = vectX - diffX;
    			}
    			if (vectY > 0){
    				diffY = (oldY - vectY);
    				newY = vectY - diffY;
    			}
    			if (vectZ > 0){
    				diffZ = (oldZ - vectZ);
    				newZ = vectZ - diffZ;
    			}

    			log.append(getType.get(i) + "\t");
				log.append(String.format("%.7f",newX)+"\t");
				log.append(String.format("%.7f",newY)+"\t");
				log.append(String.format("%.7f",newZ)+"\t");
				log.append("\n");
			}
  

        }
    }
	
	public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("PWSCF output tools");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Add content to the window.
        frame.add(new readerOUT());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	public static void main(String[] a) throws IOException {
		
		readerOUT app = new readerOUT();
		app.createAndShowGUI();
		
	}
	
	public String getStrFormUser(String title) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(null, title, "", 1);
			if (str.equals("")) JOptionPane.showMessageDialog(null, "Inserire un numero", "a", 1);
		} while (str.equals(""));
		
		return str;
	
        
	}

	public void readFile(String filename) throws IOException,NumberFormatException {

		//resetta tutto
		gotNatom = false;
		gotA = false;
		gotCell = false;
		gotData = false;
		nAtoms = 1;
		aCell = 1;
		steps = -1;
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
		lnr.setLineNumber(0);
		String line = lnr.readLine();

		do  {

			String[] words = line.replaceAll("\\t", " ").split("\\s+");
			
			if (words.length > 1) {
				
				//Becco il paametro di cella
				if (words[1].equals("lattice") && words[2].equals("parameter") && !gotA){

					aCell = (int)Double.valueOf(words[5].trim()).doubleValue();
					gotA = true;
					//System.out.println("NATOMS = " + nAtoms);

				}
				
				//Becco il numero di atomi
				if (words[1].equals("number") && words[3].equals("atoms/cell") && !gotNatom){

					nAtoms = (int)Double.valueOf(words[5].trim()).doubleValue();
					gotNatom = true;
					//System.out.println("NATOMS = " + nAtoms);

				}


				//Leggo i dati della cella
			   if (words[0].equals("CELL_PARAMETERS")){
					//System.out.println("Step ---------------"+steps);
					for (int i = 0; i < 3; i++){
						//Avanzo per ogni asse
						line = lnr.readLine();
						words = line.replaceAll("\\t", " ").split("\\s+");

						double aX = Double.valueOf(words[1].trim()).doubleValue();
						double aY = Double.valueOf(words[2].trim()).doubleValue();
						double aZ = Double.valueOf(words[3].trim()).doubleValue();
						//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);
						
						//Salvo nella lista
						getCell[i][0] = aX;
						getCell[i][0] = aY;
						getCell[i][0] = aZ;

					}
					//gotCell = true;
				}

				//posizioni
				if (words[0].equals("ATOMIC_POSITIONS")){
					
					
					for (int thisAtom = 0; thisAtom < nAtoms; thisAtom++){
						//Avanzo per ogni atomo
						line = lnr.readLine();
						words = line.replaceAll("\\t", " ").split("\\s+");
						
						double posX = Double.valueOf(words[1].trim()).doubleValue();
						double posY = Double.valueOf(words[2].trim()).doubleValue();
						double posZ = Double.valueOf(words[3].trim()).doubleValue();
						//System.out.println("Atomo " + (thisAtom+1)+" - "+posX+" "+posY+" "+posZ);
						
						//Salvo i nomi
						getType.add(words[0].trim());
						
						//Salvo le posizioni
						Double[] posTemp = {posX,posY,posZ};
						getPositions.add(thisAtom,posTemp);
					}
					//gotData = true;
				}
				
			}

		} while (((line = lnr.readLine()) != null) || (gotData && gotCell && gotA && gotNatom));
		
		gotData = true;
		
	}
	

	
	public void analyseData(){
		
		
		double[][] rdtEachT = new double[nAtoms][steps];
		double[] rdtAllT = new double[steps*nAtoms];
		
		
		for (int i=0; i<steps; i++){
			
			
			for (int j=0; j<nAtoms; j++){
				
				Double[] oldPos = new Double[3];
				
				if (i>0){
					oldPos = ((Double [])getPositions.get((i-1)*nAtoms + j));
				} else {
					oldPos = ((Double [])getPositions.get(i*nAtoms +j));
				}
				Double[] newPos = new Double[3]; 
				newPos =	(Double [])getPositions.get(i*nAtoms + j);
				
				
				double Rdtau = Math.sqrt(Math.pow((newPos[0]-oldPos[0]),2) + Math.pow((newPos[1]-oldPos[1]),2) + Math.pow((newPos[2]-oldPos[2]),2));
				
				rdtAllT[i*nAtoms+j] = Rdtau*aCell*Ry;
				rdtEachT[j][i] = Rdtau*aCell*Ry;
				
			}			
		}
		rdtAll = rdtAllT.clone();
		rdtEach = rdtEachT.clone();
		
	}


	
	public void writeAtomsPos(String filename) throws IOException,NumberFormatException {
		
		
		
		PrintStream out = new PrintStream(new FileOutputStream(filename));
		//Intestazione
		out.print("#Time\t ");
		for (int i=0; i<nAtoms; i++){ 
			out.print("atom"+i+"\t");
		}
		out.print("\n");
		
		//Posizione per atomo
		for (int i=0; i<steps; i++){
			
			
			for (int j=0; j<nAtoms; j++){
				
				out.print(String.format("%.5f",rdtEach[j][i])+"\t");
			}
			out.println("\n");
		}
		
	}


	
}
