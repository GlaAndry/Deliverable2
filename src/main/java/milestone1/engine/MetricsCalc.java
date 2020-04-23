package milestone1.engine;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.builder.Diff;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsCalc {

    /**
     * Questa classe è responsabile del calcolo delle metriche delle classi.
     * In particolare, per il deliverable, dobbiamo considerare solamente 9 metriche
     * di nostra scelta.
     */

    private static final Logger LOGGER = Logger.getLogger(MetricsCalc.class.getName());



    static String classPath = "";
    static String gitPath = "";

    public static void main(String[] args) throws IOException {


        importResources();
        //new MetricsCalc().sizeOfClass();
        new MetricsCalc().locTouched();
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

            classPath = prop.getProperty("classesPath");
            gitPath = prop.getProperty("gitPathBOOK");

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void locTouched() throws IOException {
        /**
         * in --> classes.csv
         *
         * Questo metodo restituisce la somma delle linee di codice che sono state aggiunte
         * ed eliminate per ogni revisione del file.
         *
         * out -->
         */

        String blm = "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blameFinal.csv";

        List<String[]> cls = new ArrayList<>();
        List<String[]> sizes = new ArrayList<>();
        Integer size;
        Integer loc;


        Git git = new Git(new FileRepository(gitPath));

        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged = 0;

        Repository repository = git.getRepository();

        ObjectReader reader = repository.newObjectReader();

        //CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        //CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        //List<DiffEntry> diffs = null;

        String tree = "";
        int counter = 0;

        try(FileReader fileReader = new FileReader(classPath);
            CSVReader csvReader = new CSVReader(fileReader)) {

            cls = csvReader.readAll();

            for(String[] str : cls){

                //FileRepository repo = new FileRepository(new File("repo/.git"));
                RevWalk rw = new RevWalk(repository);
                RevCommit commit = rw.parseCommit(repository.resolve("HEAD")); // Any ref will work here (HEAD, a sha1, tag, branch)
                //RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

                Iterable<RevCommit> commits = git.log().all().call();

                for (RevCommit revCommit : commits) {

                    RevCommit parent = revCommit;


                    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                    df.setRepository(repository);
                    df.setDiffComparator(RawTextComparator.DEFAULT);
                    df.setDetectRenames(true);
                    List<DiffEntry> diffs;
                    diffs = df.scan(parent.getTree(), commit.getTree());
                    filesChanged = diffs.size();
                    for (DiffEntry diff : diffs) {
                        for (Edit edit : df.toFileHeader(diff).toEditList()) {
                            linesDeleted += edit.getEndA() - edit.getBeginA();
                            linesAdded += edit.getEndB() - edit.getBeginB();
                            System.out.println(linesAdded);
                            System.out.println(linesDeleted);

                        }
                    }
                }









                /*

                System.out.println("2");

                old = repository.resolve("HEAD~1" + "^{tree}");
                head = repository.resolve("HEAD^{tree}");
                //Reset this parser to walk through the given tree
                oldTreeIter.reset(reader, blameResult.getSourceCommit(0).getTree());
                newTreeIter.reset(reader, blameResult.getSourceCommit(0).getTree());

                System.out.println("3");

                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                //TODO sistemare java.lang.IllegalStateException: Repository is required.
                // e fare le altre metriche.
                diffs = df.scan(oldTreeIter, newTreeIter);

                for (DiffEntry diff : diffs) {
                    for (Edit edit : df.toFileHeader(diff).toEditList()) {
                        System.out.println("4");

                        linesDeleted += edit.getEndA() - edit.getBeginA();
                        linesAdded += edit.getEndB() - edit.getBeginB();
                    }
                }

                 */
            }


        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }





        /*
        FileRepository repo;


        int linesAdded = 0;
        int linesDeleted = 0;
        int filesChanged = 0;
        try {
            repo = new FileRepository(new File("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook\\.git"));
            RevWalk rw = new RevWalk(repo);
            RevCommit commit = rw.parseCommit(repo.resolve("HEAD")); // Any ref will work here (HEAD, a sha1, tag, branch)
            RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repo);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs;
            diffs = df.scan(parent.getTree(), commit.getTree());
            filesChanged = diffs.size();
            for (DiffEntry diff : diffs) {
                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                    linesAdded += edit.getEndB() - edit.getBeginB();
                }
            }

            System.out.println(linesAdded);
            System.out.println(linesDeleted);


        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

         */



/*
        Git git = new Git(new FileRepository(gitPath));

        try(FileReader fileReader = new FileReader(classPath);
            CSVReader csvReader = new CSVReader(fileReader)){

            cls = csvReader.readAll();

            for(String[] str : cls){

                BlameCommand blameCommand = git.blame()
                        .setStartCommit(git.getRepository().resolve("HEAD"))
                        .setFilePath(str[1].substring(1));

                BlameResult blameResult = blameCommand.call();


                size = blameResult.getResultContents().size();

                for(int i = 0; i < size; i++){

                    loc = blameResult.
                }

            }


        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

 */

    }

    private List<String[]> sizeOfClass() throws IOException {
        /**
         * in --> classes.csv
         *
         * Attraverso questo metodo andiamo a calcolare la lunghezza di una classe
         * espressa in termini di linee di codice che la compongono.
         *
         * out -->
         */

        List<String[]> cls = new ArrayList<>();
        List<String[]> sizes = new ArrayList<>();
        Integer size;



        Git git = new Git(new FileRepository(gitPath));

        try(FileReader fileReader = new FileReader(classPath);
            CSVReader csvReader = new CSVReader(fileReader)){

            cls = csvReader.readAll();

            for(String[] str : cls){

                BlameCommand blameCommand = git.blame()
                        .setStartCommit(git.getRepository().resolve("HEAD"))
                        .setFilePath(str[1].substring(1));

                BlameResult blameResult = blameCommand.call();


                size = blameResult.getResultContents().size();
                System.out.println(size);
                sizes.add(new String[] {Integer.toString(size), str[1]});

            }


        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }

        return sizes;

    }

    //TODO calcolo delle metriche
}
