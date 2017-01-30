package br.ufpe.cin.dsoa.platform.service.selector;

public class DataValue {
	private double value;
	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	public double getValue() {
		return this.value;
	}

	public DataValue(double value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}
}