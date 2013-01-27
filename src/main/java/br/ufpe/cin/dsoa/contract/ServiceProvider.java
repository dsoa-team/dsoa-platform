package br.ufpe.cin.dsoa.contract;

public class ServiceProvider {

	private String pid;
	private String name;
	
	public ServiceProvider(String pid, String name, ServiceImpl description) {
		this.pid = pid;
		this.name = name;
	}
	
	public String getPid() {
		return pid;
	}

	public String getName() {
		return name;
	}

}
