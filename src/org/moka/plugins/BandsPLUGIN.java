package org.moka.plugins;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.moka.GraphXY;
import org.moka.Moka;


import org.moka.qeReader.qeParser;
import org.moka.structures.Kpoints;
import org.moka.structures.Plugin;

import org.moka.tools.ArrayTools;

import org.moka.tools.*;
import org.moka.tools.gui.RadioDialog;

public class BandsPLUGIN extends JFrame implements ActionListener, Plugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7602854889748241547L;

	Moka app;
	
    private JFileChooser fc;
    
    //PlotFrame plotBANDS;
    //HPlot plotter;
    
    Kpoints temp;
    double[][] bands;
    
    ///---------------------------
    /// CONSTRUCTOR	(bottoni, pannelli)
    ///---------------------------
	public BandsPLUGIN(Moka _app) {
		
		this.app = _app;
		fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}

    ///---------------------------
    /// CREA INTERFACCIA
    ///---------------------------

	 public JMenu makePluginMenu() {
		 
		 JMenu mBANDS;
		 JMenuItem mPlotOrig, mImport, mPrint;
		 
		 mBANDS = new JMenu("BANDS");

		 mImport = new JMenuItem("Import...");
		 mImport.addActionListener(this);
		 mImport.setActionCommand("mImport");
		 mBANDS.add(mImport);
		 
		 mPlotOrig = new JMenuItem("Plot");
		 mPlotOrig.addActionListener(this);
		 mPlotOrig.setActionCommand("mPlotOrig");
		 mBANDS.add(mPlotOrig);
		 
		 mPrint = new JMenuItem("Print");
		 mPrint.addActionListener(this);
		 mPrint.setActionCommand("mPrint");
		 mBANDS.add(mPrint);

		 return mBANDS;
	        
	 }
	
	public void actionPerformed(ActionEvent e) {

		String buttClicked = e.getActionCommand(); //getClassName(e.getSource().getClass());

		//------------------
		// menu FILE
		//------------------
		
		if (buttClicked.equals("mImport")) {
			
			if (app.status.getWorkingDir()!=null) fc.setCurrentDirectory(new File(app.status.getWorkingDir()));
			
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {            	
				File fileBANDS = fc.getSelectedFile();

				try {

					bands = (double[][]) qeParser.readBANDS(fileBANDS.getAbsolutePath(),0);
					temp = (Kpoints) qeParser.readBANDS(fileBANDS.getAbsolutePath(),1);
					app.log("Band file correctely imported. Bands# "+bands[0].length);
				} catch (NumberFormatException e1) {
					app.log("Number format error");
				} catch (IOException e1) {
					app.log("IO error");
				}
			}
		}
		
		if (buttClicked.equals("mPrint")) {
		    
			writeBands();

		}
		
		if (buttClicked.equals("mPlotOrig")) {
		    
			//makeSeries();
			int ans = RadioDialog.showDialog(this, null, "Repeat first", "repeat first", new String[] {"Yes", "No"}, 0);
			
			Double eFermi = 0.0;
			if (!app.workConf.getData().isNull("eFermi")) {
				eFermi = (Double) app.workConf.getData().getDataByName("eFermi");
				app.log("Efermi: "+eFermi);
			}
			
			plotBands(ans == 0 ? true : false,eFermi);
			//writeTimeEnTemp();
		}
    }
	
	public void resetArrays() {
		
		
	}
	
	
	///---------------------------
    /// PLOTTER
    ///---------------------------	

	
	public void plotBands(boolean repeatFirst, double eFermi) {

        if (bands==null) return;

		GraphXY graph = new GraphXY(0,600,400);
		
		
		
		
//		plotBANDS = new PlotFrame("K space","En (eV)", "Bands");
//		plotBANDS.pack();
//		plotBANDS.setVisible(true);
//		plotBANDS.setConnected(true);
//		
		//H1D h1 = new H1D();
		
		double[] kSpace = new double[repeatFirst ? temp.getNPoints() +1 : temp.getNPoints()];
		
		
		kSpace[0] = 0;
		for (int j=1; j<temp.getNPoints(); j++){

			double[] Kzero = temp.getKpoint(j-1);
			double[] thisK = temp.getKpoint(j);

			double dist = MathTools.getDistance(Kzero, thisK); 
			kSpace[j] = kSpace[j-1] + dist;

		}
		
		if (repeatFirst) {
			
			double[] Kzero = temp.getKpoint(0);
			double[] thisK = temp.getKpoint(temp.getNPoints()-1);

			double dist = MathTools.getDistance(Kzero, thisK);
			kSpace[temp.getNPoints()] = kSpace[temp.getNPoints()-1] + dist; 
		}
		
		
		
		for (int j=0; j<bands[0].length; j++){

			double[] oneBand = new double[repeatFirst ? temp.getNPoints() + 1 : temp.getNPoints()];

			for (int k=0; k<temp.getNPoints(); k++){

				oneBand[k] = bands[k][j];

			}
			
			if (repeatFirst) {
				oneBand[temp.getNPoints()] = bands[0][j];
			}
			
			graph.addSeriesXY(kSpace, MathTools.arrayAdd(oneBand, -1*eFermi), Color.black);
			
			//h1.
//			plotBANDS.append(j, kSpace, MathTools.arrayAdd(oneBand, -1*eFermi));
//			plotBANDS.setMarkerSize(j, 0);
//			plotBANDS.setLineColor(j, Color.black);
//			//plotBANDS.setConnected(j, true);
		}
		
		graph.addSeriesXY(kSpace, ArrayTools.arrayNewFill(kSpace.length, 0.0), Color.red);
			
		graph.createAndShowGUI();
		
//		plotBANDS.append(bands[0].length, kSpace, ArrayTools.arrayNewFill(kSpace.length, 0.0));
//		plotBANDS.setMarkerSize(bands[0].length, 0);
//		plotBANDS.setLineColor(bands[0].length, Color.red);
		
	}


	
	public void writeBands() {

        if (bands==null) return;

		app.output("#Kspace bands"+"\n");
		
		double[] kSpace = new double[temp.getNPoints()];

		for (int j=0; j<temp.getNPoints(); j++){

			double[] Kzero = temp.getKpoint(0);
			double[] thisK = temp.getKpoint(j);

			kSpace[j] = MathTools.getDistance(Kzero, thisK);

		}

		for (int j=0; j<temp.getNPoints(); j++){
			
			app.output(kSpace[j]+"\t");
			
			for (int k=0; k<bands[j].length; k++){
				app.output(bands[j][k]+"\t");
				

			}

			app.output("\n");

		}
		
		
	}

    public void confChanged() {

        bands = null;

    }
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	/* (non-Javadoc)
	 * @see MokaPLUGIN#reciveSelection()
	 */
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
