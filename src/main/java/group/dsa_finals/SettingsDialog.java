/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.dsa_finals;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author Akutan
 */
public class SettingsDialog extends JDialog {
    
    private JSlider _textSpeedSlider;
    private JButton _saveButton;
    private JButton _cancelButton;
    
    public SettingsDialog(JFrame parentFrame){
        super(parentFrame, "Settings", true);
        
        setLayout(new FlowLayout());
        setSize(600,400);
        setLocationRelativeTo(parentFrame);
        
        // text speed slider
        JLabel textSpeedLabel = new JLabel("Text Speed");
        _textSpeedSlider = new JSlider(0,100,50);
        _textSpeedSlider.setMajorTickSpacing(20);
        _textSpeedSlider.setPaintTicks(true);
        _textSpeedSlider.setPaintLabels(true);
        
        _saveButton = new JButton("Save");
        _cancelButton = new JButton("Cancel");
        
        _saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        _cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        add(textSpeedLabel);
        add(_textSpeedSlider);
        add(_saveButton);
        add(_cancelButton);
    }
}
