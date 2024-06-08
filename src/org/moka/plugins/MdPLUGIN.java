package org.moka.plugins;



import java.io.*;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.opensourcephysics.frames.PlotFrame;

import org.moka.common.Costants;
import org.moka.Moka;

import org.moka.structures.Plugin;
import org.moka.tools.*;


public class MdPLUGIN extends JFrame implements ActionListener, Plugin{

	Moka app;
	newAreaPlot areaStacked;
	
	public int[] orbitalsSelected, atomsSelected; 
    public double eFermi=0; 
    public boolean spin=false;
    
    XYSeries seriesEtot,seriesEkin,seriesEkinpot,seriesEdiff,seriesTemp,seriesR;
    PlotFrame plotMD;
    
    ///---------------------------
    /// CONSTRUCTOR	(bottoni, pannelli)
    ///---------------------------
	public MdPLUGIN(Moka _app) {
		
		this.app = _app;

	}

    ///---------------------------
    /// CREA INTERFACCIA
    ///---------------------------

	 public JMenu makePluginMenu() {
		 
		 JMenu mMD;
		 JMenuItem mPlotOrig,mPlotOne, mCalc, mPrint;
		 
		 mMD = new JMenu("MD");

		 mPlotOrig = new JMenuItem("Plot");
		 mPlotOrig.addActionListener(this);
		 mPlotOrig.setActionCommand("mPlotOrig");
		 mMD.add(mPlotOrig);
		 
		 mPrint = new JMenuItem("Print");
		 mPrint.addActionListener(this);
		 mPrint.setActionCommand("mPrint");
		 mMD.add(mPrint);

		 return mMD;
	        
	 }
	
	public void actionPerformed(ActionEvent e) {

		String buttClicked = e.getActionCommand(); //getClassName(e.getSource().getClass());

		//------------------
		// menu FILE
		//------------------
		if (buttClicked.equals("mPrint")) {
		    
			//makeSeries();
			//plotEkinpot();
			writeTimeEnTemp();
		}
		
		if (buttClicked.equals("mPlotOrig")) {
		    
			//makeSeries();
			plotEkinpot();
			//writeTimeEnTemp();
		}
    }
	
	public void resetArrays() {
		
		
	}
	
	
	///---------------------------
    /// PLOTTER
    ///---------------------------	
	
	public void makeSeries() {


		seriesEtot = new XYSeries("E Tot");
		seriesEkin = new XYSeries("E Kin");
		seriesEkinpot = new XYSeries("E Kin + Pot");
		seriesEdiff = new XYSeries("E diff");
		seriesTemp = new XYSeries("Temperature");
		
		for (int i=0; i<app.confDB.size(); i++){
			
			seriesEtot.add((Double)app.confDB.get(i).getData().getDataByName("timeStep"),(Double)app.confDB.get(i).getData().getDataByName("eTot"));
			seriesEkin.add((Double)app.confDB.get(i).getData().getDataByName("timeStep"),(Double)app.confDB.get(i).getData().getDataByName("eKineTot"));

			//seriesR.add((double)md.getTime().get(i),avgR/nAtoms);
						
		}
	}
	
	public void plotEkinpot() {
		
		plotMD = new PlotFrame("time","PA/NkT", "Mean Pressure");
		plotMD.pack();
		plotMD.setVisible(true);
		
		for (int i=0; i<app.confDB.size(); i++){
			plotMD.append(0, (Double)app.confDB.get(i).getData().getDataByName("timeStep"),(Double)app.confDB.get(i).getData().getDataByName("eTot"));
			plotMD.append(1, (Double)app.confDB.get(i).getData().getDataByName("timeStep"),(Double)app.confDB.get(i).getData().getDataByName("eKineTot"));
									
		}
	}

	
	public void writeTimeEnTemp() {
		
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
