package org.moka;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.moka.structures.Module;

import org.moka.common.Costants;


/**
 * @author riki
 *
 */
public class Console extends JFrame implements ActionListener, Module {

	public Moka app;
	public JTextArea textArea;
	private JFileChooser fc;
    

	JTextField mainText;
	JButton sendB, readSelB, setB;
	
	//------------------------
	//	INTERFACCIA
	//------------------------
	
	public Console(Moka _app) {
		
		JPanel framePanel = new JPanel();
		setContentPane(framePanel);
		getContentPane().setLayout(new BorderLayout());
		
		this.app = _app;
		
        Box mainPanel = Box.createHorizontalBox();
        
        textArea = new JTextArea(40, 15);
        textArea.setText("");
        JScrollPane scrollPane2 = new JScrollPane(textArea);
        
        mainPanel.add(scrollPane2);
        mainPanel.setMaximumSize(new Dimension(350,400));
        mainPanel.setPreferredSize(new Dimension(350,400));

		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		Box buttonPanel = Box.createHorizontalBox();
        
        readSelB= new JButton("Get selction");
        readSelB.setActionCommand("readSelB");
        readSelB.addActionListener(this);
        buttonPanel.add(readSelB);
       
        sendB= new JButton("Send");
        sendB.setActionCommand("sendB");
        sendB.addActionListener(this);
        buttonPanel.add(sendB);
        
        setB= new JButton("Set");
        setB.setActionCommand("setB");
        setB.addActionListener(this);
        buttonPanel.add(setB);
        
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
       
		
		setSize(550,250);

	}
	
	//------------------------
	//	Azioni
	//------------------------
	
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == readSelB) {
			textArea.setText(textArea.getText()+Costants.newline+app.viewer.getExecutionCommand("selectedAtomsList"));
		}
		
		if (e.getSource() == sendB) {
			app.viewer.execCommand(textArea.getText());
		}
		
		if (e.getSource() == setB) {
			app.workConf.setDrawSpecs(textArea.getText());
		}
		
	}
	
	class ClosingWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			System.exit(0);
		}
	}

	/* PER MODULI
	 *
	 */
	public void confChanged() {
		// TODO Auto-generated method stub
		textArea.setText(app.workConf.getDrawSpecs());
		
	}

	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	} 
	
	public void reset() {
		// TODO Auto-generated method stub
		textArea.setText(app.workConf.getDrawSpecs());
	} 

	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	} 
}
