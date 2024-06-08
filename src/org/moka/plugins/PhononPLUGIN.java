package org.moka.plugins;



import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import org.moka.GraphXY;
import org.moka.Moka;

import org.moka.qeReader.qeParser;

import org.moka.structures.Plugin;
import org.moka.tools.ArrayTools;

public class PhononPLUGIN extends JFrame implements ActionListener, Plugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387738545758818823L;

	Moka app;
	
    private JFileChooser fc;
    
    PhononConfiguration[] omegaK;
	//KLIST KPath;
	
    ///---------------------------
    /// CONSTRUCTOR	(bottoni, pannelli)
    ///---------------------------
	public PhononPLUGIN(Moka _app) {
		
		this.app = _app;
		fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

    ///---------------------------
    /// CREA INTERFACCIA
    ///---------------------------

	 public JMenu makePluginMenu() {
		 
		 JMenu mPH;
		 JMenuItem mPlot,mImport, mPrint;
		 
		 mPH = new JMenu("PH");

		 mImport = new JMenuItem("Import...");
		 mImport.addActionListener(this);
		 mImport.setActionCommand("mImport");
		 mPH.add(mImport);
		 
		 mPlot = new JMenuItem("Plot");
		 mPlot.addActionListener(this);
		 mPlot.setActionCommand("mPlot");
		 mPH.add(mPlot);
		 
		 
		 mPrint = new JMenuItem("Print");
		 mPrint.addActionListener(this);
		 mPrint.setActionCommand("mPrint");
		 mPH.add(mPrint);

		 return mPH;
	        
	 }
	
	public void actionPerformed(ActionEvent e) {

		String buttClicked = e.getActionCommand(); //getClassName(e.getSource().getClass());

		//------------------
		// menu FILE
		//------------------
		if (buttClicked.equals("mImport")) {
			
			if (app.status.getWorkingDir()!=null) fc.setCurrentDirectory(new File(app.status.getWorkingDir()));
			
			if (app.workConf.getKpoints().repeatedPointExist()) {
				app.log("Delete repeated k points");
				return;
			}
			
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {            	
				File filePH = fc.getSelectedFile();

		    	try {
		    			//KPath = qeParser.readKList(filePH);
		    			omegaK = qeParser.readPHall(filePH,app.workConf.getKpoints().getNPoints());
		    			
		    			app.log("Imported "+omegaK.length+" frequecies");
		    			
		    	} catch (NumberFormatException e1) {
		    		app.log("Error "+e1.getMessage());
				} catch (IOException e1) {
					app.log("Error "+e1.getMessage());
				}
			}
		}
		
		if (buttClicked.equals("mPrint")) {
		    
			//makeSeries();
			//plotEkinpot();
			writePhononKlist();
		}
		
		if (buttClicked.equals("mPlot")) {
		    
			//makeSeries();
			plotPhonon(new int[] {-1}, 0);
			//writeTimeEnTemp();
		}
    }
	
	public void resetArrays() {
		
		
	}
	
	
	///---------------------------
    /// PLOTTER
    ///---------------------------	
	
	public double cmToPeriodAU (double cm) {
		
		double cmToHz = 29979245800.0;
		double timeAUHartree = 2.418884326505E-17;
		double timeAURy = 2*timeAUHartree;
		
		
		return ((2*Math.PI)/(cm*cmToHz))/timeAURy;
			
	}
	
	public void plotPhonon(int[] modes, int type) {
		
		boolean repeatFirst = true;
		
		GraphXY graph = new GraphXY(0,600,400);
		
		double[] kPath = app.workConf.getKpoints().spanKPath(repeatFirst);
		
		
		for (int j=0; j<omegaK[0].size(); j++){

			if (ArrayTools.isInArray(modes, (j+1)) || modes[0]==-1) {
				
				int vectLenght = kPath.length;
				double thisMode[][] = new double [2][vectLenght];
				
				thisMode[0] = kPath;
				
				for (int k=0; k<omegaK.length; k++){ 
					if (type==0){
						thisMode[1][k] = omegaK[k].omega.get(j);
					} else if (type==1) {
						thisMode[1][k] = cmToPeriodAU(omegaK[k].omega.get(j));
					}
					
				}
				
				if (repeatFirst) {
					if (type==0){
						thisMode[1][vectLenght-1] = omegaK[0].omega.get(j);
					} else if (type==1) {
						thisMode[1][vectLenght-1] = cmToPeriodAU(omegaK[0].omega.get(j));
					}
					
				}
				
				graph.addSeriesXY(thisMode[0], thisMode[1]);
				
			}
		}

		graph.createAndShowGUI();
		
	}

	
	public void writePhononKlist() {
		
		app.output("#Time\t Etot\t EkinEtot\t Ekin\t Temp"+"\n");
		
		for (int i=0; i<app.confDB.size(); i++){
			app.output((Double)app.confDB.get(i).getData().getDataByName("timeStep")+"\t"
					+(Double)app.confDB.get(i).getData().getDataByName("eTot")+"\t"
					+(Double)app.confDB.get(i).getData().getDataByName("eKineTot")+"\t"
					+(Double)app.confDB.get(i).getData().getDataByName("eKin")+"\t"
					+(Double)app.confDB.get(i).getData().getDataByName("temp")+"\n");
		}
		
		
		
	}
	
	public boolean isReadyToReciveSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#reciveSelection(int)
	 */
	public boolean receiveSelection(int atomSelected) {
		// TODO Auto-generated method stub
		return false;
	}

    public void confChanged() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#getAtomProperty(int)
	 */
	public Object getAtomProperty(int atomSelected) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#getConfProperty(int)
	 */
	public Object getConfProperty(int _property) {
		// TODO Auto-generated method stub
		return null;
	} 

	
}
