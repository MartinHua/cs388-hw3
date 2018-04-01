package edu.stanford.nlp.parser.nndep;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.parser.nndep.DependencyTree;
import edu.stanford.nlp.util.ScoredObject;
import edu.stanford.nlp.util.ScoredComparator;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;
import java.util.List;


/**
 * Created by abhisheksinha on 3/20/17.
 */
public class DependencyParserAPIUsage {
    public static void main(String[] args) {
        //  Training type
        // String type = args[0]
        //  Training Data path
        String trainPath = "./train.conllx";
        // String trainPath = args[1];
        //  unlabeled Data Path
        String unlabeledPath = "./unlabeled.conllx";
        // String unlabeledPath = args[2];
        // Test Data Path
        String testPath = "./test.conllx";
        // String testPath = args[3];
        // Output Data Path
        String outputPath = "./output.conllx";
        // Path to embedding vectors file
        String embeddingPath = "/projects/nlp/penn-dependencybank/en-cw.txt";
        // Path where model is to be saved
        String modelPath = "outputs/model1";
        // Path where test data annotations are stored
        String testAnnotationsPath = "outputs/test_annotation.conllx";

        // Configuring propreties for the parser. A full list of properties can be found
        // here https://nlp.stanford.edu/software/nndep.shtml
        Properties prop = new Properties();
        prop.setProperty("maxIter", "1");
        DependencyParser p = new DependencyParser(prop);

        // Argument 1 - Training Path
        // Argument 2 - Dev Path (can be null)
        // Argument 3 - Path where model is saved
        // Argument 4 - Path to embedding vectors (can be null)
        p.train(trainPath, null, modelPath, embeddingPath);

        // Load a saved path
        DependencyParser model = DependencyParser.loadFromModelFile(modelPath);

        // Test model on test data, write annotations to testAnnotationsPath
        System.out.println(model.testCoNLL(testPath, testAnnotationsPath));

        List<CoreMap> trainSents = new List<>();
        List<DependencyTree> trainTrees = new List<>();
        Util.loadConllFile(unlabeledPath, trainSents, trainTrees);
        Util.writeConllFile(outputPath, trainSents, trainTrees);

        // returns parse trees for all the sentences in test data using model, this function does not come with default parser and has been written for you
        List<DependencyTree> predictedParses = model.testCoNLLProb(unlabeledPath);

        // By default NN parser does not give you any probability 
        // https://cs.stanford.edu/~danqi/papers/emnlp2014.pdf explains that the parsing is performed by picking the transition with the highest output in the final layer 
        // To get a certainty measure from the final layer output layer, we take use a softmax function.
        // For Raw Probability score We sum the logs of probability of every transition taken in the parse tree to get the following metric
        // For Margin Probability score we sum the log of margin between probabilities assigned to two top transitions at every step
        // Following line prints that probability metrics for 12-th sentence in test data
        // all probabilities in log space to reduce numerical errors. Adjust your code accordingly!
        System.out.printf("Raw Probability: %f\n",predictedParses.get(12).RawScore);
        System.out.printf("Margin Probability: %f\n",predictedParses.get(12).MarginScore);


        // You probably want to use the ScoredObject and scoredComparator classes for this assignment
        // https://nlp.stanford.edu/nlp/javadoc/javanlp-3.6.0/edu/stanford/nlp/util/ScoredObject.html
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/util/ScoredComparator.html

    }
}
