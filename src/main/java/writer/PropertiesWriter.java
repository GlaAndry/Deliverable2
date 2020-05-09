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

            // set the properties value for BOOKKEEPER Project
            prop.setProperty("gitDirBOOKPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook");
            prop.setProperty("commitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\commits.csv");
            prop.setProperty("gitPathBOOK", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook\\.git");
            prop.setProperty("BugTicketFromJira", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\BugTicket.csv");
            prop.setProperty("resourcePath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources");
            prop.setProperty("projectNameBOOK", "BOOKKEEPER");
            prop.setProperty("nameLenghtBOOK", "15");
            prop.setProperty("gitUrlBOOK", "https://github.com/apache/bookkeeper");
            prop.setProperty("classesPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\Classes.csv");
            prop.setProperty("blameRes", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\blame.csv");
            prop.setProperty("blameNew", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\blameNew.csv");
            prop.setProperty("AVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\AV.csv");
            prop.setProperty("FVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\FV.csv");
            prop.setProperty("versionInfoBOOK", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\BOOKKEEPERVersionInfo.csv");
            prop.setProperty("blameFinal", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\blameFinal.csv");
            prop.setProperty("buggyPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\buggy.csv");
            prop.setProperty("AssCB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\AssociationCommitBlame.csv");
            prop.setProperty("AssAB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\AssociationAVBlame.csv");
            prop.setProperty("AssOV", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\associationOVBlame.csv");
            prop.setProperty("BugTicketAV", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\BugAV.csv");
            prop.setProperty("variables", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\var.csv");
            prop.setProperty("outLocClasses", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\outLocClasses.csv");
            prop.setProperty("numBugFix", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\numBugFix.csv");
            prop.setProperty("numRevAuth", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\numRevAuth.csv");
            prop.setProperty("locMetrics", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\locMetrics.csv");
            prop.setProperty("sizeAndAge", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\sizeAndAge.csv");
            prop.setProperty("outBOOK", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\bookkeeper\\M1D2.csv");


            //set the properties value for TAJO Project
            prop.setProperty("gitDirTAJOPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirTajo");
            prop.setProperty("projectNameTAJO", "TAJO");
            prop.setProperty("gitUrlTAJO", "https://github.com/apache/tajo");
            prop.setProperty("versionInfoTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\TAJOVersionInfo.csv");
            prop.setProperty("commitPathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\commitsTAJO.csv");
            prop.setProperty("gitPathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirTajo\\.git");
            prop.setProperty("nameLenghtTAJO", "10");
            prop.setProperty("classesPathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\ClassesTAJO.csv");
            prop.setProperty("AVpathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AVTAJO.csv");
            prop.setProperty("FVpathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\FVTAJO.csv");
            prop.setProperty("BugTicketFromJiraTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\BugTicketTAJO.csv");
            prop.setProperty("blameResTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameTAJO.csv");
            prop.setProperty("blameNewTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameNewTAJO.csv");
            prop.setProperty("blameFinalTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameFinalTAJO.csv");
            prop.setProperty("BugTicketAVTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\BugAVTAJO.csv");
            prop.setProperty("variablesTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\varTAJO.csv");
            prop.setProperty("AssABTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AssociationAVBlameTAJO.csv");
            prop.setProperty("AssCBTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AssociationCommitBlameTAJO.csv");
            prop.setProperty("AssOVTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\associationOVBlameTAJO.csv");
            prop.setProperty("buggyPathTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\buggyTAJO.csv");
            prop.setProperty("outLocClassesTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\outLocClassesTAJO.csv");
            prop.setProperty("numBugFixTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\numBugFixTAJO.csv");
            prop.setProperty("numRevAuthTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\numRevAuthTAJO.csv");
            prop.setProperty("locMetricsTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\locMetricsTAJO.csv");
            prop.setProperty("sizeAndAgeTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\sizeAndAgeTAJO.csv");
            prop.setProperty("outTAJO", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\tajo\\M1D2TAJO.csv");

            // save properties to project root folder
            prop.store(output, null);

            String properties = String.valueOf(prop);

            logger.log(Level.INFO, properties);

        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        }
    }
}
