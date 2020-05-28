package milestone.uno.restandgit;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssociationJiraGit {

    /**
     * Questa classe sfrutta i dati provenienti dai commit di Git, dalle rest API di Jira e
     * dal blame effettuato in locale, per andare a determinare una associazione tra ticket
     * e classe java. In particolare andremo a sfruttare il Tree, l'unico elemento in
     * comunune in questi file.
     */

    private static final Logger LOGGER = Logger.getLogger(AssociationJiraGit.class.getName());

    static String blame = "";
    static String comm = "";
    static String outAssCB = "";
    static String outAssAB = "";
    static String bugAV = "";
    static String outAssOV = "";
    static String fvPath = "";


    public static void main(String[] args){

        importResources(1);
        new AssociationJiraGit().associateCommitsAndBlame();
        new AssociationJiraGit().associateAVAndBlame();
        new AssociationJiraGit().associateFixAndBlame();

    }

    private static void importResources(int value){
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            if(value == 0){
                blame = prop.getProperty("blameFinal");
                comm = prop.getProperty("commitPath");
                outAssCB = prop.getProperty("AssCB");
                outAssAB = prop.getProperty("AssAB");
                bugAV = prop.getProperty("BugTicketAV");
                outAssOV = prop.getProperty("AssOV");
                fvPath = prop.getProperty("FVpath");
            }
            if(value == 1){
                blame = prop.getProperty("blameFinalTAJO");
                comm = prop.getProperty("commitPathTAJO");
                outAssCB = prop.getProperty("AssCBTAJO");
                outAssAB = prop.getProperty("AssABTAJO");
                bugAV = prop.getProperty("BugTicketAVTAJO");
                outAssOV = prop.getProperty("AssOVTAJO");
                fvPath = prop.getProperty("FVpathTAJO");
            }


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void associateFixAndBlame(){
        /**
         * Questo metodo genera un nuovo file CSV avente come colonne i ticket (presi da jira attraverso
         * la query indicata), la classe java, la data associata. In particolare stiamo prendendo l'intersezione
         * tra i ticket che risultano su jira e quelli che risultano dal blame.
         *
         * out --> associationOVBlame.csv
         */

        List<String[]> blmAssCmt;
        List<String[]> fix;

        List<String[]> res = new ArrayList<>();

        try(FileReader b = new FileReader(outAssCB);
            CSVReader csvReader = new CSVReader(b);
            FileReader c = new FileReader(fvPath);
            CSVReader csvReader1 = new CSVReader(c);
            FileWriter fileWriter = new FileWriter(outAssOV);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){


            blmAssCmt = csvReader.readAll();
            fix = csvReader1.readAll();

            for(String[] str : blmAssCmt){
                for(String[] str2 : fix){
                    /**
                     * Andiamo a controllare l'uguaglianza tra i ticket. In questo caso
                     * abbiamo una associazione.
                     */
                    if(str[3].equals(str2[1])){
                        res.add(new String[]{str[0], str[1], str[2], str2[1], str2[2]});
                        //data, albero, classe, ticket, OV//
                    }
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void associateAVAndBlame(){

        /**
         * Questo metodo genera un nuovo file CSV avente come colonne i ticket (presi da jira attraverso
         * la query indicata), la classe java, la data associata. In particolare stiamo prendendo l'intersezione
         * tra i ticket che risultano su jira e quelli che risultano dla blame.
         *
         * out --> associationAVBlame.csv
         */

        List<String[]> blmAssCmt;
        List<String[]> bugT;

        List<String[]> res = new ArrayList<>();

        try(FileReader b = new FileReader(outAssCB);
            CSVReader csvReader = new CSVReader(b);
            FileReader c = new FileReader(bugAV);
            CSVReader csvReader1 = new CSVReader(c);
            FileWriter fileWriter = new FileWriter(outAssAB);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){


            blmAssCmt = csvReader.readAll();
            bugT = csvReader1.readAll();

            for(String[] str : blmAssCmt){
                for(String[] str2 : bugT){
                    /**
                     * Andiamo a controllare l'uguaglianza tra i ticket. In questo caso
                     * abbiamo una associazione.
                     */
                    if(str[3].equals(str2[0])){
                        res.add(new String[]{str[0], str[1], str[2], str2[0], str2[1], str2[2]});
                        //data, albero, classe, ticket, fixversion, affectedversion//
                    }
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void associateCommitsAndBlame(){
        /**
         * Questo metodo genera un nuovo file CSV avente come colonne ticket e classe java,
         * in modo tale da andare a generare una associazione tra questi due.
         *
         * out --> associationCommitBlame.csv
         */

        List<String[]> blm;
        List<String[]> cmt;

        List<String[]> res = new ArrayList<>();

        try(FileReader b = new FileReader(blame);
            CSVReader csvReader = new CSVReader(b);
            FileReader c = new FileReader(comm);
            CSVReader csvReader1 = new CSVReader(c);
            FileWriter fileWriter = new FileWriter(outAssCB);
            CSVWriter csvWriter = new CSVWriter(fileWriter)){


            blm = csvReader.readAll();
            cmt = csvReader1.readAll();

            for(String[] str : blm){
                for(String[] str2 : cmt){
                    /**
                     * Andiamo a controllare l'uguaglianza tra i due Tree. In questo caso
                     * abbiamo una associazione.
                     */
                    if(str[1].equals(str2[1])){
                        res.add(new String[]{str[0], str[1], str[2], str2[2]});
                    }
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(res);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
