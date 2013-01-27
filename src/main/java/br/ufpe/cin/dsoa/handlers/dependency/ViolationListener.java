package br.ufpe.cin.dsoa.handlers.dependency;

public interface ViolationListener {
	public void notifyViolation(Violation violation);
}
