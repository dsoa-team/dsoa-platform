package br.ufpe.cin.dsoa.platform.component.autonomic;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.ComponentInstance;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;

public class DsoaComponentInstanceAutonomicManager {

	private Map<String, DsoaBindingManager> bindingManagerMap = new HashMap<String, DsoaBindingManager>();

	private DsoaPlatform dsoaPlatform;

	private ComponentInstance instance;

	public DsoaComponentInstanceAutonomicManager(DsoaPlatform platform,
			ComponentInstance instance) {
		this.dsoaPlatform = platform;
		this.instance = instance;
	}

	public DsoaBindingManager getBindingManager(Binding bindingInstance) {
		DsoaBindingManager bindingManager = new DsoaBindingManager(
				dsoaPlatform, this, bindingInstance);
		this.bindingManagerMap.put(bindingInstance.getName(), bindingManager);
		return bindingManager;
	}

	public ComponentInstance getComponentInstance() {
		return instance;
	}

}
