package writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesWriter {

    public static void main(String[] args){

        final Logger logger = Logger.getLogger(PropertiesWriter.class.getName());

        try (OutputStream output = new FileOutputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config.properties")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("gitDirBOOKPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook");
            prop.setProperty("gitDirTAJOPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirTajo");
            prop.setProperty("commitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\commits.csv");
            prop.setProperty("gitPathBOOK", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook\\.git");
            prop.setProperty("BugTicketFromJira", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\BugTicket.csv");
            prop.setProperty("resourcePath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources");
            prop.setProperty("projectNameBOOK", "BOOKKEEPER");
            prop.setProperty("projectNameTAJO", "TAJO");
            prop.setProperty("gitUrlBOOK", "https://github.com/apache/bookkeeper");
            prop.setProperty("gitUrlTAJO", "https://github.com/apache/bookkeeper");
            prop.setProperty("classesPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\Classes.csv");
            prop.setProperty("blameRes", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blame.csv");
            prop.setProperty("AVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\AV.csv");
            prop.setProperty("versionInfoBOOK", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\BOOKKEEPERVersionInfo.csv");
            prop.setProperty("blameFinal", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\blameFinal.csv");
            prop.setProperty("buggyPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\buggy.csv");
            prop.setProperty("AssCB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\AssociationCommitBlame.csv");
            prop.setProperty("AssAB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\AssociationAVBlame.csv");
            prop.setProperty("BugTicketAV", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\BugAV.csv");
            prop.setProperty("variables", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\var.csv");
            prop.setProperty("outLocClasses", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\outLocClasses.csv");


            // save properties to project root folder
            prop.store(output, null);

            String properties = String.valueOf(prop);

            logger.log(Level.INFO, properties);

        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        }
    }
}
