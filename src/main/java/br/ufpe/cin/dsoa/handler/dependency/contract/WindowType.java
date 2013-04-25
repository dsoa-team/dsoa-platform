package br.ufpe.cin.dsoa.handler.dependency.contract;

public enum WindowType {
	LENGHT("lenght"), TIME("time");
	
	private String name;
	
	WindowType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	
}
