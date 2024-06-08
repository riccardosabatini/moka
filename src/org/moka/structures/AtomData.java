/**
 * 
 */
package org.moka.structures;

import java.io.Serializable;

import org.moka.tools.ArrayTools;
import org.moka.common.Costants;

/**
 * @author riki
 *
 */
public class AtomData implements Serializable {
	
	static final public int numDATA = Costants.atomsDataNames.length; 
	
	public String[] names = new String[numDATA];
	public Class[] types = new Class[numDATA];
	public Object[] values = new Object[numDATA];
	
	public AtomData() {

		names = Costants.atomsDataNames;
		types = Costants.atomsDataTypes;

		for (int i=0; i<numDATA; i++) {
			values[i] = dataNull(types[i]);
		}
	
	}
	
	// ---------------------------------------------
	// CLEAR
	// ---------------------------------------------

	public void clear() {
		
		names = Costants.atomsDataNames;
		types = Costants.atomsDataTypes;

		for (int i=0; i<numDATA; i++) {
			values[i] = dataNull(types[i]);
		}

		
	}
	
	//---------------------
	//	IMPOSTA I VAOLIR DI NULL
	//---------------------
	
	public Object dataNull(Class _class) {
		
		if (_class.equals(Double.class)) {
			return Double.MAX_VALUE;
		}
		else if (_class.equals(Integer.class)) {
			return Integer.MAX_VALUE;
		}
		else if (_class.equals(String.class)) {
			return "null";
		}
		else {
			return null;	
		}
		
	}
	
	//---------------------
	//	SALVA
	//---------------------
	
	public void storeData(int index, Object _in) {
		
		if (_in.getClass().equals(types[index])){
			values[index] = _in;
		}
		
	}
	
	public void storeDataByName(String _name, Object _in) {
		
		for (int i=0; i<numDATA; i++) {
			if (names[i].equals(_name)) {
				
				storeData(i, _in);
				
			}
		}
		
		
		
	}
	
	public void storeData(int index, String _in) {
		
		if (types[index].equals(Double.class)) {
			values[index] = Double.valueOf(_in);
		}
		else if (types[index].equals(Integer.class)) {
			values[index] = Integer.valueOf(_in);
		}
		else if (types[index].equals(String.class)) {
			values[index] = _in;
		}
		else {
			values[index] = dataNull(types[index]);	
		}
	}

	//---------------------
	//	LEGGI
	//---------------------
	
	public String printData(int index) {
		
		if (values[index].equals(dataNull(types[index]))) return "null";
		
		if (types[index] == Double.class || types[index] == Integer.class) {
			return String.format(Costants.decimalPlace8,values[index]); 
		}
			
		return values[index].toString();
	}
	
	public Object getData(int index) {
		
		return values[index];
		
	}
	
	public Object getName(int index) {
		
		return names[index];
		
	}
	
	public Object getDataByName(String _name) {
		
		for (int i=0; i<numDATA; i++) {
			if (names[i].equals(_name)) {
				
				return values[i];
			}
		}
		return null;
		
	}
	
	public Class getClassForTable(int index) {
		
		if (types[index] == Double.class ||
				types[index] == Integer.class) {
			return Number.class;
		} else 
		{
			return types[index];
		}
		
		
	}

	//---------------------
	//	Controlli
	//---------------------
	
	public boolean isNull(String varName) {
		
		int varId = ArrayTools.whereIsInArray(names, varName);
		
		if (values[varId].equals(dataNull(values[varId].getClass()))) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isNull(int varId) {
		
		if (values[varId].equals(dataNull(values[varId].getClass()))) {
			return true;
		} else {
			return false;
		}
	}

	//---------------------
	//	GETTERS / SETTERS - BEAN
	//---------------------
	
	/**
	 * @return the names
	 */
	public String[] getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(String[] names) {
		this.names = names;
	}

	/**
	 * @return the types
	 */
	public Class[] getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(Class[] types) {
		this.types = types;
	}

	/**
	 * @return the values
	 */
	public Object[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Object[] values) {
		this.values = values;
	}


	

}
