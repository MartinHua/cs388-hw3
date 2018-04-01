import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.parser.nndep.DependencyTree;
import edu.stanford.nlp.parser.nndep.Util;
import edu.stanford.nlp.util.ScoredObject;
import edu.stanford.nlp.util.ScoredComparator;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by abhisheksinha on 3/20/17.
 */
public class DependencyParserAPIUsage {

    private void update(String inputTrainPath, String inputUnlabeledPath, String outputTrainPath, String outputUnlabeledPath, String option, List<DependencyTree> predictedParses) {
        List<CoreMap> trainSents = new ArrayList<>();
        List<DependencyTree> trainTrees = new ArrayList<>();
        List<CoreMap> unlabeledSents = new ArrayList<>();
        List<DependencyTree> unlabeledTrees = new ArrayList<>();
        Util.loadConllFile(inputTrainPath, trainSents, trainTrees);
        Util.loadConllFile(inputUnlabeledPath, unlabeledSents, unlabeledTrees);
        int wordCnt = 0;
        List<int> addList = new ArrayList<>();
        if (option == "generate") {
            trainSents = trainSents.subList(0, 50);
            trainTrees = trainTrees.subList(0, 50);
        } else{
            List<ScoredObject<int>> unlabeled = new ArrayList<>();
            for (int i=0; i<unlabeledSents.size(); i++) {
                if (options == "random") {
                    unlabeled.add(ScoredObject(i, (int)(Math.random()*unlabeledSents.size())));
                } else if (options == "length") {
                    unlabeled.add(ScoredObject(i, predictedParses.get(i).n));
                } else if (options == "raw") {
                    unlabeled.add(ScoredObject(i, predictedParses.get(i).RawScore));
                } else if (options == "margin") {
                    unlabeled.add(ScoredObject(i, predictedParses.get(i).MarginScore));
                }
            }
            Collections.sort(unlabeled, ScoredComparator.DESCENDING_COMPARATOR);
            while (wordCnt < 1500) {
                addList.add(unlabeled.get(0));
                wordCnt += predictedParses.get(unlabeled.get(0)).n;
                unlabeled.remove(0);
            }
            Collections.sort(addList ,Collections.reverseOrder());
            for (i=0; i<addList.size(); i++) {
                int index = addList.get(i);
                trainSents.add(unlabeledSents.get(index));
                trainTrees.add(unlabeledTrees.get(index));
                unlabeledSents.remove(index);
                unlabeledTrees.remove(index);
            }
        }
        Util.writeConllFile(outputTrainPath, trainSents, trainTrees);
        Util.writeConllFile(outputUnlabeledPath, unlabeledSents, unlabeledTrees);
    }
    public static void main(String[] args) {
        //  Training type
        String type = args[0];
        //  Training Data path
        String trainPath = "./train.conllx";
        // String trainPath = args[1];
        //  unlabeled Data Path
        String unlabeledPath = "./unlabeled.conllx";
        // String unlabeledPath = args[2];
        // Test Data Path
        String testPath = "./test.conllx";
        // String testPath = args[3];
        // Output train and unlabeled data path
        String outputTrainPath = "./outputTrain.conllx";
        String outputUnlabeledPath = "./outputUnlabeled.conllx";
        // Path to embedding vectors file
        String embeddingPath = "/projects/nlp/penn-dependencybank/en-cw.txt";
        // Path where model is to be saved
        String modelPath = "outputs/model1";
        // Path where test data annotations are stored
        String testAnnotationsPath = "outputs/test_annotation.conllx";

        List<DependencyTree> predictedParses;

        // generate sets
        update(trainPath, unlabeledPath, outputTrainPath, outputUnlabeledPath, "generate", predictedParses);

        // new DependencyParser
        Properties prop = new Properties();
        prop.setProperty("maxIter", "1");
        DependencyParser p = new DependencyParser(prop);

        for (int iter=0; iter<20; iter++) {
            // Configuring propreties for the parser. A full list of properties can be found
            // here https://nlp.stanford.edu/software/nndep.shtml
            
            // Argument 1 - Training Path
            // Argument 2 - Dev Path (can be null)
            // Argument 3 - Path where model is saved
            // Argument 4 - Path to embedding vectors (can be null)
            p.train(outputTrainPath, null, modelPath, embeddingPath);

            // Load a saved path
            DependencyParser model = DependencyParser.loadFromModelFile(modelPath);

            // Test model on test data, write annotations to testAnnotationsPath
            System.out.println(model.testCoNLL(testPath, testAnnotationsPath));

            // returns parse trees for all the sentences in test data using model, this function does not come with default parser and has been written for you
            predictedParses = model.testCoNLLProb(outputUnlabeledPath);

            // By default NN parser does not give you any probability 
            // https://cs.stanford.edu/~danqi/papers/emnlp2014.pdf explains that the parsing is performed by picking the transition with the highest output in the final layer 
            // To get a certainty measure from the final layer output layer, we take use a softmax function.
            // For Raw Probability score We sum the logs of probability of every transition taken in the parse tree to get the following metric
            // For Margin Probability score we sum the log of margin between probabilities assigned to two top transitions at every step
            // Following line prints that probability metrics for 12-th sentence in test data
            // all probabilities in log space to reduce numerical errors. Adjust your code accordingly!
            update(outputTrainPath, outputUnlabeledPath, outputTrainPath, outputUnlabeledPath, args[0], predictedParses);
            
            // System.out.printf("Raw Probability: %f\n",predictedParses.get(12).RawScore);
            // System.out.printf("Margin Probability: %f\n",predictedParses.get(12).MarginScore);

            // You probably want to use the ScoredObject and scoredComparator classes for this assignment
            // https://nlp.stanford.edu/nlp/javadoc/javanlp-3.6.0/edu/stanford/nlp/util/ScoredObject.html
            // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/util/ScoredComparator.html
        }




    }
}
