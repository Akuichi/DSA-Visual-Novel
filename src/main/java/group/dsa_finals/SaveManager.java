package group.dsa_finals;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class SaveManager
{
    public void SaveGame(String currentScene)
    {
        try (FileWriter writer = new FileWriter("savedgames/save.txt"))
        {
            writer.write(currentScene);
        }
        catch (IOException e)
        {
            System.out.println("Error writing save");
            e.printStackTrace();
        }
    }

    public void LoadGame()
    {
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt")))
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                System.out.println(line);
            }
        }
        catch (IOException e)
        {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
}
