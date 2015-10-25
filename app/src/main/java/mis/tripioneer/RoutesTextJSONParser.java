package mis.tripioneer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jenny on 2015/8/19.
 */
public class RoutesTextJSONParser
{

    //回傳單層ArrayList
    public ArrayList<String> parse(JSONObject jObject, String match)
    {
        ArrayList<String> json = new ArrayList<String>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        int temp;
        Log.d("TAG", "RoutesTextJSONParser");
        switch (match) {
            case "TEXT"://只有main_road有用
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++) {
                                String t;
                                t = jSteps.getJSONObject(k).getString("html_instructions");
                                String noHTMLString = t.replaceAll("\\<.*?>", "");
                                json.add(noHTMLString);
                                Log.d("k is", String.valueOf(k));
                                Log.d("TEXT", noHTMLString);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "SUMMARY": //routes個數決定summary
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++) {
                        String t;
                        t = ((JSONObject) jRoutes.get(i)).getString("summary");
                        json.add(t);
                        Log.d("TAG", "i is" + String.valueOf(i));
                        Log.d("TAG", "summary" + t);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            /*case "BOUNDS": //routes個數決定summary
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    /** Traversing all routes */
            /*        for (int i = 0; i < jRoutes.length(); i++)
                    {
                        String t;
                        t = ((JSONObject) jRoutes.get(i)).getJSONObject("bounds").getString("");
                        json.add(t);
                        Log.d("TAG", "i is"+String.valueOf(i));
                        Log.d("TAG","summary"+ t);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;*/
            case "DISTANCE":
                Log.d("TAG", "Distance");
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        temp = 0;
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("value");
                            temp += Integer.parseInt(t);
                            Log.d("TAG", "j is" + String.valueOf(j));
                            Log.d("TAG", "distance" + t);
                            Log.d("TAG", "temp" + temp);
                        }
                        json.add(convert(temp, "dis"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "DURATION": //不同routes存在不同arraylist
                Log.d("TAG", "Duration");
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        temp = 0;
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("value");
                            temp += Integer.parseInt(t);
                            Log.d("TAG", "j is" + String.valueOf(j));
                            Log.d("TAG", "duration" + t);
                            Log.d("TAG", "temp" + temp);
                        }
                        json.add(convert(temp, "dur"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "PLACE_NAVI_DISTANCE":
                Log.d("TAG", "place_navi_Distance");
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("distance").getString("text");
                            Log.d("TAG", "j is" + String.valueOf(j));
                            Log.d("TAG", "distance" + t);
                            json.add(t);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "PLACE_NAVI_DURATION": //不同routes存在不同arraylist
                Log.d("TAG", "place_navi_Duration");
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        for (int j = 0; j < jLegs.length(); j++)
                        {
                            String t = "";
                            t = ((JSONObject) jLegs.get(j)).getJSONObject("duration").getString("text");
                            json.add(t);
                            Log.d("TAG", "j is" + String.valueOf(j));
                            Log.d("TAG", "duration" + t);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        Log.d("TEXT", "TEXT");
        return json;
    }

    //回傳雙層ArrayList
    public ArrayList<ArrayList<String>> parseroad(JSONObject jObject, int position, String match)
    {
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        ArrayList<ArrayList<String>> road = new ArrayList<>();
        switch (match)
        {
            case "PLACE_NAVI_TEXT":
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        ArrayList<String> json = new ArrayList<String>();
                        //for (int j = 0; j < jLegs.length(); j++)
                        //{
                            jSteps = ((JSONObject) jLegs.get(0)).getJSONArray("steps");

                            for (int k = 0; k < jSteps.length(); k++)
                            {
                                String t;
                                t = jSteps.getJSONObject(k).getString("html_instructions");
                                String noHTMLString = t.replaceAll("\\<.*?>", "");
                                json.add(noHTMLString);
                                Log.d("TAG","k is"+ String.valueOf(k));
                                Log.d("TAG", "TEXT"+noHTMLString);
                            }
                        //}
                        road.add(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "PLACE_NAVI_STEPS_DIS":
                try {
                    jRoutes = jObject.getJSONArray("routes");
                    for (int i = 0; i < jRoutes.length(); i++)
                    {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        ArrayList<String> json = new ArrayList<String>();
                        //for (int j = 0; j < jLegs.length(); j++)
                        //{
                            jSteps = ((JSONObject) jLegs.get(0)).getJSONArray("steps");

                            for (int k = 0; k < jSteps.length(); k++) {
                                String t;
                                t = ((JSONObject)jSteps.get(k)).getJSONObject("distance").getString("text");
                                json.add(t);
                                Log.d("TAG", "k is"+String.valueOf(k));
                                Log.d("TAG", "TEXT"+t);
                            }
                        //}
                        road.add(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "HTML_INSTRUCTION":
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing i routes */
                    jLegs = ((JSONObject) jRoutes.get(position)).getJSONArray("legs");

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++)
                    {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        ArrayList<String> json = new ArrayList<String>();
                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++)
                        {
                            String t;
                            t = ((JSONObject)jSteps.get(k)).getString("html_instructions");
                            String noHTMLString = t.replaceAll("\\<.*?>", "");
                            json.add(noHTMLString);
                            Log.d("TAG", "k is" + String.valueOf(k));
                            Log.d("TAG", "instruction" + noHTMLString);
                        }
                        road.add(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "STEPS_DIS":
                try {
                    jRoutes = jObject.getJSONArray("routes");

                    /** Traversing i routes */
                    jLegs = ((JSONObject) jRoutes.get(position)).getJSONArray("legs");

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++)
                    {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        ArrayList<String> json = new ArrayList<String>();
                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String t;
                            t = ((JSONObject)jSteps.get(k)).getJSONObject("distance").getString("text");
                            json.add(t);
                            Log.d("TAG", "k is" + String.valueOf(k));
                            Log.d("TAG", "distance" + t);
                        }
                        road.add(json);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return road;
    }

    private String convert(int num,String match)
    {
        int n=num;
        String par="";
        int hr;
        int min;
        int kilo;
        switch(match)
        {
            case "dur":
                hr=n/3600;
                n %=3600;
                min=n/60;
                n %= 60;
                if (hr!=0)
                {
                    par= par+hr+"小時";
                }
                if (min!=0)
                {
                    par=par+min+"分";
                }
                if (n!=0)
                {
                    par=par+n+"秒";
                }
                break;
            case "dis":
                kilo=n/1000;
                n%=1000;
                if (kilo!=0)
                {
                    par= par+kilo+"公里";
                }
                if (n!=0)
                {
                    par=par+n+"公尺";
                }

                break;
            default:
                break;
        }
        return par;
    }
}
