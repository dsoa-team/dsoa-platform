package br.ufpe.cin.dsoa.api.service;

public interface RequiredPort extends Port {

	public void setMandatory(boolean mandatory);

	public boolean isMandatory();

}