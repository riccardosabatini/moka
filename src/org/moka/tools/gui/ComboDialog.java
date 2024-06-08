package org.moka.tools.gui;
/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/*
 * ListDialog.java is meant to be used by programs such as
 * ListDialogRunner.  It requires no additional files.
 */

/**
 * Use this modal dialog to let the user choose one string from a long
 * list.  See ListDialogRunner.java for an example of using ListDialog.
 * The basics:
 * <pre>
    String[] choices = {"A", "long", "array", "of", "strings"};
    String selectedName = ListDialog.showDialog(
                                componentInControllingFrame,
                                locatorComponent,
                                "A description of the list:",
                                "Dialog Title",
                                choices,
                                choices[0]);
 * </pre>
 */
public class ComboDialog extends JDialog
                        implements ActionListener {
    private static ComboDialog dialog;
    private static int value = -1;
    private JComboBox list;
    
    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public static int showDialog(Component frameComp,
                                    Component locationComp,
                                    String labelText,
                                    String title,
                                    String[] possibleValues,
                                    int initialValue) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new ComboDialog(frame,
                                locationComp,
                                labelText,
                                title,
                                possibleValues,
                                initialValue);
        dialog.setVisible(true);
        return value;
    }

    private void setValue(int newValue) {
        value = newValue;
        list.setSelectedIndex(value);
    }

    private ComboDialog(Frame frame,
                       Component locationComp,
                       String labelText,
                       String title,
                       String[] data,
                       int initialValue) {
        super(frame, title, true);

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        //
        final JButton setButton = new JButton("Select");
        setButton.setActionCommand("Select");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //main part of the dialog
        list = new JComboBox(data);
        list.setSelectedIndex(initialValue);
        
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(list);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(locationComp);
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Select".equals(e.getActionCommand())) {
        	
            ComboDialog.value = list.getSelectedIndex();
            
        } else if ("Cancel".equals(e.getActionCommand())) {
        	ComboDialog.value = -1;
        }
        
        ComboDialog.dialog.setVisible(false);
    }
}
