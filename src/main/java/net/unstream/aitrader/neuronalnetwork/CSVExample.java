package net.unstream.aitrader.neuronalnetwork;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * On Basic of CSV Example of
 * org.deeplearning4j.examples.dataexamples.CSVExample from
 * 
 * @author Adam Gibson
 */
public class CSVExample {

	private static final String TESTDATA_FILE = "src/main/resources/iris_testdata.txt";
	private static Logger log = LoggerFactory.getLogger(CSVExample.class);

	/**
	 * read a CSV File
	 * 
	 * @param csvFile
	 * @param labelIndex
	 *            - on what position is the output label in the CSV file, zero
	 *            based
	 * @param numClasses
	 *            - how many different label values are possible
	 * @param csvLineNumbers
	 *            - number of lines in CSV
	 * @return RecordReader
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static RecordReader readCSVFile(File csvFile, int labelIndex, int numClasses, int csvLineNumbers)
			throws IOException, InterruptedException {
		// First: get the dataset using the record reader. CSVRecordReader
		// handles loading/parsing
		// should first line in csv be ignored?
		int numLinesToSkip = 0;
		// CSV delimiter
		char delimiter = ',';
		RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
		recordReader.initialize(new FileSplit(csvFile));
		return recordReader;
	}

	/**
	 * @param recordReader
	 *            - the recordReader for CSV file
	 * @param reader
	 * @param csvLineNumbers
	 * @param labelIndex
	 * @param numClasses
	 * @return - dataset with all entries from CSV file
	 */
	private static DataSet readDataSet(RecordReader recordReader, int csvLineNumbers, int labelIndex, int numClasses) {
		DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, csvLineNumbers, labelIndex,
				numClasses);
		DataSet dataSet = iterator.next();
		dataSet.shuffle();
		return dataSet;
	}

	/**
	 * 
	 * @param allData
	 *            - dataSet including all test and training data
	 * @param percentTrainingData
	 *            - how much percent to use for training data - 0.95 => 95%
	 * @return a List with trainingDataSet and testDataSet <br>
	 *         get(0)=> trainingData <br>
	 *         get(1) => testData
	 */
	private static List<DataSet> splitDataSetInTestAndTrainingData(DataSet allData, double percentTrainingData) {
		List<DataSet> dataSetList = new ArrayList<DataSet>();
		SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(percentTrainingData);

		DataSet trainingData = testAndTrain.getTrain();
		DataSet testData = testAndTrain.getTest();
		dataSetList.add(trainingData);
		dataSetList.add(testData);
		return dataSetList;
	}

	/**
	 * // We need to normalize our data. We'll use NormalizeStandardize (which
	 * // gives us mean 0, unit variance): //
	 * https://nd4j.org/doc/org/nd4j/linalg/dataset/api/preprocessor/
	 * NormalizerStandardize.html
	 * 
	 * @param trainingDataSet
	 * @param testDataSet
	 */
	private static void normalizeTrainingAndTestdata(DataSet trainingDataSet, DataSet testDataSet) {
		DataNormalization normalizer = new NormalizerStandardize();
		normalizer.fit(trainingDataSet); // Collect the statistics (mean/stdev)
											// from the training data. This does
											// not
											// modify the input data
		normalizer.transform(trainingDataSet); // Apply normalization to the
												// training data
		normalizer.transform(testDataSet); // Apply normalization to the test
											// data.
											// This is using statistics
											// calculated
											// from the *training* set
	}

	/**
	 * build the neuronal network
	 * 
	 * @return
	 */
	private static MultiLayerNetwork buildModel(int numberInputNeurons, int numberHiddenNeurons,
			int numberOutputNeurons) {
		log.info("Build model....");
		long seed = 6;

		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed).activation(Activation.TANH)
				.weightInit(WeightInit.XAVIER).updater(new Sgd(0.1)).l2(1e-4).list()
				.layer(0, new DenseLayer.Builder().nIn(numberInputNeurons).nOut(numberHiddenNeurons).build())
				.layer(1, new DenseLayer.Builder().nIn(numberHiddenNeurons).nOut(numberOutputNeurons).build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.activation(Activation.SOFTMAX).nIn(numberOutputNeurons).nOut(numberOutputNeurons).build())
				.backprop(true).pretrain(false).build();

		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(100));

		return model;
	}

	/**
	 * 
	 * @param model
	 *            - the neuronal network model
	 * @param numberOfTrainingIterations
	 *            - how much iterations?
	 * @param trainingDataSet
	 *            - training data
	 */
	private static void trainModel(MultiLayerNetwork model, int numberOfTrainingIterations, DataSet trainingDataSet) {
		for (int i = 0; i < numberOfTrainingIterations; i++) {
			model.fit(trainingDataSet);
		}
	}

	/**
	 * 
	 * @param model
	 *            - the neuronal network model
	 * @param numClasses
	 *            - the different classes or the number of output neurons
	 */
	private static void evaluateModel(MultiLayerNetwork model, int numClasses, DataSet testDataSet) {
		// evaluate the model on the test set
		Evaluation eval = new Evaluation(numClasses);
		INDArray output = model.output(testDataSet.getFeatureMatrix());
		eval.eval(testDataSet.getLabels(), output);
		log.info(eval.stats());
		System.out.print(eval.stats());
	}

	public static void main(String[] args) throws Exception {
		int labelIndex = 4; // 5 values in each row of the iris.txt CSV: 4 input
		// features followed by an integer label (class)
		// index. Labels are the 5th value (index 4) in each
		// row
		int numClasses = 3; // 3 classes (types of iris flowers) in the iris
		// data set. Classes have integer values 0, 1 or 2
		int csvLineNumbers = 150; // Iris data set: 150 examples total. We are
		// loading all of them into one DataSet (not
		// recommended for large data sets)

		int numberInputNeurons = labelIndex;
		int numberHiddenNeurons = numClasses;
		int numberOutputNeurons = numClasses;

		// how to split between test- and training data
		double percentTrainingData = 0.65;

		int trainingDataIndex = 0;
		int testDataIndex = 1;

		int numberOfTrainingIterations = 1000;

		// 1. read CSV file
		RecordReader recordReader = readCSVFile(new File(TESTDATA_FILE), labelIndex, numClasses,
				csvLineNumbers);

		// 2. store result in a data set which can be used for neuronal networks
		DataSet dataSet = readDataSet(recordReader, csvLineNumbers, labelIndex, numClasses);

		// 3. split dataSet in test- and training dataSet
		List<DataSet> dataSetList = splitDataSetInTestAndTrainingData(dataSet, percentTrainingData);
		DataSet trainingDataSet = dataSetList.get(trainingDataIndex);
		DataSet testDataSet = dataSetList.get(testDataIndex);

		// 4. normalize so called features (inputs) and labels (output)
		normalizeTrainingAndTestdata(trainingDataSet, testDataSet);

		System.out.println("model builded!");
		// 5. build neuronal network model
		MultiLayerNetwork model = buildModel(numberInputNeurons, numberHiddenNeurons, numberOutputNeurons);

		// 6. traing neuronal network model
		trainModel(model, numberOfTrainingIterations, trainingDataSet);

		// 7. evaluate neuronal network model against testdata
		evaluateModel(model, numClasses, testDataSet);
		
		//8. ask the neuronal network model for a prediction
		//TODO: http://www.opencodez.com/java/deeplearaning4j.htm
		//INDArray output = model.output(testDataSet.getFeatureMatrix());
	}

}
