package milestone.uno.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionDivisor {

    /**
     * Lo scopo di questa classe è quello di andare a prendere le versioni delle classi ricavate attraverso
     * la rest API eseguita su jira, in modo tale da andare a ricavare i valori di OV,IV ed FV per poi
     * sfruttarli nel calcolo di P.
     */

    private static final Logger LOGGER = Logger.getLogger(VersionDivisor.class.getName());

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    static String avPath = "";
    static String bugPath = "";
    static String assAVB = "";
    static String outRM = "";
    static String version = "";
    static String varCal = "";
    static int lenght;



    /**
     * Variabili globali necessarie al metodo determineVar()
     */
    Integer index = 0;
    String[] values = {"", "", ""}; //OV, FV, IV



    public static void main(String[] args) {

        importResources(1);
        new VersionDivisor().avTicketOnly();
        new VersionDivisor().determineVar();

    }

    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            if (value == 0) {
                avPath = prop.getProperty("AVpath");
                bugPath = prop.getProperty("BugTicketFromJira");
                outRM = prop.getProperty("BugTicketAV");
                version = prop.getProperty("versionInfoBOOK");
                varCal = prop.getProperty("variables");
                assAVB = prop.getProperty("AssAB");
                lenght = Integer.parseInt(prop.getProperty("nameLenghtBOOK"));

            }
            if (value == 1) {
                avPath = prop.getProperty("AVpathTAJO");
                bugPath = prop.getProperty("BugTicketFromJiraTAJO");
                outRM = prop.getProperty("BugTicketAVTAJO");
                version = prop.getProperty("versionInfoTAJO");
                varCal = prop.getProperty("variablesTAJO");
                assAVB = prop.getProperty("AssABTAJO");
                lenght = Integer.parseInt(prop.getProperty("nameLenghtTAJO"));

            }


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    private List<String[]> adjustingList(List<String[]> list, List<String[]> list2) {
        //association, bugAV
        /**
         * Ritorna una lista contenente la data del ticket, il ticket stesso e
         * Le FIXVERSION, AFFECTED VERSION del ticket prese dal file BugAV.
         * es --> 2013-04-04BOOKKEEPER-5964.2.24.2.0
         */
        List<String[]> ret = new ArrayList<>();

        for (String[] str : list) {
            for (String[] str2 : list2) {
                if (str[3].equals(str2[0])) {
                    ret.add(new String[]{str[0], str[3], str2[1], str2[2]}); //data, ticket, versions[]
                }
            }
        }

        return ret;
    }

    private void determineVar() {
        /**
         * Attraverso questo metodo andiamo a sfruttare l'output ottenuto tramite il metodo removeOthers
         * e tenendo conto del file CSV nel quale abbiamo tutte le versione del progetto, andiamo a trasformare
         * le versioni del file BugAV in variabili OV,IV ed FV da sfruttare nel calcolo di P.
         *
         * Per la data del ticket sfruttiamo il file CSV AssociationAVBlame, andando a prendere solamente i
         * ticket che risultano dall'intersezione tra i due file.
         *
         *
         * (Necessariamente FV > OV && FV > IV)
         * in particolare avremo un output del tipo --> "Ticket","FV","IV","OV"
         *
         * Out: var.csv
         */

        List<String[]> bugAV;
        List<String[]> versionInfo;
        List<String[]> ver2;
        List<String[]> association;

        List<String[]> out;

        Date dateStart;
        Date dateEnd;
        Date dateTicket;

        /**
         *   FV-->Lo prendo direttamente dal file
         *   IV-->Rappresenta la più vecchia versione tra le AV di un determinato ticket.
         *   OV-->Lo determino attraverso la "traduzione" della data del ticket in versione.
         */

        HashMap<Integer, String> hashMap = new HashMap<>();

        try (FileReader fileReader = new FileReader(outRM);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(version);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileReader fileReader2 = new FileReader(assAVB);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             FileWriter fileWriter = new FileWriter(varCal);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {


            bugAV = csvReader.readAll();
            versionInfo = csvReader1.readAll();
            ver2 = versionInfo.subList(1, versionInfo.size()); // salto la prima riga
            association = csvReader2.readAll();

            out = adjustingList(association, bugAV); //sfrutto una nuova lista contente il ticket con la data
            //e le versioni di fix version ed affected version in modo da sfruttarla per determinare IV,OV ed FV.


            //out --> Data, Ticket, FV, IV
            for (String[] str : out) {
                for (String[] strings : ver2) {

                    dateEnd = formatter.parse(strings[4]);
                    dateStart = formatter.parse(strings[3]);
                    dateTicket = format.parse(str[0]);

                    /**
                     * Il controllo sulla data è necessario per determinare in quale verisione ci troviamo.
                     */

                    returnValues(dateStart, dateEnd, dateTicket, strings[0], strings[2], str[2], str[3]);


                    if (!values[0].equals("") && !values[1].equals("") && !values[2].equals("")) {
                        /**
                         * Controllo sulla qualità dei dati. In particolare accettiamo solamente dati
                         * dove FV > IV, poiché non è possibile che venga rilasciato un Fix ancora prima
                         * di aver riscontrato il BUG
                         */
                        if (Integer.parseInt(values[1]) > Integer.parseInt(values[2])) {
                            addOnHashmap(values[0], str[1], values[1], values[2], str[0], hashMap); // da testare.

                        }
                        values = new String[]{"", "", ""};
                    }
                }
            }

            //scrivo nel csv finale
            for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getValue().substring(0, 1), entry.getValue().substring(1, lenght), entry.getValue().substring(lenght, lenght + 1), entry.getValue().substring(lenght + 1, lenght + 2)
                        , entry.getValue().substring(lenght + 2)});
            }

            index = 0;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void addOnHashmap(String str1, String str2, String str3, String str4, String str5, HashMap<Integer, String> hashMap) {

        if (!hashMap.containsValue(str1 + str2 + str3 + str4 + str5)) {
            hashMap.put(index, str1 + str2 + str3 + str4 + str5);
        } else {
            index++;
        }

    }


    private String[] returnValues(Date dateStart, Date dateEnd, Date date, String str, String versionName, String fvVersion, String ivVersion) {

        /**
         * Il controllo sulla data è necessario per determinare in quale verisione ci troviamo.
         */

        if (date.after(dateStart) && date.before(dateEnd)) {
            values[0] = str; //OV
        }
        if (versionName.equals(fvVersion)) {
            values[1] = str; //FV
        }
        if (versionName.equals(ivVersion)) {
            values[2] = str; //IV
        }
        return values;
    }


    private void avTicketOnly() {

        /**
         * Questo metodo confronta il CSV ottenuto attraverso la query eseguita su jira, che prende tutti i
         * ticket che possiedono sia AV che FV con il CSV ottenuto attraverso la classe RetrieveTicketID, che
         * invece esegue una query del tipo:
         * Type == “Bug” AND (status == “Closed” OR status == “Resolved”) AND Resolution == “Fixed”
         * Attraverso questa intersezione quindi ricaviamo i soli ticket di nostro interesse che hanno AF e FV.
         *
         * out: BugAV.csv
         */

        List<String[]> bugTicket = new ArrayList<>();
        List<String[]> av = new ArrayList<>();

        List<String[]> out = new ArrayList<>();

        try (FileReader fileReader = new FileReader(avPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(bugPath);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileWriter fileWriter = new FileWriter(outRM);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            bugTicket = csvReader1.readAll();
            av = csvReader.readAll();

            out.add(new String[]{"Ticket", "FixVersion", "AffectedVersion"});

            for (String[] str : bugTicket) {
                for (String[] str2 : av) {

                    if (str[0].equals(str2[1])) {
                        out.add(new String[]{str2[1], str2[2], str2[3]});
                    }
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(out);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
