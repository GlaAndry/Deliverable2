package milestone.uno.engine;

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

    private static final Logger LOGGER = Logger.getLogger(GetDefectedClasses.class.getName());

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    static String versionInfo = "";
    static String assBlameAV = "";
    static String assBlameOV = "";
    static String buggyPath = "";
    static String classPath = "";
    static String varP = "";
    static String pValue = "";

    public static void main(String[] args) throws IOException {

        importResources(0);

        List<String[]> avblm;
        List<String[]> classes;
        List<String[]> vers;
        List<String[]> ovBlame;

        try (FileReader fileReader = new FileReader(assBlameAV);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(classPath);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileReader fileReader2 = new FileReader(versionInfo);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             FileReader fileReader3 = new FileReader(assBlameOV);
             CSVReader csvReader3 = new CSVReader(fileReader3)) {

            avblm = csvReader.readAll();
            classes = csvReader1.readAll();
            vers = csvReader2.readAll();
            ovBlame = csvReader3.readAll();


            new GetDefectedClasses().determineDefectiveWithProportion(new GetDefectedClasses().calculateProportion(), vers, classes, ovBlame, avblm);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        String prf = "";

        if (value == 0) {
            prf = "Book";
        } else if (value == 1) {
            prf = "Tajo";
        }

        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config" + prf + ".properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            versionInfo = prop.getProperty("versionInfo");
            assBlameAV = prop.getProperty("AssAB");
            buggyPath = prop.getProperty("buggyPath");
            classPath = prop.getProperty("classesPath");
            varP = prop.getProperty("variables");
            pValue = prop.getProperty("pValue");
            assBlameOV = prop.getProperty("AssOV");


        } catch (
                IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }

    }

    private List<String[]> calculateProportion() {
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

        double p = 0.0;

        List<String[]> variables = new ArrayList<>();
        List<String[]> version = new ArrayList<>();
        List<String[]> ver2 = new ArrayList<>();

        List<String[]> out = new ArrayList<>();

        Double iv = 0.0;
        Double ov = 0.0;
        Double fv = 0.0;

        Double value = 0.0;

        int count = 1;

        Date dateVar;
        Date dateIn;
        Date dateFin;

        try (FileReader fileReader = new FileReader(varP);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(versionInfo);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileWriter fileWriter = new FileWriter(pValue);
             CSVWriter csvWriter = new CSVWriter(fileWriter);) {

            version = csvReader1.readAll();
            ver2 = version.subList(1, version.size());
            variables = csvReader.readAll();

            for (String[] strings : ver2) {
                dateIn = formatter.parse(strings[3]);
                dateFin = formatter.parse(strings[4]);
                for (String[] str : variables) {

                    dateVar = format.parse(str[4]);

                    if (dateVar.after(dateIn) && dateVar.before(dateFin)) {
                        count++;
                        ov = Double.parseDouble(str[0]);
                        fv = Double.parseDouble(str[2]);
                        iv = Double.parseDouble(str[3]);

                        if ((fv - ov) != 0) {
                            p = p + ((fv - iv) / (fv - ov));
                        }
                    }

                }
                value = p / count;
                if (value.isNaN()) {
                    value = 0.0;
                }
                out.add(new String[]{strings[0], Double.toString(value)});
                count = 1;
                p = 0;
            }


            csvWriter.writeAll(out);
            csvWriter.flush();


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return out;
    }

    private List<String[]> indexingList(List<String[]> list, List<String[]> version) {

        /**
         * Attraverso questo metodo andiamo a trasformare le versioni nei proprio index
         * eg: 4.1.1 --> 3 in accordo con il file VersionInfo.csv
         */

        List<String[]> out = new ArrayList<>();

        String fix = "";
        String aff = "";

        for (String[] str : list) {
            for (String[] str2 : version) {
                if (str[4].equals(str2[2])) {
                    fix = str2[0];
                }
                if (str[5].equals(str2[2])) {
                    aff = str2[0];

                }
                if (!fix.equals("") && !aff.equals("")) {
                    out.add(new String[]{str[0], str[2], str[3], fix, aff});
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

        String fixVer2 = "";
        String ov = "";

        for (String[] str : list) {
            for (String[] str2 : versions) {

                Date dateB = format.parse(str[0]);
                Date dateV = formatter.parse(str2[3]);

                if (dateB.before(dateV)) {
                    ov = str2[0];
                }

                if (str[4].equals(str2[2])) {
                    fixVer2 = str2[0];
                }

                if (!fixVer2.equals("")) {
                    out.add(new String[]{str[0], str[2], str[3], fixVer2, ov});
                    //Data, classe, ticket, FixVersion, OV//
                    fixVer2 = "";
                    ov = "";
                }

            }

        }

        return out;

    }

    private List<String[]> determineJiraDefective(List<String[]> vers, List<String[]> classes, List<String[]> avblm) {

        /**
         * Attraverso questo metodo andiamo a determinare se una classe è defective o meno per una determinata
         * release sfruttando solamente i dati forniti da Jira.
         *
         * Ticket Jira AV --> AssociationAVBlame.csv
         * Ticket Jira OV --> AssociationOVBlame.csv
         * Ticket con AV e FV --> BugAV.csv
         * Release --> ProjVersionInfo.csv
         * P = calculateProportion()
         * Classi java --> Classes.csv
         */

        List<String[]> vers2;
        List<String[]> bug = new ArrayList<>();
        List<String[]> bugIndexed;


        vers2 = vers.subList(1, vers.size());

        /**
         * In questo modo andiamo a trasformare le versioni negli Index, in modo tale da
         * poterle confrontare.
         */

        bugIndexed = new GetDefectedClasses().indexingList(avblm, vers2); //Indexing della lista

        Integer affVer = 0;

        for (String[] strings : vers2) {
            for (String[] strings1 : classes) {
                for (String[] strings2 : bugIndexed) {
                    if (strings1[2].equals(strings2[1])) { //controllo se la classe è presente negli AV registrati
                        affVer = Integer.parseInt(strings2[4]);
                        if (affVer == Integer.parseInt(strings[0])) {
                            bug.add(new String[]{strings[0], strings1[2], "YES"});
                        }
                    }
                }
            }
        }


        return bug;
    }

    private void determineDefectiveWithProportion(List<String[]> pValues, List<String[]> vers, List<String[]> classes, List<String[]> ovBlame, List<String[]> avblm)
            throws ParseException {

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

        List<String[]> ovBlameInd;
        List<String[]> vers2 = vers.subList(1, vers.size());

        /////////
        List<String[]> bug = determineJiraDefective(vers, classes, avblm);  //prima determino i bug da jira
        ////////


        /**
         * In questo modo andiamo a trasformare le versioni negli Index, in modo tale da
         * poterle confrontare.
         */

        ovBlameInd = new GetDefectedClasses().indexingListWithDate(ovBlame, vers2); //Indexing della lista con la data
        double p = 0.0;

        /**
         * In questo caso andiamo a sfruttare il metodo Proportion.
         * Come sappiamo, attraverso il metodo proportion possiamo andare a determinare
         * l'affected version sfruttando l'equazione:
         *
         * IV = FV - (FV-OV)*P --> IV infatti rappresenta la più vecchia versione tra le
         * affected version.
         *
         */

        for (String[] strings : vers2) {
            for (String[] str : pValues) {
                if (str[0].equals(strings[0])) {
                    p = Double.parseDouble(str[1]);
                }
                for (String[] strings1 : classes) {
                    for (String[] strings3 : ovBlameInd) {

                        determinePredictedAVandBugs(strings1[2], strings3[1], strings3[4], strings3[3], strings[0], bug, p);

                    }
                }
            }
        }

        //richiamo la funzione per creare il file CSV
        createDefectiveCSV(vers2, classes, bug);


    }

    private List<String[]> determinePredictedAVandBugs(String className, String className2, String ov, String fvString, String vers, List<String[]> bugs, double p) {

        /**
         * All'interno di questo metodo andiamo a predirre l'affected version di riferimento e successivamente verifichiamo se una classe è difettiva
         * o meno se l'affected version calcolata è uguale alla versione del progetto che stiamo analizzando.
         */


        int s = 0;
        int fv;
        int ovInt;
        Double predictedAffVer;

        if (className.equals(className2)) { //controllo se la classe è uguale
            if (!ov.equals("") && !fvString.equals("")) {
                fv = Integer.parseInt(fvString);
                ovInt = Integer.parseInt(ov);
                predictedAffVer = (fv - (fv - ovInt) * p);
                s = predictedAffVer.intValue();
            } else {
                s = 0;
            }

            if (s == Integer.parseInt(vers)) {
                bugs.add(new String[]{vers, className, "YES"});
            }
        }

        return bugs;
    }


    private void createDefectiveCSV(List<String[]> list, List<String[]> list2, List<String[]> list3) { //ver2, classes, bug

        String appoggio = "";
        String appoggio2 = "";

        Integer index = 0;

        List<String[]> out = new ArrayList<>();

        /**
         * Andiamo a scorrere la lista finale che contine tutte le classi difettive. In
         * particolare, se troviamo una classe difettiva (ricavata o attraverso jira o
         * attraverso il metodo proportion) la andiamo a scrivere, altrimenti scriviamo
         * che la classe non è difettiva.
         */

        try (FileWriter fileWriter = new FileWriter(buggyPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            for (String[] str : list) {
                for (String[] str2 : list2) {
                    for (String[] str3 : list3) {

                        appoggio = str[0] + str2[2];
                        appoggio2 = str3[0] + str3[1];

                        if (appoggio.equals(appoggio2)) {
                            out.add(new String[]{str3[0], str3[1], str3[2]});
                            index++;
                            break;
                        }
                    }
                    if (index == 0) {
                        out.add(new String[]{str[0], str2[2], "NO"});
                    }
                    index = 0;

                }
            }

            csvWriter.writeAll(out);
            csvWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


