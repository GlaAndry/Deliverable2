package milestone1.restandgit;

import com.opencsv.CSVWriter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadCommit {

    private static final Logger LOGGER = Logger.getLogger(DownloadCommit.class.getName());

    String path = "";
    String commitPath = "";
    String completePath = "";
    String gitUrl = "";

    public void getAllCommits() throws GitAPIException {

        /**
         * Questo metodo prende tutti i commit del progetto e crea un file CSV contenente
         * Data, Albero e Ticket.
         *
         * out --> commits.csv
         */


        ////////////////carico i dati da config.properties
        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            path = prop.getProperty("gitDirBOOKPath");
            commitPath = prop.getProperty("commitPath");
            completePath = prop.getProperty("gitPathBOOK");
            gitUrl = prop.getProperty("gitUrlBOOK");

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, String.valueOf(ex));
        }
        ///////////////////////////////////////

        File dir = new File(path);

        if(!dir.exists()) {
            LOGGER.info("Comando: Clone Repository\nProcedo con il Download...");
            dir.mkdir();
            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(dir)
                    .call();
            LOGGER.info("Clone Repository eseguito correttamente.\n\n");
            LOGGER.info("Eseguire nuovamente per scaricare tutti i commit.\n");
        }

        try(FileWriter fileWriter = new FileWriter(commitPath)){

            //Impostazione di Git e della repo.
            Git git = Git.open(new File(completePath));

            Repository repository = FileRepositoryBuilder.create(new File(completePath));
            String repo = String.valueOf(repository);
            LOGGER.info(repo);
            List<Ref> branches = git.branchList().call();
            for (Ref ref : branches)
            {

                LOGGER.info(ref.getName());

            }

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit revCommit : commits) { //itero tutti i commit.

                //commit.add(revCommit.getFullMessage());

                //cast della data per scriverla all'interno del file...
                String pattern = "MM/dd/yyyy HH:mm:ss";
                DateFormat df = new SimpleDateFormat(pattern);
                String date = df.format(revCommit.getAuthorIdent().getWhen());


                if(revCommit.getFullMessage().length() < 15) {
                    fileWriter.append(date);
                    fileWriter.append(",");
                    fileWriter.append(revCommit.getTree().toString());
                    fileWriter.append(",");
                    fileWriter.append("NONE");
                    fileWriter.append("\n");

                } else {
                    if(revCommit.getFullMessage().substring(0,15).contains("BOOKKEEPER-")){
                        fileWriter.append(date);
                        fileWriter.append(",");
                        fileWriter.append(revCommit.getTree().toString());
                        fileWriter.append(",");
                        if(revCommit.getFullMessage().substring(0,15).contains(":"))  {
                            fileWriter.append(revCommit.getFullMessage().substring(0,15).replace(":",""));
                        } else if (revCommit.getFullMessage().substring(0,15).contains(" ")){
                            fileWriter.append(revCommit.getFullMessage().substring(0,15).replace(" ",""));
                        } else{
                            fileWriter.append(revCommit.getFullMessage().substring(0,15));
                        }
                        fileWriter.append("\n");

                    }
                }

            }

        } catch (IOException e){
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }

    }

    public static void main(String[] args) throws GitAPIException {

        LOGGER.info( "Scrivo tutti i commit eseguiti fino a questo momento all'interno del file.\n");
        new DownloadCommit().getAllCommits();
        LOGGER.info("Fatto!!\n");
    }
}
