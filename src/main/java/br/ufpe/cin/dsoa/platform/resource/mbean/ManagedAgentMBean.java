package br.ufpe.cin.dsoa.platform.resource.mbean;

public interface ManagedAgentMBean {

	public void setWindowSize(int size);

	public int getWindowSize();

	public String getId();

	public String getDescription();

	public String getWindowType();
	
	public void setWindowType(String windowType);
	
	public void update();
	
	public String getObjectName();

}