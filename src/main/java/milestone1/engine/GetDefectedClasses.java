package milestone1.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetDefectedClasses {

    /**
     * Con questa classe vogliamo creare una associazione tra il blame fatto e le release del progetto
     * andando a confrontare le date di cui disponiamo e quindi andando ad associare ad ogni classe una
     * determinata release rispetto a quando questa è stata modificata.
     */

    private static final Logger LOGGER = Logger.getLogger(DownloadCommit.class.getName());


    static String versionInfo = "";
    static String blameFinal = "";
    static String buggyPath = "";
    static String classPath= "";

    public static void main(String[] args) throws IOException {

        importResources();
        new GetDefectedClasses().determineDefectiveFromBlame();
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

            versionInfo = prop.getProperty("versionInfoBOOK");
            blameFinal = prop.getProperty("blameFinal");
            buggyPath = prop.getProperty("buggyPath");
            classPath = prop.getProperty("classesPath");

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void removeDuplicatesFromDevective(){

    }

    private void determineDefectiveFromBlame(){

        /**
         * Con questo metodo andiamo a verificare se una classe è buggy per una determinata release.
         * In particolare Verifico che la data del blame sia antecedente alla data di una determinata release,
         * in quel caso posso dire che la classe è buggy per quella release.
         * Il metodo restituisce un file CSV, all'interno del quale abbiamo l'index che determina il numero di
         * release, il nome della classe, e se questa sia o meno buggy.
         */



        try (FileReader version = new FileReader(versionInfo);
             CSVReader csvReader = new CSVReader(version);
             FileReader blameF = new FileReader(blameFinal);
             CSVReader csvReader1 = new CSVReader(blameF);
             FileReader classes = new FileReader(classPath);
             CSVReader csvReader2 = new CSVReader(classes);
             FileWriter fileWriter = new FileWriter(buggyPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)){

            List<String[]> vers;
            List<String[]> blm;

            vers = csvReader.readAll();
            blm = csvReader1.readAll();

            List<String[]> realVers = vers.subList(1,vers.size());
            List<String[]> cls = csvReader2.readAll();


            List<String[]> results = new ArrayList<>();
            results.add(new String[]{"Index","Version","ClassName","Buggy"});

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            /**
             * Andiamo a controllare classe per classe in base alla versione. Ci sono molti file che si ripetono
             * all'interno di BlameFinal.csv a causa della differenziazione dei commit. Possiamo comunque dire che,
             * se almeno per una delle ripetizioni abbiamo una difettività, allora quella classe possiamo considerarla
             * difettiva, in quanto nel ciclo andiamo a considerare tutti i commit.
             */

            for(String[] versions : realVers){
                for(String[] clss : cls){
                    for (String[] blmDate : blm){
                        if(clss[2].equals(blmDate[1])){

                            Date dateV = formatter.parse(versions[3]);
                            Date dateB = format.parse(blmDate[0]);

                            if(dateB.before(dateV)){
                                results.add(new String[] {versions[0],versions[2],blmDate[1], "SI"});
                            } else {
                                results.add(new String[] {versions[0],versions[2],blmDate[1], "NO"});
                            }

                        }
                    }
                }
            }


            /*

            for(String[] versions : realVers){
                for(String[] blmDate : blm){

                    Date dateV = formatter.parse(versions[3]);
                    Date dateB = format.parse(blmDate[0]);

                    if(dateB.before(dateV)){
                        results.add(new String[] {versions[0],versions[2],blmDate[1], "SI"});
                    } else {
                        results.add(new String[] {versions[0],versions[2],blmDate[1], "NO"});
                    }
                }
            }

             */

            csvWriter.writeAll(results);
            csvWriter.flush();




        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    private void determineDefictiveFromJiraTicket(){

        /**
         * Attraverso questo metodo andiamo a prendere il CSV che abbiamo ricavato dal metodo determineDefectiveFromBlame
         * e andiamo a fare una ulteriore verifica con i dati ricavati da Jira. In questo modo siamo certi che,
         * per i dati che abbiamo preso da jira, allora è confermata la difettività della classe.
         */
    }



}
