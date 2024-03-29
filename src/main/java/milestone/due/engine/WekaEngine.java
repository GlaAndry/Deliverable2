package milestone.due.engine;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import writer.PropertiesWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WekaEngine {

    /**
     * Questa classe ha lo scopo di andare a calcolare, attraverso il metodo Walk Forward
     * Le statistiche. In particolare il numero di run da effettuare sarà pari al numero
     * delle parti in cui è diviso il file - 1 --> eg: Bookeeper avrà n=6 in quanto le release
     * sono 7 in totale
     * <p>
     * Una volta eseguite tutte le run verrà fatta la media dei risultati.
     * <p>
     * Classificatori --> RandomForest, NaiveBayes, IBK
     */

    private static final Logger LOGGER = Logger.getLogger(WekaEngine.class.getName());

    static String m1d2Test = "";
    static String m1d2Train = "";
    static String m1d2TestCSV = "";
    static String m1d2TrainCSV = "";
    static String prefix = "";
    static String out = "";
    static String numRelease = "";

    static String classi;

    static final String RANDOM_FOREST = "Random Forest";
    static final String NAIVE_BAYES = "Naive Bayes";
    static final String IBK = "IBK";

    static final String NO_SELECTION = "No Selection";
    static final String BEST_FIRST = "Best First";

    static final String NO_SAMPLING = "No Sampling";
    static final String UNDERSAMPLING = "Undersampling";
    static final String OVERSAMPLING = "Oversampling";
    static final String SMOTE = "SMOTE";

    double defTrain = 0;
    double defTest = 0;

    private static void importResources(int value) {
        /**
         * Attraverso config.properties andiamo a caricare i valori delle stringhe per le open e le write dei file.
         * Necessario al fine di evitare copie inutili dello stesso codice in locazioni diverse della classe.
         */
        String prf = new PropertiesWriter().determinePrefix(value);

        try (InputStream input = new FileInputStream("C:\\Users\\Alessio Mazzola\\Desktop\\Prove ISW2\\Deliverable2\\config" + prf + ".properties")) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);


            m1d2Test = prop.getProperty("ARFFTESTING");
            m1d2Train = prop.getProperty("ARFFTRAINING");
            m1d2TestCSV = prop.getProperty("M1D2TEST");
            m1d2TrainCSV = prop.getProperty("M1D2TRAIN");
            prefix = prop.getProperty("prefix");
            out = prop.getProperty("OUT");
            numRelease = prop.getProperty("NUM");


        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.valueOf(e));
        }
    }

    private Object[] calculateNoFilter(Classifier classifier, ConverterUtils.DataSource train, ConverterUtils.DataSource test, String csvTest, String csvTrain, int mode) throws Exception {

        Instances testing = test.getDataSet();
        Instances training = train.getDataSet();

        int numAttr = training.numAttributes();
        training.setClassIndex(numAttr - 1);
        testing.setClassIndex(numAttr - 1);

        classifier.buildClassifier(training);

        double percentage;

        try (FileReader fileReader = new FileReader(csvTest);
             FileReader fileReader1 = new FileReader(csvTrain);
             CSVReader csvReader = new CSVReader(fileReader);
             CSVReader csvReader1 = new CSVReader(fileReader1)) {

            defTrain = new WekaEngine().numberOfYesPercentage(csvReader1, numAttr - 1, "YES");
            defTest = new WekaEngine().numberOfYesPercentage(csvReader, numAttr - 1, "YES");

        }

        Evaluation eval = null;

        if (mode == 0) { //No Filter///////////
            eval = new Evaluation(testing);
            eval.evaluateModel(classifier, testing);
            return new Object[]{eval, defTrain, defTest};

        } else if (mode == 1) { //Undersampling

            Resample resample = new Resample();
            resample.setInputFormat(training);
            FilteredClassifier fc = new FilteredClassifier();

            fc.setClassifier(classifier);

            SpreadSubsample spreadSubsample = new SpreadSubsample();
            String[] opts = new String[]{"-M", "1.0"};
            spreadSubsample.setOptions(opts);
            fc.setFilter(spreadSubsample);

            fc.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(fc, testing); //sampled


            return new Object[]{eval, defTrain, defTest};

        } else if (mode == 2) { //Oversampling

            Resample resample = new Resample();
            resample.setInputFormat(training);
            FilteredClassifier fc = new FilteredClassifier();

            fc.setClassifier(classifier);

            percentage = (1 - defTrain) * 100;

            String[] opts = new String[]{"-B", "1.0", "-Z", String.valueOf(percentage)};
            resample.setOptions(opts);
            fc.setFilter(resample);

            fc.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(fc, testing); //sampled


            return new Object[]{eval, defTrain, defTest};

        } else if (mode == 3) { //SMOTE

            Resample resample = new Resample();
            resample.setInputFormat(training);
            FilteredClassifier fc = new FilteredClassifier();

            SMOTE smote = new SMOTE();
            smote.setInputFormat(training);
            fc.setFilter(smote);
            fc.setClassifier(classifier);

            fc.buildClassifier(training);
            eval = new Evaluation(testing);


            eval.evaluateModel(fc, testing); //sampled
            return new Object[]{eval, defTrain, defTest};
        }
        return new Object[]{eval, defTrain, defTest};
    }

    private Evaluation calculateWithFilter(Classifier classifier, Instances training, Instances testing, Evaluation eval) throws Exception {

        //Filter
        AttributeSelection filter = new AttributeSelection();
        CfsSubsetEval evalFilter = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        //set the algorithm to search backward
        search.setSearchBackwards(true);
        //set the filter to use the evaluator and search algorithm
        filter.setEvaluator(evalFilter);
        filter.setSearch(search);
        //specify the dataset
        filter.setInputFormat(training);
        //apply
        Instances filteredTraining = Filter.useFilter(training, filter);

        int numAttrFiltered = filteredTraining.numAttributes();
        //evaluation with filtered
        filteredTraining.setClassIndex(numAttrFiltered - 1);
        Instances testingFiltered = Filter.useFilter(testing, filter);
        testingFiltered.setClassIndex(numAttrFiltered - 1);
        classifier.buildClassifier(filteredTraining);
        eval.evaluateModel(classifier, testingFiltered);

        return eval;
    }


    public List<String[]> walkForwardValidation(int numOfSteps) throws Exception {

        /**
         * Con questo metodo andiamo ad applicare la tecnica Walk Forward come tecnica di validazione
         * per il nostro dataset, diviso in base alle release.
         *
         * @param numOfSteps: Int --> Numero di passi per la validazione.
         */
        String tst = "";
        String trn = "";
        String tstCSV = "";
        String trnCSV = "";
        String subPrefix = "\\M1D2";


        List<String[]> ret = new ArrayList<>();
        ret.add(new String[]{"Dataset", "#TrainingRelease", "%Training", "%DefectiveInTraining", "%DefectiveInTesting", "Classifier", "Balancing",
                "Feature Selection", "TruePositive", "FalsePositive", "TrueNegative", "FalseNegative", "Precision", "Recall", "AUC", "Kappa"});

        for (int i = 2; i <= numOfSteps; i++) {
            tst = m1d2Test + subPrefix + prefix + i + "testing.arff";
            trn = m1d2Train + subPrefix + prefix + i + "training.arff";
            tstCSV = m1d2TestCSV + subPrefix + prefix + i + "testing.csv";
            trnCSV = m1d2TrainCSV + subPrefix + prefix + i + "training.csv";
            ConverterUtils.DataSource ts = new ConverterUtils.DataSource(tst);
            ConverterUtils.DataSource tr = new ConverterUtils.DataSource(trn);

            RandomForest randomForest = new RandomForest();
            determineClassifierName(randomForest);

            calculateClassifierNoSampling(randomForest, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierOversampling(randomForest, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierUndersampling(randomForest, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierSMOTE(randomForest, tr, ts, i, ret, tstCSV, trnCSV);

            NaiveBayes naiveBayes = new NaiveBayes();
            determineClassifierName(naiveBayes);

            calculateClassifierNoSampling(naiveBayes, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierOversampling(naiveBayes, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierUndersampling(naiveBayes, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierSMOTE(naiveBayes, tr, ts, i, ret, tstCSV, trnCSV);

            IBk iBk = new IBk();
            determineClassifierName(iBk);

            calculateClassifierNoSampling(iBk, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierOversampling(iBk, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierUndersampling(iBk, tr, ts, i, ret, tstCSV, trnCSV);
            calculateClassifierSMOTE(iBk, tr, ts, i, ret, tstCSV, trnCSV);
        }

        return ret;
    }

    private static void determineClassifierName(Classifier classifier) {

        if (classifier.getClass() == RandomForest.class) {
            classi = RANDOM_FOREST;
        } else if (classifier.getClass() == NaiveBayes.class) {
            classi = NAIVE_BAYES;
        } else if (classifier.getClass() == IBk.class) {
            classi = IBK;
        }

    }

    private void calculateClassifierNoSampling(Classifier classifier, ConverterUtils.DataSource train, ConverterUtils.DataSource test,
                                               int counter, List<String[]> list, String csvTest, String csvTrain) throws Exception {

        Object[] obj = calculateNoFilter(classifier, train, test, csvTest, csvTrain, 0);

        Instances testing = test.getDataSet();
        Instances training = train.getDataSet();

        Evaluation eval = (Evaluation) obj[0];

        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, NO_SAMPLING, NO_SELECTION, String.valueOf(eval.truePositiveRate(1)),
                String.valueOf(eval.falsePositiveRate(1)), String.valueOf(eval.trueNegativeRate(1)), String.valueOf(eval.falseNegativeRate(1)), Double.toString(eval.precision(1)),
                Double.toString(eval.recall(1)), Double.toString(eval.areaUnderROC(1)), Double.toString(eval.kappa())});


        Evaluation evalNew = calculateWithFilter(classifier, training, testing, eval);

        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, NO_SAMPLING, BEST_FIRST, String.valueOf(evalNew.truePositiveRate(1)),
                String.valueOf(evalNew.falsePositiveRate(1)), String.valueOf(evalNew.trueNegativeRate(1)), String.valueOf(evalNew.falseNegativeRate(1)), Double.toString(evalNew.precision(1)),
                Double.toString(evalNew.recall(1)), Double.toString(evalNew.areaUnderROC(1)), Double.toString(evalNew.kappa())});


    }

    private void calculateClassifierUndersampling(Classifier classifier, ConverterUtils.DataSource train, ConverterUtils.DataSource test,
                                                  int counter, List<String[]> list, String csvTest, String csvTrain) throws Exception {


        Object[] obj = calculateNoFilter(classifier, train, test, csvTest, csvTrain, 1);

        Instances testing = test.getDataSet();
        Instances training = train.getDataSet();

        Evaluation eval = (Evaluation) obj[0];
        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, UNDERSAMPLING, NO_SELECTION, String.valueOf(eval.truePositiveRate(1)),
                String.valueOf(eval.falsePositiveRate(1)), String.valueOf(eval.trueNegativeRate(1)), String.valueOf(eval.falseNegativeRate(1)), Double.toString(eval.precision(1)),
                Double.toString(eval.recall(1)), Double.toString(eval.areaUnderROC(1)), Double.toString(eval.kappa())});


        Evaluation evalNew = calculateWithFilter(classifier, training, testing, eval);


        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, UNDERSAMPLING, BEST_FIRST, String.valueOf(evalNew.truePositiveRate(1)),
                String.valueOf(evalNew.falsePositiveRate(1)), String.valueOf(evalNew.trueNegativeRate(1)), String.valueOf(evalNew.falseNegativeRate(1)), Double.toString(evalNew.precision(1)),
                Double.toString(evalNew.recall(1)), Double.toString(evalNew.areaUnderROC(1)), Double.toString(evalNew.kappa())});


    }

    private void calculateClassifierOversampling(Classifier classifier, ConverterUtils.DataSource train, ConverterUtils.DataSource test,
                                                 int counter, List<String[]> list, String csvTest, String csvTrain) throws Exception {


        Object[] obj = calculateNoFilter(classifier, train, test, csvTest, csvTrain, 2);

        Instances testing = test.getDataSet();
        Instances training = train.getDataSet();

        Evaluation eval = (Evaluation) obj[0];

        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, OVERSAMPLING, NO_SELECTION, String.valueOf(eval.truePositiveRate(1)),
                String.valueOf(eval.falsePositiveRate(1)), String.valueOf(eval.trueNegativeRate(1)), String.valueOf(eval.falseNegativeRate(1)), Double.toString(eval.precision(1)),
                Double.toString(eval.recall(1)), Double.toString(eval.areaUnderROC(1)), Double.toString(eval.kappa())});


        Evaluation evalNew = calculateWithFilter(classifier, training, testing, eval);


        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, OVERSAMPLING, BEST_FIRST, String.valueOf(evalNew.truePositiveRate(1)),
                String.valueOf(evalNew.falsePositiveRate(1)), String.valueOf(evalNew.trueNegativeRate(1)), String.valueOf(evalNew.falseNegativeRate(1)), Double.toString(evalNew.precision(1)),
                Double.toString(evalNew.recall(1)), Double.toString(evalNew.areaUnderROC(1)), Double.toString(evalNew.kappa())});

    }

    private void calculateClassifierSMOTE(Classifier classifier, ConverterUtils.DataSource train, ConverterUtils.DataSource test,
                                          int counter, List<String[]> list, String csvTest, String csvTrain) throws Exception {


        Object[] obj = calculateNoFilter(classifier, train, test, csvTest, csvTrain, 3);

        Instances testing = test.getDataSet();
        Instances training = train.getDataSet();

        Evaluation eval = (Evaluation) obj[0];

        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, SMOTE, NO_SELECTION, String.valueOf(eval.truePositiveRate(1)),
                String.valueOf(eval.falsePositiveRate(1)), String.valueOf(eval.trueNegativeRate(1)), String.valueOf(eval.falseNegativeRate(1)), Double.toString(eval.precision(1)),
                Double.toString(eval.recall(1)), Double.toString(eval.areaUnderROC(1)), Double.toString(eval.kappa())});


        Evaluation evalNew = calculateWithFilter(classifier, training, testing, eval);


        list.add(new String[]{prefix, Integer.toString(counter), Double.toString(counter / Double.parseDouble(numRelease) * 100), String.valueOf(defTrain), String.valueOf(defTest),
                classi, SMOTE, BEST_FIRST, String.valueOf(evalNew.truePositiveRate(1)),
                String.valueOf(evalNew.falsePositiveRate(1)), String.valueOf(evalNew.trueNegativeRate(1)), String.valueOf(evalNew.falseNegativeRate(1)), Double.toString(evalNew.precision(1)),
                Double.toString(evalNew.recall(1)), Double.toString(evalNew.areaUnderROC(1)), Double.toString(evalNew.kappa())});

    }


    private double numberOfYesPercentage(CSVReader csvReader, int position, String value) throws IOException {

        int size;
        double ret = 0;

        List<String[]> list = csvReader.readAll();
        size = list.size();

        for (String[] str : list) {

            if (str[position].equals(value)) {
                ret++;
            }
        }

        return ret / size;

    }

    private void writeCSV(List<String[]> wrt) {

        try (FileWriter fileWriter = new FileWriter(out);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            csvWriter.writeAll(wrt);
            csvWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {

        importResources(0); //0 Bookkeeper, 1 Tajo
        new WekaEngine().writeCSV(new WekaEngine().walkForwardValidation(Integer.parseInt(numRelease)));
    }
}
