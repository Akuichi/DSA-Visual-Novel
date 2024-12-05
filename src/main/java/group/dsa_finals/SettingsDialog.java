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
    private int _currentScene;
    private SceneManager _sceneManager;
    private JSlider _textSpeedSlider;
    private JButton _saveButton;
    private JButton _cancelButton;
    private JComboBox<String> _windowTypeComboBox;
    
    public SettingsDialog(JFrame parentFrame, SceneManager sceneManager)
    {
        super(parentFrame, "Settings", true);
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
        add(Box.createVerticalStrut(50));
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
        var fileManager = new FileManager(this, _sceneManager);
        int textSpeed = 200 - fileManager.GetSettings().textSpeed;
        int windowType = fileManager.GetSettings().windowType;
        _sceneManager.textSpeed = textSpeed;
        _textSpeedSlider.setValue(textSpeed);
        _windowTypeComboBox.setSelectedIndex(windowType);
    }

    public void SaveSettings()
    {
        int textSpeed = 200 - _textSpeedSlider.getValue();
        _sceneManager.textSpeed = textSpeed;
        int windowType = _windowTypeComboBox.getSelectedIndex();
        var fileManager = new FileManager(this, _sceneManager);
        fileManager.SaveSettings(textSpeed, windowType);
        _sceneManager.SetWindowType(windowType);

    }


}
