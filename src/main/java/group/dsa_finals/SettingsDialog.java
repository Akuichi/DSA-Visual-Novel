/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.dsa_finals;


import javax.swing.*;
import java.awt.*;

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

        _saveButton = new JButton("Save");
        _cancelButton = new JButton("Cancel");

        textSpeedPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        _cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Save settings
        _saveButton.addActionListener(e -> SaveSettings());

        _cancelButton.addActionListener(e ->
        {
            System.out.println("Test");
            dispose();
        });

        add(Box.createVerticalStrut(100));
        add(textSpeedPanel);
        add(Box.createVerticalStrut(100));
        add(_saveButton);
        add(Box.createVerticalStrut(10));
        add(_cancelButton);
    }


    private void LoadSettings()
    {
        var fileManager = new FileManager(this, _sceneManager);
        int textSpeed = 200 - fileManager.GetTextSpeed();
        _sceneManager.textSpeed = textSpeed;
        _textSpeedSlider.setValue(textSpeed);
    }

    public void SaveSettings()
    {
        int textSpeed = 200 - _textSpeedSlider.getValue();
        _sceneManager.textSpeed = textSpeed;
        var fileManager = new FileManager(this, _sceneManager);
        fileManager.SaveSettings(textSpeed);
    }


}
