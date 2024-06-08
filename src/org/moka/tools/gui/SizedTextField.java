/**
 * 
 */
package org.moka.tools.gui;

import java.awt.Dimension;

import javax.swing.JTextField;

/**
 * @author riki
 *
 */
public class SizedTextField extends JTextField
{
    Dimension d;
    
    public SizedTextField (int cWidth)
    {
        d = new Dimension (cWidth * 16, 21);
    }
    
    public Dimension getPreferedSize () { return d; }
    public Dimension getMaximumSize () { return d; }
    public Dimension getMinimumSize () { return d; }
}