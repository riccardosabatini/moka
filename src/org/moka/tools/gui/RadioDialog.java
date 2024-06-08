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
import java.util.Enumeration;

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
public class RadioDialog extends JDialog
                        implements ActionListener {
    private static RadioDialog dialog;
    private static int value;
    
    final JButton setButton;
    JButton cancelButton;
    ButtonGroup radioGroup;
    JRadioButton radios[];
    
    public static int showDialog(Component frameComp,
                                    Component locationComp,
                                    String labelText,
                                    String title,
                                    String[] possibleValues,
                                    int initialValue) {
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        dialog = new RadioDialog(frame,
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
        radios[value].setSelected(true);
    }

    private RadioDialog(Frame frame,
                       Component locationComp,
                       String labelText,
                       String title,
                       String[] data,
                       int initialValue) {
        super(frame, title, true);

        JPanel radioPane = new JPanel();
        radioPane.setLayout(new BoxLayout(radioPane, BoxLayout.PAGE_AXIS));
        
        //Titolo
        JLabel label = new JLabel(labelText);
        radioPane.add(label);
        radioPane.add(Box.createRigidArea(new Dimension(0,5)));
        
        //Create the radio buttons.
        radioGroup = new ButtonGroup();
        
        radios = new JRadioButton[data.length];
        for (int i=0; i<data.length; i++) {
        	radios[i] = new JRadioButton(data[i]);
        	radios[i].addMouseListener(new dbSetMouseRadio(this));
        	radioGroup.add(radios[i]);
        	radioPane.add(radios[i]);
        }
        
        radioPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        
        //Bottone che cancella
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        //Bottone id Ok
        setButton = new JButton("Ok");
        setButton.setActionCommand("Ok");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        
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
        contentPane.add(radioPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        setValue(initialValue);
        pack();
        setLocationRelativeTo(locationComp);
    }

    public static int getSelection(ButtonGroup group) {
    	int i=-1;
        for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            i++;
            if (b.getModel() == group.getSelection()) {
                return i;
            }
        }
        return i;
    }
    
    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Ok".equals(e.getActionCommand())) {
            RadioDialog.value = getSelection(radioGroup);
        } else if ("Cancel".equals(e.getActionCommand())) {
        
        	RadioDialog.value = -1;
        	
        }
        RadioDialog.dialog.setVisible(false);
    }
}

class dbSetMouseRadio extends MouseAdapter {
	
	RadioDialog dialog;
	dbSetMouseRadio(RadioDialog _dialog) {
		this.dialog = _dialog;
	}
	public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            dialog.setButton.doClick(); //emulate button click
        }
	}
}
    
// This method returns the selected radio button in a button group


