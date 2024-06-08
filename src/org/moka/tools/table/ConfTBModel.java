package org.moka.tools.table;


import  org.moka.structures.AtomData;

/**
 * @author riki
 *
 */
public class ConfTBModel extends SortableTableModel {

	AtomData dataINDEX = new AtomData();
	boolean[] columnsVisible = new boolean[dataINDEX.numDATA+1];

	//Imposta le colonne che si possono vedere
	public ConfTBModel(boolean[] _visIn) {

		columnsVisible[0] = true;
		
		for (int i=1; i<columnsVisible.length; i++) {
			if ((i-1)<_visIn.length) {
				columnsVisible[i] = _visIn[(i-1)];
			} else {
				columnsVisible[i] = false;
			}
		}

	}
	
	
	//Prende i nomi delle colonne
	public String getColumnName (int col) {
		
		if (col == 0) {
			return "Num";
		} 
		else {

			return dataINDEX.names[getNumber(col)-1];
			
		}
	}

	//Seleziona solo le colonne visibile e da il numero corretto
	//	tieni conto di una colonna per l'intestazione
	protected int getNumber (int col) {
		
		int n = 0;    // right number to return
		int i = 0;
		
		do {
			if (columnsVisible[i]) { n++; } 
			i++;
		} while (n<=col);
		i--;
		
		return i; //La colonna NUM
		
//		do {
//		if (!(columnsVisible[i])) n++;
//		i++;
//		} while (i < n);
//		// If we are on an invisible column, 
//		// we have to go one step further
//		while (!(columnsVisible[n])) n++;
//
//		return n;
	}

	//Conta le colonne visibili
	public int getColumnCount () {
		int n = 0;
		for (int i = 0; i < columnsVisible.length; i++)
			if (columnsVisible[i]) n++;
		return n;
	}
	
	public void setValueAt(Object value, int row, int col) {
		super.setValueAt(value, row, getNumber(col));
	}
	
	public void getValueAt(Object value, int row, int col) {
		super.setValueAt(value, row, getNumber(col));
	}

	public Class getColumnClass(int col) {
		
		if (col == 0) {
			return Number.class;
		} else { 
			return dataINDEX.getClassForTable(getNumber(col)-1);	//la prima colonna e' il numero
		}
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}


}
