package br.ufpe.cin.dsoa.platform.registry.impl.filter;

public abstract class FilterBuilder {
	
	@Override
	public final String toString() {
		return append(new StringBuilder()).toString();
	}
	
	public abstract StringBuilder append(StringBuilder builder);
	
}
