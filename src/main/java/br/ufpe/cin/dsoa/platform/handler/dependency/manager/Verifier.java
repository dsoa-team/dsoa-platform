package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.AttributeChangeListener;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public interface Verifier {
	void configure(AttributeChangeListener listener, String servicePid, List<AttributeConstraint> constraints);
}
