package writer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputWriterMilestoneUnoDevDue {
    /**
     * Questa classe restituisce il file CSV richiesto contentente tutte le classi con le diverse metriche
     * calcolate.
     * <p>
     * prova --> NRevision, NAuthor
     * prova2 --> NFix
     * prova3 --> LocAdded, LocDeleted, LocTouched, MaxLock, AverageLock
     * prova4 --> Size, Age (in weeks)
     */
    private static final Logger LOGGER = Logger.getLogger(OutputWriterMilestoneUnoDevDue.class.getName());


    static String nBugFixPath = ""; //prova2
    static String nRevAndAuthPath = ""; //prova
    static String locMetricsPath = ""; //prova3
    static String sizeAndAgePath = ""; //prova4
    static String bugPath = "";
    static String output = "";


    static List<String[]> lista1 = new ArrayList<>();
    static List<String[]> lista2 = new ArrayList<>();
    static List<String[]> lista3 = new ArrayList<>();
    static List<String[]> lista4 = new ArrayList<>();
    static List<String[]> lista5 = new ArrayList<>();

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


            nBugFixPath = prop.getProperty("numBugFix");
            nRevAndAuthPath = prop.getProperty("numRevAuth");
            locMetricsPath = prop.getProperty("locMetrics");
            sizeAndAgePath = prop.getProperty("sizeAndAge");
            bugPath = prop.getProperty("buggyPath");
            output = prop.getProperty("out");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void mergeCSV(List<String[]> list, List<String[]> list2, List<String[]> list3, List<String[]> list4, List<String[]> list5) throws IOException {

        List<String[]> retList = new ArrayList<>();
        retList.add(new String[]{"Version", "Class", "NFix", "NRevision", "NAuthor", "LocAdded", "LocDeleted", "LocTouched", "MaxLockAdded", "AverageLockAdded", "Size", "Age", "Buggy"});

        for (int i = 0; i < list.size(); i++) {

            retList.add(new String[]{list.get(i)[0]
                    , list.get(i)[1]
                    , list.get(i)[2]
                    , list2.get(i)[2]
                    , list2.get(i)[3]
                    , list3.get(i)[2]
                    , list3.get(i)[3]
                    , list3.get(i)[4]
                    , list3.get(i)[5]
                    , list3.get(i)[6]
                    , list4.get(i)[2]
                    , list4.get(i)[3]
                    , list5.get(i)[2]});
        }

        try (FileWriter fileWriter = new FileWriter(output);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.flush();
            csvWriter.writeAll(retList);

        }
    }


    public static void main(String[] args) throws IOException {


        importResources(1);

        try (FileReader fileReader = new FileReader(nBugFixPath);
             FileReader fileReader1 = new FileReader(nRevAndAuthPath);
             FileReader fileReader2 = new FileReader(locMetricsPath);
             FileReader fileReader3 = new FileReader(sizeAndAgePath);
             FileReader fileReader4 = new FileReader(bugPath);
             CSVReader csvReader = new CSVReader(fileReader);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             CSVReader csvReader3 = new CSVReader(fileReader3);
             CSVReader csvReader4 = new CSVReader(fileReader4)) {

            lista1 = csvReader.readAll();
            lista2 = csvReader1.readAll();
            lista3 = csvReader2.readAll();
            lista4 = csvReader3.readAll();
            lista5 = csvReader4.readAll();


        } catch (IOException e) {
            e.printStackTrace();
        }

        new OutputWriterMilestoneUnoDevDue().mergeCSV(lista1, lista2, lista3, lista4, lista5);

    }
}
