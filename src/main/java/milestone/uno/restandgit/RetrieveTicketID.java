package milestone.uno.restandgit;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONPointerException;
import writer.PropertiesWriter;

public class RetrieveTicketID {

    public static final Logger LOGGER = Logger.getLogger(RetrieveTicketID.class.getName());

    private static String path = "";
    public static String proj = "";


    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         *
         * 0 --> BOOKKEEPER
         * 1 --> TAJO
         */
        ////////////////carico i dati da config.properties
        String prf = new PropertiesWriter().determinePrefix(value);

        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config" + prf + ".properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            path = prop.getProperty("BugTicketFromJira");
            proj = prop.getProperty("projectName");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        ///////////////////////////////////////
    }


    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        JSONArray json;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String jsonText = readAll(rd);
            json = new JSONArray(jsonText);
            return json;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        return null;
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        JSONObject json;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            return json;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
        return null;
    }

    public void retreive() {

        /**
         * Questo metodo restituisce un file CSV contenente tutti i ticket Bug restituiti dalla
         * query.
         *
         * out --> BugTicket.csv
         */

        LOGGER.info("Scrivo i file su CSV!");
        File file = new File(path);
        List<String[]> data = new ArrayList<>();

        Integer j = 0;
        Integer i = 0;
        Integer total = 1;
        //Get JSON API for closed bugs w/ AV in the project
        do {
            //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
            j = i + 1000;

            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + proj + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    + i.toString() + "&maxResults=" + j.toString();


            try {
                JSONObject json = readJsonFromUrl(url);
                assert json != null;
                JSONArray issues = json.getJSONArray("issues");
                total = json.getInt("total");
                for (; i < total && i < j; i++) {
                    //Iterate through each bug
                    String key = issues.getJSONObject(i % 1000).get("key").toString();
                    //Aggiungo i dati alla lista.
                    data.add(new String[]{key});
                }
            } catch (NullPointerException | IOException | JSONPointerException e) {
                LOGGER.log(Level.WARNING, String.valueOf(e));
            }

            try (FileWriter outputfile = new FileWriter(file); CSVWriter writer = new CSVWriter(outputfile)) {
                //Scrivo i Dati ricavati sul file csv
                writer.writeAll(data);
                writer.flush();
            } catch (IOException | JSONException e) {
                LOGGER.log(Level.WARNING, String.valueOf(e));
            }

            LOGGER.info("Fatto!");

        } while (i < total);
    }

    public static void main(String[] args) {

        /**
         * La classe crea un file CSV e scrive all'interno tutti i ticket del progetto che rispettano la query:
         * Type == “Bug” AND (status == “Closed” OR status == “Resolved”) AND
         * Resolution == “Fixed”
         * I ticket vengono presi da Jira sfruttando le REST API.
         */
        importResources(1);
        new RetrieveTicketID().retreive();

    }
}



