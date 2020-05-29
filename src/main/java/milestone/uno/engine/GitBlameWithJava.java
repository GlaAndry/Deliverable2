package milestone.uno.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitBlameWithJava {

    /**
     * In questa classe eseguiamo il blame di tutti i file del pregetto. In particolare andiamo a vedere la data
     * restituita da blame. Se ricade nell'arco temporale che stiamo analizzando (Cioè l'ultimo mese della prima metà
     * di vita del progetto), allora possiamo classificare la classe come defective.
     */

    private static final Logger LOGGER = Logger.getLogger(GitBlameWithJava.class.getName());

    static String path = "";
    static String commitPath = "";
    static String completePath = "";
    static String gitUrl = "";
    static String blamePath = "";
    static String blameNew = "";
    static String finalBlames = "";
    static String classesPath = "";

    public static void main(String[] args) throws GitAPIException, IOException {

        importResources(1);
        new GitBlameWithJava().blame();
        new GitBlameWithJava().changeDate();
        new GitBlameWithJava().removeDuplicates();

    }

    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         *
         * 0 --> BOOKKEEPER
         * 1 --> TAJO
         */
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);


            if (value == 0) {
                path = prop.getProperty("gitDirBOOKPath");
                commitPath = prop.getProperty("commitPath");
                completePath = prop.getProperty("gitPathBOOK");
                gitUrl = prop.getProperty("gitUrlBOOK");
                blamePath = prop.getProperty("blameRes");
                blameNew = prop.getProperty("blameNew");
                finalBlames = prop.getProperty("blameFinal");
                classesPath = prop.getProperty("classesPath");
            }
            if (value == 1) {
                path = prop.getProperty("gitDirTAJOPath");
                commitPath = prop.getProperty("commitPathTAJO");
                completePath = prop.getProperty("gitPathTAJO");
                gitUrl = prop.getProperty("gitUrlTAJO");
                blamePath = prop.getProperty("blameResTAJO");
                blameNew = prop.getProperty("blameNewTAJO");
                finalBlames = prop.getProperty("blameFinalTAJO");
                classesPath = prop.getProperty("classesPathTAJO");

            }


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void removeDuplicates() throws IOException {

        /**
         * Questo metodo analizza il CSV che abbiamo ricavato attraverso il Blame e ne elimina i duplicati.
         * In particolare, il CSV che viene restituito ha due colonne, uno che identifica la data del
         * blame, mentre il secondo che identifica il nome della classe.
         */

        //apro il file blame.csv
        try (FileReader fileReader = new FileReader(blameNew);
             CSVReader csvReader = new CSVReader(fileReader);
             FileWriter fileWriter = new FileWriter(finalBlames);
             CSVWriter writer = new CSVWriter(fileWriter)) {

            List<String[]> blmWithDup = csvReader.readAll();

            /**
             * Sfruttiamo un Hasmap in quanto non è possibile avere delle coppie chiave valore che siano
             * duplicate al suo interno. In questo modo evitiamo molti passaggi, altrimenti necessari,
             * se avessimo utilizzato delle liste.
             * Il controllo con l'index risulta essere fondamentale, in quanto altrimenti, andandolo ad
             * aumentare ad oltranza, accetteremmo anche delle righe che sono identiche tra loro. In questo modo,
             * invece, lo andiamo ad aumentare solamente quando notiamo che la riga è effettivamente cambiata.
             */

            HashMap<Integer, String> hashMap = new HashMap<>();
            Integer index = 0;

            for (String[] str : blmWithDup) {

                if (!hashMap.containsValue(str[0] + str[1] + str[2])) {
                    hashMap.put(index, str[0] + str[1] + str[2]);
                } else {
                    index++;
                }

            }

            //scrivo nel csv finale
            for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
                writer.writeNext(new String[]{entry.getValue().substring(0, 10), entry.getValue().substring(10, 62), entry.getValue().substring(62)});
            }

            writer.flush();


        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }

    }


    private void changeDate() {
        /**
         * Il metodo apre il file CSV contenente il blame e va a modificare la data,
         * in modo tale da poter essere comparabile successivamente con le release.
         * (ANNO/MESE/GIORNO). Inoltre ci disfiamo del 50% dei dati in nostro possesso,
         * in quanto non sono utilizzabili per andare ad effettuare le stime.
         */

        List<String[]> blames;
        List<String[]> dates = new ArrayList<>();

        String data = "";

        try (FileReader blameFile = new FileReader(blamePath);
             CSVReader csvReader = new CSVReader(blameFile);
             FileWriter fileWriter = new FileWriter(blameNew);
             CSVWriter writer = new CSVWriter(fileWriter)) {

            blames = csvReader.readAll();

            for (String[] str : blames) {

                String anno;
                String mese;
                String giorno;

                if (str[0].length() == 27) {
                    anno = str[0].substring(25) + "-";
                } else {
                    anno = str[0].substring(24) + "-";
                }
                mese = determineMonth(str[0].substring(4, 7));

                giorno = str[0].substring(8, 10);

                data = anno + mese + giorno;

                if (data.contains(" ")) {
                    data = data.substring(1); //elimino lo spazio presente all'inizio di alcune date all'interno del set.
                }

                /**
                 * Attraverso questo controllo andiamo a "buttare" il 50% dei dati, in quanto non sono utilizzabili
                 * per andare ad effettuare la stima.
                 */

                if (((anno.contains("2016")) || (anno.contains("2017")) || (anno.contains("2018")) || (anno.contains("2019")) || (anno.contains("2020")))) {
                    LOGGER.info("Dato da Eliminare");
                } else {
                    dates.add(new String[]{data, str[1], str[2]});
                }

            }

            writer.writeAll(dates);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String determineMonth(String str){

        String ret;
        switch (str) {
            case "Jan":
                ret = "01-";
                break;
            case "Feb":
                ret = "02-";
                break;
            case "Mar":
                ret = "03-";
                break;
            case "Apr":
                ret = "04-";
                break;
            case "May":
                ret = "05-";
                break;
            case "Jun":
                ret = "06-";
                break;
            case "Jul":
                ret = "07-";
                break;
            case "Aug":
                ret = "08-";
                break;
            case "Sep":
                ret = "09-";
                break;
            case "Oct":
                ret = "10-";
                break;
            case "Nov":
                ret = "11-";
                break;
            case "Dec":
                ret = "12-";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + str);
        }
        return ret;
    }


    public void blame() throws GitAPIException {


        File dir = new File(path);
        int counter = 0;
        String result = "";

        if (!dir.exists()) {
            LOGGER.info("Comando: Clone Repository\nProcedo con il Download...");
            dir.mkdir();
            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(dir)
                    .call();
            LOGGER.info("Clone Repository eseguito correttamente.\n\n");
            LOGGER.info("Eseguire nuovamente per scaricare tutti i commit.\n");
        }

        try (FileReader fileReader = new FileReader(classesPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileWriter fileWriter = new FileWriter(blamePath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            ArrayList<String[]> wrt = new ArrayList<>();

            List<String[]> classi = csvReader.readAll();
            Git git = new Git(new FileRepository(completePath));

            for (String[] record : classi) {

                BlameCommand blameCommand = git.blame()
                        .setStartCommit(git.getRepository().resolve("HEAD"))
                        .setFilePath(record[1].substring(1));

                BlameResult blameResult = blameCommand.call();

                int size = blameResult.getResultContents().size();
                for (int i = 0; i < size; i++) {
                    /**
                     * Aggiungiamo al CSV la data, la classe associata ed il Tree. IL Tree è necessario in quanto così
                     * possiamo possiamo riferirci al file commits.
                     */
                    if (counter == 0) {
                        result = blameResult.getSourceAuthor(i).getWhen().toString() + blameResult.getSourceCommit(i).getTree().toString() + record[2];
                        wrt.add(new String[]{blameResult.getSourceAuthor(i).getWhen().toString(), blameResult.getSourceCommit(i).getTree().toString(), record[2]});
                        counter++;
                    } else {
                        if (!result.equals(blameResult.getSourceAuthor(i).getWhen().toString() + blameResult.getSourceCommit(i).getTree().toString() + record[2])) {
                            counter = 0;
                        }
                    }
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(wrt);
            csvWriter.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}



