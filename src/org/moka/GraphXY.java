package org.moka;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.moka.tools.gui.GUItools;

import jhplot.HPlot;
import jhplot.P1D;


/**
 * 
 */

/**
 * @author riki
 *
 */
public class GraphXY extends JFrame implements ActionListener {

	JTextField typeT;
	JButton exportB;
	JPanel mainPanel = new JPanel();
	
	public HPlot plotter;
	
	int plotType = 0;
	String prefix = "s";
	
	int width, height;
	int incremental = 0;
	
	public GraphXY() {
	
		this.plotType = 0;
		this.width = 600;
		this.height = 400;
		
		init();
	
		
	}
	
	public GraphXY(int _type) {
	
		this.plotType = _type;
		this.width = 600;
		this.height = 400;
		
		init();
	
	}
	
	public GraphXY(int _type, int _width, int _height) {
	
		this.plotType = _type;
		this.width = _width;
		this.height = _height;
		
		init();
		
	}
	
	public void init() {

		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		switch (plotType) {
		case 0 : plotter = new HPlot("Title", width, height); break;
		default : plotter = new HPlot("Title", width, height); break;
		}
		
		plotter.setAutoRange(true);
		
	}
	
	public void createAndShowGUI() {
		
		
		Box controlB = Box.createHorizontalBox();
		
		typeT = new JTextField();
		typeT.setText("");
		controlB.add(typeT);
		
		exportB= new JButton("Plot");
		exportB.setActionCommand("plotB");
		exportB.addActionListener(this);
		controlB.add(exportB);
		
		add(GUItools.createPanelForComponent(plotter.getCanvasPanel(), "Plotter", new Dimension(width,height)), BorderLayout.CENTER);
		add(controlB, BorderLayout.SOUTH);
	
		setSize(550,450);
		this.pack();
		this.setVisible(true);
		
		
	}
	
	public Color getIncrementalColor() {
		
		int colorList = 9;
		
		switch (incremental%colorList) {
		case 0 : return Color.black;
		case 1 : return Color.red;
		case 2 : return Color.green;
		case 3 : return Color.blue;
		case 4 : return Color.pink;
		case 5 : return Color.cyan;
		case 6 : return Color.orange;
		case 7 : return Color.yellow;
		case 8 : return Color.magenta;
		case 9 : return Color.darkGray;
		default : return Color.black;
		}
		
	}
	
	//---------------------
	//	ADD DATA
	//---------------------
	public void addSeriesXY(double[] x, double[] y) {
		
		addSeriesXY(x,y,getIncrementalColor(), prefix);
		
	}
	
	public void addSeriesXY(double[] x, double[] y, Color _c) {
		addSeriesXY(x,y,_c, prefix);	
	}
	
	public void addSeriesXY(double[] x, double[] y, String _prefix) {
		
		addSeriesXY(x,y,getIncrementalColor(), _prefix);
	}
	
	public void addSeriesXY(double[] x, double[] y, Color _c, String _prefix) {
		
		incremental++;
		
		P1D h1 = new P1D(_prefix+incremental);
		
		h1.fill(x, y);
		h1.setColor(_c);
		h1.setDrawLine(true);
		h1.setFill(false);
		h1.setSymbolSize(0);
		
		plotter.draw(h1);
		
	}
	
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == exportB) {
			
			//int numData = plotter.getData().length;
			@SuppressWarnings("unused")
			int line1 = plotter.getData()[0][0].size();
			
			
		}
			
	}
	
	
}
