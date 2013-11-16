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
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private boolean started;
	
	private BundleContext ctx;
	
	private List<Dependency> dependencies = new ArrayList<Dependency>();
	
	private DependencyHandlerDescription description;
	
	private ServiceTracker dsoaServiceTracker;
	
	private DsoaPlatform dsoa;
	
	private Logger log = Logger.getLogger(DependencyHandler.class.getName());
	
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
		ctx = this.getInstanceManager().getContext();
		
		dsoaServiceTracker = new ServiceTracker(ctx, DsoaPlatform.class.getName(), new DsoaTrackerCustomizer());
		dsoaServiceTracker.open();
		
		String componentId = metadata.getAttribute(Constants.COMPOSITION_ID_ATT);
		
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		Element[] requiresTags = metadata.getElements(Constants.REQUIRES_TAG, Constants.REQUIRES_TAG_NAMESPACE);
		for (Element requiresTag : requiresTags) {
			String field = (String) requiresTag.getAttribute(Constants.REQUIRES_ATT_FIELD);
			List<AttributeConstraint> constraintList = getConstraintList(requiresTag.getElements(Constants.CONSTRAINT_TAG));
			FieldMetadata fieldMetadata = pojoMetadata.getField(field);
			
			Class<?> clazz = null;
			String className = fieldMetadata.getFieldType();
			NonFunctionalSpecification nonFunctionalSpecification = new NonFunctionalSpecification(constraintList);
			try {
				clazz = getInstanceManager().getClazz().getClassLoader().loadClass(className);
				ServiceSpecification serviceSpecification =  new ServiceSpecification(clazz, className, nonFunctionalSpecification);
				Dependency dependency  = new Dependency(this, componentId, field, serviceSpecification);
				this.register(fieldMetadata, dependency);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationException("The required service interface cannot be loaded : " + e.getMessage());
			}
		}
		description = new DependencyHandlerDescription(this, dependencies); 
	}
	
    /**
     * Handler start method.
     * @see org.apache.felix.ipojo.Handler#start()
     */
    public void start() {
        started = true;
        setValidity(false);
    	if (dsoa != null) {
	    	startDependencies();
    	}
    }
    
    /**
     * Handler stop method.
     * @see org.apache.felix.ipojo.Handler#stop()
     */
    public void stop() {
        this.stopDependencies();
        this.setValidity(false);
        started = false;
    }

	private void startDependencies() {
		synchronized (dependencies) {
		    for (Dependency dep : dependencies) {
		        dep.start();
		    }
		}
		computeState();
	}
	
	private void stopDependencies() {
		synchronized (dependencies) {
		    for (Dependency dep : dependencies) {
		        dep.stop();
		    }
		}
	}

    protected void computeState() {
        if (!started) {
            return;
        }
        
        boolean initialState = getValidity();
        boolean valid = true;
        
        synchronized (dependencies) {
            for (Dependency dep : dependencies) {
                if (!dep.isValid()) {
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

    /**
     * Check the validity of the dependencies.
     */
    class DsoaTrackerCustomizer implements ServiceTrackerCustomizer {

    	public Object addingService(ServiceReference reference) {
			dsoa = (DsoaPlatform) ctx.getService(reference);
			startDependencies();
			return dsoa;
		}
		
		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}
		
		public void removedService(ServiceReference reference, Object service) {
			stopDependencies();
			dsoa = null;
			ctx.ungetService(reference);
			computeState();
		}	
    	
    }

	public DsoaPlatform getDsoaPlatform() {
		return dsoa;
	}
    
	private List<AttributeConstraint> getConstraintList(Element[] constraintTags) {
		List<AttributeConstraint> constraintList = new ArrayList<AttributeConstraint>();
		String attribute = null, operation = null, expression = null, threashold = null, weight = null;
		
		if(constraintTags != null){
			for (Element constraintTag : constraintTags) {
				attribute = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_METRIC);
				operation = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_OPERATION);
				expression = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_EXPRESSION);
				threashold = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_THREASHOLD);
				weight = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_WEIGHT);
				constraintList.add(defineConstraint(attribute, operation, expression, threashold, weight));
			}
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
}
