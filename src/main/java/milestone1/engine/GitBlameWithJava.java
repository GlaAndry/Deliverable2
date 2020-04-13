package milestone1.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.internal.storage.file.FileRepository;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitBlameWithJava {

    /**
     * In questa classe eseguiamo il blame di tutti i file del pregetto. In particolare andiamo a vedere la data
     * restituita da blame. Se ricade nell'arco temporale che stiamo analizzando (Cioè l'ultimo mese della prima metà
     * di vita del progetto), allora possiamo classificare la classe come defective.
     */

    private static final Logger LOGGER = Logger.getLogger(DownloadCommit.class.getName());

    static String path = "";
    static String classesPath = "";
    static String completePath = "";
    static String gitUrl = "";
    static String blameRes = "";
    //static String blamePath = "";
    String blamePath = "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blame.csv";
    String blameNew = "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blameNew.csv";
    String finalBlames = "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blameFinal.csv";
    String sortedBlames = "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blameSorted.csv";

    public static void main(String[] args) throws GitAPIException, IOException {

        importResources();
        //new GitBlameWithJava().blame();
        //new GitBlameWithJava().changeDate();
        new GitBlameWithJava().removeDuplicates();

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

            path = prop.getProperty("gitDirBOOKPath");
            classesPath = prop.getProperty("commitPath");
            completePath = prop.getProperty("gitPathBOOK");
            gitUrl = prop.getProperty("gitUrlBOOK");
            blameRes = prop.getProperty("blameRes");


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
            List<String[]> blm = new ArrayList<>();

            String record = "";

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

                if (!hashMap.containsValue(str[0]+str[1])) {
                    record = str[0]+str[1];
                    hashMap.put(index, str[0]+str[1]);
                } else {
                    if(record != (str[0] + str[1])){
                        index++;
                    }
                }

            }

            //scrivo nel csv finale
            for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {

                writer.writeNext(new String[]{entry.getValue().substring(0,10),entry.getValue().substring(10)});

            }

            writer.flush();


        } catch (ConcurrentModificationException e){
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

        List<String[]> blames = new ArrayList<>();
        List<String[]> dates = new ArrayList<>();

        String data = "";

        try (FileReader blameFile = new FileReader(blamePath);
             CSVReader csvReader = new CSVReader(blameFile);
             FileWriter fileWriter = new FileWriter(blameNew);
             CSVWriter writer = new CSVWriter(fileWriter)) {

            blames = csvReader.readAll();

            for (String[] str : blames) {

                String anno,mese,giorno;

                if (str[0].length() == 27) {
                    anno = str[0].substring(25) + "-";
                } else {
                    anno = str[0].substring(24) + "-";
                }
                switch (str[0].substring(4, 7)) {

                    case ("Jan"):
                        mese =  "01-";
                        break;
                    case ("Feb"):
                        mese =  "02-";
                        break;
                    case ("Mar"):
                        mese =  "03-";
                        break;
                    case ("Apr"):
                        mese =  "04-";
                        break;
                    case ("May"):
                        mese =  "05-";
                        break;
                    case ("Jun"):
                        mese =  "06-";
                        break;
                    case ("Jul"):
                        mese =  "07-";
                        break;
                    case ("Aug"):
                        mese =  "08-";
                        break;
                    case ("Sep"):
                        mese =  "09-";
                        break;
                    case ("Oct"):
                        mese =  "10-";
                        break;
                    case ("Nov"):
                        mese =  "11-";
                        break;
                    case ("Dec"):
                        mese =  "12-";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + str[0].substring(4, 7));
                }

                giorno = str[0].substring(8, 10);

                data = anno+mese+giorno;

                if(data.contains(" ")){
                    data = data.substring(1); //elimino lo spazio presente all'inizio di alcune date all'interno del set.
                }

                /**
                 * Attraverso questo controllo andiamo a "buttare" il 50% dei dati, in quanto non sono utilizzabili
                 * per andare ad effettuare la stima.
                 */

                if(((anno.contains("2016")) || (anno.contains("2017")) || (anno.contains("2018")) || (anno.contains("2019")) || (anno.contains("2020")))){
                    System.out.println("Dato da eliminare");
                } else {
                    dates.add(new String[]{data,str[1]});
                }

            }

            writer.writeAll(dates);
            writer.flush();

        } catch (IOException e){
            e.printStackTrace();
        }

    }



    public void blame() throws GitAPIException {


        File dir = new File(path);

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

        try(FileReader fileReader = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\BugLab\\src\\main\\resources\\Classes.csv");
            CSVReader csvReader = new CSVReader(fileReader);
            FileWriter fileWriter = new FileWriter(blameRes);
            CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            ArrayList<String[]> wrt = new ArrayList<>();

            List<String[]> classi = csvReader.readAll();
            Git git = new Git(new FileRepository(completePath));

            for(String[] record: classi){

                System.out.println(record[1]);
                BlameCommand blameCommand = git.blame()
                        .setStartCommit(git.getRepository().resolve("HEAD"))
                        .setFilePath(record[1]);

                BlameResult blameResult = blameCommand.call();

                int size = blameResult.getResultContents().size();
                for( int i = 0; i < size; i++ ) {
                    wrt.add(new String[]{blameResult.getSourceAuthor(i).getWhen().toString(), record[2]});
                }
            }

            csvWriter.writeAll(wrt);
            csvWriter.flush();


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}



