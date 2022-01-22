import com.mongodb.client.*;
import org.bson.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Semantic_Analysis {
    private static List<String[]> list1 = new ArrayList<>();
    private static List<String[]> list2 = new ArrayList<>();
    static HashMap<String,Integer> hash_map = new HashMap<>();
    static int flag1=1;
    static double max_double_log_double;

    static WriteData import_func_write_data;

    public static void main(String[] args) throws IOException {
        MongoClient client = MongoClients.create("mongodb://localhost");
        MongoDatabase database = client.getDatabase("myMongo");
        MongoCollection<Document> mongocollection = database.getCollection("mongonews");
        FindIterable<Document> iterable_Items = mongocollection.find();
        
        list1.add(new String[] {"Query", "with items", "Total / Term appeared","Log10(lastcolum)"});
        list2.add(new String[] {"canada", "Total", "Frequency(f)"});
        for (Document doc : iterable_Items) {
            if(doc.get("desc").toString().contains("Canada")){
                hash_map=Hashmap_Semantic.map_semantic("Canada",hash_map);
            }
            if(doc.get("desc").toString().contains("Moncton")){
                hash_map=Hashmap_Semantic.map_semantic("Moncton",hash_map);
            }
            if(doc.get("desc").toString().contains("Toronto")){
                hash_map=Hashmap_Semantic.map_semantic("Toronto",hash_map);
            }
            String[] wordList = doc.get("desc").toString().split(" ");

            int flag2=0;
            for (String word : wordList) {
                if (word.equals("Canada")) {
                    flag2++;
                }
            }
            if(flag2>0){
                list2.add(new String[]{String.valueOf(flag1),String.valueOf(wordList.length),String.valueOf(flag2)});
                flag1++;
                String str=String.format("%.5f",(double)flag2/wordList.length);
                double freq= Double.valueOf(str);
                if(flag1==1){
                    max_double_log_double=freq;
                }
                if(max_double_log_double<freq){
                    max_double_log_double=freq;
                }
            }
        }
        for (String key : hash_map.keySet() ) {
            list1.add(new String[]{key,String.valueOf(hash_map.get(key)),"500/"+String.valueOf(hash_map.get(key)),String.valueOf(Math.log10(500/hash_map.get(key)))});
        }
        System.out.println("highest frequency "+max_double_log_double);
        import_func_write_data.writeData("src/main/resources/Analysis1.csv",list1);
        import_func_write_data.writeData("src/main/resources/Analysis2.csv",list2);

    }
}