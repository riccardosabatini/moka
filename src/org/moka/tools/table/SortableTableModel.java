package org.moka.tools.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @version 1.0 02/25/99
 */
public class SortableTableModel extends DefaultTableModel {
	int[] indexes;
	TableSorter sorter;

	public SortableTableModel() {   
	}

	public Object getValueAt(int row, int col) {

		this.indexes = getIndexes();	//Fix, forse rallenta in render della tabella
		int rowIndex = row;
		if (indexes != null) {
			rowIndex = indexes[row];
		}
		return super.getValueAt(rowIndex, col);
	}

	public void setValueAt(Object value, int row, int col) {    
		int rowIndex = row;
		if (indexes != null) {
			rowIndex = indexes[row];
		}
		super.setValueAt(value, rowIndex, col);
	}


	public void sortByColumn(int column, boolean isAscent) {
		if (sorter == null) {
			sorter = new TableSorter(this);
		}
		sorter.sort(column, isAscent);   
		fireTableDataChanged();
	}

	public int[] getIndexes() {
		int n = getRowCount();
		if (indexes != null) {
			if (indexes.length == n) {
				return indexes;
			}
		}
		indexes = new int[n];
		for (int i=0; i<n; i++) {
			indexes[i] = i;
		}
		return indexes;
	}

    public void addColumn(String _in) {

        super.addColumn(_in);

    }

}