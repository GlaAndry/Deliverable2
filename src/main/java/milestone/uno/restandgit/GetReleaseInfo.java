package milestone.uno.restandgit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    private static Map<LocalDateTime, String> releaseNames;
    private static Map<LocalDateTime, String> releaseID;
    private static List<LocalDateTime> releases;

    public static void main(String[] args) throws IOException {

        /**
         * out --> ProjVersionInfo.csv
         */

        String projName ="TAJO"; //Change in "BOOKKEEPER" for other project.
        //Fills the arraylist with releases dates and orders them
        //Ignores releases with missing dates
        releases = new ArrayList<>();
        Integer i;
        String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
        JSONObject json = new JSONMethods().readJsonFromUrl(url);
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
        try (FileWriter fileWriter = new FileWriter(outname)){

            fileWriter.append("Index,Version ID,Version Name,DateStart,DateEnd");
            fileWriter.append("\n");

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




}
