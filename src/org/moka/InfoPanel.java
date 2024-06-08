package org.moka;


import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.moka.common.Costants;

import org.moka.structures.AtomData;
import org.moka.structures.Module;
import org.moka.tools.ArrayTools;
import org.moka.tools.MathTools;
import org.moka.tools.gui.GUItools;
import org.moka.tools.gui.JMultilineLabel;
import org.moka.tools.gui.SizedTextField;

/**
 * @author riki
 *
 */
public class InfoPanel extends JFrame implements  ActionListener, Module {

	Moka app;
	
	JTable infoTable, pluginTable;
	DefaultTableModel infoTableModel, pluginTableModel;
	
	//int barWidth = 150, barHeigth = 400;
	
	public InfoPanel(Moka _app) {
        
		JPanel frameINFO = new JPanel();
		frameINFO.setLayout(new BorderLayout());
		//setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		
		this.app = _app;
		
		//----------------------------------------
		//	Pannello per INFO
		//----------------------------------------
		
		
		infoTableModel = new DefaultTableModel();
        infoTableModel.addColumn("Name");
		infoTableModel.addColumn("Value");		
		infoTable = new JTable(infoTableModel);
		infoTable.setDragEnabled(false);
        
		
		JScrollPane tableScroller = new JScrollPane(infoTable);
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.add(new JLabel("Configuration data"), BorderLayout.PAGE_START);
		infoPanel.add(tableScroller, BorderLayout.CENTER);
		
		//----------------------------------------
		//	Pannello per PLUINGS
		//----------------------------------------
        
		pluginTableModel = new DefaultTableModel();
		pluginTableModel.addColumn("Name");
		pluginTableModel.addColumn("Value");		
		pluginTable = new JTable(pluginTableModel);
		pluginTable.setDragEnabled(false);
		
		JScrollPane pluginTableScroller = new JScrollPane(pluginTable);
		JPanel pluginPanel = new JPanel(new BorderLayout());
		pluginPanel.add(new JLabel("Plugin data"), BorderLayout.PAGE_START);
		pluginPanel.add(pluginTableScroller, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,infoPanel, pluginPanel);
		splitPane.setResizeWeight(0.5);

		
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		this.setPreferredSize(new Dimension(Math.round(app.status.mokaDimesion.width/3),app.status.mokaDimesion.height-200));
		this.setSize(new Dimension(Math.round(app.status.mokaDimesion.width/3),app.status.mokaDimesion.height-200));
		
		
	}
	

	public void actionPerformed(ActionEvent e) {
		
		if (app.status.isAppWaiting()) return;
		
		
		
    }
	
	
	
	//---------------------------------
	//	Funzioni 
	//---------------------------------

	public void printConfData() {
		
		if (app.workConf==null)	return;
		
		fillInfo("Name", app.workConf.getName(), true);
		
		for (int i=0; i<AtomData.numDATA; i++) {
			if (!app.workConf.getData().isNull(i)) {
				fillInfo(app.workConf.getData().getName(i).toString(), app.workConf.getData().getData(i).toString(), false);
			}
		}
		
	}
	
	//----------------------------
	//	FILLERS TABELLE
	//----------------------------
	
	public void fillInfo(String text, String value, boolean clean) {
		
		if (clean){
			infoTableModel.setRowCount(0);
			infoTable.editingCanceled(new ChangeEvent(this));
		}
		infoTableModel.addRow(new String[] { text, value});
		
	}
	
	public void fillInfo(String text, double value, boolean clean) {
		
		fillInfo(text, String.format(Costants.decimalPlace4,value), clean);
				
	}
	
	public void fillPlugin(String text, String value, boolean clean) {
		
		if (clean){ pluginTableModel.setRowCount(0); pluginTable.editingCanceled(new ChangeEvent(this));}
		
		pluginTableModel.addRow(new String[] { text, value});
		
	}
	
	public void fillPlugin(String text, double value, boolean clean) {
		
		fillPlugin(text, String.format(Costants.decimalPlace4,value), clean);
				
	}
	
	//---------------------------------
	//	FUNZIONE DEL MODULO 
	//---------------------------------

	public void reset() {
				
		printConfData();
	}
	
	public void confChanged() {
				
		printConfData();
	}
	
	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	} 
	
	class ClosingWindowAdapter extends WindowAdapter {
		 public void windowClosing(WindowEvent e) {
		  setVisible(false);
		  System.exit(0);
		 }
		} 
	
	protected JButton makeNavigationButton(String imageName,
			String actionCommand,
			String toolTipText,
			String altText) {
//		Look for the image.
		String imgLocation = "images/"
			+ imageName;
		
		URLClassLoader urlLoader = (URLClassLoader)this.getClass().getClassLoader();
		URL imageURL = urlLoader.findResource(imgLocation);
		
//		Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
//		button.addActionListener(this);

		if (imageURL != null) {                      //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {                                     //no image found
			button.setText(altText);
			System.err.println("Resource not found: "
					+ imgLocation);
		}

		return button;
	}
	
}

