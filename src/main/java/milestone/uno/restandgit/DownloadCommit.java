package milestone.uno.restandgit;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import writer.PropertiesWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadCommit {

    private static final Logger LOGGER = Logger.getLogger(DownloadCommit.class.getName());

    static String projName = "";
    static String path = "";
    static String commitPath = "";
    static String commitTmp = "";
    static String completePath = "";
    static String gitUrl = "";
    static int lenght;

    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         *
         * 0 --> BOOKKEEPER
         * 1 --> TAJO
         */
        ////////////////carico i dati da config.properties
        String prf = new PropertiesWriter().determinePrefix(value);

        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config" + prf + ".properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            path = prop.getProperty("gitDirPath");
            commitPath = prop.getProperty("commitPath");
            completePath = prop.getProperty("gitPath");
            gitUrl = prop.getProperty("gitUrl");
            projName = prop.getProperty("projectName");
            lenght = Integer.parseInt(prop.getProperty("nameLenght"));
            commitTmp = prop.getProperty("tmp");


        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, String.valueOf(ex));
        }
        ///////////////////////////////////////

    }

    public void getAllCommits() throws GitAPIException {

        /**
         * Questo metodo prende tutti i commit del progetto e crea un file CSV contenente
         * Data, Albero e Ticket.
         *
         * out --> commits.csv
         */

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

        try (FileWriter fileWriter = new FileWriter(commitTmp)) {

            //Impostazione di Git e della repo.
            Git git = Git.open(new File(completePath));

            Repository repository = FileRepositoryBuilder.create(new File(completePath));
            String repo = String.valueOf(repository);
            LOGGER.info(repo);

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit revCommit : commits) { //itero tutti i commit.

                //cast della data per scriverla all'interno del file...
                String pattern = "MM/dd/yyyy HH:mm:ss";
                DateFormat df = new SimpleDateFormat(pattern);
                String date = df.format(revCommit.getAuthorIdent().getWhen());

                if (revCommit.getFullMessage().length() < lenght) {
                    fileWriter.append(date);
                    fileWriter.append(",");
                    fileWriter.append(revCommit.getTree().toString());
                    fileWriter.append(",");
                    fileWriter.append("NONE");
                    fileWriter.append("\n");

                } else {
                    if (revCommit.getFullMessage().substring(0, lenght).contains(projName + "-")) {
                        fileWriter.append(date);
                        fileWriter.append(",");
                        fileWriter.append(revCommit.getTree().toString());
                        fileWriter.append(",");
                        if (revCommit.getFullMessage().substring(0, lenght).contains(":")) {
                            fileWriter.append(revCommit.getFullMessage().substring(0, lenght).replace(":", ""));
                        } else if (revCommit.getFullMessage().substring(0, lenght).contains(" ")) {
                            fileWriter.append(revCommit.getFullMessage().substring(0, lenght).replace(" ", ""));
                        } else {
                            fileWriter.append(revCommit.getFullMessage().substring(0, lenght));
                        }
                        fileWriter.append("\n");

                    }
                }

            }

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }

    }

    private void removeBlankSpace() {
        /**
         * Questa funzione elimina possibile righe vuote dal CSV (il problema è presente su Tajo
         * ma non su Bookkeeper).
         */

        List<String[]> commits;
        List<String[]> res = new ArrayList<>();

        try (FileReader b = new FileReader(commitTmp);
             CSVReader csvReader = new CSVReader(b);
             FileWriter fileWriter = new FileWriter(commitPath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            commits = csvReader.readAll();
            for (String[] str : commits) {
                if (str.length > 1) {
                    res.add(new String[]{str[0], str[1], str[2]});
                }
            }
            csvWriter.writeAll(res);
            csvWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws GitAPIException, IOException {

        importResources(1);
        LOGGER.info("Scrivo tutti i commit eseguiti fino a questo momento all'interno del file.\n");
        new DownloadCommit().getAllCommits();
        new DownloadCommit().removeBlankSpace();

        //delete tempCommit
        Files.deleteIfExists(Paths.get(commitTmp));

        LOGGER.info("Fatto!!\n");
    }
}
