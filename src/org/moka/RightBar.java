/**
 * 
 */
package org.moka;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.moka.common.Costants;

import org.moka.structures.Module;
import org.moka.structures.MokaController;
import org.moka.tools.gui.GUItools;

/**
 * @author riki
 *
 */
public class RightBar extends JToolBar implements ChangeListener, ActionListener, Module {

	Moka app;
	JTextField confText;
	//JButton confPrev,confNext;
	JLabel totConfsLabel;
	JTabbedPane mainBarTab;
	
	RightBar(Moka _app) {
		
		this.app = _app;
		
		this.setFloatable(true);
		this.setRollover(true);
    	this.setSize(app.status.rightBarWidht,app.status.mokaDimesion.height-200);
    	
    	Box mainBarPanel = Box.createVerticalBox();
    	
    	mainBarTab = new JTabbedPane();
    	mainBarTab.addTab("View", GUItools.createImageIcon("org/moka/images/Bookmarks16.gif", app), app.viewcontroller,"Viewer Panel");
    	mainBarTab.addTab("Transform", GUItools.createImageIcon("org/moka/images/Bookmarks16.gif",app), app.controller,"Transform Panel");
    	mainBarTab.addTab("Draw", GUItools.createImageIcon("org/moka/images/Bookmarks16.gif", app), app.drawer,"Drawing Panel");
    	
    	mainBarTab.setSelectedIndex(0);
    	mainBarTab.addChangeListener(this);
    	mainBarPanel.add(mainBarTab);
    	//----------------------------------------
		//	Pannello selezione CONF ATOMS
		//----------------------------------------
        
		Box stepPanel = Box.createHorizontalBox();
		
		JButton confPrev = GUItools.makeNavigationButton("org/moka/images/Back16.gif", "confPrev", "Prev conf", "Prev",this);
		confPrev.addActionListener(this);
		//confPrev.setPreferredSize(new Dimension(30,50));
		stepPanel.add(confPrev);
		
		confText = new JTextField("0");
		confText.setActionCommand("confText");
		confText.addActionListener(this);
		stepPanel.add(confText);
		
		totConfsLabel = new JLabel("/ 0");
		stepPanel.add(totConfsLabel);
		
		JButton confNext = GUItools.makeNavigationButton("org/moka/images/Forward16.gif", "confNext", "Next conf", "Next",this);
		confNext.addActionListener(this);
		//confNext.setPreferredSize(new Dimension(30,50));
		stepPanel.add(confNext);
		mainBarPanel.add(GUItools.createPanelForComponent(stepPanel, "Confs", new Dimension(app.status.rightBarWidht,50)));
		
		this.add(mainBarPanel);
				
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		
		
		if (app.status.isWorking()) return;
		
		for (int i=0; i<mainBarTab.getTabCount(); i++) {
			
			if (((MokaController)mainBarTab.getComponentAt(i)).isActive()) {
				
				((MokaController)mainBarTab.getComponentAt(i)).setActive(false);
				//System.out.println("Disattiva "+mainBarTab.getComponentAt(i).toString());
			}
			
		}
		
		((MokaController)mainBarTab.getSelectedComponent()).setActive(true);
		
		
	}

	public void selectBar(int _bar) {
		
		if (_bar==-1 || _bar >= mainBarTab.getTabCount()) return;
		
		if (mainBarTab.getSelectedIndex()!=_bar ) {
			
			app.status.setWorkingOn();
		
			((MokaController)mainBarTab.getSelectedComponent()).setActive(false);
			mainBarTab.setSelectedIndex(_bar);
			((MokaController)mainBarTab.getSelectedComponent()).setActive(true);
			
			app.status.setWorkingOff();
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		
		double temp = app.thisConf;

        String buttClicked = e.getActionCommand(); // getClassName(e.getSource().getClass());

		if (buttClicked.equals("confText")) {
			
			int thisStep = Integer.valueOf(confText.getText());
			 
			app.loadConf((thisStep - 1 ));
			
			if (temp!=app.thisConf) {
				app.confChanged();
			} else {
				reset();
			}
		}
		
		if (buttClicked.equals("confPrev")) {

			int thisStep = Integer.valueOf(confText.getText());
			
			app.loadConf((thisStep - 1 ) - 1);
			
			if (temp!=app.thisConf) {
				app.confChanged();
			} else {
				reset();
			}
			
		}

		if (buttClicked.equals("confNext")) {

			int thisStep = Integer.valueOf(confText.getText());
			
			app.loadConf((thisStep -1) + 1);
			
			if (temp!=app.thisConf) {
				app.confChanged();
			} else {
				reset();
			}			
	
		}
		
	}
	
	public void reset(){
		
		//Num conf
		totConfsLabel.setText("/ "+app.confDB.size());
		confText.setText(""+(app.thisConf+1));
		
	}

	public void confChanged() {
		
		//Num conf
		totConfsLabel.setText("/ "+app.confDB.size());
		confText.setText(""+(app.thisConf+1));
		
		
	}

	/* (non-Javadoc)
	 * @see structures.Module#atomsChanged(int[])
	 */
	public void atomsChanged(int[] listChanged) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see structures.Module#reciveMouseSelection(int)
	 */
	public void reciveMouseSelection(int site) {
		// TODO Auto-generated method stub
		
	}
}
