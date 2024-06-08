package org.moka.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InputCoordinateTransform extends JDialog
                        implements ActionListener {
    
	private static InputCoordinateTransform dialog;
    private static String[] value;
    
    final JButton setButton, cancelButton;
    final JTextField functionT = new JTextField(30);
    final JComboBox coordC = new JComboBox(new String[] {"x","y","z"});
    
    public static String[] showDialog(Component frameComp,
                                    Component locationComp,
                                    String title) {
    	
        Frame frame = JOptionPane.getFrameForComponent(frameComp);
        
        dialog = new InputCoordinateTransform(frame,
                                locationComp,
                                title);
        dialog.setVisible(true);
        return value;
    }

    private InputCoordinateTransform(Frame frame, Component locationComp, String title) {
    	
        super(frame, title, true);

        JPanel mainPain = new JPanel();
        mainPain.setLayout(new BoxLayout(mainPain, BoxLayout.PAGE_AXIS));
        
        Box centerBox = Box.createHorizontalBox();
        centerBox.add(coordC);
        centerBox.add(Box.createHorizontalGlue());
        centerBox.add(functionT);
        
        mainPain.add(centerBox);
        mainPain.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        //Bottone id Ok
        setButton = new JButton("Ok");
        setButton.setActionCommand("Ok");
        setButton.addActionListener(this);
        getRootPane().setDefaultButton(setButton);

        //Bottone che cancella
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        
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
        contentPane.add(mainPain, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        setValue(new String[] {"x",""});
        pack();
        setLocationRelativeTo(locationComp);
    }
    
    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
    	
        if ("Ok".equals(e.getActionCommand())) {
        	
        	setValue(new String[] {coordC.getSelectedIndex()+"",functionT.getText()});
        	
        }else if ("Cancel".equals(e.getActionCommand())) {
        
        	setValue(new String[] {"-1"});
        	
        }
        InputCoordinateTransform.dialog.setVisible(false);
    }
    
    private void setValue(String[] newValues) {
    	
    	dialog.value=newValues;
        
    }


}

// This method returns the selected radio button in a button group


