package milestone.uno.engine;

import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateCSVPath {

    /**
     * Attraverso questa classe andiamo a creare un file CSV che contiene due colonne,
     * nella prima colonna metteremo il nome della classe, mentre nella seconda colonna il suo percorso.
     * Useremo poi questi dati per andare ad eseguire git Blame.
     */

    private static final Logger LOGGER = Logger.getLogger(CreateCSVPath.class.getName());


    static String path = "";
    static String classes = "";
    static ArrayList<String[]> pathAndName = new ArrayList<>();

    private static void importResources(){
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            path = prop.getProperty("gitDirBOOKPath");
            classes = prop.getProperty("classesPath");

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }


    private ArrayList<String[]> retrievePath(File folder, StringBuilder pathName, StringBuilder className){

        /**funzione ricorsiva che ricostruisce il path di tutti i file ".java" all'interno della cartella
         * git.
         *
         * out --> classes.csv
         */

        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Non e' una cartella!");
        }

        for(File file : Objects.requireNonNull(folder.listFiles())){
            if (!file.isDirectory()){

                pathName.append(file.getPath());
                className.append(file.getAbsoluteFile().getName());
                if(pathName.toString().contains(".java")){
                    pathAndName.add(new String[]{pathName.toString().replace("\\", "/"),
                            pathName.toString().substring(86).replace("\\", "/"),
                            className.toString()});
                    /**
                     * Il primo path all'interno del file CSV è il Path completo, Il secondo path è il
                     * "Path from repository root" ed è necessario per eseguire blame, mentre il terzo elemento del
                     * file CSV è il nome della classe a cui ci stiamo riferendo.
                     * Nel CSV vengono considerati solamente i file ".java".
                     */
                }
                pathName.delete(0,pathName.length());
                className.delete(0,className.length());

            }
            else{
                retrievePath(file, pathName, className);
            }
        }

    return pathAndName;
    }

    private void writeCSV(ArrayList<String[]> list){

        /**
         * Scrive gli elementi nella lista in formato CSV.
         * le colonne del file saranno formate dal path completo, path ridotto e nome
         * della classe java.
         */
        try(FileWriter fileWriter = new FileWriter(classes);
            CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeAll(list);
            csvWriter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args){

        importResources();
        File dir = new File(path);
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        new CreateCSVPath().writeCSV(new CreateCSVPath().retrievePath(dir, sb, sb1));


    }
}
