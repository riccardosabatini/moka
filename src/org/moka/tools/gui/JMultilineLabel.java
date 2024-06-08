/*
	This file is part of the JKTest
    Copyright (C) 2008 Royce Y. Feng <ryfeng@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.moka.tools.gui;

import javax.swing.JComponent;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;

/**
 * 
 * The Multilined label used for most of the test panels. Credits for this go 
 * entirely to PaulFMendler. Thanks.
 * 
 * @author PaulFMendler
 */

public class JMultilineLabel extends JComponent {
    private String text;
    private Insets margin = new Insets(5,5,5,5);
    private int maxWidth = Integer.MAX_VALUE;
    private boolean justify;
    private final FontRenderContext frc = new FontRenderContext(null, false, false);
 
    private void morph() {
        revalidate();
        repaint();
    }
 
    public String getText() {
        return text;
    }
 
    public void setText(String text) {
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, this.text);
        if ((old == null) ? text!=null : !old.equals(text))
            morph();
    }
 
    public int getMaxWidth() {
        return maxWidth;
    }
 
    public void setMaxWidth(int maxWidth) {
        if (maxWidth <= 0)
            throw new IllegalArgumentException();
        int old = this.maxWidth;
        this.maxWidth = maxWidth;
        firePropertyChange("maxWidth", old, this.maxWidth);
        if (old !=  this.maxWidth)
            morph();
    }
 
    public boolean isJustified() {
        return justify;
    }
 
    public void setJustified(boolean justify) {
        boolean old = this.justify;
        this.justify = justify;
        firePropertyChange("justified", old, this.justify);
        if (old != this.justify)
            repaint();
    }
 
    public Dimension getPreferredSize() {
        return paintOrGetSize(null, getMaxWidth());
    }
 
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintOrGetSize((Graphics2D)g, getWidth());
    }
 
    private Dimension paintOrGetSize(Graphics2D g, int width) {
        Insets insets = getInsets();
        width -= insets.left + insets.right + margin.left + margin.right;
        float w = insets.left + insets.right + margin.left + margin.right;
        float x = insets.left + margin.left, y=insets.top + margin.top;
        if (width > 0 && text != null && text.length() > 0) {
            AttributedString as = new AttributedString(getText());
            as.addAttribute(TextAttribute.FONT, getFont());
            AttributedCharacterIterator aci = as.getIterator();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
            float max = 0;
            while (lbm.getPosition() < aci.getEndIndex()) {
                TextLayout textLayout = lbm.nextLayout(width);
                if (g != null && isJustified() && textLayout.getVisibleAdvance() > 0.80 * width)
                    textLayout = textLayout.getJustifiedLayout(width);
                if (g != null)
                    textLayout.draw(g, x, y + textLayout.getAscent());
                y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
                max = Math.max(max, textLayout.getVisibleAdvance());
            }
            w += max;
        }
        return new Dimension((int)Math.ceil(w), (int)Math.ceil(y) + insets.bottom + margin.bottom);
    }
}