package mis.tripioneer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/************************************************************************************************************
 * File : JsonParser
 * Function: Parse json format to get useful info
 * Return : String json
 ************************************************************************************************************/
public class JsonParser
{

    private String key ="";
    private String CASE ="";

    public JsonParser(String scenario)
    {
        CASE = scenario;
    }


    public ArrayList<String> Parse(String val, String match)

    {
        ArrayList<String> json = new ArrayList<String>();
        JSONArray object;
        key = match;

        try
        {
            switch (CASE) {
                case "LOGIN":
                    object = (JSONArray) new JSONTokener(val).nextValue();

                    for (int i = 0; i < object.length(); i++) {
                        json.add(object.getJSONObject(i).getString(key));
                    }

                    break;
                case "RECOMMENDATION":
                    try
                    {
                        object = (JSONArray)new JSONTokener(val).nextValue();
                        for(int i=0;i<object.length();i++)
                        {
                            json.add(object.getJSONObject(i).getString(key));

                        }
                    }
                    catch(ClassCastException e)
                    {
                        JSONObject obj = new JSONObject(val);
                        json.add(obj.getString(key));
                    }

                    break;
                case "TRIP":
                    object = (JSONArray) new JSONTokener(val).nextValue();

                    for (int i = 0; i < object.length(); i++) {
                        json.add(object.getJSONObject(i).getString(key));

                    }
                    break;
                case "CHANNEL_CONTENT":
                    object = (JSONArray) new JSONTokener(val).nextValue();

                    for (int i = 0; i < object.length(); i++) {
                        json.add(object.getJSONObject(i).getString(key));
                    }
                    break;
                case "PLACE":
                    object = new JSONArray(val);
                    for (int i = 0; i < object.length(); i++) {
                        json.add(object.getString(i));
                    }
                case "CHANNEL_INFO":
                    object = (JSONArray) new JSONTokener(val).nextValue();
                    for (int i = 0; i < object.length(); i++) {
                        json.add(object.getJSONObject(i).getString(key));
                    }
                    break;
                case "MAP":
                    object = (JSONArray) new JSONTokener(val).nextValue();
                    for (int i = 0; i < object.length(); i++)
                    {
                        json.add(object.getJSONObject(i).getString(key));
                    }
                    break;
                case "PLACE_NAV":
                    object = (JSONArray) new JSONTokener(val).nextValue();
                    for (int i = 0; i < object.length(); i++)
                    {
                        json.add(object.getJSONObject(i).getString(key));
                    }
                    break;
                case "CHANNEL_CATEGORY":
                    object = (JSONArray)new JSONTokener(val).nextValue();
                default:
                    break;
            }

        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return json;
        }
    }
}
