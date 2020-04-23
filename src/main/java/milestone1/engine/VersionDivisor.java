package milestone1.engine;

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

    static String avPath = "";
    static String bugPath = "";
    static String assAVB = "";
    static String outRM = "";
    static String version = "";
    static String varCal = "";


    public static void main(String[] args){

        importResources();
        //new VersionDivisor().avTicketOnly();
        new VersionDivisor().determineVar();

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

            avPath = prop.getProperty("AVpath");
            bugPath = prop.getProperty("BugTicketFromJira");
            outRM = prop.getProperty("BugTicketAV");
            version = prop.getProperty("versionInfoBOOK");
            varCal = prop.getProperty("variables");
            assAVB = prop.getProperty("AssAB");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
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



        List<String[]> bugAV = new ArrayList<>();
        List<String[]> versionInfo = new ArrayList<>();
        List<String[]> association = new ArrayList<>();

        List<String[]> out = new ArrayList<>();

        try(FileReader fileReader = new FileReader(outRM);
            CSVReader csvReader = new CSVReader(fileReader);
            FileReader fileReader1 = new FileReader(version);
            CSVReader csvReader1 = new CSVReader(fileReader1);
            FileReader fileReader2 = new FileReader(assAVB);
            CSVReader csvReader2 = new CSVReader(fileReader2);
            FileWriter fileWriter = new FileWriter(varCal);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){


            bugAV = csvReader.readAll();
            versionInfo = csvReader1.readAll();
            association = csvReader2.readAll();

            int counter = 0;

            String fv = ""; //Lo prendo direttamente dal file
            String iv = ""; //Rappresenta la più vecchia versione tra le AV di un determinato ticket.
            String ov = ""; //Lo determino attraverso la "traduzione" della data del ticket in versione.

            HashMap<Integer, String> hashMap = new HashMap<>();
            Integer index = 0;

            for(String[] str : association){
                for(String[] str2 : bugAV){
                        if(str[3].equals(str2[0])){
                            out.add(new String[]{str[0],str[3],str2[1],str2[2]});
                        }
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            for(String[] str : out){
                System.out.println(str[0]+str[1]+str[2]+str[3]); //testing
            }


            /**
             * Il contatore è necessario per "saltare" la prima riga del File VersionInfo.csv
             */

            int x = 0; //Intero necessario per prendere la versione precedente.

            for (String[] str : out) {
                for(int i = 0; i < versionInfo.size(); i++){

                    if(counter > 0){

                        Date dateV = formatter.parse(versionInfo.get(i)[3]);
                        Date dateB = format.parse(str[0]);

                        /**
                         * Il controllo sulla data è necessario per determinare in quale verisione ci troviamo.
                         * Andiamo a prendere il la versione e togliamo -1 altrimenti per OV andremmo a prendere
                         * la versione successiva!
                         */

                        if(dateV.before(dateB)){
                            x = i -1;
                            if(x == 0){
                                ov = "0";
                            } else {
                                ov = versionInfo.get(x)[0];
                            }

                        } else {
                            x = i -1;
                            if(x == 0){
                                ov = "0";
                            } else {
                                ov = versionInfo.get(x)[0];
                            }
                        }
                    }
                    counter++;

                    if(versionInfo.get(i)[2].equals(str[2])){
                        fv = versionInfo.get(i)[0];
                    }
                    if(versionInfo.get(i)[2].equals(str[3])){
                        iv = versionInfo.get(i)[0];
                    }
                    if(!fv.equals("") && !iv.equals("") && !ov.equals("")){

                        /**
                         * Controllo sulla qualità dei dati. In particolare accettiamo solamente dati
                         * dove FV > IV, poiché non è possibile che venga rilasciato un Fix ancora prima
                         * di aver riscontrato il BUG
                         */

                        if(Integer.parseInt(fv) > Integer.parseInt(iv)){
                            if (!hashMap.containsValue(ov+str[1]+fv+iv)) {
                                hashMap.put(index, ov+str[1]+fv+iv);
                            } else {
                                index++;
                            }
                        }

                        ov = "";
                        fv = "";
                        iv = "";
                    }
                }
                counter = 0;

            }

            //scrivo nel csv finale
            for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                csvWriter.writeNext(new String[]{entry.getValue().substring(0,1), entry.getValue().substring(1,15), entry.getValue().substring(15,16), entry.getValue().substring(16)});
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void avTicketOnly(){

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

        try(FileReader fileReader = new FileReader(avPath);
            CSVReader csvReader = new CSVReader(fileReader);
            FileReader fileReader1 = new FileReader(bugPath);
            CSVReader csvReader1 = new CSVReader(fileReader1);
            FileWriter fileWriter = new FileWriter(outRM);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){

            bugTicket = csvReader1.readAll();
            av = csvReader.readAll();

            out.add(new String[]{"Ticket","FixVersion","AffectedVersion"});

            for(String[] str : bugTicket){
                for(String[] str2 : av){

                    if(str[0].equals(str2[1])){
                        out.add(new String[] {str2[1],str2[2],str2[3]});
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
