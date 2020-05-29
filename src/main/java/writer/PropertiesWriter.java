package writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesWriter {

    Properties prop = new Properties();

    public String determinePrefix(int value){

        String ret = "";

        if (value == 0) {
            ret = "Book";
        } else if (value == 1) {
            ret = "Tajo";
        }

        return ret;
    }

    private void writeBookkeeper(){


        final Logger logger = Logger.getLogger(PropertiesWriter.class.getName());

        try (OutputStream output = new FileOutputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\configBook.properties")) {


            ///////MILESTONE 1///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // set the properties value for BOOKKEEPER Project
            prop.setProperty("gitDirPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook");
            prop.setProperty("commitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\commits.csv");
            prop.setProperty("gitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirBook\\.git");
            prop.setProperty("BugTicketFromJira", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\BugTicket.csv");
            prop.setProperty("resourcePath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources");
            prop.setProperty("projectName", "BOOKKEEPER");
            prop.setProperty("nameLenght", "15");
            prop.setProperty("gitUrl", "https://github.com/apache/bookkeeper");
            prop.setProperty("classesPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\Classes.csv");
            prop.setProperty("blameRes", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\blame.csv");
            prop.setProperty("blameNew", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\blameNew.csv");
            prop.setProperty("AVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\AV.csv");
            prop.setProperty("FVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\FV.csv");
            prop.setProperty("versionInfo", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\BOOKKEEPERVersionInfo.csv");
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
            prop.setProperty("out", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\bookkeeper\\M1D2.csv");
            prop.setProperty("pValue", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileBOOK\\valueOfP.csv");



            ////////////////////////MILESTONE 2///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // set the properties value for BOOKKEEPER Project
            prop.setProperty("M1D2", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\csvFile\\M1D2BOOK.csv");
            prop.setProperty("M1D2ARFF", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\arffFile\\M1D2BOOK.arff");
            prop.setProperty("prefix", "BOOK");
            prop.setProperty("M1D2TEST", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outCSVmethods\\testing");
            prop.setProperty("M1D2TRAIN", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outCSVmethods\\training");
            prop.setProperty("OUT", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outMilestone2\\outM1D2BOOK.csv");
            prop.setProperty("NUM", "7");
            prop.setProperty("ARFFTESTING", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\arffFile\\testing");
            prop.setProperty("ARFFTRAINING", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\arffFile\\training");

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // save properties to project root folder
            prop.store(output, null);

            String properties = String.valueOf(prop);

            logger.log(Level.INFO, properties);

        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        }

    }

    private void writeTajo(){

        final Logger logger = Logger.getLogger(PropertiesWriter.class.getName());

        try (OutputStream output = new FileOutputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\configTajo.properties")) {

            //set the properties value for TAJO Project
            prop.setProperty("gitDirPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirTajo");
            prop.setProperty("projectName", "TAJO");
            prop.setProperty("gitUrl", "https://github.com/apache/tajo");
            prop.setProperty("versionInfo", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\TAJOVersionInfo.csv");
            prop.setProperty("commitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\commitsTAJO.csv");
            prop.setProperty("gitPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\GitDirTajo\\.git");
            prop.setProperty("nameLenght", "10");
            prop.setProperty("classesPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\ClassesTAJO.csv");
            prop.setProperty("AVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AVTAJO.csv");
            prop.setProperty("FVpath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\FVTAJO.csv");
            prop.setProperty("BugTicketFromJira", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\BugTicketTAJO.csv");
            prop.setProperty("blameRes", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameTAJO.csv");
            prop.setProperty("blameNew", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameNewTAJO.csv");
            prop.setProperty("blameFinal", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\blameFinalTAJO.csv");
            prop.setProperty("BugTicketAV", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\BugAVTAJO.csv");
            prop.setProperty("variables", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\varTAJO.csv");
            prop.setProperty("AssAB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AssociationAVBlameTAJO.csv");
            prop.setProperty("AssCB", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\AssociationCommitBlameTAJO.csv");
            prop.setProperty("AssOV", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\associationOVBlameTAJO.csv");
            prop.setProperty("buggyPath", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\buggyTAJO.csv");
            prop.setProperty("outLocClasses", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\outLocClassesTAJO.csv");
            prop.setProperty("numBugFix", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\numBugFixTAJO.csv");
            prop.setProperty("numRevAuth", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\numRevAuthTAJO.csv");
            prop.setProperty("locMetrics", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\locMetricsTAJO.csv");
            prop.setProperty("sizeAndAge", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\sizeAndAgeTAJO.csv");
            prop.setProperty("out", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\tajo\\M1D2TAJO.csv");
            prop.setProperty("pValue", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\fileTAJO\\valueOfPTAJO.csv");


            ////////////////////////MILESTONE 2///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //set the properties value for TAJO Project
            prop.setProperty("M1D2", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\csvFile\\M1D2TAJO.csv");
            prop.setProperty("M1D2ARFF", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\M1D2BOOK.arff");
            prop.setProperty("prefix", "TAJO");
            prop.setProperty("M1D2TEST", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outCSVmethods\\testing");
            prop.setProperty("M1D2TRAIN", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outCSVmethods\\training");
            prop.setProperty("OUT", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outMilestone2\\outM1D2TAJO.csv");
            prop.setProperty("NUM", "5");
            prop.setProperty("ARFFTESTING", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\arffFile\\testing");
            prop.setProperty("ARFFTRAINING", "C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\arffFile\\training");

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // save properties to project root folder
            prop.store(output, null);

            String properties = String.valueOf(prop);

            logger.log(Level.INFO, properties);

        } catch (IOException e) {
            logger.log(Level.WARNING, String.valueOf(e));
        }
    }


    public static void main(String[] args) {
        new PropertiesWriter().writeBookkeeper();
        new PropertiesWriter().writeTajo();
    }
}
