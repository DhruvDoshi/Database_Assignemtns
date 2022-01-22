import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteData {
    public static void writeData(String filePathList, List<String[]> analysisList)
    {
        File file = new File(filePathList);
        try {
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeAll(analysisList);
            csvWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}