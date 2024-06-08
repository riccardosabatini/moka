/**
 * 
 */
package org.moka.tools.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;

/**
 * @author riki
 *
 */
public class JMolPanelAdv extends JPanel {
    public JmolViewer viewer;
    public JmolAdapter adapter;
    public JMolPanelAdv() {
      adapter = new SmarterJmolAdapter();
      viewer = JmolViewer.allocateViewer(this, adapter);
    }

    public JmolViewer getViewer() {
      return viewer;
    }

    final Dimension currentSize = new Dimension();
    final Rectangle rectClip = new Rectangle();

    public void paint(Graphics g) {
      getSize(currentSize);
     g.getClipBounds(rectClip);
      viewer.renderScreenImage(g, currentSize, rectClip);
    }
  }