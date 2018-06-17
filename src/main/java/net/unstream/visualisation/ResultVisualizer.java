package net.unstream.visualisation;

import java.util.ArrayList;
import java.util.List;

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
		
		XYDataset dataset = createDataset(strategy1Name, strategy2Name,strategy1DataPointList, strategy2DataPointList);
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
	 * Starting point for the demonstration application.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(final String[] args) {
		List<DataPoint> series1DataPointList = new ArrayList<>();
		List<DataPoint> series2DataPointList = new ArrayList<>();
		
		//read in data strategy 1
		for (int count=0;count<10000;count++){
			series1DataPointList.add(new DataPoint(count,2000+2*count));
		}
		
		//read in data strategy 2
		for (int count=0;count<10000;count++){
			series2DataPointList.add(new DataPoint(count,2*count));
		}
		
		
		final ResultVisualizer demo = new ResultVisualizer("Guthabenentwicklung", "Zeit", "Guthaben in €", "Halten",
				"Neuonales Netz",series1DataPointList, series2DataPointList);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}
}
