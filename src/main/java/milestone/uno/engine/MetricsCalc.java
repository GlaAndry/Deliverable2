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

    static String nBugFixPath = ""; //prova2
    static String nRevAndAuthPath = ""; //prova
    static String locMetricsPath = ""; //prova3
    static String sizeAndAgePath = ""; //prova4

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    int lock = 0; //semaforo


    public static void main(String[] args) throws IOException {


        importResources(0);

        List<String[]> classes;
        List<String[]> versions;
        List<String[]> outL;
        List<String[]> assCoBlame;


        try (FileReader fileReader = new FileReader(classPath);
             CSVReader csvReader = new CSVReader(fileReader);
             FileReader fileReader1 = new FileReader(version);
             CSVReader csvReader1 = new CSVReader(fileReader1);
             FileReader fileReader2 = new FileReader(outLoc);
             CSVReader csvReader2 = new CSVReader(fileReader2);
             FileReader fileReader3 = new FileReader(assCoBlm);
             CSVReader csvReader3 = new CSVReader(fileReader3)) {

            classes = csvReader.readAll();
            versions = csvReader1.readAll();
            outL = csvReader2.readAll();
            assCoBlame = csvReader3.readAll();

            new MetricsCalc().numberOfRevisionsAndAuthors(classes, versions); //fatto
            new MetricsCalc().locMetrics(versions, classes, outL);
            new MetricsCalc().retrieveLocFromTrees(assCoBlame); //impiega almeno 3.5 ore per Tajo e 2 ore per Book

        } catch (ParseException | GitAPIException e) {
            e.printStackTrace();
        }


        new MetricsCalc().sizeAndAgeOfClasses();
        new MetricsCalc().numberOfBugFixes();


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

            if (value == 0) {
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
            if (value == 1) {
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

    private void writeOnCSV(List<String[]> list, String path) throws IOException {

        try (FileWriter fileWriter = new FileWriter(path);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeAll(list);
            csvWriter.flush();
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


    private List<String[]> numberOfRevisionsAndAuthors(List<String[]> cls, List<String[]> ver) throws IOException, ParseException, GitAPIException {
        /**
         * in --> classes.csv, versionInfo.csv
         *
         * Questo metodo restituisce il numero di commit che sono stati effettuati per
         * una determinata classe in una determinata release. (Sommando alle release
         * successive il numero ottenuto dalle release precedenti.
         *
         * out --> Csv con colonne [INDEX VERSION,CLASSE,REVNUM, AUTHNUM]
         */

        List<String[]> ver2;
        List<String[]> revNum = new ArrayList<>();

        ///
        List<String[]> authorList = new ArrayList<>();
        int lock2 = 0;
        String authName;
        ///


        Integer size;
        Date commitDate;
        Date versionDate;

        int commitCounter = 0;

        Git git = new Git(new FileRepository(gitPath));


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

                    if (versionDate.compareTo(commitDate) > 0) {
                        /**
                         * Aumento il contatore dei commit e aggiungiamo anche gli autori.
                         */
                        authName = blameResult.getSourceAuthor(i).getName();
                        if (lock2 == 0) {
                            lock2++;
                            authorList.add(new String[]{authName});
                        }
                        commitCounter++;
                    }

                }

                revNum.add(new String[]{strings[0], str[2], Integer.toString(commitCounter), Integer.toString(authorList.size())});
                commitCounter = 0;
                lock2 = 0;
                authorList = new ArrayList<>();

            }


        }

        writeOnCSV(revNum, nRevAndAuthPath);


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

    private int[] calculateMaxAndAverage(String numLock, String version, List<Integer> maxList, int[] returnVal) {

        /**
         * Metodo sfruttato da LocMetrics per andare ad effettuare i controlli sul MAXLOCK ed AVERAGE
         */

        int max = 0;
        int average = 0;

        maxList.add(Integer.parseInt(numLock));
        for (Integer z : maxList) {
            max = Integer.max(max, z);
            average = max / Integer.parseInt(version);
        }


        returnVal[0] = max;
        returnVal[1] = average;

        return returnVal;

    }

    private List<String[]> addMaxAndAverage(String[] ver, String[] cls, String[] outL, List<Integer> maxList, int[] val, List<String[]> ret) throws ParseException {



        Date dateVer = formatter.parse(ver[3]);
        Date date = format.parse(outL[0]);

        if (cls[2].equals(outL[1])) {

            calculateMaxAndAverage(outL[2], ver[0], maxList, val);

            if (date.compareTo(dateVer) > 0 && lock == 0) {
                lock++;
                ret.add(new String[]{ver[0], cls[2], outL[2], outL[3], outL[4], Integer.toString(val[0]), Integer.toString(val[1])});
                //val[0] = max, val[1]= average
            }
        }
        return ret;
    }


    private void locMetrics(List<String[]> ver, List<String[]> classes, List<String[]> outL) throws IOException, ParseException {

        /**
         * Questo metodo prende sfrutta il file CSV creato attraverso locTouched() e va ad eseguire
         * delle misurazioni per determinare le linee di codice che sono state modificate per
         * ogni classe ad ogni release del progetto.
         *
         * [Data, Classe, LocAdded, LocDeleted, LocTouched, MaxLineTouched, AverageLineTouched]
         *
         */

        List<String[]> ver2;
        List<String[]> ret = new ArrayList<>();

        ver2 = ver.subList(1, ver.size());

        sortByDate(outL); // sorting della lista

        List<Integer> maxList = new ArrayList<>();
        int[] val = new int[2];

        for (String[] str : ver2) {
            for (String[] str2 : classes) {
                for (String[] str3 : outL) {
                    addMaxAndAverage(str, str2, str3, maxList, val, ret);
                }
                if (lock == 0) {
                    ret.add(new String[]{str[0], str2[2], "0", "0", "0", "0", "0"});
                }
                maxList = new ArrayList<>();
                val = new int[2];
                lock = 0;
            }
        }

        writeOnCSV(ret, locMetricsPath);

    }

    private int[] lines(List<DiffEntry> entries, DiffFormatter diffFormatter) {

        /**
         * Metodo di supporto a retrieveLocFromTrees per il calcolo delle
         * Linee di codice aggiunte, eliminate e toccate (cioè somma di linee aggiunte ed
         * eliminate)
         * 0--> added
         * 1--> deleted
         * 2-->touched
         */

        int[] ret = new int[3];

        try {

            for (DiffEntry entry : entries) {
                for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
                    ret[0] += edit.getEndA() - edit.getBeginA();
                    ret[1] += edit.getEndB() - edit.getBeginB();
                    ret[2] = ret[1] + ret[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private void retrieveLocFromTrees(List<String[]> trees) throws IOException, ParseException {
        /**
         * in --> classes.csv, associationCommitBlame.csv
         *
         * Questo metodo restituisce un file CSV contenente le classi e l'analisi sulle linee di codice che sono state
         * aggiunte, eliminate e la loro somma per ogni revisione del file.
         *
         * out --> outLocClasses.csv [Data, Classe, LocAdded, LocDeleted, LocTouched]
         */

        List<String[]> out = new ArrayList<>();

        int[] line;

        //setup della repo
        Git git = new Git(new FileRepository(gitPath));
        Repository repository = git.getRepository();
        /////


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

                    line = lines(entries, diffFormatter);

                    out.add(new String[]{trees.get(x)[0], trees.get(i)[2], Integer.toString(line[0]), Integer.toString(line[1]), Integer.toString(line[2])});
                    /// data, classe, locAdded, locDeleted, locTouched

                }

            }


        }

        writeOnCSV(out, outLoc);


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
