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
public class SettingsDialog extends JDialog
{
    private JSlider _textSpeedSlider;
    private JButton _saveButton;
    private JButton _cancelButton;
    
    public SettingsDialog(JFrame parentFrame)
    {
        super(parentFrame, "Settings", true);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600,400);
        setLocationRelativeTo(parentFrame);
        
        // text speed slider
        JLabel textSpeedLabel = new JLabel("Text Speed");
        _textSpeedSlider = new JSlider(10,100,50);
        _textSpeedSlider.setMajorTickSpacing(90);
        _textSpeedSlider.setPaintTicks(true);
        _textSpeedSlider.setPaintLabels(true);

        JPanel textSpeedPanel = new JPanel();
        textSpeedPanel.setLayout(new FlowLayout());
        textSpeedPanel.setPreferredSize(new Dimension(400, 50)); //Height keep yung width 1280
        textSpeedPanel.setMaximumSize(new Dimension(400, 50));



        textSpeedPanel.add(textSpeedLabel);
        textSpeedPanel.add(_textSpeedSlider);

        _saveButton = new JButton("Save");
        _cancelButton = new JButton("Cancel");

        textSpeedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        _saveButton.addActionListener(e ->
        {
            dispose();
            System.out.println("Test");
            var saveManager = new SaveManager();
            saveManager.SaveGame("1");
        });
        
        _cancelButton.addActionListener(e ->
        {
            dispose();
        });

        add(Box.createVerticalStrut(100));
        add(textSpeedPanel);
        add(Box.createVerticalStrut(100));
        add(_saveButton);
        add(Box.createVerticalStrut(10));
        add(_cancelButton);
    }
}
