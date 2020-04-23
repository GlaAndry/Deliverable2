package milestone1.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.revwalk.FIFORevQueue;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetDefectedClasses {

    /**
     * Con questa classe vogliamo creare una associazione tra il blame fatto e le release del progetto
     * andando a confrontare le date di cui disponiamo e quindi andando ad associare ad ogni classe una
     * determinata release rispetto a quando questa è stata modificata.
     */

    private static final Logger LOGGER = Logger.getLogger(GetDefectedClasses.class.getName());


    static String versionInfo = "";
    static String assBlameComm = "";
    static String assBlameAV = "";
    static String buggyPath = "";
    static String classPath= "";
    static String varP = "";
    static String bAV = "";

    public static void main(String[] args) throws IOException {

        importResources();
        //new GetDefectedClasses().determineDefectiveFromBlame();
        double p = new GetDefectedClasses().calculateProportion();
        System.out.println("Valore di P:" + p);
        new GetDefectedClasses().determineDefectiveClasses(p);
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
            assBlameComm = prop.getProperty("AssCB");
            assBlameAV = prop.getProperty("AssAB");
            buggyPath = prop.getProperty("buggyPath");
            classPath = prop.getProperty("classesPath");
            varP = prop.getProperty("variables");
            bAV = prop.getProperty("BugTicketAV");

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private double calculateProportion(){
        /**
         * Questo metodo ha lo scopo di andare a calcolare il valore proportion. In particolare
         * andiamo a sfruttare il metodo standard, imponendo quindi l'equazione:
         * P = (FV-IV) / (FV-OV) --> andiamo a calcolare P su valori che conosciamo, in modo tale
         *                          da poter andare ad eseguire una previsione su valori di cui non
         *                          possediamo i dati.
         *
         * NB: FV = Fixed Version; IV = Introduction Version e OV = Opening Version
         * IV è la più vecchia versione tra le Affected Version
         * OV trasformo la data di jira del ticket in versione.
         */

        double P = 0.0;

        List<String[]> variables = new ArrayList<>();

        Double iv = 0.0;
        Double ov = 0.0;
        Double fv = 0.0;

        try(FileReader fileReader = new FileReader(varP);
            CSVReader csvReader = new CSVReader(fileReader)){

            variables = csvReader.readAll();

            for (String[] str : variables){
                ov = Double.parseDouble(str[0]);
                fv = Double.parseDouble(str[2]);
                iv = Double.parseDouble(str[3]);

                if((fv-iv) != 0){
                    P += ((fv-iv)/(fv-ov));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return P/variables.size();
    }

    private List<String[]> indexingList(List<String[]> list, List<String[]> version){

        /**
         * Attraverso questo metodo andiamo a trasformare le versioni nei proprio index
         * eg: 4.1.1 --> 3 in accordo con il file VersionInfo.csv
         */

        List<String[]> out = new ArrayList<>();

        String fix = "";
        String aff = "";

        for(String[] str : list){
            for(String[] str2 : version) {
                if(str[4].equals(str2[2])){
                    fix = str2[0];
                }
                if(str[5].equals(str2[2])){
                    aff = str2[0];

                }
                if(!fix.equals("") && !aff.equals("")){
                    out.add(new String[] {str[0], str[2], str[3], fix, aff});
                    //Data, classe, ticket, fixV, affV
                    fix = "";
                    aff = "";
                }

            }

        }
        return out;

    }

    private List<String[]> indexingListWithDate(List<String[]> list, List<String[]> versions) throws ParseException {
        /**
         * Attraverso questo metodo andiamo a trasformare le versioni nei proprio index
         * In particolare teniamo conto anche della data del ticket, in modo tale da
         * determinare anche OV.
         * eg: 4.1.1 --> 3 in accordo con il file VersionInfo.csv
         */
        List<String[]> out = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        String fixVer2 = "";
        String ov = "";

        for(String[] str : list){
            for(String[] str2 : versions) {

                Date dateB = format.parse(str[0]);
                Date dateV = formatter.parse(str2[3]);

                if(dateB.before(dateV)){
                    ov = str2[0];
                }

                if(str[4].equals(str2[2])){
                    fixVer2 = str2[0];
                }

                if(!fixVer2.equals("") ){
                    out.add(new String[] {str[0], str[2], str[3], fixVer2, ov});
                    //Data, classe, ticket, FixVersion, OV//
                    fixVer2 = "";
                    ov = "";
                }

            }

        }

        return out;

    }

    private void determineDefectiveClasses(double p){

        /**
         * Attraverso questo metodo andiamo a determinare se una classe è defective o meno per una determinata
         * release. In particolare, se per questa classe possediamo un ticket di Jira che ci conferma la
         * difettività, allora lo utilizziamo, altrimenti sfruttiamo il metodo proportion.
         *
         * Ticket Jira AV --> AssociationAVBlame.csv
         * Ticket Jira OV --> AssociationOVBlame.csv
         * Ticket con AV e FV --> BugAV.csv
         * Release --> ProjVersionInfo.csv
         * P = calculateProportion()
         * Classi java --> Classes.csv
         */

        List<String[]> avblm = new ArrayList<>();
        List<String[]> ovBlame = new ArrayList<>();
        List<String[]> ovBlameInd = new ArrayList<>();
        List<String[]> classes = new ArrayList<>();
        List<String[]> vers = new ArrayList<>();
        List<String[]> vers2 = new ArrayList<>();
        List<String[]> bug = new ArrayList<>();
        List<String[]> bugIndexed = new ArrayList<>();

        List<String[]> out = new ArrayList<>();


        try(FileReader fileReader = new FileReader(assBlameAV);
            CSVReader csvReader = new CSVReader(fileReader);
            FileReader fileReader1 = new FileReader(classPath);
            CSVReader csvReader1 = new CSVReader(fileReader1);
            FileReader fileReader2 = new FileReader(versionInfo);
            CSVReader csvReader2 = new CSVReader(fileReader2);
            FileReader fileReader3 = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\associationOVBlame.csv");
            CSVReader csvReader3 = new CSVReader(fileReader3);
            FileWriter fileWriter = new FileWriter(buggyPath);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){

            avblm = csvReader.readAll();
            classes = csvReader1.readAll();
            vers = csvReader2.readAll();
            vers2 = vers.subList(1,vers.size());
            //bug = csvReader3.readAll();
            ovBlame = csvReader3.readAll();


            /**
             * In questo modo andiamo a trasformare le versioni negli Index, in modo tale da
             * poterle confrontare.
             */

            bugIndexed = new GetDefectedClasses().indexingList(avblm,vers2); //Indexing della lista
            ovBlameInd = new GetDefectedClasses().indexingListWithDate(ovBlame,vers2); //Indexing della lista con la data


            Integer fixVer  = 0;
            Integer affVer = 0;
            Integer ver = 0;
            Double predictedAffVer = 0.0;
            int s = 0;

            int fv;
            int ovInt;

            for(String[] strings : vers2){
                for(String[] strings1 : classes){
                    for(String[] strings2 : bugIndexed){
                        if(strings1[2].equals(strings2[1])) { //controllo se la classe è presente negli AV registrati
                            affVer = Integer.parseInt(strings2[4]);
                            ver = Integer.parseInt(strings[0]);
                            if(affVer == Integer.parseInt(strings[0])) {
                                bug.add(new String[]{strings[0], strings1[2], "YES"});
                            }
                        }
                    }
                    /**
                     * In questo caso andiamo a sfruttare il metodo Proportion.
                     * Come sappiamo, attraverso il metodo proportion possiamo andare a determinare
                     * l'affected version sfruttando l'equazione:
                     *
                     * IV = FV - (FV-OV)*P --> IV infatti rappresenta la più vecchia versione tra le
                     * affected version.
                     *
                     */
                    for(String[] strings3 : ovBlameInd){
                        if(strings1[2].equals(strings3[1])) { //controllo se la classe è uguale
                            if(!strings3[4].equals("") && !strings3[3].equals("")){
                                fv = Integer.parseInt(strings3[3]);
                                ovInt = Integer.parseInt(strings3[4]);
                                predictedAffVer = (fv - (fv - ovInt)*p);
                                s = predictedAffVer.intValue();
                            } else{
                                s = 0;
                            }

                            if(s == Integer.parseInt(strings[0])){
                                bug.add(new String[]{strings[0], strings1[2], "YES"});
                            }
                        }

                    }
                }
            }

            String appoggio = "";
            String appoggio2 = "";

            Integer index = 0;

            /**
             * Andiamo a scorrere la lista finale che contine tutte le classi difettive. In
             * particolare, se troviamo una classe difettiva (ricavata o attraverso jira o
             * attraverso il metodo proportion) la andiamo a scrivere, altrimenti scriviamo
             * che la classe non è difettiva.
             */

            for(String[] str : vers2){
                for(String[] str2 : classes){
                    for(String[] str3 : bug){

                        appoggio = str[0]+str2[2];
                        appoggio2 = str3[0]+str3[1];

                        if(appoggio.equals(appoggio2)){
                            out.add(new String[] {str3[0],str3[1], str3[2]});
                            index++;
                            break;
                        }
                    }
                    if(index == 0){
                        out.add(new String[] {str[0],str2[2], "NO"});
                    }
                    index = 0;

                }
            }

            csvWriter.writeAll(out);
            csvWriter.flush();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }





}
