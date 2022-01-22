import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class readFile {
    static List<String> readinfFile(String path) throws IOException {
        File file_name = new File(path);
        List<String> info = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file_name));
        String each_line;
        while ((each_line = reader.readLine()) != null) {
            info.add(each_line);
        }
        reader.close();
        return info;
    }
}
