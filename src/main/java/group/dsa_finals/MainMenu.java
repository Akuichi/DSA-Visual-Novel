package group.dsa_finals;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame
{
    private Image _backgroundImage;

    public MainMenu()
    {
        DrawGUIComponents();
    }

    private void DrawGUIComponents()
    {
        setTitle("Main Menu");
        setSize(1280,720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        _backgroundImage = new ImageIcon("images/mainmenu.png").getImage();

        add(new MenuPanel());

        // Make the frame visible
        setVisible(true);

    }

    private void NewGame()
    {
        new SceneManager(0);
        dispose();
    }

    private void LoadGame()
    {
        var fileManager = new FileManager(null,null);
        fileManager.LoadGame();
        dispose();
    }

    private void ExitGame()
    {
        System.exit(0);
    }

    private class MenuPanel extends JPanel
    {

        public MenuPanel()
        {
            setOpaque(false); //Set the panel to be transparent para kita background
            setLayout(new BorderLayout());

            //Create button panel at the bottom
            JPanel buttonPanel = new JPanel();
            buttonPanel.setOpaque(false);

            JButton newGameButton = new JButton("New Game");
            JButton loadGameButton = new JButton("Load Game");
            JButton exitButton = new JButton("Exit");


            //action listeners for the buttons
            newGameButton.addActionListener(e -> NewGame());

            loadGameButton.addActionListener(e -> LoadGame());

            exitButton.addActionListener(e -> ExitGame());
            //----


            //Add buttons to the panel
            buttonPanel.add(newGameButton);
            buttonPanel.add(loadGameButton);
            buttonPanel.add(exitButton);

            //Add button panel to the main panel (centered bottom)
            add(buttonPanel, BorderLayout.SOUTH);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            //Draw the background image
            if (_backgroundImage != null)
            {
                g.drawImage(_backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
