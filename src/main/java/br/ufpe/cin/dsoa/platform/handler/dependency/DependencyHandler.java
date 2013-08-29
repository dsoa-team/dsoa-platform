package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;

import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.Expression;
import br.ufpe.cin.dsoa.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.service.ServiceConsumer;
import br.ufpe.cin.dsoa.service.ServiceSpecification;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private List<Dependency> dependencies = new ArrayList<Dependency>();
	private DependencyHandlerDescription description;
	private boolean started;
	
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
				Dependency dependency  = new Dependency(this, serviceConsumer, new ServiceSpecification(specification, className, nonFunctionalSpecification));
				this.register(fieldMetadata, dependency);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException("The required service interface cannot be loaded : " + e.getMessage());
			}
		}
		description = new DependencyHandlerDescription(this, dependencies); // Initialize
																			// the
																			// description.
	}

	private List<AttributeConstraint> getConstraintList(Element[] constraintTags) {
		List<AttributeConstraint> constraintList = new ArrayList<AttributeConstraint>();
		String metric = null, operation = null, expression = null, threashold = null, weight = null;
		for (Element constraintTag : constraintTags) {
			metric = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_METRIC);
			operation = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_OPERATION);
			expression = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_EXPRESSION);
			threashold = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_THREASHOLD);
			weight = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_WEIGHT);
			constraintList.add(defineConstraint(metric, operation, expression, threashold, weight));
		}
		return constraintList;
	}

	private AttributeConstraint defineConstraint(String metric, String operation, String expression, String threashold,
			String weight) {
		Expression exp = Expression.valueOf(expression);
		double thr = Double.parseDouble(threashold);
		long wgt = Long.parseLong(weight);
		return new AttributeConstraint(metric, operation, exp, thr, wgt);
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

}
