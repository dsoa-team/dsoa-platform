package br.ufpe.cin.dsoa.api.qos;

public class QosObjectNotFoundException extends RuntimeException {

	public QosObjectNotFoundException(String type, String name) {
		super("QoS object not found! Type:" + type + " name: " + name);
	}

}
