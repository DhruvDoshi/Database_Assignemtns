import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.io.FileWriter;

public class extractor {
    public static void main( String[] args ) throws Exception
    {
        String s[]={"Canada", "University", "Dalhousie", "Toronto", "Halifax", "Education","Moncton" };
        extractfunc(s);
    }

    @SuppressWarnings("deprecation")
    public static void extractfunc( String str[] ) throws Exception
    {
        String apikey = "6071cbb4d6ee4e1eb07f96f4fa3a6a63";
        int k=0;
        int cntr=1;
        for(int i=0;i<75;i++)
        {
            for(int j=0;j<str.length;j++)
            {
                String address ="https://newsapi.org/v2/everything?q="+str[j]+"&pageSize=1&apiKey=";
                HttpResponse<JsonNode> response = Unirest.get(address + apikey).asJson();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser js=new JsonParser();
                JsonElement element=js.parse(response.getBody().toString());
                String jsonString = gson.toJson(element);
                k++;
                if(k%5==0)
                    cntr++;
                else{
                    FileWriter fw=new FileWriter("src/main/resources/"+Integer.toString(cntr)+".txt",true);
                    fw.write(jsonString);
                    fw.write("\r\n");
                    fw.close();
                    System.out.println(str[j]+" addition to path " +"src/main/resources/"+Integer.toString(cntr)+".txt");
                }
            }
        }
    }
}