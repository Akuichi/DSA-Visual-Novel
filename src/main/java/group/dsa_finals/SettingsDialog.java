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
public class SettingsDialog extends JDialog
{
    private SceneManager _sceneManager = null;
    private JSlider _textSpeedSlider;
    private JButton _saveButton;
    private JButton _cancelButton;
    private JComboBox<String> _windowTypeComboBox;
    private JComboBox<String> _resolutionComboBox;
    private JFrame _parentFrame;
    
    public SettingsDialog(JFrame parentFrame, SceneManager sceneManager)
    {
        super(parentFrame, "Settings", true);
        _parentFrame = parentFrame;
        if (sceneManager != null)
            _sceneManager = sceneManager;
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setSize(600,400);
        setLocationRelativeTo(parentFrame);

        DrawGUIComponents();
        LoadSettings();

    }

    private void DrawGUIComponents()
    {


        // text speed slider
        JLabel textSpeedLabel = new JLabel("Text Speed");
        _textSpeedSlider = new JSlider(0,200,50);
        //_textSpeedSlider.setMajorTickSpacing(90);
        _textSpeedSlider.setPaintTicks(true);
        _textSpeedSlider.setPaintLabels(true);

        JPanel textSpeedPanel = new JPanel();
        textSpeedPanel.setLayout(new FlowLayout());
        textSpeedPanel.setPreferredSize(new Dimension(400, 50)); //Height keep yung width 1280
        textSpeedPanel.setMaximumSize(new Dimension(400, 50));



        textSpeedPanel.add(textSpeedLabel);
        textSpeedPanel.add(_textSpeedSlider);


        JLabel windowTypeLabel = new JLabel("Window Type");
        _windowTypeComboBox = new JComboBox<>(new String[] { "Fullscreen", "Windowed" });
        _windowTypeComboBox.setPreferredSize(new Dimension(150, 30));
        _windowTypeComboBox.setMaximumSize(new Dimension(150, 30));
        _windowTypeComboBox.setEditable(false);

        JPanel windowTypePanel = new JPanel();
        windowTypePanel.setLayout(new FlowLayout());
        windowTypePanel.setPreferredSize(new Dimension(400, 50));
        windowTypePanel.setMaximumSize(new Dimension(400, 50));
        windowTypePanel.add(windowTypeLabel);
        windowTypePanel.add(_windowTypeComboBox);

        JLabel resolutionLabel = new JLabel("Display Resolution");
        _resolutionComboBox = new JComboBox<>(new String[] { "1920x1080", "1600x900", "1366x768", "1280x720" });
        _resolutionComboBox.setPreferredSize(new Dimension(150, 30));
        _resolutionComboBox.setMaximumSize(new Dimension(150, 30));
        _resolutionComboBox.setEditable(false);

        JPanel resolutionPanel = new JPanel();
        resolutionPanel.setLayout(new FlowLayout());
        resolutionPanel.setPreferredSize(new Dimension(400, 50));
        resolutionPanel.setMaximumSize(new Dimension(400, 50));
        resolutionPanel.add(resolutionLabel);
        resolutionPanel.add(_resolutionComboBox);




        _saveButton = new JButton("Save");
        _cancelButton = new JButton("Cancel");

        textSpeedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Save settings
        _saveButton.addActionListener(e -> SaveSettings());

        _cancelButton.addActionListener(e ->
        {
            dispose();
        });

        add(Box.createVerticalStrut(50));
        add(textSpeedPanel);
        add(Box.createVerticalStrut(20));
        add(windowTypePanel);
        add(Box.createVerticalStrut(20));
        add(resolutionPanel);
        add(Box.createVerticalStrut(40));
        add(_saveButton);
        add(Box.createVerticalStrut(10));
        add(_cancelButton);



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
                    CloseSettingsMenu();
                }
            }
        });
    }

    private void CloseSettingsMenu()
    {
        dispose();
        PauseMenuDialog pauseMenuDialog = new PauseMenuDialog(_sceneManager.frame,_sceneManager);
        pauseMenuDialog.setVisible(true);
    }

    private void LoadSettings()
    {
        var fileManager = new FileManager(this, _sceneManager, _parentFrame);
        int textSpeed = 200 - fileManager.GetSettings().textSpeed;
        int windowType = fileManager.GetSettings().windowType;
        int x = fileManager.GetSettings().resolutionX;
        int y = fileManager.GetSettings().resolutionY;
        _textSpeedSlider.setValue(textSpeed);
        _windowTypeComboBox.setSelectedIndex(windowType);
        String curResolution = x+"x"+y;
        boolean itemExists = false;

        for (int i = 0; i < _resolutionComboBox.getItemCount(); i++)
        {
            if (_resolutionComboBox.getItemAt(i).equals(curResolution))
            {
                itemExists = true;
            }
        }
        if (!itemExists)
        {
            _resolutionComboBox.addItem(curResolution); // Add item only if it doesn't exist
        }
        _resolutionComboBox.setSelectedItem(curResolution);
        if (_sceneManager != null)
            _sceneManager.textSpeed = textSpeed;
    }

    public void SaveSettings()
    {
        int textSpeed = 200 - _textSpeedSlider.getValue();

        int windowType = _windowTypeComboBox.getSelectedIndex();
        var fileManager = new FileManager(this, _sceneManager, _parentFrame);

        String resolution = (String) _resolutionComboBox.getSelectedItem();

        String[] parts = resolution.split("x"); //split sa x
        int width = Integer.parseInt(parts[0]);
        int height = Integer.parseInt(parts[1]);
        fileManager.SaveSettings(textSpeed, windowType, width, height);

        if (_sceneManager == null)
        {
            _parentFrame.setVisible(false);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            if (windowType == 0)
            {
                _parentFrame.dispose();
                _parentFrame.setUndecorated(true);  //Remove the title bar and borders
                _parentFrame.setResizable(false);

                //Set the window size to screen's resolution
                int screenWidth = gs.getDefaultConfiguration().getBounds().width;
                int screenHeight = gs.getDefaultConfiguration().getBounds().height;
                _parentFrame.setSize(screenWidth, screenHeight);

                _parentFrame.setLocation(0, 0);
                _parentFrame.setVisible(true);
            }
            else if (windowType == 1)
            {
                _parentFrame.dispose();
                gs.setFullScreenWindow(null);
                _parentFrame.setUndecorated(false);
                _parentFrame.setResizable(true);
                int resolutionX = fileManager.GetSettings().resolutionX;
                int resolutionY = fileManager.GetSettings().resolutionY;
                _parentFrame.setSize(resolutionX, resolutionY);
                _parentFrame.setLocationRelativeTo(null);
                _parentFrame.setVisible(true);
            }
            return;
        }
        _sceneManager.SetWindowType(windowType);
        _sceneManager.textSpeed = textSpeed;

    }


}
