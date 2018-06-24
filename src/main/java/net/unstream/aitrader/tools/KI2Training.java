package net.unstream.aitrader.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

;


/**
 * Extarct training data sets from the dax data files.
 */
public class KI2Training {
	private static Logger log = LoggerFactory.getLogger(KI2Training.class);

	private static final int ROWS_NEARTERM = 50;
	private static final int ROWS_LONGTERM = 50;


	private void train() {




		DataSet data = DataSet.empty();

		// Read all the Data
		List<Double> values = readOpeningValues();
		int rows = values.size() / 2 - ROWS_NEARTERM - 1;
		double [][] f = new double [rows][ROWS_NEARTERM];
		double [][] l = new double [rows][2];
		for (int i = 0; i < rows; i++) {
			double current = values.get(i + ROWS_NEARTERM);
			double currentm1 = values.get(i + ROWS_NEARTERM - 1);

			for (int j = 0; j < ROWS_NEARTERM; j++) {
				double value = values.get(i + j) / currentm1;
				f[i][j] = value;
			}
			if (current > currentm1) {
				l[i][0] = 1;
				l[i][1] = 0;
			} else {
				l[i][0] = 0;
				l[i][1] = 1;
			}
		}
		DataSet allData = new DataSet(Nd4j.create(f), Nd4j.create(l));
		allData.shuffle();
		SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(0.65);  //Use 65% of data for training
		DataSet trainingData = testAndTrain.getTrain();
		DataSet testData = testAndTrain.getTest();

		//We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
		DataNormalization normalizer = new NormalizerStandardize();
		normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
		normalizer.transform(trainingData);     //Apply normalization to the training data
		normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set


		final int numInputs = ROWS_NEARTERM;
		int outputNum = 2;
		long seed = 6;


		log.info("Build model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.activation(Activation.TANH)
				.weightInit(org.deeplearning4j.nn.weights.WeightInit.XAVIER)
				.updater(new Sgd(0.1))
				.l2(1e-4)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(10)
						.build())
				.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
						.activation(Activation.SIGMOID)
						.nIn(10).nOut(outputNum).build())
				.backprop(true).pretrain(false)
				.build();



		//run the model
		MultiLayerNetwork model = new MultiLayerNetwork(conf);
		model.init();
		model.setListeners(new ScoreIterationListener(10));

		for(int i=0; i<1000; i++ ) {
			model.fit(trainingData);
		}

		log.info("dfdf");
		//evaluate the model on the test set
		Evaluation eval = new Evaluation(2);
		INDArray output = model.output(testData.getFeatureMatrix());
		eval.eval(testData.getLabels(), output);
		log.info(eval.stats());
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("GDAXI.csv");
		try {
			File modelFile = new File("model2.zip");
			model.save(modelFile);
			log.info("Wrote file to " + modelFile.getAbsolutePath());
			NormalizerSerializer.getDefault().write(normalizer, "normalizer2.norm");
		} catch (IOException e) {
			e.printStackTrace();
		}


	}


	private List<Double> readOpeningValues() {
		List<Double> openingValues;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("GDAXI.csv");
			Reader in = new InputStreamReader(is);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
			openingValues = new ArrayList<>();
			for (CSVRecord record : records) {
				String open = record.get("Open");
				try {
					openingValues.add(Double.parseDouble(open));
				} catch ( NumberFormatException e) {
					//Ignore, also the net misses the information, thatr no trading occured.
				}
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return openingValues;
	}
	


	
	public static void main(String[] args) throws IOException {
		KI2Training trainer = new KI2Training();
		trainer.train();
	}

}
