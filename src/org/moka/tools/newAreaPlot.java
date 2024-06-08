package org.moka.tools;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class newAreaPlot {

	XYSeries lastSeries = new XYSeries("Random 1");
	XYSeriesCollection xy;
	public double xVector[], yMax[];
	public JFreeChart chart;
	public ChartFrame frame;
	
	@SuppressWarnings("deprecation")
	public newAreaPlot(String title, double[] xIn) {
		
		this.xVector = xIn;
		this.yMax = new double[xVector.length];
		for (int i=0; i<yMax.length; i++) yMax[i] = 0;
		
		xy = new XYSeriesCollection();
		chart = ChartFactory.createXYAreaChart( 
				title, // chart title 
				"x", // x axis label 
				"y", // y axis label 
				xy, // data 
				PlotOrientation.VERTICAL, 
				false, // include legend 
				true, // tooltips 
				false // urls 
				); 
		
		frame = new ChartFrame(title, chart);
		frame.pack();
		frame.setVisible(true);
		
		final XYPlot plot = chart.getXYPlot();
		plot.setForegroundAlpha(0.5f);

	}	
	
	
	public void plotData(double[][] toPlot, String title) {
		
		int cols = toPlot.length;
		for (int i=0; i< cols; i++){
			this.plotAppend(toPlot[i], "Serie "+i);
		}
	}
	
	public void plotData(double[] toPlot, String title) {
		
		this.plotAppend(toPlot, title);
		
	}
			
	public void plotAppend(double[] toPlot, String name) {
		
		XYSeries datasetDistApp  = new XYSeries(name);
		
		for (int i=0; i< toPlot.length; i++) {
			
			this.yMax[i]+=toPlot[i];
			datasetDistApp.add(xVector[i], yMax[i]);
			
		}
		
		this.xy.addSeries(datasetDistApp);
		
	}
	
	public void reset() {
		for (int i=0; i<yMax.length; i++) yMax[i] = 0;
	}
	
	//Parser per chiamete differenti
	public void plotData(double[][] toPlot) {this.plotData(toPlot, "none");}
	public void plotData(double[] toPlot){ this.plotData(toPlot, "none"); }
	
	public XYSeriesCollection getCollection() {return xy;}
	
}
