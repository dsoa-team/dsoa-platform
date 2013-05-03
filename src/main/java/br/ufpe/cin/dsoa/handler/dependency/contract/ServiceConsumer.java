package br.ufpe.cin.dsoa.handler.dependency.contract;

public class ServiceConsumer {

	private String id;
	private String name;
	

	public ServiceConsumer(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}