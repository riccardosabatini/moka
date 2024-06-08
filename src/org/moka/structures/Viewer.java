package org.moka.structures;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.vecmath.Point3f;


public interface Viewer extends Module {

	//-----------------------
	//-----   DRAW CONF
	//-----------------------
	public void drawConf(Configuration _in);
	public void drawConf(Configuration _in, String colorCell);
	
	public void drawCell(double[][] _inCell, String name);
	public void drawCell(double[][] _inCell, String name, String color);
		
	//public void drawAtomsColors(Configuration _in);
	//public void resetAtomsColors();
	
	//-----------------------
	//-----   COMMANDS
	//-----------------------
	
	public void execCommand(String _in);
	
	public String getExecutionCommand(String _comm);
	
	//-----------------------
	//-----   VIEWS
	//-----------------------
	
	public void refreshDraw(boolean keepView);
	
	public Hashtable<String, String> getView();
	public void setView(Hashtable<String, String> _view);
	public void setView(int _view);

	//--------------------------
	//-----   TRASFORMATIONS
	//--------------------------
	
	public void traslateAtoms(int[] atomsToMove, double[] moveVect);
	public void rotateAtomCoordAboutAxis(int[] atomsToMove, double[] axis, double[] center, double angle);
	//public void rotateAtomCoordAboutAxesInBaricenter(int[] atomsToMove, double[] angles, double[] point);

	//--------------------------
	//-----   GETTERS
	//--------------------------
	
	public JComponent getViewerPanel();
	
	public int findNearestAtomIndex(int x, int y);
	
	public BufferedImage getImage(int width, int height);

	public Point3f getAtomPoint3f(int site);

	//--------------------------
	//-----   DRAWABLES
	//--------------------------
	
	public void drawDrawable(Drawable _in);
	
	public void removeDrawable(Drawable _in);
	
	public void removeAllDrawables();
	
	//--------------------------
	//-----   DRAWABLES
	//--------------------------
	
	public void colorAtom(int idAtom, String color);
	
	public void colorByProperty(double[] _property, Double min, Double max);
	
	//--------------------------
	//-----   MOUSE LISTENER
	//--------------------------
	
	public void removeAllMouseListeners();
	public void addMouseListener(MouseListener l);
	
}
