package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;

import br.ufpe.cin.dsoa.api.event.EventChannel;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.OutputTerminal;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ServiceConsumer;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.platform.event.impl.EventAdminChannel;
import br.ufpe.cin.dsoa.platform.event.impl.OutputTerminalAdapter;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private List<Dependency> dependencies = new ArrayList<Dependency>();
	private DependencyHandlerDescription description;
	private boolean started;
	private Logger log = Logger.getLogger(DependencyHandler.class.getName());
	
	private EventTypeCatalog eventTypeCatalog;
	private EventProcessingService epService;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
		String consumerId = metadata.getAttribute(Constants.COMPONENT_ID_ATT);
		String consumerName = metadata.getAttribute(Constants.COMPONENT_NAME_ATT);
		ServiceConsumer serviceConsumer = new ServiceConsumer(consumerId, consumerName);
		
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		Element[] requiresTags = metadata.getElements(Constants.REQUIRES_TAG, Constants.REQUIRES_TAG_NAMESPACE);
		for (Element requiresTag : requiresTags) {
			String field = (String) requiresTag.getAttribute(Constants.REQUIRES_ATT_FIELD);
			List<AttributeConstraint> constraintList = getConstraintList(requiresTag.getElements(Constants.CONSTRAINT_TAG));
			FieldMetadata fieldMetadata = pojoMetadata.getField(field);
			
			Class<?> specification = null;
			String className = fieldMetadata.getFieldType();
			NonFunctionalSpecification nonFunctionalSpecification = new NonFunctionalSpecification(constraintList);
			try {
				specification = getInstanceManager().getClazz().getClassLoader().loadClass(className);
				ServiceSpecification serviceSpecification =  new ServiceSpecification(specification, className, nonFunctionalSpecification);
				EventType invocationEventType = getInvocationEventType();
				Dependency dependency  = new Dependency(this, serviceConsumer, serviceSpecification, invocationEventType);
				this.register(fieldMetadata, dependency);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException("The required service interface cannot be loaded : " + e.getMessage());
			}
		}
		description = new DependencyHandlerDescription(this, dependencies); // Initialize
																			// the
																			// description.
	}

	private EventType getInvocationEventType() {
		return eventTypeCatalog.get(Constants.INVOCATION_EVENT);
	}

	private List<AttributeConstraint> getConstraintList(Element[] constraintTags) {
		List<AttributeConstraint> constraintList = new ArrayList<AttributeConstraint>();
		String attribute = null, operation = null, expression = null, threashold = null, weight = null;
		for (Element constraintTag : constraintTags) {
			attribute = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_METRIC);
			operation = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_OPERATION);
			expression = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_EXPRESSION);
			threashold = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_THREASHOLD);
			weight = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_WEIGHT);
			constraintList.add(defineConstraint(attribute, operation, expression, threashold, weight));
		}
		return constraintList;
	}

	private AttributeConstraint defineConstraint(String attribute, String operation, String expression, String threashold,
			String weight) {
		Expression exp = Expression.valueOf(expression);
		double thr = Double.parseDouble(threashold);
		long wgt;
		if (NumberUtils.isNumber(weight)) {
			wgt = Long.parseLong(weight);
		} else {
			log.warning("Weight was not recognized as a valid number, so a default value (1) was used.");
				wgt = 1;
		}
		return new AttributeConstraint(attribute, operation, exp, thr, wgt);
	}

	private void register(FieldMetadata fieldmeta, Dependency dependency) {
		dependencies.add(dependency);
		getInstanceManager().register(fieldmeta, dependency);
	}
	
	@Override
	public String toString() {
		return "DependencyHandler [dependencies=" + dependencies + "]";
	}

	@Override
	public HandlerDescription getDescription() {
		return this.description;
	}

	public void validate() {
		checkContext();
	}

	public void invalidate() {
		setValidity(false);
	}
	
    /**
     * Handler start method.
     * @see org.apache.felix.ipojo.Handler#start()
     */
    public void start() {
        // Start the dependencies
        for (Dependency dep : dependencies) {
            dep.start();
        }
        // Check the state
        started = true;
        setValidity(false);
        checkContext();
    }

    /**
     * Handler stop method.
     * @see org.apache.felix.ipojo.Handler#stop()
     */
    public void stop() {
    	started = false;
        for (Dependency dep : dependencies) {
        	dep.stop();
        }
    }
	
    /**
     * Check the validity of the dependencies.
     */
    protected void checkContext() {
        if (!started) {
            return;
        }
        synchronized (dependencies) {
            // Store the initial state
            boolean initialState = getValidity();

            boolean valid = true;
            for (Dependency dep : dependencies) {
                if (dep.getStatus() != DependencyStatus.RESOLVED) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                if (!initialState) {
                    setValidity(true);
                }
            } else {
                if (initialState) {
                    setValidity(false);
                }
            }

        }
    }

	public EventChannel getEventChannel() {
		EventType invocationEvent = eventTypeCatalog.get(Constants.INVOCATION_EVENT);
		EventChannel channel = epService.getEventChannel(invocationEvent);
		
		return channel;
	}

}
