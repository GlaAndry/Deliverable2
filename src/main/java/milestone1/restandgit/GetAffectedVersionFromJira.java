package milestone1.restandgit;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetAffectedVersionFromJira {

    /**
     * La classe restituisce un file CSV all'interno del quale si hanno una coppia Ticket,AV
     * per la quale ad ogni ticket è assegnata una affected version se esiste, altrimenti viene
     * riportata la stringa "Non Pervenuto".
     */


    private static final Logger LOGGER = Logger.getLogger(GetAffectedVersionFromJira.class.getName());
    static String AVpath = "";


    public static void main(String[] args) throws IOException, JSONException {

        importResources();
        //new GetAffectedVersionFromJira().retrieveFromJira();
        new GetAffectedVersionFromJira().retriveOnlyFixFromJira();
    }

    private static void importResources(){
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            AVpath = prop.getProperty("AVpath");

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void retriveOnlyFixFromJira() throws IOException {

        /**
         * La limitazione dei 50 elementi è state eliminata attraverso la stringa
         * ",created&startAt=0&maxResults=1000" presente all'interno dell'url
         *
         * out: FV.csv
         */

        List<String[]> lista = new ArrayList<>();

        String projName = "BOOKKEEPER";

        //String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
        String urlAffectedVersion = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20=%20BOOKKEEPER%20AND%20" +
                "fixVersion%20!=%20null%20ORDER%20BY%20fixVersion%20ASC"
                +",created&startAt=0&maxResults=1000";

        JSONObject json = readJsonFromUrl(urlAffectedVersion);
        JSONArray issues = json.getJSONArray("issues");

        int z,h;
        Integer counter = 0;

        lista.add(new String[]{"Index", "Ticket", "FixVersion"});

        for (z = 0;z < issues.length(); z++) {

            String ticket = issues.getJSONObject(z).get("key").toString();
            /**
             * Questi ulteriri array JSON sono stati necessari per accedere alle sottoliste presenti all'interno
             * di issues, cioè filed, che contiene sia FixVersions che version per determinare AV e FV.
             */
            JSONArray FV = issues.getJSONObject(z).getJSONObject("fields").getJSONArray("fixVersions");
            //JSONArray AV = issues.getJSONObject(z).getJSONObject("fields").getJSONArray("versions");

            String fixVersion = "";
            //String affectedVersion = "";


            //affectedVersion = AV.getJSONObject(0).get("name").toString();

            /**
             * Il ciclo è necessario in quanto alcuni ticket possiedono molteplici fixed-version. In questo
             * modo li includiamo all'interno del file e manteniamo l'index corretto sfruttando la variabile
             * counter.
             */

            for (h = 0; h < FV.length(); h++) {
                counter++;
                fixVersion = FV.getJSONObject(h).get("name").toString();
                lista.add(new String[]{counter.toString(), ticket, fixVersion});

            }
        }
        try (FileWriter fileWriter = new FileWriter("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\FV.csv");
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeAll(lista);
        }

    }

    private void retrieveFromJira() throws IOException {

        /**
         * La limitazione dei 50 elementi è state eliminata attraverso la stringa
         * ",created&startAt=0&maxResults=1000" presente all'interno dell'url
         *
         * out: AV.csv
         */

        List<String[]> lista = new ArrayList<>();

        String projName = "BOOKKEEPER";

        //String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
        String urlAffectedVersion = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20=%20BOOKKEEPER%20AND%20" +
                "fixVersion%20!=%20null%20AND%20affectedVersion%20!=%20null%20ORDER%20BY%20fixVersion,affectedVersion%20ASC"
                +",created&startAt=0&maxResults=1000";

        JSONObject json = readJsonFromUrl(urlAffectedVersion);
        JSONArray issues = json.getJSONArray("issues");

        int z,h;
        Integer counter = 0;

        lista.add(new String[]{"Index", "Ticket", "FixVersion", "AffectedVersion"});

        for (z = 0;z < issues.length(); z++) {

            String ticket = issues.getJSONObject(z).get("key").toString();
            /**
             * Questi ulteriri array JSON sono stati necessari per accedere alle sottoliste presenti all'interno
             * di issues, cioè filed, che contiene sia FixVersions che version per determinare AV e FV.
             */
            JSONArray FV = issues.getJSONObject(z).getJSONObject("fields").getJSONArray("fixVersions");
            JSONArray AV = issues.getJSONObject(z).getJSONObject("fields").getJSONArray("versions");

            String fixVersion = "";
            String affectedVersion = "";


            affectedVersion = AV.getJSONObject(0).get("name").toString();

            /**
             * Il ciclo è necessario in quanto alcuni ticket possiedono molteplici fixed-version. In questo
             * modo li includiamo all'interno del file e manteniamo l'index corretto sfruttando la variabile
             * counter.
             */

            for (h = 0; h < FV.length(); h++) {
                counter++;
                fixVersion = FV.getJSONObject(h).get("name").toString();
                lista.add(new String[]{counter.toString(), ticket, fixVersion, affectedVersion});

            }
        }
        try (FileWriter fileWriter = new FileWriter(AVpath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeAll(lista);
        }
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
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
