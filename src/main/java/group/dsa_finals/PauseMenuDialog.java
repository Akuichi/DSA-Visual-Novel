/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.dsa_finals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Akutan
 */
public class PauseMenuDialog extends JDialog
{
    private JButton _resumeButton, _settingsButton, _saveButton, _loadButton, _mainMenuButton, _quitButton;
    private SceneManager _sceneManager;
    public PauseMenuDialog(JFrame parentFrame, SceneManager sceneManager)
    {
        super(parentFrame, "Game Paused", true); //parent frame, window title, true = meaning modal siya, so parang naka lock yung main window unless na exit tong dialog
        _sceneManager = sceneManager;
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600, 400);
        setLocationRelativeTo(parentFrame);

        //Add buttons
        _settingsButton = new JButton("Settings");
        _resumeButton = new JButton("Resume");
        _saveButton = new JButton("Save Game");
        _loadButton = new JButton("Load Game");
        _mainMenuButton = new JButton("Main Menu");
        _quitButton = new JButton("Quit Game");

        _resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);




        //Button listeners
        //Resume button
        _resumeButton.addActionListener(e -> dispose());


        //Settings button
        _settingsButton.addActionListener(e ->
        {
            var settingsDialog = new SettingsDialog(parentFrame, _sceneManager);
            dispose();
            settingsDialog.setVisible(true);

        });

        //Save button (save game)
        _saveButton.addActionListener(e ->
        {
            var fileManager = new FileManager(this, _sceneManager, parentFrame);
            fileManager.SaveGame();
        });

        //Load button
        _loadButton.addActionListener(e ->
        {
            var fileManager = new FileManager(this, _sceneManager, parentFrame);
            fileManager.LoadGame();
        });

        //Main Menu
        _mainMenuButton.addActionListener(e ->
        {
            int response = JOptionPane.showConfirmDialog( //confirmation dialog
                    _sceneManager.frame,
                    "Unsaved progress will be lost, return to main menu?",
                    "Quit to Main Menu",
                    JOptionPane.YES_NO_OPTION
            );
            if (response != JOptionPane.YES_OPTION) return;
            _sceneManager.frame.dispose();
            new MainMenu();
            dispose();
        });

        //Quit
        _quitButton.addActionListener(e ->
        {
            int response = JOptionPane.showConfirmDialog( //confirmation dialog
                    _sceneManager.frame,
                    "Unsaved progress will be lost, are you sure you want to quit?",
                    "Quit to Desktop",
                    JOptionPane.YES_NO_OPTION
            );
            if (response != JOptionPane.YES_OPTION) return;
            System.exit(0);
        });





        add(Box.createVerticalStrut(100));
        add(_resumeButton);
        add(Box.createVerticalStrut(10));
        add(_saveButton);
        add(Box.createVerticalStrut(10));
        add(_loadButton);
        add(Box.createVerticalStrut(10));
        add(_settingsButton);
        add(Box.createVerticalStrut(10));
        add(_mainMenuButton);
        add(Box.createVerticalStrut(10));
        add(_quitButton);



        this.setFocusable(true);
        this.requestFocusInWindow();


        //New key listener for ESC button para sa pause menu
        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                //Check if ESC yung button pressed
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    dispose();
                }
            }
        });




    }
    
}
