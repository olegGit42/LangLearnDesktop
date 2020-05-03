package com.app.colibri.view.chart;

import static com.app.colibri.service.MainLocaleManager.addTrackedItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import com.app.colibri.controller.GUIController;
import com.app.colibri.model.Word;
import com.app.colibri.view.buttons.RefreshButton;

public class BadRememberWordChartFactory {

	public static final List<Word> badRememberWordList = new ArrayList<>();
	public static final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	public static JFreeChart chart;
	public static ChartPanel chartPanel;
	public static JPanel chartPanelWithButton;
	public static BarRenderer barRenderer;

	static {
		updateDataset();
		initChart();
		initChartPanel();
	}

	private static void initChartPanel() {
		chart.setPadding(new RectangleInsets(4, 8, 2, 2));
		chartPanel = new ChartPanel(chart);
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setPreferredSize(new Dimension(600, 300));

		chartPanelWithButton = new JPanel(new BorderLayout());
		chartPanelWithButton.add(chartPanel, BorderLayout.CENTER);
		chartPanelWithButton.add(new RefreshButton(BadRememberWordChartFactory::updateDataset), BorderLayout.NORTH);
	}

	private static void initChart() {
		chart = ChartFactory.createBarChart3D(null, null, // x-axis label
				"Repetitions number", // y-axis label
				dataset);
		addTrackedItem(chart, "Repetitions number");
		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setOrientation(PlotOrientation.HORIZONTAL);

		barRenderer = (BarRenderer) plot.getRenderer();
		setApropriateItemMargin();
		barRenderer.setMaximumBarWidth(0.05); // set maximum width to 5% of chart

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		// chart.getLegend().setFrame(BlockBorder.NONE);
		chart.removeLegend();
	}

	public static void updateDataset() {
		GUIController.badRememberWords(badRememberWordList);
		dataset.clear();
		badRememberWordList
				.forEach(word -> dataset.addValue(word.getRepeateIndicator(), word.getTranslate() /* "Word" */ , word.getWord()));

		setApropriateItemMargin();
	}

	public static void setApropriateItemMargin() {
		if (barRenderer != null) {
			barRenderer.setItemMargin(dataset.getRowCount() <= 3 ? 0 : -10);
		}
	}
}
