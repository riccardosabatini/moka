/**
 * 
 */
package org.moka.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/**
 * @author riki
 *
 */
public class logFrame extends JFrame {
	
	public JTextArea logArea;
	
	public logFrame() {
		 JPanel framePanel = new JPanel();
		 setContentPane(framePanel);
		 getContentPane().setLayout(new BorderLayout());
		 getContentPane().setBackground(new Color(129));
		 
		 logArea = new JTextArea(40, 20);
		 logArea.setText("");
		 JScrollPane scrollPane = new JScrollPane(logArea);
		 JPanel textAreaP = GUItools.createPanelForComponent(scrollPane, "Output", new Dimension(500,400));

		 this.getContentPane().add(textAreaP, BorderLayout.CENTER);
		 
		 setSize(550,450);

    }
	
	class ClosingWindowAdapter extends WindowAdapter {
		 public void windowClosing(WindowEvent e) {
		  setVisible(false);
		  System.exit(0);
		 }
		} 
	
}
