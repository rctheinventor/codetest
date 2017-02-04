import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by rchase on 10/3/2015.
 */
public class Torn2Pieces {

    public static HashMap<String, StationInfo> stationMap = new HashMap<>();

    public static void main(String[] args) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        Integer numLines = 0;
        try {
            numLines = Integer.parseInt(bf.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < (numLines); i++) {
            String word = "";
            try {
                word = bf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            StationInfo info = new StationInfo(word);
        }

        repairStationMap();

        try {
            StringTokenizer route = new StringTokenizer(bf.readLine());
            String startStation = route.nextToken();
            String endStation = route.nextToken();
            findRoute(startStation, endStation);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void repairStationMap() {
        Collection<StationInfo> stations = stationMap.values();
        for (StationInfo station : stations) {
            if ((station.prevStation == null) && (station.prevName != null)) {
                StationInfo conn1 = stationMap.get(station.prevName);
                if (conn1 != null) {
                    station.prevStation = conn1;
                    System.err.println("Repairing station : " + station + " with route to " + station.prevName);
                }
            }

            if ((station.nextStation == null) && (station.nextName != null)) {
                StationInfo conn2 = stationMap.get(station.nextName);
                if (conn2 != null) {
                    station.nextStation = conn2;
                    System.err.println("Repairing station : " + station + " with route to " + station.nextName);
                }
            }
        }
    }

    public static void findRoute(String start, String end) {
        StationInfo currentStation = stationMap.get(start);
        String route = new String(start);
        String previousName = null;
        StationInfo previousStation = null;
        String nextName = currentStation.nextName;
        StationInfo nextStation = stationMap.get(nextName);

        // first, go foreward
        while (currentStation != null) {

            if ((nextName != null) && !nextName.equals(previousName)) {
                System.err.println("Adding " + nextName + " to the route");
                route = route + " " + nextName;
                // have we arrived at the end?
                if (nextName.equals(end)) {
                    System.err.println("We have arrived at the end");
                    System.out.println(route);
                    return;
                }
                previousStation = currentStation;
                previousName = currentStation.stationName;
                currentStation = stationMap.get(nextName);
                nextName = currentStation.nextName;
                nextStation = stationMap.get(nextName);
            }
            else if ((nextName != null) && nextName.equals(previousName)) {
                // looking at the reverse link, need to check the next one
                nextName = currentStation.prevName;
                nextStation = stationMap.get(nextName);
            }

            System.err.println("nextName = " + nextName);


        }

        // now try going back
        currentStation = stationMap.get(start);
         route = new String(start);
         previousName = null;
         previousStation = null;
         nextName = currentStation.prevName;
         nextStation = stationMap.get(nextName);

        while (currentStation != null) {

            if ((nextName != null) && !nextName.equals(previousName)) {
                System.err.println("Adding " + nextName + " to the route");
                route = route + " " + nextName;
                // have we arrived at the end?
                if (nextName.equals(end)) {
                    System.err.println("We have arrived at the end");
                    System.out.println(route);
                    return;
                }
                previousStation = currentStation;
                previousName = currentStation.stationName;
                currentStation = stationMap.get(nextName);
                nextName = currentStation.nextName;
                nextStation = stationMap.get(nextName);
            }
            else if ((nextName != null) && nextName.equals(previousName)) {
                // looking at the reverse link, need to check the next one
                nextName = currentStation.prevName;
                nextStation = stationMap.get(nextName);
            }

            System.err.println("nextName = " + nextName);


        }

        System.out.println("no route found");

    }

}

class StationInfo {
    String prevName;
    String nextName;
    String stationName;
    List<StationInfo> connectedStations = new ArrayList<StationInfo>();
    StationInfo prevStation;
    StationInfo nextStation;
    boolean visited = false;

    public StationInfo(String line) {
        StringTokenizer st = new StringTokenizer(line);
        stationName = st.nextToken();
        nextName = st.nextToken();
        prevName = null;
        if (st.hasMoreTokens()) {
            prevName = st.nextToken();
        }
        StationInfo conn1 = Torn2Pieces.stationMap.get(nextName);
        if (conn1 != null) {
            nextStation = conn1;
        }

        StationInfo conn2 = Torn2Pieces.stationMap.get(prevName);
        if (conn2 != null) {
            prevStation = conn2;
        }

        Torn2Pieces.stationMap.put(stationName, this);
        System.out.println("Adding station : " + this);

    }

    @Override
    public String toString() {
        return "StationInfo{" +
                "stationName='" + stationName + '\'' +
                ", nextName='" + nextName + '\'' +
                ", prevName='" + prevName + '\'' +
                '}';
    }
}