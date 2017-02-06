package br.ufpe.cin.dsoa.platform.service.selector;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;


public class StatisticsComputingService
{
	private DescriptiveStatistics stats;

	public StatisticsComputingService() {
		this.stats = new DescriptiveStatistics(100);
	}

	public StatisticsComputingService(int windowSize) {
		this.stats = new DescriptiveStatistics(windowSize);
	}
	
	public int getWindowSize() {
		return this.stats.getWindowSize();
	}

	public void addValue(double value) {
		this.stats.addValue(value);
	}


	public double getGeometricMean() {
		return this.stats.getGeometricMean();
	}

	public double getKurtosis() {
		return this.stats.getKurtosis();
	}

	public double getMax() {
		return this.stats.getMax();
	}

	public double getMean() {
		return this.stats.getMean();
	}

	public double getMin() {
		return this.stats.getMin();
	}

	public double getVariance() {
		return this.stats.getVariance();
	}

	public double getSumsq() {
		return this.stats.getSumsq();
	}

	public double getStandardDeviation() {
		return this.stats.getStandardDeviation();
	}

	public double getSkewness() {
		return this.stats.getSkewness();
	}

	public double getPercentile(double p) {
		return this.stats.getPercentile(p);
	}

}
