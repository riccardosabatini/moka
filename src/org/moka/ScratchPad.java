package org.moka;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import mathParser.EvaluationTree;
import mathParser.MathParser;
import mathParser.MathParserException;
import mathParser.Variable;

import org.moka.common.Costants;

import org.moka.qeReader.qeParser;
import org.moka.structures.Configuration;
import org.moka.structures.Module;
import org.moka.tools.gui.GUItools;


/**
 * @author riki
 *
 */
public class ScratchPad extends JFrame implements ActionListener, Module {

	public Moka app;
	public JTextField mathInput;
	JButton mathCalc;
	public JTextArea scratchArea, notesArea, mathArea;
	//boolean appWorking = false;
	
	JButton readCellButton,readAtomsButton, readFileButton;
	
	
	//------------------------
	//	INTERFACCIA
	//------------------------
	
	public ScratchPad(Moka _app) {
		
		this.app = _app;

		//---Parser
		
		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		
		//-----------------------
		//	SCRATCH PANEL
		//-----------------------
		Box scratchPanel = Box.createVerticalBox();
		
		scratchArea = new JTextArea(40, 20);
		scratchArea.setText("");
        JScrollPane scrollPane = new JScrollPane(scratchArea);
        
        Box buttonPanel = Box.createHorizontalBox();
        readFileButton= new JButton("Read File OUT");
        readFileButton.setActionCommand("readFileButton");
        readFileButton.addActionListener(this);
        buttonPanel.add(readFileButton);
        
        
        readCellButton= new JButton("Read Cell");
        readCellButton.setActionCommand("readCellButton");
        readCellButton.addActionListener(this);
        buttonPanel.add(readCellButton);
        
        readAtomsButton= new JButton("Read Atom");
        readAtomsButton.setActionCommand("readAtomsButton");
        readAtomsButton.addActionListener(this);
        buttonPanel.add(readAtomsButton);
        
        scratchPanel.add(scrollPane);
        scratchPanel.add(buttonPanel);
        
        scratchPanel.setMaximumSize(new Dimension(350,500));
        scratchPanel.setPreferredSize(new Dimension(350,500));
        
        
        tabbedPane.addTab("Scratch", null, scratchPanel,"");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		//-----------------------
		//	NOTES PANEL
		//-----------------------
        Box notesPanel = Box.createVerticalBox();
        
        notesArea = new JTextArea(40, 20);
        notesArea.setText("");
        notesArea.getDocument().addDocumentListener(new notesChangeListener());
        JScrollPane scrollPane2 = new JScrollPane(notesArea);
        
        notesPanel.add(scrollPane2);
        notesPanel.setMaximumSize(new Dimension(350,500));
        notesPanel.setPreferredSize(new Dimension(350,500));

        tabbedPane.addTab("Notes", null, notesPanel,"");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		//-----------------------
		//	MATH PANEL
		//-----------------------
        Box mathPanel = Box.createVerticalBox();
        
        Box mathInputPanel = Box.createHorizontalBox();
        mathInputPanel.add(new JLabel("y = "));
        mathInput = new JTextField("");
        mathInputPanel.add(mathInput);
        
        mathArea = new JTextArea(40, 20);
        mathArea.setText("");
        JScrollPane scrollPane3 = new JScrollPane(mathArea);
        
        Box mathButtonPanel = Box.createHorizontalBox();
        mathButtonPanel.add(Box.createHorizontalGlue());
        mathCalc = new JButton("Evaluate");
        mathCalc.setActionCommand("mathCalc");
        mathCalc.addActionListener(this);
        mathButtonPanel.add(mathCalc);
        mathButtonPanel.add(Box.createHorizontalGlue());
        
        mathPanel.add(mathInputPanel);
        mathPanel.add(scrollPane3);
        mathPanel.add(mathButtonPanel);
        mathPanel.setMaximumSize(new Dimension(350,500));
        mathPanel.setPreferredSize(new Dimension(350,500));

        tabbedPane.addTab("Math", null, mathPanel,"");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);

        
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		this.setSize(550,450);

	}
	
	//------------------------
	//	Azioni
	//------------------------
	
	public void actionPerformed(ActionEvent e) {
		
		
		if (e.getSource() == readCellButton) {
        	
			try {
				
            		app.workConf.setCell(qeParser.readOutTextCELL(scratchArea.getText().split("\\n+")));

            	} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	       
        } 
		
		if (e.getSource() == readAtomsButton) {
	
			
			try {
				
				Configuration atomsTemp = qeParser.readOutTextATOMS(scratchArea.getText().split("\\n+"));
				
				for (int i = 0; i< atomsTemp.getNAtoms(); i++) {
					double[] pos = app.workConf.getCell().convertVector(atomsTemp.getCoordType(), app.workConf.getCoordType(), atomsTemp.getAtomPos(i));
					app.addAtom(atomsTemp.getAtomName(i),pos);
				}
				app.reset();
				
				
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	       
		}
		
		if (e.getSource() == mathCalc) {
		
			mathArea.append("\n ---------------\n");
			mathArea.append("y = "+mathInput.getText()+"\n");
			
			try {
				
				mathArea.append("Eval: "+app.math.evaluateEq(mathInput.getText()));
			} catch (MathParserException e1) {
				// TODO Auto-generated catch block
				mathArea.append("ERRORE : "+e1.message);
			}
			
		}
	}

	class notesChangeListener implements DocumentListener {
	 
		public void insertUpdate(DocumentEvent e) {
	        if (!app.status.isWorking()) updateNotes();
	    }
	    public void removeUpdate(DocumentEvent e) {
	    	if (!app.status.isWorking()) updateNotes();
	    }
	    public void changedUpdate(DocumentEvent e) {
	        //Plain text components do not fire these events
	    }

	    public void updateNotes() {
	        
	    	String newNote = notesArea.getText();
	    	app.workConf.setNotes(newNote);
	    }
	}
	

	
	//------------------------
	//	Funzioni di output
	//------------------------
	
	public void printStructure (String type) {
		scratchArea.setText("");
		
		if (type.toLowerCase().equals("qe")) {
		
			if (app.workConf.getCell()!=null) app.io.writeSystem(scratchArea);
			scratchArea.append(Costants.newline);
			
			if (app.workConf.getAtoms()!=null) app.io.writeAtoms(scratchArea, false);
			scratchArea.append(Costants.newline);
			
			if (app.workConf.getKpoints()!=null) app.io.writeKpoints(scratchArea);
			
		}
		
		if (type.toLowerCase().equals("xyz")) {
			
			app.io.writeXYZ(scratchArea, false);
			
		}
		
	}
	
	public void printSelected () {
		scratchArea.setText("");
		
		app.io.writeSystem(scratchArea);
		scratchArea.append(Costants.newline);
		app.io.writeAtoms(scratchArea, true);
		
	}
	
	
	public void append (String _in) {
		
		scratchArea.append(_in);
		
		
	}
	
	public void clean() {
		
		scratchArea.setText("");
		
		
	}
	
	//------------------------
	//	Funzione di flow
	//------------------------
	
	public void confChanged() {

		app.status.setWorkingOn(); //
		notesArea.setText(app.workConf.getNotes());
		
		app.status.setWorkingOff(); //
	}

	public void reset() {

		app.status.setWorkingOn(); //
		notesArea.setText(app.workConf.getNotes());
		
		app.status.setWorkingOff(); //
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

}
