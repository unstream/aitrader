package net.unstream.visualisation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ResultVisualizer extends ApplicationFrame {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs the demo application.
	 *
	 * @param title
	 *            the frame title.
	 */
	public ResultVisualizer(final String title, String xAxisName, String yAxisName, String strategy1Name,
			String strategy2Name, List<DataPoint> strategy1DataPointList, List<DataPoint> strategy2DataPointList) {

		super(title);

		XYDataset dataset = createDataset(strategy1Name, strategy2Name, strategy1DataPointList, strategy2DataPointList);
		JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisName, yAxisName, dataset,
				PlotOrientation.VERTICAL, true, false, false);
		XYPlot plot = (XYPlot) chart.getPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
		setContentPane(chartPanel);

	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return A dataset.
	 */
	private XYDataset createDataset(String timeSeries1Name, String timeSeries2Name,
			List<DataPoint> timeSeries1DataPointList, List<DataPoint> timeSeries2DataPointList) {
		XYSeries series1 = new XYSeries(timeSeries1Name);
		XYSeries series2 = new XYSeries(timeSeries2Name);

		for (DataPoint dataPoint : timeSeries1DataPointList) {
			series1.add(dataPoint.getTime(), dataPoint.getGuthaben());
		}

		for (DataPoint dataPoint : timeSeries2DataPointList) {
			series2.add(dataPoint.getTime(), dataPoint.getGuthaben());
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		return dataset;
	}

	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available
	// *
	// * to purchase from Object Refinery Limited: *
	// * *
	// * http://www.object-refinery.com/jfreechart/guide.html *
	// * *
	// * Sales are used to provide funding for the JFreeChart project - please *
	// * support us so that we can continue developing free software. *
	// ****************************************************************************

	/**
	 * 
	 * @param csvResultFile
	 *            - the csv result file
	 * @return - a list of datapoints to plot (x=time / y=balance)
	 * @throws IOException 
	 */
	private static List<DataPoint> readCSVResultFile(File csvResultFile) throws IOException {
		Reader reader = new FileReader(csvResultFile);
		CSVParser csvParser = new CSVParser(reader,
				CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

		DataPoint dataPoint;
		List<DataPoint> dataPointList = new ArrayList<>();

		for (CSVRecord csvRecord : csvParser) {
			// Accessing values by Header names
			String time = csvRecord.get("time");
			String balance = csvRecord.get("balance");
			String dax = csvRecord.get("dax");

			dataPoint = new DataPoint(Integer.valueOf(time), Double.valueOf(balance));
			dataPointList.add(dataPoint);
		}

		return dataPointList;
	}

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args
	 *            ignored.
	 * @throws IOException 
	 */
	public static void main(final String[] args) throws IOException {

		/* set here the csv filenames to plot */
		String CSVResultFilename1 = "CrashDetectionStrategy.csv";
		String CSVResultFilename2 = "ZyclicStrategy.csv";
		String CSVResultFilePath="src/main/resources/";
		
		File CSVResultFile1 = new File(CSVResultFilePath+CSVResultFilename1);
		File CSVResultFile2=new File(CSVResultFilePath+CSVResultFilename2);

		
		
		List<DataPoint> series1DataPointList = new ArrayList<>();
		List<DataPoint> series2DataPointList = new ArrayList<>();
		
		series1DataPointList=readCSVResultFile(CSVResultFile1);
		series2DataPointList=readCSVResultFile(CSVResultFile2);

		final ResultVisualizer demo = new ResultVisualizer("Guthabenentwicklung", "Zeit", "Guthaben in €", CSVResultFilename1,
				CSVResultFilename2, series1DataPointList, series2DataPointList);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}
}
