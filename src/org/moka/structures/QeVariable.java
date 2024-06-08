/**
 * 
 */
package org.moka.structures;

/**
 * @author riki
 *
 */
public class QeVariable {

	Object value = 1;
	Class thisClass = Integer.class;
	
	Class[] allClasses = new Class[] {Integer.class, (new int[] {0}).getClass(), Double.class, (new double[] {0.0}).getClass(), Boolean.class, String.class, (new String[] {""}).getClass()}; 
	
	public QeVariable (int _in) {
		value = _in;
		thisClass = Integer.class;
	}
	public QeVariable (int[] _in) {
		value = _in;
		thisClass = (new int[] {0}).getClass();
	}
	
	public QeVariable (double _in) {
		value = _in;
		thisClass = Double.class;
	}
	public QeVariable (double[] _in) {
		value = _in;
		thisClass = (new double[] {0.0}).getClass();
	}
	
	public QeVariable (boolean _in) {
		value = _in;
		thisClass = Boolean.class;
	}
	
	public QeVariable (String _in) {
		value = _in;
		thisClass = String.class;
	}
	public QeVariable (String[] _in) {
		value = _in;
		thisClass = (new String[] {""}).getClass();
	}
	
	
	
	public Class varClass() {
		return thisClass;
	}
	
	public boolean validate(String _in) {
		return validate(_in, thisClass);
	}
	
	public boolean validate(String _in, Class _class) {
		
		boolean out = false;
		int numClass = whichClass(_class);
		
		if (numClass==0) {	// Interi
			
			try {
				int prova = Integer.parseInt(_in);
			} catch (NumberFormatException e) {
				out = false;
			}
		} else
			
		if (numClass==2) {	//Double e real
			
			try {
				double prova = Double.parseDouble(_in);
			} catch (NumberFormatException e) {
				out = false;
			}
		}
		
		if (numClass==4) {
			
			if (!_in.equals(".true.") || !_in.equals(".false.")) {
				out = false;
			}
		}
		
		return out;
	}
		
	public String qeOutput() {
		return "";
	}
	
	
	public int whichClass(Object _in) {
		
		int out = -1;
		for (int i =0; i<allClasses.length; i++) {
			if (allClasses[i].equals(_in.getClass())) {
				out = i;
			}
		}
		
		return out;
	}
	
}
