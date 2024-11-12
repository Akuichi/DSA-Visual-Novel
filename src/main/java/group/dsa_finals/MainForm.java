/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.dsa_finals;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 *
 * @author Akutan
 */
public class MainForm extends JFrame {

    private JPanel _cards;  // The panel that will hold different "cards" (forms)
    private CardLayout _cardLayout;  // CardLayout to switch between forms
    
    public MainForm() {
        // Set up the main menu window
        setTitle("Main Menu");
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        _cardLayout = new CardLayout();
        _cards = new JPanel(_cardLayout);
        
        
        // Panel to hold the button
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Add a "Start Game" button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // When the "Start Game" button is clicked, open the game window
                SceneManager gameFrame = new SceneManager(0);
                dispose();  // Close the main menu frame
            }
        });

        panel.add(startButton, BorderLayout.CENTER);

        // Add panel to the JFrame
        add(panel);

        setVisible(true);
    }
}
