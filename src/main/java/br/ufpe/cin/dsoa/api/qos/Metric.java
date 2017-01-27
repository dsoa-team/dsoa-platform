package br.ufpe.cin.dsoa.api.qos;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public interface Metric extends NamedElement {
	Attribute getAttribute();

	String getFullname();
}
