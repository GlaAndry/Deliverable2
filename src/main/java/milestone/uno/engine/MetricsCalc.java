package milestone.uno.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsCalc {

    /**
     * Questa classe è responsabile del calcolo delle metriche delle classi dei progetti.
     * Le Nove metriche considerate sono...
     */

    private static final Logger LOGGER = Logger.getLogger(MetricsCalc.class.getName());


    static String classPath = "";
    static String gitPath = "";
    static String version = "";
    static String assCoBlm = "";
    static String outLoc = "";

    static String nBugFixPath=""; //prova2
    static String nRevAndAuthPath = ""; //prova
    static String locMetricsPath = ""; //prova3
    static String sizeAndAgePath = ""; //prova4

    static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {


        importResources(0);
        //new MetricsCalc().sizeAndAgeOfClasses(); //fatto
        //new MetricsCalc().numberOfRevisionsAndAuthors(); //fatto
        new MetricsCalc().numberOfBugFixes(); //fatto
        //new MetricsCalc().retrieveLocFromTrees(); //ci mette almeno 3.5 ore per Tajo e 1 ora per Book
        //new MetricsCalc().locMetrics();


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

            if(value == 0){
                classPath = prop.getProperty("classesPath");
                gitPath = prop.getProperty("gitPathBOOK");
                version = prop.getProperty("versionInfoBOOK");
                assCoBlm = prop.getProperty("AssCB");
                outLoc = prop.getProperty("outLocClasses");

                nBugFixPath = prop.getProperty("numBugFix");
                nRevAndAuthPath = prop.getProperty("numRevAuth");
                locMetricsPath = prop.getProperty("locMetrics");
                sizeAndAgePath = prop.getProperty("sizeAndAge");

            }
            if(value == 1){
                classPath = prop.getProperty("classesPathTAJO");
                gitPath = prop.getProperty("gitPathTAJO");
                version = prop.getProperty("versionInfoTAJO");
                assCoBlm = prop.getProperty("AssCBTAJO");
                outLoc = prop.getProperty("outLocClassesTAJO");

                nBugFixPath = prop.getProperty("numBugFixTAJO");
                nRevAndAuthPath = prop.getProperty("numRevAuthTAJO");
                locMetricsPath = prop.getProperty("locMetricsTAJO");
                sizeAndAgePath = prop.getProperty("sizeAndAgeTAJO");
            }


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void numberOfBugFixes() throws IOException {
        /**
         * in --> classes.csv, AssociationCommitBlame.csv, versionInfo.csv
         *
         * Questo metodo restituisce il numero di bug Fix per una determinata classe
         * in una determinata release.
         *
         * out --> Csv con colonne [INDEX VERSION,CLASSE,BUGFIXNUM]
         */

        List<String[]> cls = new ArrayList<>();
        List<String[]> ver = new ArrayList<>();
        List<String[]> ver2 = new ArrayList<>();
        List<String[]> assCommBlm = new ArrayList<>();
        List<String[]> bugFixNum = new ArrayList<>();

        int commitCounter = 0;

        try (FileReader fileReader = new FileReader(classPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(version);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileReader fileReader2 = new FileReader(assCoBlm);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             FileWriter fileWriter = new FileWriter(nBugFixPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            cls = csvReader.readAll();
            ver = csvReader1.readAll();
            assCommBlm = csvReader2.readAll();

            ver2 = ver.subList(1, ver.size());

            Date versionDate;
            Date assCommBlmDate;

            for (String[] strings : ver2) {
                versionDate = formatter.parse(strings[3]);
                for (String[] str : cls) {
                    for (String[] str2 : assCommBlm) {
                        assCommBlmDate = format.parse(str2[0]);
                        if (versionDate.compareTo(assCommBlmDate) > 0 && str2[2].equals(str[2])) { //controllo sulla data della versione e controllo se la classe coincide
                            commitCounter++;
                        }
                    }
                    bugFixNum.add(new String[]{strings[0], str[1], Integer.toString(commitCounter)});
                    commitCounter = 0;
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(bugFixNum);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    private List<String[]> numberOfRevisionsAndAuthors() throws IOException {
        /**
         * in --> classes.csv, versionInfo.csv
         *
         * Questo metodo restituisce il numero di commit che sono stati effettuati per
         * una determinata classe in una determinata release. (Sommando alle release
         * successive il numero ottenuto dalle release precedenti.
         *
         * out --> Csv con colonne [INDEX VERSION,CLASSE,REVNUM, AUTHNUM]
         */

        List<String[]> cls = new ArrayList<>();
        List<String[]> ver = new ArrayList<>();
        List<String[]> ver2 = new ArrayList<>();
        List<String[]> revNum = new ArrayList<>();

        ///
        HashMap<Integer, String> hashMap = new HashMap<>();
        String authName;
        int index = 0;
        ///


        Integer size;
        Date commitDate;
        Date commitDate2 = null;
        Date versionDate;

        int commitCounter = 0;

        Git git = new Git(new FileRepository(gitPath));

        try (FileReader fileReader = new FileReader(classPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(version);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileWriter fileWriter = new FileWriter(nRevAndAuthPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            cls = csvReader.readAll();
            ver = csvReader1.readAll();

            ver2 = ver.subList(1, ver.size());

            for (String[] strings : ver2) {

                versionDate = formatter.parse(strings[3]);

                for (String[] str : cls) {

                    BlameCommand blameCommand = git.blame()
                            .setStartCommit(git.getRepository().resolve("HEAD"))
                            .setFilePath(str[1].substring(1));

                    BlameResult blameResult = blameCommand.call();
                    size = blameResult.getResultContents().size();

                    for (int i = 0; i < size; i++) {

                        commitDate = blameResult.getSourceAuthor(i).getWhen();
/*
                        if (commitDate2 != null && commitDate.compareTo(commitDate2) == 0) {
                            continue;
                        }

 */
                        if (versionDate.compareTo(commitDate) > 0) {
                            //commitDate2 = blameResult.getSourceAuthor(i).getWhen();
                            /**
                             * Aumento il contatore solamente quando sono sicuro che il commit considerato
                             * non è gia' stato considerato anche in precedenza.
                             */
                            /// aggiungiamo gli autori del commit in modo tale da determinarne il numero
                            authName = blameResult.getSourceAuthor(i).getName();

                            if (!hashMap.containsValue(authName)) {
                                hashMap.put(index, authName);
                            } else {
                                index++;
                            }
                            ////
                            commitCounter++;
                        }

                    }

                    revNum.add(new String[]{strings[0], str[2], Integer.toString(commitCounter), Integer.toString(hashMap.size())});
                    commitCounter = 0;
                    index = 0;
                    hashMap = new HashMap<>();

                }


            }

            csvWriter.flush();
            csvWriter.writeAll(revNum);


        } catch (IOException | GitAPIException | ParseException e) {
            e.printStackTrace();
        }

        return revNum;

    }

    private List<String[]> sortByDate(List<String[]> list) {
        /**
         * Questo metodo esegue il sorting della lista
         * andando a considerare la data.
         * Return list sorted.
         */

        list.sort((strings, t1) -> {
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = format.parse(strings[0]);
                date2 = format.parse(t1[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date1.compareTo(date2);
        });

        return list;
    }

    private void locMetrics() {

        /**
         * Questo metodo prende sfrutta il file CSV creato attraverso locTouched() e va ad eseguire
         * delle misurazioni per determinare le linee di codice che sono state modificate per
         * ogni classe ad ogni release del progetto.
         *
         * [Data, Classe, LocAdded, LocDeleted, LocTouched, MaxLineTouched, AverageLineTouched]
         *
         */


        List<String[]> ver = new ArrayList<>();
        List<String[]> ver2 = new ArrayList<>();
        List<String[]> classes = new ArrayList<>();
        List<String[]> outL = new ArrayList<>();

        List<String[]> ret = new ArrayList<>();

        int lock = 0; //semaforo

        Date dateVer;
        Date date;


        try (FileReader fileReader = new FileReader(version);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(classPath);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileReader fileReader2 = new FileReader(outLoc);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             FileWriter fileWriter = new FileWriter(locMetricsPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            ver = csvReader.readAll();
            ver2 = ver.subList(1, ver.size());

            classes = csvReader1.readAll();
            outL = csvReader2.readAll();


            sortByDate(outL); // sorting della lista

            int max = 0;
            List<Integer> maxList = new ArrayList<>();
            int average = 0;

            for (String[] str : ver2) {
                dateVer = formatter.parse(str[3]);
                for (String[] str2 : classes) {
                    for (String[] str3 : outL) {
                        date = format.parse(str3[0]);
                        if (str2[2].equals(str3[1])) {
                            maxList.add(Integer.parseInt(str3[2]));
                            for (Integer z : maxList) {
                                max = Integer.max(max, z);
                                average = max / Integer.parseInt(str[0]);
                            }
                            if (date.compareTo(dateVer) > 0 && lock == 0) {
                                lock++;
                                ret.add(new String[]{str[0], str2[2], str3[2], str3[3], str3[4], Integer.toString(max), Double.toString(average)});
                            }
                        }
                    }
                    if (lock == 0) {
                        ret.add(new String[]{str[0], str2[2], "0", "0", "0", "0", "0"});
                    }
                    maxList = new ArrayList<>();
                    max = 0;
                    average = 0;
                    lock = 0;
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(ret);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void retrieveLocFromTrees() throws IOException {
        /**
         * in --> classes.csv, associationCommitBlame.csv
         *
         * Questo metodo restituisce un file CSV contenente le classi e l'analisi sulle linee di codice che sono state
         * aggiunte, eliminate e la loro somma per ogni revisione del file.
         *
         * out --> outLocClasses.csv [Data, Classe, LocAdded, LocDeleted, LocTouched]
         */

        List<String[]> trees = new ArrayList<>();
        List<String[]> out = new ArrayList<>();

        int linesAdded = 0;
        int linesDeleted = 0;
        int linesTouched = 0;

        //setup della repo
        Git git = new Git(new FileRepository(gitPath));
        Repository repository = git.getRepository();
        /////

        try (FileReader fileReader1 = new FileReader(assCoBlm);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileWriter fileWriter = new FileWriter(outLoc);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            trees = csvReader1.readAll();

            for (int i = 0; i < trees.size(); i++) {
                for (int x = 0; x < trees.size(); x++) {

                    Date dateTreeI = format.parse(trees.get(i)[0]);
                    Date dateTreeX = format.parse(trees.get(x)[0]);

                    //controllo sul nome della classe
                    if ((trees.get(i)[2].equals(trees.get(x)[2])) && dateTreeX.after(dateTreeI)) {

                        //ottenimento degli alberi per attraversare i commit
                        ObjectReader reader = repository.newObjectReader();
                        CanonicalTreeParser oldTree = new CanonicalTreeParser();
                        ObjectId oldCommit = ObjectId.fromString(trees.get(i)[1].substring(5, trees.get(i)[1].length() - 7));
                        oldTree.reset(reader, oldCommit);

                        ObjectId newCommit = ObjectId.fromString(trees.get(x)[1].substring(5, trees.get(x)[1].length() - 7)); //!!!!!
                        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                        newTreeIter.reset(reader, newCommit);

                        // Use a DiffFormatter to compare new and old tree and return a list of changes
                        DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                        diffFormatter.setRepository(git.getRepository());
                        diffFormatter.setContext(0);
                        List<DiffEntry> entries = diffFormatter.scan(newTreeIter, oldTree);

                        for (DiffEntry entry : entries) {
                            for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
                                linesDeleted += edit.getEndA() - edit.getBeginA();
                                linesAdded += edit.getEndB() - edit.getBeginB();
                                linesTouched = linesAdded + linesDeleted;

                            }
                        }
                        out.add(new String[]{trees.get(x)[0], trees.get(i)[2], Integer.toString(linesAdded), Integer.toString(linesDeleted), Integer.toString(linesTouched)});
                        /// data, classe, locAdded, locDeleted, locTouched
                        linesAdded = 0;
                        linesDeleted = 0;
                        linesTouched = 0;
                    }

                }


            }

            csvWriter.flush();
            csvWriter.writeAll(out);


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    private List<String[]> sizeAndAgeOfClasses() throws IOException {
        /**
         * in --> classes.csv
         *
         * Attraverso questo metodo andiamo a calcolare la lunghezza di una classe
         * espressa in termini di linee di codice che la compongono.
         *
         * out --> List<String[]>
         */

        List<String[]> cls = new ArrayList<>();
        List<String[]> ver = new ArrayList<>();
        List<String[]> ver2 = new ArrayList<>();

        List<String[]> ret = new ArrayList<>();
        Integer size;
        int weeks = 0;
        Date commitTime;
        Date verTime;

        Git git = new Git(new FileRepository(gitPath));

        try (FileReader fileReader = new FileReader(classPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(version);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileWriter fileWriter = new FileWriter(sizeAndAgePath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            cls = csvReader.readAll();
            ver = csvReader1.readAll();
            ver2 = ver.subList(1, ver.size());

            for (String[] strings : ver2) {
                verTime = formatter.parse(strings[3]);
                DateTime versionTime = new DateTime(verTime);
                for (String[] str : cls) {

                    BlameCommand blameCommand = git.blame()
                            .setStartCommit(git.getRepository().resolve("HEAD"))
                            .setFilePath(str[1].substring(1));

                    BlameResult blameResult = blameCommand.call();

                    size = blameResult.getResultContents().size();
                    commitTime = blameResult.getSourceCommitter(0).getWhen();

                    DateTime cmtTime = new DateTime(commitTime);
                    weeks = Weeks.weeksBetween(versionTime, cmtTime).getWeeks();
                    if (weeks < 0) {
                        weeks = 0; //poniamo a 0 il numero di settimane se la data del commit
                        // è precedente alla data della versione, questo infatti significa che la
                        // classe, prima di quella versione, non esisteva.
                    }
                    ret.add(new String[]{strings[0], str[2], Integer.toString(size), Integer.toString(weeks)});
                }
            }

            csvWriter.flush();
            csvWriter.writeAll(ret);

        } catch (IOException | GitAPIException | ParseException e) {
            e.printStackTrace();
        }

        return ret;

    }

}
