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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        _backgroundImage = new ImageIcon("images/mainmenu.png").getImage();

        add(new MenuPanel());
        SetWindowType();
        // Make the frame visible
        setVisible(true);

    }

    public void SetWindowType()
    {
        var fileManager = new FileManager(null, null, this);
        int windowType = fileManager.GetSettings().windowType;
        setVisible(false);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        if (windowType == 0)
        {
            dispose();
            setUndecorated(true);  //Remove the title bar and borders
            setResizable(false);

            //Set the window size to screen's resolution
            int screenWidth = gs.getDefaultConfiguration().getBounds().width;
            int screenHeight = gs.getDefaultConfiguration().getBounds().height;
            setSize(screenWidth, screenHeight);

            setLocation(0, 0);
            setVisible(true);
        }
        else if (windowType == 1)
        {
            dispose();
            gs.setFullScreenWindow(null);
            setUndecorated(false);
            setResizable(true);
            int resolutionX = fileManager.GetSettings().resolutionX;
            int resolutionY = fileManager.GetSettings().resolutionY;
            setSize(resolutionX, resolutionY);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    private void NewGame()
    {
        new SceneManager(0,0);
        dispose();
    }

    private void LoadGame()
    {
        dispose();
        var fileManager = new FileManager(null,null, this);
        fileManager.LoadGame();

    }

    private void ExitGame()
    {
        System.exit(0);
    }

    private void Settings()
    {
        var settingsDialog = new SettingsDialog(this, null);
        settingsDialog.setVisible(true);
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
            JButton settingsButton = new JButton("Settings");
            JButton exitButton = new JButton("Exit");


            //action listeners for the buttons
            newGameButton.addActionListener(e -> NewGame());

            loadGameButton.addActionListener(e -> LoadGame());

            exitButton.addActionListener(e -> ExitGame());
            settingsButton.addActionListener(e -> Settings());
            //----


            //Add buttons to the panel
            buttonPanel.add(newGameButton);
            buttonPanel.add(loadGameButton);
            buttonPanel.add(settingsButton);
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
