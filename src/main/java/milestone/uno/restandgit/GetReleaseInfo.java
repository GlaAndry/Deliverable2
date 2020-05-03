package milestone.uno.restandgit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetReleaseInfo {

    /**
     * Questa classe restituisce tutte le release disponibili per il progetto che stiamo analizzando
     * andando a dividerle anche sotto il profilo temporale (quindi sappiamo quanto è durata una determinata
     * versione del progetto e quando si è passati alla versione successiva). Viene anche riportata la version ID
     * riferita alla daterminata versione del progetto che stiamo considerando.
     */

    private static final Logger LOGGER = Logger.getLogger(GetReleaseInfo.class.getName());


    public static Map<LocalDateTime, String> releaseNames;
    public static Map<LocalDateTime, String> releaseID;
    public static List<LocalDateTime> releases;
    public static Integer numVersions;

    public static void main(String[] args) throws IOException {

        /**
         *
         * out --> ProjVersionInfo.csv
         */

        String projName ="BOOKKEEPER";
        //Fills the arraylist with releases dates and orders them
        //Ignores releases with missing dates
        releases = new ArrayList<>();
        Integer i;
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
        JSONObject json = readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("versions");
        releaseNames = new HashMap<>();
        releaseID = new HashMap<> ();
        for (i = 0; i < versions.length(); i++ ) {
            String name = "";
            String id = "";
            if(versions.getJSONObject(i).has("releaseDate")) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                if (versions.getJSONObject(i).has("id"))
                    id = versions.getJSONObject(i).get("id").toString();
                addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
                        name,id);
            }
        }
        // order releases by date
        releases.sort(LocalDateTime::compareTo);

        String outname = projName + "VersionInfo.csv";
        if (releases.size() < 6)
            return;
        //FileWriter fileWriter = null;
        try (FileWriter fileWriter = new FileWriter(outname);){

            fileWriter.append("Index,Version ID,Version Name,DateStart,DateEnd");
            fileWriter.append("\n");
            numVersions = releases.size();
            for ( i = 0; i < (releases.size()/2); i++) { //Considero solamente la prima metà delle versioni.
                Integer index = i + 1;
                fileWriter.append(index.toString());
                fileWriter.append(",");
                fileWriter.append(releaseID.get(releases.get(i)));
                fileWriter.append(",");
                fileWriter.append(releaseNames.get(releases.get(i)));
                fileWriter.append(",");
                fileWriter.append(releases.get(i).toString());
                fileWriter.append(",");
                fileWriter.append(releases.get(index).toString());
                fileWriter.append("\n");
            }

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error in CSV Writer\n");
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    public static void addRelease(String strDate, String name, String id) {
        LocalDate date = LocalDate.parse(strDate);
        LocalDateTime dateTime = date.atStartOfDay();
        if (!releases.contains(dateTime))
            releases.add(dateTime);
        releaseNames.put(dateTime, name);
        releaseID.put(dateTime, id);
    }


    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


}
