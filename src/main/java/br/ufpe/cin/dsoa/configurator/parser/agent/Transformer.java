package br.ufpe.cin.dsoa.configurator.parser.agent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.configurator.parser.Property;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Transformer {

	public static final String[] TYPES = {"mapping", "class", "query"};
	
	public static final String MAPPING_TYPE = "mapping";
	
	public static final String CLASS_TYPE = "class";
	
	public static final String QUERY_TYPE = "query";
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlElement(name = "query")
	private String query;
	
	@XmlElementWrapper(name = "input-events")
	@XmlElement(name = "input-event")
	private List<InputEvent> inputEvents;
	
	@XmlElementWrapper(name = "output-events")
	@XmlElement(name = "output-event")
	private List<OutputEvent> outputEvents;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getQueries() {
		List<String> queries = new ArrayList<String>();
		if (MAPPING_TYPE.equals(this.getType())) {
			for (OutputEvent out : this.getOutputEvents()) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("INSERT INTO ");
				buffer.append(out.getType()).append(" (");
				Mapping mapping = out.getMapping();
				List<Property> properties = mapping.getProperties();
				boolean first = true;
				for (Property property : properties) {
					if (!first) {
						buffer.append(",");
					} else {
						first = false;
					}
					buffer.append(property.getId());
				}
				buffer.append(") ");
				buffer.append("SELECT ");
				first = true;
				for (Property property : properties) {
					if (!first) {
						buffer.append(",");
					} else {
						first = false;
					}
					if (property.isGenerated()) {
						buffer.append(property.getId());
					} else {
						boolean isStatistical = false;
						if (property.getStatistic() != null) {
							buffer.append(property.getStatistic()).append("(");
							isStatistical = true;
						}
						buffer.append(property.getExpression());
						if (isStatistical) {
							buffer.append(")");
						}
					}
				}
				buffer.append(" FROM ");
				List<InputEvent> inList = this.getInputEvents();
				first = true;
				for (InputEvent in : inList) {
					if (!first) {
						buffer.append(",");
					} else {
						first = false;
					}
					buffer.append(in.getType());
				}
				queries.add(buffer.toString());
			}
			
		} else {
			queries.add(query);
		}
		return queries;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<InputEvent> getInputEvents() {
		return inputEvents;
	}

	public void setInputEvents(List<InputEvent> inputEvents) {
		this.inputEvents = inputEvents;
	}

	public List<OutputEvent> getOutputEvents() {
		return outputEvents;
	}

	public void setOutputEvents(List<OutputEvent> outputEvents) {
		this.outputEvents = outputEvents;
	}
}
