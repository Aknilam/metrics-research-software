package pl.einformatyka.mmse.prediction;


import org.xml.sax.SAXException;
import pl.einformatyka.mmse.Configuration;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PredictionModel {

    private Configuration configuration;
    private Instances dataSet;
    private Instances testSet;
    private Instances trainSet;
    private RandomForest randomForest;

    private final String result_file_path = "result_file_path";
    private int lastAttributeIndex;

    public PredictionModel(Configuration configuration){
        this.configuration = configuration;
    }
    private void saveIntoOutputFile(Instances listToSave) throws ParserConfigurationException, SAXException {
        try( BufferedWriter writer = new BufferedWriter( new FileWriter(configuration.getResultFilePath()))){
            writer.write(listToSave.toString());
            writer.flush();
        }
        catch(IOException ex) {
            System.out.println("Error durning save instances into file : "+ex.getMessage());
        }
    }

    public void learnPredictionModel(String fileName) throws Exception {
        loadData(fileName);
        getFixingRevisionIndex();
        divideDataSet();
        buildClassifier();
        classify();
        saveIntoOutputFile(sortIntances());
        System.out.println("Learn of prediction model process end");

    }

    private Instances sortIntances() {
        testSet.sort(lastAttributeIndex);

        List<Instance> list =  new ArrayList<>();
        for(Instance instance : testSet){
            list.add(instance);
        }
        Collections.reverse(list);

        Instances result = new Instances(testSet);
        result.clear();
        result.delete();
        result.addAll(list);


        return result;

    }

    private void classify() throws Exception {

        for(int i=0; i < testSet.numInstances(); i++) {
            double jripResult = randomForest.classifyInstance(testSet.instance(i));
            testSet.instance(i).setValue(lastAttributeIndex,jripResult);
        }
    }


    private void buildClassifier() throws Exception {
        randomForest = new RandomForest();
        randomForest.setNumTrees(200);
        randomForest.setMaxDepth(12);
        randomForest.setNumFeatures(lastAttributeIndex);
        randomForest.buildClassifier(trainSet);
    }

    private void divideDataSet() {
        Random random = new Random();
        for (int n = 0; n < dataSet.numInstances() ; n++) {
            float number = random.nextFloat();
            if( number > 0.1) {
                trainSet.add(dataSet.instance(n));
            }
            else {
                Instance instance = dataSet.instance(n);
                instance.setClassMissing();
                testSet.add(instance);
            }
        }
        trainSet.setClassIndex(trainSet.numAttributes() - 1);
        testSet.setClassIndex(testSet.numAttributes() - 1);
    }

    private void loadData(String fileName) {
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            dataSet = new Instances(reader);
            dataSet.setClassIndex(dataSet.numAttributes() - 1);

            trainSet = new Instances(dataSet);
            testSet = new Instances(dataSet);
        }
        catch(IOException ex) {
            System.out.println("Cannot find file with data " + ex.getMessage());
        }
    }

    public void getFixingRevisionIndex() {
        this.lastAttributeIndex = configuration.getMetrics().size();
    }
}
