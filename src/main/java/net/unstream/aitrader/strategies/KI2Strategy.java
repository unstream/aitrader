package net.unstream.aitrader.strategies;

import net.unstream.aitrader.DaxData;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerSerializer;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Anticyclic on a daily basis.
 * @author eric
 *
 */
public class KI2Strategy implements Strategy {
	private static MultiLayerNetwork model;
	private static NormalizerStandardize normalizer;
	{
		loadModel();
	}

	public static void loadModel() {
		File modelFile = new File("model2.zip");      //Where to save the network. Note: the file is in .zip format - can be opened externally
		File normalizerFile = new File("normalizer2.norm");

		//Load the model
		try {
			model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
			normalizer = NormalizerSerializer.getDefault().restore(normalizerFile);

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean invest(List<DaxData> dax) {
		//The model expects 50 input values

		if (dax.size() > 50) {
			int t = dax.size() - 1;
			double currentm1 = dax.get(t).getOpen();
			double [] data = new double[50];

			for(int i = 0; i< 50; i++) {
				data[i] = dax.get( t - 50  + i + 1).getOpen() / currentm1;
			}
			INDArray f = Nd4j.create(data);

			normalizer.transform(f);
			INDArray result = model.output(f, Layer.TrainingMode.TEST);

			System.out.println("Result: " + result);
			if (result.getDouble(0) >  0.3) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
}
