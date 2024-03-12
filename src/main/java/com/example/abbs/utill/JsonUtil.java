package com.example.abbs.utill;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonUtil {

    public String list2Json(List<String> list) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", list);
        return jsonObject.toString();
    }

    public List<String> json2List(String jsonString)  {
        JSONParser jsonParser = new JSONParser();
        List<String> list = null;
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);
            JSONArray jsonArray = (JSONArray) jsonObject.get("list");
            list = (List<String>) jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
