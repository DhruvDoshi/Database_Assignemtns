import com.mongodb.client.*;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sentiment_Analysis {
    static List<String> negativeList = new ArrayList<>();
    static List<String> positiveList = new ArrayList<>();
    static List<String[]> analysisList = new ArrayList<>();

    static int count = 1;
    static WriteData import_func_write_data;
    static readFile import_func_read_file;

    public static void main(String[] args) throws IOException {

        String positivePath = "src/main/resources/positive.txt";
        String negativePath = "src/main/resources/negative.txt";
        positiveList = import_func_read_file.readinfFile(positivePath);
        negativeList = import_func_read_file.readinfFile(negativePath);

        // Conencting with MONGODB
        MongoClient client = MongoClients.create("mongodb://localhost");
        MongoDatabase database = client.getDatabase("CSCI5408");
        MongoCollection<Document> mongocollection = database.getCollection("mongonews");
        FindIterable<Document> iterable_Items = mongocollection.find();

        analysisList.add(new String[]{"Article", "News Description", "Match", "Polarity"});

        for (Document document_loop : iterable_Items) {
            int counter = 0;
            String[] list_of_words = document_loop.get("desc").toString().split(" ");
            Map<String, Integer> bag_of_words = new HashMap<>();
            List<String> matching_word_list = new ArrayList<>();

            for (String word_loop : list_of_words) {
                if (!bag_of_words.containsKey(word_loop)) {
                    bag_of_words.put(word_loop, 1);
                } else {
                    int count = bag_of_words.get(word_loop);
                    bag_of_words.put(word_loop, count + 1);
                }
            }

            for (String key : bag_of_words.keySet()) {
                if (positiveList.contains(key)) {
                    if (!matching_word_list.contains(key)) {
                        matching_word_list.add(key);
                    }
                    counter = counter + bag_of_words.get(key);
                }
                if (negativeList.contains(key)) {
                    if (!matching_word_list.contains(key)) {
                        matching_word_list.add(key);
                    }
                    counter = counter - bag_of_words.get(key);
                }
            }

            String assign_polar;
            if (counter < 0) {
                assign_polar = "negative";
            } else if (counter > 0) {
                assign_polar = "positive";
            } else {
                assign_polar = "netural";
            }
            analysisList.add(new String[]{String.valueOf(count), document_loop.get("desc").toString(), matching_word_list.toString(), assign_polar});
            count++;
        }


        import_func_write_data.writeData("src/main/resources/Sentiment_Analysis.csv", analysisList);
    }

    public static void setImport_func_read_file(readFile import_func_read_file) {
        Sentiment_Analysis.import_func_read_file = import_func_read_file;
    }
}
