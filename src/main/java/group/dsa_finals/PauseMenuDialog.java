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
public class PauseMenuDialog extends JDialog {
    private JButton _resumeButton, _settingsButton, _saveButton, _loadButton;
    
    public PauseMenuDialog(JFrame parentFrame){
        super(parentFrame, "Game Paused", true); //parent frame, window title, true = meaning modal siya, so parang naka lock yung main window unless na exit tong dialog
        
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600, 400);
        setLocationRelativeTo(parentFrame);
        
        _settingsButton = new JButton("Settings");
        _resumeButton = new JButton("Resume");
        _saveButton = new JButton("Save Game");
        _loadButton = new JButton("Load Game");
        
        _settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);



        //Resume button
        _resumeButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });


        //Settings button
        _settingsButton.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                var settingsDialog = new SettingsDialog(parentFrame);
                dispose();
                settingsDialog.setVisible(true);
                
            }
        });

        //Save button
        _saveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

            }
        });

        //Load button
        _loadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

            }
        });
        
        add(Box.createVerticalStrut(100));
        add(_saveButton);
        add(Box.createVerticalStrut(10));
        add(_loadButton);
        add(Box.createVerticalStrut(10));
        add(_settingsButton);
        add(Box.createVerticalStrut(10));
        add(_resumeButton);
    }
    
}
