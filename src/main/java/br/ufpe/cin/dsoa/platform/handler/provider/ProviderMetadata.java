package br.ufpe.cin.dsoa.platform.handler.provider;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.platform.component.metadata.DsoaComponentMetadata;
import br.ufpe.cin.dsoa.platform.handler.provider.capability.PSlo;
import br.ufpe.cin.dsoa.platform.handler.provider.capability.Profile;

public class ProviderMetadata {

	private String componentName;
	private String componentClassname;
	private String portName;
	private String portType;

	private List<PSlo> slos;
	private List<Profile> profiles;

	public ProviderMetadata() {
		slos = new ArrayList<PSlo>();
		profiles = new ArrayList<Profile>();
	}

	@SuppressWarnings("rawtypes")
	public void loadMetadata(Element metadata, Dictionary configuration) {

		// Component meta-data
		this.componentName = metadata.getAttribute(DsoaComponentMetadata.COMPONENT_NAME);
		this.componentClassname = metadata.getAttribute(DsoaComponentMetadata.COMPONENT_CLASSNAME);
		
		// Provided port meta-data
		Element[] elements = metadata
				.getElements(HANDLE_NAME, HANDLE_NAMESPACE);
		for (Element element : elements) {}
		this.portName = elements[0].getAttribute(PROVIDED_PORT_NAME);
		this.portType = elements[0].getAttribute(PROVIDED_PORT_NAME);
		// slo
		if (elements[0].containsElement(SLO)) {
			Element[] tagSlo = elements[0].getElements(SLO);
			for (Element e_slo : tagSlo) {
				PSlo slo = new PSlo();
				slo.loadMetadata(e_slo, configuration);
				slos.add(slo);
			}
		}

		// profile
		if (elements[0].containsElement(PROFILES)) {
			Element[] tagProfiles = elements[0].getElements(PROFILES);
			if (tagProfiles[0].containsElement(PROFILE)) {
				Element[] tagProfile = tagProfiles[0].getElements(PROFILE);
				for (Element e_profile : tagProfile) {
					Profile profile = new Profile();
					profile.loadMetadata(e_profile, configuration);
					profiles.add(profile);
				}
			}
		}

	}

	public Dictionary<String, Object> getRegisterProperties() {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(DsoaComponentMetadata.COMPONENT_NAME, componentName);
		
		
		for (PSlo slo : slos) {

			if (slo.getOperation() != null) {
				// METRIC.OPERATION :: VALUE
				properties.put(slo.getMetric() + "." + slo.getOperation(),
						slo.getThreshold());
			} else {
				// METRIC :: VALUE
				properties.put(slo.getMetric(), slo.getThreshold());
			}
		}

		return properties;
	}

	public String getPid() {
		return componentName;
	}

	public void setPid(String pid) {
		this.componentName = pid;
	}

	public List<PSlo> getSlos() {
		return slos;
	}

	public void setSlos(List<PSlo> slos) {
		this.slos = slos;
	}

	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}

	// Constants//
	public static final String HANDLE_NAME = "provides";
	public static final String HANDLE_NAMESPACE = "br.ufpe.cin.dsoa";
	
	// Provided port
	public static final String PROVIDED_PORT_NAME = "name";

	// tags
	public static final String SLO = "slo";
	public static final String PROFILES = "profiles";
	public static final String PROFILE = "profile";
	public static final String RESOURCE = "resource";

	// slo
	public static final String SLO_METRIC = "metric";
	public static final String SLO_STATISTIC = "statistic";
	public static final String SLO_THRESHOLD = "threshold";
	public static final String SLO_OPERATION = "operation";
	public static final String SLO_EXPRESSION = "expression";
	public static final String SLO_WEIGHT = "weight";

	// profile
	public static final String PROFILE_POLICY = "policy";

	// resource
	public static final String RESOURCE_TYPE = "type";
	public static final String RESOURCE_ATTRIBUTE = "attribute";
	public static final String RESOURCE_THRESHOLD = "threshold";
	public static final String RESOURCE_EXPRESSION = "expression";
}
