package milestone1.engine;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionDivisor {

    /**
     * Lo scopo di questa classe è quello di andare a prendere le versioni delle classi ricavate attraverso il blame
     * e andare a determinare a quale release appartengono. In particolare una versione della classe appartiene ad una
     * determinata release solamente se la data del suo blame è successiva alla release che stiamo considerando e
     * contemporaneamente precedente alla data della release presa in considerazione.
     */

    private static final Logger LOGGER = Logger.getLogger(DownloadCommit.class.getName());


    public static void main(String[] args){


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

            //path = prop.getProperty("gitDirBOOKPath");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private void divisor(){

    }
}
