package writer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputWriterMilestoneUnoDevDue {
    /**
     * Questa classe restituisce il file CSV richiesto contentente tutte le classi con le diverse metriche
     * calcolate.
     *
     * prova --> NRevision, NAuthor
     * prova2 --> NFix
     * prova3 --> LocAdded, LocDeleted, LocTouched, MaxLock, AverageLock
     * prova4 --> Size, Age (in weeks)
     *
     */



    private void mergeCSV(List<String[]> list, List<String[]> list2, List<String[]> list3, List<String[]> list4, List<String[]> list5) throws IOException {

        List<String[]> retList = new ArrayList<>();
        retList.add(new String[] {"Version", "Class", "NFix", "NRevision", "NAuthor", "LocAdded", "LocDeleted", "LocTouched", "MaxLockAdded", "AverageLockAdded", "Size", "Age", "Buggy"});

        for(int i = 0; i < list.size(); i++){

            retList.add(new String[] {list.get(i)[0]
                    , list.get(i)[1]
                    , list.get(i)[2]
                    , list2.get(i)[2]
                    , list2.get(i)[3]
                    , list3.get(i)[2]
                    , list3.get(i)[3]
                    , list3.get(i)[4]
                    , list3.get(i)[5]
                    , list3.get(i)[6]
                    , list4.get(i)[2]
                    , list4.get(i)[3]
                    , list5.get(i)[2]});
        }

        try(FileWriter fileWriter = new FileWriter("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\bookkeeper\\M1D2.csv");
            CSVWriter csvWriter = new CSVWriter(fileWriter)){

            csvWriter.flush();
            csvWriter.writeAll(retList);

        }
    }


    public static void main(String[] args) throws IOException {


        List<String[]> lista1 = new ArrayList<>();
        List<String[]> lista2 = new ArrayList<>();
        List<String[]> lista3 = new ArrayList<>();
        List<String[]> lista4 = new ArrayList<>();
        List<String[]> lista5 = new ArrayList<>();

        try(FileReader fileReader = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\prova2.csv");
            FileReader fileReader1 = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\prova.csv");
            FileReader fileReader2 = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\prova3.csv");
            FileReader fileReader3 = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\outputMilestone1\\prova4.csv");
            FileReader fileReader4 = new FileReader("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\src\\main\\resources\\buggy.csv");
            CSVReader csvReader = new CSVReader(fileReader);
            CSVReader csvReader1 = new CSVReader(fileReader1);
            CSVReader csvReader2 = new CSVReader(fileReader2);
            CSVReader csvReader3 = new CSVReader(fileReader3);
            CSVReader csvReader4 = new CSVReader(fileReader4)){

            lista1 = csvReader.readAll();
            lista2 = csvReader1.readAll();
            lista3 = csvReader2.readAll();
            lista4 = csvReader3.readAll();
            lista5 = csvReader4.readAll();


        } catch (IOException e) {
            e.printStackTrace();
        }

        new OutputWriterMilestoneUnoDevDue().mergeCSV(lista1,lista2,lista3,lista4,lista5);

    }
}
