/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package group.dsa_finals;

/*

  @author Akutan
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.*;
import com.github.kwhat.jnativehook.GlobalScreen;

public class SceneManager
{
    private int _resolutionX, _resolutionY, _textAreaHeight;
    private boolean _isOneShotGif = false, _isPlayingAnimation = false;
    private List<BufferedImage> _gifFrames;
    private int _currentFrame = 0;
    private Timer _timer;
    public JFrame frame;
    private BackgroundPanel _backgroundPanel;
    private Image _backgroundImage, _characterImage;
    private JLabel _dialogueCharacterLabel;
    private JTextArea _textArea;
    private JButton _option1Button, _option2Button;
    private JPanel _textPanel;
    private JsonNode _storyData;
    private Thread _typingThread;
    private final SceneManager _sceneManager;
    public int textSpeed, windowType;
    public int currentScene, currentChapter;

    public SceneManager(int currentScene, int currentChapter)
    {
        _sceneManager = this;
        //Save passed scene id as current scene
        this.currentScene = currentScene;
        this.currentChapter = currentChapter;
        //Load the story data from the JSON file
        LoadStoryData();
        
        
        DrawGUIComponents();



        frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                int width = frame.getContentPane().getWidth();
                int height = frame.getContentPane().getHeight();;
                _resolutionX = width;
                _resolutionY = (int) (height / 1.384615384615385);
                _textAreaHeight = height - _resolutionY;
                _backgroundPanel.revalidate();
                _backgroundPanel.repaint();
                LoadDialogueButtons();
            }
        });

        var fileManager = new FileManager(null,this);
        textSpeed = fileManager.GetSettings().textSpeed;
        windowType = fileManager.GetSettings().windowType;

        SetWindowType(windowType);
        DisplayScene();


        frame.setVisible(true);
    }

    public void LoadStoryData()
    { //Load lang yung JSON for our story data
        ObjectMapper mapper = new ObjectMapper(); //Object mapper is part of the Jackson Library na inimport para makapag interact sa JSON file
        try
        {
            _storyData = mapper.readTree(new File("chap_" + currentChapter + ".json")); //readTree, function to read a JSON file para ma read yung tree of JsonNodes,
                                                                 //Yung JsonNode naman is a representation ng isang element sa JSON tulad ng nasatin na option, scene number
                                                                 //Inistore natin yung data sa storyData variable so we can call it later pag magpapalit ng scenes
        }
        catch (IOException e)
        { //Just to catch errors
            e.printStackTrace(); //print to console yung error details, para mas madali mag debug if ever
            JOptionPane.showMessageDialog(frame, "Failed to load story data", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); //Exit program, status code of 1 just to indicate na error
        }
    }

    public void DisplayScene()
    {
        JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene)); //get from json the scene node (scene number)
        if (sceneNode == null)
        {  //If blank yung scenenode for some reason may mali sa JSON
            _textArea.setText("Scene not found");
            _option1Button.setVisible(false);
            _option2Button.setVisible(false);
            return; //return, end method/function
        }

        UpdateCharacterLabel(sceneNode); //C-check if merong character label for the current scene then display siya if yes otherwise blank

        //Set scene text
        _textArea.setText(""); //Reset yung text ng dialogue para start from scratch yung typing effect ng dialogue
        String dialogueText = sceneNode.get("text").asText(); //store yung dialogue text sa string

        DisplayTypingEffect(dialogueText); //Do the typing effect on a separate thread
        DisplayImages(sceneNode); //Checks if may bg image and character images ba and depending dun ppaint over siya sa background panel


    }

    private void UpdateCharacterLabel(JsonNode sceneNode)
    {
        if (sceneNode.has("character"))
        {
            var characterNode = sceneNode.get("character"); //store as variable yung character node sa JSON
            _dialogueCharacterLabel.setText(characterNode.asText());
        }
        else
        {
            _dialogueCharacterLabel.setText("");
        }
    }

    private void DisplayTypingEffect(String dialogueText)
    {
        //Gagamit tayo ng thread para while running tong code na to makakapag interact parin tayo sa app
        //kasi nasa separate thread siya nag rrun
        _typingThread = new Thread(() ->
        {
            try
            {
                //Temporarily hide buttons while dialogue is typing and adjust panel size to fill the gap
                _textPanel.setPreferredSize(new Dimension(_resolutionX, _textAreaHeight));
                _textPanel.setBounds(0, 0, _resolutionX, _textAreaHeight);
                _option1Button.setVisible(false);
                _option2Button.setVisible(false);
                for (int i = 0; i < dialogueText.length(); i++)
                { //Simple for loop to print characters of string 1 by 1
                    _textArea.append(String.valueOf(dialogueText.charAt(i)));
                    Thread.sleep(textSpeed); // Wait for (textSpeed variable speed) before typing the next character
                }
                Thread.sleep(200); //wait 200ms before showing buttons
                LoadDialogueButtons(); //If done na typing show buttons
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
        _typingThread.start(); //start the thread we created
    }

    private void DisplayImages(JsonNode sceneNode)
    {
        //LOAD Background image for scene and char image if meron
        try
        {

            String imagePath = sceneNode.get("bg_img").asText(); // Get image path from JSON
            _backgroundImage = new ImageIcon(imagePath).getImage();
            if (sceneNode.has("oneShotGif"))
            {
                PlayOneShotGif(imagePath);
                _isOneShotGif = true;
                _currentFrame = 0;
            }
            else
            {
                _isOneShotGif = false;
            }
            if (sceneNode.has("char_img"))
            {
                imagePath = sceneNode.get("char_img").asText();
                if (imagePath.isEmpty())
                {
                    _characterImage = null;
                }
                else
                {
                    _characterImage = new ImageIcon(imagePath).getImage();
                }
            }
            else
            {
                _characterImage = null;
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }

        _backgroundPanel.revalidate();
        _backgroundPanel.repaint();
    }



    private void LoadDialogueButtons()
    {
        JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene)); //get from json the scene node (scene number)
        if (sceneNode == null)
        {
            _textArea.setText("Scene not found");
            _option1Button.setVisible(false);
            _option2Button.setVisible(false);
            return;
        }
        //Buttons
        JsonNode optionsNode = sceneNode.get("options"); //Options in JSON file is yung dialogue options natin for that scene

        //So baiscally if may option 1 or 2 show the appropriate buttons, if wala hide
        if (optionsNode.has("1"))
        { //For button 1
            _option1Button.setText(optionsNode.get("1").get("text").asText());
            _option1Button.setVisible(true);
        }
        else
        {
            _option1Button.setVisible(false);
        }

        if (optionsNode.has("2"))
        { //For button 2
            _option2Button.setText(optionsNode.get("2").get("text").asText());
            _option2Button.setVisible(true);
        }
        else
        {
            _option2Button.setVisible(false);
        }


        if (_option1Button.isVisible() && _option2Button.isVisible())
        {
            _textPanel.setPreferredSize(new Dimension(_resolutionX, _textAreaHeight - 50));
            _textPanel.setBounds(0, 0, _resolutionX, _textAreaHeight - 50);
        }
        else if (_option1Button.isVisible() || _option2Button.isVisible())
        {
            _textPanel.setPreferredSize(new Dimension(_resolutionX, _textAreaHeight - 25)); // Adjust height to fill the space
            _textPanel.setBounds(0, 0, _resolutionX, _textAreaHeight - 25);
        }
        else
        {
            _textPanel.setPreferredSize(new Dimension(_resolutionX, _textAreaHeight));
            _textPanel.setBounds(0, 0, _resolutionX, _textAreaHeight);
        }
    }

    //Eto yung event na ginawa para sa dialogue choices button the (int option) here refers to the option number (1, 2) in our case sa ngayon
    private void HandleDialogueOption(int option)
    {
        JsonNode optionsNode = _storyData.get("scenes").get(String.valueOf(currentScene)).get("options"); //Get from current scene yung options
        if (optionsNode.has(String.valueOf(option))) //If may option (from int option, this is either 1 or 2) continue
        {
            JsonNode optionNode = optionsNode.get(String.valueOf(option));
            if (optionNode.has("nextChapter")) //check if may chapter, if yes change chapters
            {
                currentChapter = optionNode.get("nextChapter").asInt();
                currentScene = 0;
                LoadStoryData();
                DisplayScene();
                return;
            }
            currentScene = optionNode.get("nextScene").asInt(); //Store the next scene ID sa result ng option choice as int
            DisplayScene(); //Display Scene kasi iba na yung currentScene
        }
        else
        {
            JOptionPane.showMessageDialog(frame, "No valid option selected."); //Just to know if di na configure ng tama
        }
    }

    private void StopTypingEffect()
    {
        if (_typingThread != null && _typingThread.isAlive()) //Check if active ba yung typing
        { 
            _typingThread.interrupt(); // Interrupt the typing thread
            // Display the full text immediately
            JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene));
            if (sceneNode != null)
            {
                //_textArea.setText(sceneNode.get("text").asText());
                _textArea.setText(sceneNode.get("text").asText());
                LoadDialogueButtons();
            }           
            
        }
        
    }

    class BackgroundPanel extends JPanel
    {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath)
        {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (_backgroundImage != null)
            {
                //Draw the image onto the panel

                if (_isOneShotGif)
                {
                    BufferedImage currentImage = _gifFrames.get(_currentFrame);
                    g.drawImage(currentImage, 0, 0, _resolutionX, _resolutionY, this);
                }
                else
                    g.drawImage(_backgroundImage, 0, 0, _resolutionX, _resolutionY, this);
                if (_characterImage != null) //if may image for the character, paint over
                {
                    g.drawImage(_characterImage, 0, 0, _resolutionX, _resolutionY, this);
                }
            }
        }
    }

    public void PlayOneShotGif(String gifPath) throws IOException
    {
        _gifFrames = loadGif(gifPath);

        _textPanel.setPreferredSize(new Dimension(_resolutionX, _textAreaHeight));
        _textPanel.setBounds(0, 0, _resolutionX, _textAreaHeight);
        _option1Button.setVisible(false);

        _isPlayingAnimation = true;

        //Timer to display each frame, 41ms para close sa 24fps
        _timer = new Timer(41, e -> {
            if (_currentFrame < _gifFrames.size() - 1) {
                _currentFrame++;
                _backgroundPanel.repaint();
            } else {
                _timer.stop();  //Stop after the last frame

                //Go to next scene after animation
                JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene));
                if (sceneNode.has("isTransition"))
                {
                    currentScene = sceneNode.get("options").get("1").get("nextScene").asInt();
                    DisplayScene();
                }
                _option1Button.setVisible(true);
                _isPlayingAnimation = false;
            }
        });

        _timer.start();
    }

    private List<BufferedImage> loadGif(String path) throws IOException {
        //Load the GIF into a BufferedImage
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream stream = ImageIO.createImageInputStream(new File(path));
        reader.setInput(stream);

        int numFrames = reader.getNumImages(true);
        List<BufferedImage> framesList = new java.util.ArrayList<>();

        //Extract all frames from the GIF
        for (int i = 0; i < numFrames; i++) {
            BufferedImage frame = reader.read(i);
            framesList.add(frame);
        }

        return framesList;
    }




    public void SetWindowType(int windowType)
    {
        frame.setVisible(false);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        if (windowType == 0)
        {
            frame.dispose();
            //undecorated = no borders, title bar
            frame.setUndecorated(true);
            //Fullscreen
            frame.setResizable(false);
            gs.setFullScreenWindow(frame);
        }
        else if (windowType == 1)
        {
            frame.dispose();
            gs.setFullScreenWindow(null);
            frame.setUndecorated(false);
            frame.setResizable(true);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
        }
        LoadDialogueButtons();
        frame.setVisible(true);
    }

    private void DrawGUIComponents()
    {
        frame = new JFrame("Visual Novel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        _backgroundPanel = new BackgroundPanel("images/scene_1");
        _backgroundPanel.setOpaque(false);
        _backgroundPanel.setLayout(new BoxLayout(_backgroundPanel, BoxLayout.Y_AXIS));
        _backgroundPanel.setFocusable(false);

        frame.add(_backgroundPanel, BorderLayout.CENTER);



        //text panel to put text area (dialogue text)
        _textPanel = new JPanel();
        _textPanel.setLayout(new BorderLayout());
        _textPanel.setBackground(Color.BLACK);
        _textPanel.setBorder(BorderFactory.createEmptyBorder());

        //jLabel to show the name of the character speaking if there is one
        _dialogueCharacterLabel = new JLabel();
        _dialogueCharacterLabel.setPreferredSize(new Dimension(1000, 25));
        _dialogueCharacterLabel.setBackground(Color.BLACK);
        _dialogueCharacterLabel.setForeground(Color.WHITE);
        _dialogueCharacterLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        _dialogueCharacterLabel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        
        //Text area, this is the dialogue text
        _textArea = new JTextArea();
        _textArea.setEditable(false); //Disable typing
        _textArea.setLineWrap(true); //To auto wrap para di lumagpas text sa window
        _textArea.setWrapStyleWord(true); //para buo parin yung word pag na cut off siya to the next line
        _textArea.setBackground(Color.DARK_GRAY);  //Background color for text area
        _textArea.setForeground(Color.WHITE);  //Text color
        _textArea.setFont(new Font("Roboto", Font.PLAIN, 18)); //Font
        _textArea.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));





        _textPanel.add(new JScrollPane(_textArea), BorderLayout.CENTER); //Add sa textPanel yung text area and include scrollbar
        _textPanel.add(_dialogueCharacterLabel, BorderLayout.NORTH);

        
        //Create panel for dialogue option buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); //Y for vertical layout ng boxlayout

        //Create buttons---
        _option1Button = new JButton();
        _option2Button = new JButton();



        //1280 width para buong width ng screen, then height ng buttons
        _option1Button.setPreferredSize(new Dimension(1280, 25));
        _option2Button.setPreferredSize(new Dimension(1280, 25));

        _option1Button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        _option2Button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        _option1Button.setMargin(new Insets(0, 0, 0, 0));
        _option2Button.setMargin(new Insets(0, 0, 0, 0));        
        
        Color buttonBackgroundColor = Color.WHITE;
        _option1Button.setBackground(buttonBackgroundColor);
        _option2Button.setBackground(buttonBackgroundColor);
       
        _option1Button.setForeground(Color.BLACK);
        _option2Button.setForeground(Color.BLACK);

        //Remove button border
        _option1Button.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        _option2Button.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        
        _option1Button.setFont(new Font("Roboto", Font.PLAIN, 18));
        _option2Button.setFont(new Font("Roboto", Font.PLAIN, 18));

        _option1Button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        _option2Button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        _option1Button.setHorizontalAlignment(SwingConstants.LEFT);
        _option2Button.setHorizontalAlignment(SwingConstants.LEFT);
        //-----
        
        

        //Add buttons to the buttonPanel
        buttonPanel.add(_option1Button);
        buttonPanel.add(_option2Button);

        JPanel dialoguePanel = new JPanel(new BorderLayout()); //Dialogue panel kugn san natin lalagay yung Dialogue text (sa taas), Button choices (sa baba)
        dialoguePanel.add(_textPanel, BorderLayout.NORTH);  //Add text area panel
        dialoguePanel.add(buttonPanel, BorderLayout.SOUTH); //Add buttons panel        
        dialoguePanel.setBackground(Color.DARK_GRAY);

        frame.add(dialoguePanel, BorderLayout.SOUTH);


        //Button listeners para sa choices button
        _option1Button.addActionListener(e -> HandleDialogueOption(1));
        _option2Button.addActionListener(e -> HandleDialogueOption(2));

        dialoguePanel.setFocusable(false);
        _textArea.setFocusable(false);
        buttonPanel.setFocusable(false);
        _option2Button.setFocusable(false);
        _option1Button.setFocusable(false);

        //JNativeHook para sa click anywhere to skip typing effect
        try
        {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(new NativeMouseListener()
            {
                @Override
                public void nativeMousePressed(NativeMouseEvent e)
                {
                    int x = e.getX();
                    int y = e.getY();

                    //Check if click is inside frame
                    if (frame != null && frame.getBounds().contains(x, y))
                    {
                        StopTypingEffect();
                    }

                    if (_isPlayingAnimation)
                    {
                        _currentFrame = _gifFrames.size() -  1;
                    }
                }
            });
        }
        catch (NativeHookException e)
        {
            throw new RuntimeException(e);
        }


        //New key listener for ESC button para sa pause menu
        frame.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                //Check if ESC yung button pressed
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    PauseMenuDialog pauseDialog = new PauseMenuDialog(frame, _sceneManager);
                    pauseDialog.setVisible(true);
                    //Open pause menu dialog
                }
                //If spacebar (skip typing)
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    StopTypingEffect();
                }

            }
        });

        frame.revalidate();
        frame.repaint();
    }
    
    
    
    
}
