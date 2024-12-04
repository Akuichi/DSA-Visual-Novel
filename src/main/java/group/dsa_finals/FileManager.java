package group.dsa_finals;
import java.io.File;
import javax.swing.*;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileManager
{
    private String _saveFolderPath = "saved_games", _saveFilePath = _saveFolderPath + "/save.json";
    private String _settingsFolderPath = "settings", _settingsFilePath = _saveFolderPath + "/config.json";
    private JDialog _parentDialog;
    private SceneManager _sceneManager;

    public FileManager(JDialog parentDialog, SceneManager sceneManager)
    {
        _parentDialog = parentDialog;
        _sceneManager = sceneManager;
    }
    public void SaveGame()
    {
        int response = JOptionPane.showConfirmDialog( //confirmation dialog
                null,
                "This will overwrite the saved data, continue?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION
        );
        if (response == JOptionPane.NO_OPTION) return; //if no yung response, return;

        File folder = new File(_saveFolderPath);
        //check kung may saved_games folder na ba or not
        if (!folder.exists())
        {
            boolean dirCreated = folder.mkdirs(); //create the folder
            if (!dirCreated)
            {
                JOptionPane.showMessageDialog(
                        null,
                        "An error occurred while creating the folder",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        SaveData saveData = new SaveData(_sceneManager.currentChapter, _sceneManager.currentScene); //make the save data to store sa json
        // Save data as JSON
        try
        {
            mapper.writeValue(new File(_saveFilePath), saveData);
            JOptionPane.showMessageDialog(
                    null,
                    "Game saved successfully",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while saving: " + e.getMessage(),
                    "Save Game Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    public void LoadGame()
    {
        if (_sceneManager != null)
        {
            int response = JOptionPane.showConfirmDialog( //confirmation dialog
                    null,
                    "Unsaved progress will be lost, continue?",
                    "Confirm Load Game",
                    JOptionPane.YES_NO_OPTION
            );
            if (response == JOptionPane.NO_OPTION) return; //if no yung response, return;

            ObjectMapper mapper = new ObjectMapper();
            try
            {
                JsonNode saveDataNode = mapper.readTree(new File(_saveFilePath));
                if (saveDataNode.has("chapter") && saveDataNode.has("scene"))
                {
                    _sceneManager.currentChapter = saveDataNode.get("chapter").asInt();
                    _sceneManager.currentScene = saveDataNode.get("scene").asInt();

                    JOptionPane.showMessageDialog(
                            null,
                            "Game loaded successfully",
                            "Load Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    _parentDialog.dispose();
                    _sceneManager.LoadStoryData();
                    _sceneManager.DisplayScene();
                }
                else
                {
                    JOptionPane.showMessageDialog(
                            null,
                            "Missing or corrupt save data",
                            "Load Game Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }



            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                JsonNode saveDataNode = mapper.readTree(new File(_saveFilePath));

                if (saveDataNode.has("chapter") && saveDataNode.has("scene"))
                {
                    int chapterToLoad = saveDataNode.get("chapter").asInt();
                    int sceneToLoad = saveDataNode.get("scene").asInt();
                    new SceneManager(sceneToLoad,chapterToLoad);
                    JOptionPane.showMessageDialog(
                            null,
                            "Game loaded successfully",
                            "Load Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                else
                {
                    JOptionPane.showMessageDialog(
                            null,
                            "Missing or corrupt save data",
                            "Load Game Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


    }

    public int GetTextSpeed()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.readValue(new File(_settingsFilePath), Integer.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return 50;
    }
    public void SaveSettings(int textSpeed)
    {
        File folder = new File(_settingsFolderPath);
        //check kung may settings folder na ba or not
        if (!folder.exists())
        {
            boolean dirCreated = folder.mkdirs(); //create the folder
            if (!dirCreated)
            {
                JOptionPane.showMessageDialog(
                        null,
                        "An error occurred while creating the folder",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        //Save text speed to JSON
        try
        {
            mapper.writeValue(new File(_settingsFilePath), textSpeed);
            _sceneManager.textSpeed = textSpeed;
            JOptionPane.showMessageDialog(
                    null,
                    "Settings saved successfully",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred while saving: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }


    public static class SaveData
    {
        public int chapter;
        public int scene;

        public SaveData(int chapter, int scene)
        {
            this.chapter = chapter;
            this.scene = scene;
        }
    }


}
