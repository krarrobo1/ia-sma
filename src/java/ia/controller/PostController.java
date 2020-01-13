package ia.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PostController {

    public static JSONArray getPosts() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("assets/data.json")) {
            Object obj = jsonParser.parse(reader);
            JSONArray data = (JSONArray) obj;
            return data;

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
