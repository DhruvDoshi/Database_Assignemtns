import java.util.HashMap;

public class Hashmap_Semantic {
    static HashMap<String,Integer> map_semantic(String string_name, HashMap<String,Integer> hash_map){
        if (!hash_map.containsKey(string_name)) {
            hash_map.put(string_name, 1);
        } else {
            int flag2 = hash_map.get(string_name);
            hash_map.put(string_name, flag2 + 1);
        }
        return hash_map;
    }
}
