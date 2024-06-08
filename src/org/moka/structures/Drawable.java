package org.moka.structures;
import java.io.Serializable;

import org.jmol.api.JmolViewer;

import org.moka.common.Costants;

/**
 * 
 */

/**
 * @author riki
 *
 */
public class Drawable implements Serializable {


	double points[][];
	double offset[];
	
	int type;
	String name = "";
	int perc = 100;
	
	int[] color;
	double alpha = 0.0;
	
	boolean active=true; 
	
	public Drawable() {
		
		this.name = "null";
		this.type = -1;
		this.active = false;
		
	}
	
	public Drawable(int _type, String _name, double[][] _points) {
		this.name = _name;
		this.type = _type;
		this.points = _points;
		
		this.offset = new double[] {0,0,0};
	}
	
	public Drawable(int _type, String _name, double[][] _points, double[] _dataOff, int _perc, int[] _color, double _alpha, boolean _active) {
		this.name = _name;
		this.type = _type;
		this.points = _points;
		this.offset = _dataOff;
		this.perc = _perc;
		this.color = _color;
		this.alpha = _alpha;
		this.active = _active;
	}
	
	public Drawable(int _type, String _name, double[][] _points, double[] _dataOff, int _perc, int[] _color) {
		this.name = _name;
		this.type = _type;
		this.points = _points;
		this.offset = _dataOff;
		this.perc = _perc;
		this.color = _color;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getName() {
		return name;
	}
	
	public double[][] getPoints() {
		
		if (points==null) return null;
		return points;
	}
	
	public double[] getOffset() {
		
		if (offset==null) return null;
		return offset;
	}
	
	public int getType() {
		return type;
	}
	
	public int getPercentual() {
		return perc;
	}
	
	public double getAlpha() {
		return alpha;
	}
	
	public int[] getColor() {
		
		if (color==null) return null;
		return color;
		
	}
	
	public int getPerc() {
		return perc;
	}

	public void setPerc(int perc) {
		this.perc = perc;
	}

	public void setPoints(double[][] points) {
		this.points = points;
	}

	public void setOffset(double[] offset) {
		this.offset = offset;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setColor(int[] color) {
		this.color = color;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setActive(boolean _in) {
		this.active = _in;
	}
}
