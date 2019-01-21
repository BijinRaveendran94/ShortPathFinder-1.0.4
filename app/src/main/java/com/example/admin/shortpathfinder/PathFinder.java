package com.example.admin.shortpathfinder;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PathFinder extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_finder);
    }

    public List<JSONObject> getWaypointsForRoom(String json, JSONObject room) {
        List<JSONObject> arrayList = new ArrayList<JSONObject>();
        try {

            JSONObject metaData = new JSONObject(json);
            JSONArray buildings = metaData.getJSONArray("Countries").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("Cities").getJSONObject(0).getJSONArray("Buildings");
            JSONArray getCampusWaypoints = metaData.getJSONArray("Countries").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("Cities").getJSONObject(0).getJSONArray("CampusWaypoints");
            JSONArray Floors = null;
            for (int i = 0; i < buildings.length(); i++) {

                try {
                    Floors = buildings.getJSONObject(i).getJSONArray("Floors");

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                } catch (ClassCastException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int j = 0; j < Floors.length(); j++) {

                    JSONArray Waypoints = Floors.getJSONObject(j).getJSONArray("Waypoints");
                    JSONArray Rooms = Floors.getJSONObject(j).getJSONArray("Rooms");

                    for (int k = 0; k < Rooms.length(); k++) {
                        String RoomName = Rooms.getJSONObject(k).getString("RoomName");
                        String RoomNumber = Rooms.getJSONObject(k).getString("RoomNumber");
                        if (RoomName.equalsIgnoreCase(room.getString("RoomName")) && RoomNumber.equalsIgnoreCase(room.getString("RoomNumber"))) {

                            List<JSONObject> jsonObject = new ArrayList<JSONObject>();

                            for (int m = 0; m < Waypoints.length(); m++) {
                                jsonObject.add(Waypoints.getJSONObject(m));
                            }
                            for (int n = 0; n < getCampusWaypoints.length(); n++) {
                                jsonObject.add(getCampusWaypoints.getJSONObject(n));
                            }
                            arrayList = jsonObject;
                            break;
                        }
                    }
                }
            }

            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (ClassCastException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return arrayList;
    }

    private LatLng getNearestNodeFor(LatLng location, ArrayList<JSONObject> nodesHash) {
        try {
            LatLng selectedNode = null;

            double distance = 0;

            for (int v = 0; v < nodesHash.size(); v++) {
                JSONObject node = nodesHash.get(v);
                Iterator<String> keys = node.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String Pointb = key.replace("{", " ");
                    String PointBb = Pointb.replace("}", " ");
                    String[] latlongb = PointBb.split(",");
                    double latitude = Double.parseDouble(latlongb[0]);
                    double longitude = Double.parseDouble(latlongb[1]);
                    LatLng LatLng = new LatLng(latitude, longitude);

                    double dist = distance(location.latitude, location.longitude, LatLng.latitude, LatLng.longitude);

                    if (dist < distance || distance == 0) {
                        distance = dist;
                        selectedNode = LatLng;
                    }
                }
            }
            return selectedNode;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public double distance(double lat_a, double lng_a, double lat_b, double lng_b) {

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).doubleValue();
    }



    private ArrayList<JSONObject> getNodesForWaypoints(List<JSONObject> Waypoints) {
        ArrayList<JSONObject> node = new ArrayList<JSONObject>();
        try {
            for (int i = 0; i < Waypoints.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = Waypoints.get(i);

                String PointA = jsonObject.getString("PointA");
                String PointB = jsonObject.getString("PointB");

                String ah = null;
                String bh = null;
                if (node.size() != 0) {
                    Boolean b = false;
                    Boolean a = false;

                    for (int j = 0; j < node.size(); j++) {
                        if (node.get(j).has(PointA)) {
                            a = true;
                            ah = Integer.toString(j);

                            break;
                        }
                    }

                    for (int j = 0; j < node.size(); j++) {

                        if (node.get(j).has(PointB)) {
                            b = true;
                            bh = Integer.toString(j);
                            break;
                        }
                    }
                    if (a) {
                        for (int j = 0; j < node.size(); j++) {

                            if (ah == Integer.toString(j)) {
                                JSONArray jsonArray = node.get(j).getJSONArray(PointA);

                                ArrayList<Object> jsonData = new ArrayList<Object>();
                                if (jsonArray.length() > 0) {

                                    for (int k = 0; k < jsonArray.length(); k++) {
                                        jsonData.add(jsonArray.get(k));
                                    }
                                    if (!jsonData.contains(jsonObject)) {
                                        jsonArray.put(jsonObject);
                                    }
                                    break;
                                } else {
                                    jsonArray.put(jsonObject);
                                }

                            }
                        }
                    } else {
                        for (int j = 0; j < node.size(); j++) {
                            if (node.get(j).has(PointA)) {
                                a = true;
                                ah = Integer.toString(j);

                                break;


                            }

                        }

                        if (a) {
                            for (int j = 0; j < node.size(); j++) {

                                if (ah == Integer.toString(j)) {
                                    JSONArray jsonArray = node.get(j).getJSONArray(PointA);

                                    ArrayList<Object> jsonData = new ArrayList<Object>();
                                    if (jsonArray.length() > 0) {

                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            jsonData.add(jsonArray.get(k));
                                        }
                                        if (!jsonData.contains(jsonObject)) {
                                            jsonArray.put(jsonObject);
                                        }
                                        break;
                                    } else {
                                        jsonArray.put(jsonObject);
                                    }

                                }
                            }
                        } else {
                            JSONObject json = new JSONObject();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                json.put(PointA, new JSONArray(new Object[]{jsonObject}));
                            }
                            node.add(json);
                        }

                    }


                    if (b) {
                        for (int j = 0; j < node.size(); j++) {

                            if (bh == Integer.toString(j)) {
                                JSONArray jsonArray = node.get(j).getJSONArray(PointB);

                                ArrayList<Object> jsonData = new ArrayList<Object>();
                                if (jsonArray.length() > 0) {

                                    for (int k = 0; k < jsonArray.length(); k++) {
                                        jsonData.add(jsonArray.get(k));
                                    }
                                    if (!jsonData.contains(jsonObject)) {
                                        jsonArray.put(jsonObject);
                                    }
                                    break;
                                } else {
                                    jsonArray.put(jsonObject);
                                }


                            }

                        }
                    } else {

                        for (int j = 0; j < node.size(); j++) {
                            if (node.get(j).has(PointA)) {
                                a = true;
                                ah = Integer.toString(j);

                                break;


                            }

                        }

                        if (b) {
                            for (int j = 0; j < node.size(); j++) {

                                if (bh == Integer.toString(j)) {
                                    JSONArray jsonArray = node.get(j).getJSONArray(PointB);

                                    ArrayList<Object> jsonData = new ArrayList<Object>();
                                    if (jsonArray.length() > 0) {

                                        for (int k = 0; k < jsonArray.length(); k++) {
                                            jsonData.add(jsonArray.get(k));
                                        }
                                        if (!jsonData.contains(jsonObject)) {
                                            jsonArray.put(jsonObject);
                                        }
                                        break;
                                    } else {
                                        jsonArray.put(jsonObject);
                                    }

                                }
                            }
                        } else {
                            JSONObject json = new JSONObject();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                json.put(PointB, new JSONArray(new Object[]{jsonObject}));
                            }
                            node.add(json);
                        }

                    }


                } else {
                    JSONObject json = new JSONObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        json.put(PointA, new JSONArray(new Object[]{jsonObject}));
                    }
                    node.add(json);


                    JSONObject js = new JSONObject();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        js.put(PointB, new JSONArray(new Object[]{jsonObject}));
                    }
                    node.add(js);

                }

            }
            return node;
        } catch (JSONException e) {
            e.printStackTrace();


        } catch (ClassCastException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return node;
    }

}

