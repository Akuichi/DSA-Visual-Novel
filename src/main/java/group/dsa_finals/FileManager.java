package group.dsa_finals;
import java.io.File;
import javax.swing.*;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileManager
{
    private String _saveFolderPath = "saved_games", _saveFilePath = _saveFolderPath + "/save.json";
    private String _settingsFolderPath = "settings", _settingsFilePath = _settingsFolderPath + "/config.json";
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
                _sceneManager.frame,
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
                        _sceneManager.frame,
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
                    _sceneManager.frame,
                    "Game saved successfully",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                    _sceneManager.frame,
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
                    _sceneManager.frame,
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
                            _sceneManager.frame,
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
                            _sceneManager.frame,
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
                            _sceneManager.frame,
                            "Game loaded successfully",
                            "Load Successful",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
                else
                {
                    JOptionPane.showMessageDialog(
                            _sceneManager.frame,
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

    public SettingsData GetSettings()
    {
        if(!new File(_settingsFilePath).exists())
        {
            SaveSettings(50,1);
        }
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            JsonNode settingsNode = mapper.readTree(new File(_settingsFilePath));
            return new SettingsData(settingsNode.get("textSpeed").asInt(), settingsNode.get("windowType").asInt());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new SettingsData(50, 0); //Default to 50 text speed and false fullscreen pag walang value
    }
    public void SaveSettings(int textSpeed, int windowType)
    {
        File folder = new File(_settingsFolderPath);
        //check kung may settings folder na ba or not
        if (!folder.exists())
        {
            boolean dirCreated = folder.mkdirs(); //create the folder
            if (!dirCreated)
            {
                JOptionPane.showMessageDialog(
                        _sceneManager.frame,
                        "An error occurred while creating the folder",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        SettingsData settingsData = new SettingsData(textSpeed, windowType);

        //Save settings to JSON
        try
        {
            mapper.writeValue(new File(_settingsFilePath), settingsData);
            _sceneManager.textSpeed = textSpeed; //update actual textspeed sa game
            JOptionPane.showMessageDialog(
                    _sceneManager.frame,
                    "Settings saved successfully",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                    _sceneManager.frame,
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


    public static class SettingsData
    {
        public int textSpeed;
        public int windowType;

        public SettingsData(int textSpeed, int windowType)
        {
            this.textSpeed = textSpeed;
            this.windowType = windowType;
        }
    }


}
