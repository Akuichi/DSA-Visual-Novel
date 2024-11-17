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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SceneManager
{
    public JFrame frame;
    private JLabel _imageLabel, _dialogueCharacterLabel;
    private JTextArea _textArea;
    private JButton _option1Button, _option2Button;
    private JPanel _textPanel;
    private JsonNode _storyData;
    private Thread _typingThread;
    private final SceneManager _sceneManager;
    public int textSpeed;
    public int currentScene;

    public SceneManager(int currentScene)
    {
        _sceneManager = this;
        //Load the story data from the JSON file
        LoadStoryData();
        
        
        DrawGUIComponents();

        var fileManager = new FileManager(null,this);
        textSpeed = fileManager.GetTextSpeed();

        //Save passed scene id as current scene
        this.currentScene = currentScene;
        DisplayScene();


        frame.setVisible(true);
    }

    private void LoadStoryData()
    { //Load lang yung JSON for our story data
        ObjectMapper mapper = new ObjectMapper(); //Object mapper is part of the Jackson Library na inimport para makapag interact sa JSON file
        try
        {
            _storyData = mapper.readTree(new File("story.json")); //readTree, function to read a JSON file para ma read yung tree of JsonNodes,
                                                                 //Yung JsonNode naman is a representation ng isang element sa JSON tulad ng nasatin na option, scene number
                                                                 //Inistore natin yung data sa storyData variable so we can call it later pag magpapalit ng scenes
        }
        catch (IOException e)
        { //Just to catch errors
            e.printStackTrace(); //print to console yung error details, para mas madali mag debug if ever
            JOptionPane.showMessageDialog(null, "Failed to load story data", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); //Exit program, status code of 1 just to indicate na error
        }
    }

    public void DisplayScene()
    {
        JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene)); //get from json the scene node (scene number)
        if (sceneNode == null)
        {  //If blank yung scenenode for some reason may mali sa JSON
            _textArea.setText("Scene not found");
            _imageLabel.setIcon(null);
            _option1Button.setVisible(false);
            _option2Button.setVisible(false);
            return; //return, end method/function
        }
        
        var characterNode = sceneNode.get("character"); //store as variable yung character node sa JSON
        if (characterNode == null) //Check if the character node exists sa current scene
        { //if null ibig sabihin walang character speaking so set blank yung Label ng character name
            _dialogueCharacterLabel.setText("");
        }
        else //else lagay name ng character sa label
        {
            _dialogueCharacterLabel.setText(characterNode.asText());
        }

        //Set scene text
        _textArea.setText(""); //Reset yung text ng dialogue para start from scratch yung typing effect ng dialogue
        String dialogueText = sceneNode.get("text").asText(); //store yung dialogue text sa string
        
        //Gagamit tayo ng thread para while running tong code na to makakapag interact parin tayo sa app
        //kasi nasa separate thread siya nag rrun
        _typingThread = new Thread(() ->
        {
            try
            {
                //Temporarily hide buttons while dialogue is typing and adjust panel size to fill the gap
                _textPanel.setPreferredSize(new Dimension(1280, 150)); //150 yung height because we tempoararily disabled the buttons sa next line of code, para walang empty gap
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

        //Load and set image
        try
        {
            //Load and set image
            String imagePath = sceneNode.get("image").asText(); //Get from JSON yung image file path sa current scene
            File imgFile = new File(imagePath); //Store as file
            if (imgFile.exists())
            { //Check kung may image file ba, if yes continue
                BufferedImage originalImage = ImageIO.read(imgFile); //Convert to buffered image yung file

                //Width and height ng image
                int targetWidth = 1280;
                int targetHeight = 530;

                Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH); //Scale the image as a new image
                ImageIcon imageIcon = new ImageIcon(scaledImage); //Change it to as an image icon

                _imageLabel.setIcon(imageIcon); //palitan icon ng jlabel para maging image na naka display
                _imageLabel.setPreferredSize(new Dimension(targetWidth, targetHeight));
            }
            else //If walang image file, no image to load (set as null)
            {
                _imageLabel.setIcon(null);
            }

        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }

    }

    private void LoadDialogueButtons()
    {
        JsonNode sceneNode = _storyData.get("scenes").get(String.valueOf(currentScene)); //get from json the scene node (scene number)
        if (sceneNode == null)
        {
            _textArea.setText("Scene not found");
            _imageLabel.setIcon(null);
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
            //If the buttons are visible, give the textPanel a fixed height (100px)
            _textPanel.setPreferredSize(new Dimension(1280, 100));
        }
        else if (_option1Button.isVisible() || _option2Button.isVisible())
        {
            _textPanel.setPreferredSize(new Dimension(1280, 125)); // Adjust height to fill the space
        }
        else
        {
            _textPanel.setPreferredSize(new Dimension(1280, 150));
        }

    }

    //Eto yung event na ginawa para sa dialogue choices button the (int option) here refers to the option number (1, 2) in our case sa ngayon
    private void HandleDialogueOption(int option)
    {
            JsonNode optionsNode = _storyData.get("scenes").get(String.valueOf(currentScene)).get("options"); //Get from current scene yung options
        if (optionsNode.has(String.valueOf(option))) //If may option (from int option, this is either 1 or 2) continue
        {
            currentScene = optionsNode.get(String.valueOf(option)).get("nextScene").asInt(); //Store the next scene ID sa result ng option choice as int
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
                _textArea.setText(sceneNode.get("text").asText());
                LoadDialogueButtons();
            }           
            
        }
        
    }
    
    private void DrawGUIComponents()
    {
        frame = new JFrame("Visual Novel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        //Image panel
        _imageLabel = new JLabel();
        _imageLabel.setHorizontalAlignment(JLabel.CENTER);
        _imageLabel.setFocusable(false);
        frame.add(_imageLabel, BorderLayout.NORTH);//Add to frame

        //text panel to put text area (dialogue text)
        _textPanel = new JPanel();
        _textPanel.setFocusable(false);
        _textPanel.setLayout(new BorderLayout());
        _textPanel.setBackground(Color.BLACK);
        _textPanel.setPreferredSize(new Dimension(1280, 100)); //Height keep yung width 1280
        _textPanel.setMaximumSize(new Dimension(1280, 150));
        _textPanel.setBorder(BorderFactory.createEmptyBorder());

        //jLabel to show the name of the character speaking if there is one
        _dialogueCharacterLabel = new JLabel();
        _dialogueCharacterLabel.setPreferredSize(new Dimension(1000, 25));
        _dialogueCharacterLabel.setBackground(Color.BLACK);
        _dialogueCharacterLabel.setForeground(Color.WHITE);
        _dialogueCharacterLabel.setText("YES");
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
        
        Color buttonBackgroundColor = Color.DARK_GRAY;
        _option1Button.setBackground(buttonBackgroundColor);
        _option2Button.setBackground(buttonBackgroundColor);
       
        _option1Button.setForeground(Color.WHITE);
        _option2Button.setForeground(Color.WHITE);

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


        frame.add(_imageLabel, BorderLayout.NORTH);
        frame.add(dialoguePanel, BorderLayout.SOUTH);

        _dialogueCharacterLabel.setFocusable(false);
        _imageLabel.setFocusable(false);
        _textPanel.setFocusable(false);
        _textArea.setFocusable(false);
        _option1Button.setFocusable(false);
        _option2Button.setFocusable(false);


        //Button listeners para sa choices button
        _option1Button.addActionListener(e -> HandleDialogueOption(1));
        _option2Button.addActionListener(e -> HandleDialogueOption(2));

        MouseAdapter StopTypingEffectListener = new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                StopTypingEffect();  //Call yung stop typing effect
            }
        };

        //register sa event na ginawa natin^
        frame.addMouseListener(StopTypingEffectListener);
        dialoguePanel.addMouseListener(StopTypingEffectListener);
        //_textArea.addMouseListener(StopTypingEffectListener);


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
            }
        });

    }
    
    
    
    
}
