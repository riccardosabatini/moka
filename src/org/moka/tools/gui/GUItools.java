/**
 * 
 */
package org.moka.tools.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author riki
 *
 */
public class GUItools {

	static public String getStrFormUser(String title) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(null, title, "", 1);
            if (str==null) return null;

			if (str.equals("")) JOptionPane.showMessageDialog(null, "Inserire un numero", "a", 1);
		} while (str.equals(""));
		
		return str;
        
	}
	
	static public String getStrFormUser(String title, String content) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(title, content);
            if (str==null) return null;
            
		} while (str.equals(""));
		
		return str;
        
	}
	
	static public int[] getArrayIntFormUser(String title, String content) {
		
		String ans = JOptionPane.showInputDialog(title, content);
		if (ans==null) return null;
		
		String[] nums = ans.trim().subSequence(1, ans.length()-1).toString().split(" ");
		int[] dims = new int[] {Integer.parseInt(nums[0]),Integer.parseInt(nums[1]),Integer.parseInt(nums[2])};
		
		return dims;
        
	}
	
	static public double[] getArrayDoubleFormUser(String title, String content) {
		
		String ans;
		do {
			ans = JOptionPane.showInputDialog(title, content);
            if (ans==null) return null;
            
		} while (ans.equals(""));
		
		String[] nums = ans.trim().subSequence(1, ans.length()-1).toString().split(" ");
		double[] dims = new double[] {Double.parseDouble(nums[0]),Double.parseDouble(nums[1]),Double.parseDouble(nums[2])};
		
		return dims;
        
	}
	
	static public Integer getIntFormUser(String title) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(null, title, "", 1);
			if (str==null) return null;

			if (str.equals("")) JOptionPane.showMessageDialog(null, "Inserire un numero", "a", 1);
			
		} while (str.equals(""));
		
		return Integer.valueOf(str);
        
	}
	
	static public Integer getIntFormUser(String title, int initValue) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(null, title, "", 1);
			if (str == null) return null;
			
			if (str.equals("")) JOptionPane.showMessageDialog(null, "Inserire un numero", "a", initValue);
			
		} while (str.equals(""));
		
		return Integer.valueOf(str);
        
	}
	
	static public boolean getYesNoFromUser(String title) {
		
		
		int ans = JOptionPane.showConfirmDialog(null,title,"Attention",JOptionPane.YES_NO_OPTION);
		
		if (ans==1) 
			return false;
		else 
			return true;
		

	}
	static public Double getDoubleFormUser(String title) {
		
		String str;
		do {
			str = JOptionPane.showInputDialog(null, title, "", 1);
            if (str==null) return null;
            
			if (str.equals("")) JOptionPane.showMessageDialog(null, "Inserire un numero", "a", 1);
		} while (str.equals(""));
		
		return Double.valueOf(str);
        
	}
	
	
	public static JPanel createVerticalBoxPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return p;
	}
	
	public static JPanel createHorizontalBoxPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		return p;
	}

	public static JPanel createPanelForComponent(JComponent comp,String title, Dimension dim) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(comp, BorderLayout.CENTER);
		if (title != null) {
			panel.setBorder(BorderFactory.createTitledBorder(title));
		}
		panel.setPreferredSize(dim);
		panel.setMaximumSize(dim);
		return panel;
	}

    public static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    public static ImageIcon createImageIcon(String path, Class _in) {
        java.net.URL imgURL = _in.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
   
    public static JButton makeNavigationButton(String imgLocation,
			String actionCommand,
			String toolTipText,
			String altText,
			Object _this) {
//		Look for the image.
		
    	URLClassLoader urlLoader = (URLClassLoader)_this.getClass().getClassLoader();
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
 
    public static ImageIcon createImageIcon(String path, Object _this) {
    	
    	URLClassLoader urlLoader = (URLClassLoader)_this.getClass().getClassLoader();
		URL imgURL = urlLoader.findResource(path);
        
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    
}
