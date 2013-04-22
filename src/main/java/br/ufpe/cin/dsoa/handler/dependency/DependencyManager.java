package br.ufpe.cin.dsoa.handler.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.felix.ipojo.IPOJOServiceFactory;
import org.apache.felix.ipojo.InstanceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.event.InvocationEvent;

public class DependencyManager {

	private BundleContext context;
	private InstanceManager instanceManager;

	private ServiceListener listener;
	/**
	 * The component responsible for service selection.
	 */
	private Broker broker;
	
	/**
	 * The component responsible for service monitoring.
	 */
	//private Monitor monitor;
	
	private DynamicProxyFactory proxyFactory;

	private Dependency dependency;
	private ServiceReference reference;
	private Object realService;
	private Boolean waiting;
	private String serviceId;

	public DependencyManager(Dependency dependency, InstanceManager instanceManager) {
		this.instanceManager = instanceManager;
		this.context = instanceManager.getContext();
		this.dependency = dependency;
		this.proxyFactory = new DynamicProxyFactory();
		this.waiting = false;
		this.listener = new ServiceListenerImpl();
		new ServiceTracker(context, Broker.class.getName(), new BrokerTrackerCustomizer()).open();
	}

	public Object getService() {
		if (this.realService == null) {
			this.serviceId = (String)reference.getProperty(Constants.SERVICE_PID);
			this.realService = context.getService(reference);
			if (this.realService instanceof IPOJOServiceFactory) {
				this.realService = ((IPOJOServiceFactory) this.realService).getService(instanceManager);
			}
		}
		return this.realService;
	}
	
	public void resolve() {
		if (broker != null) {
			broker.getBestService(context, dependency.getSpecificationName(), dependency.getConstraintList(),
				dependency.getBlackList(), listener);
		} else {
			synchronized (waiting) {
				waiting = true;
			}
		}
	}

	public void release() {
		this.reference = null;
		this.serviceId = null;
		this.dependency.setServiceObject(null);
	}


	/**
	 * Creates java dynamic proxy .
	 */
	private class DynamicProxyFactory implements InvocationHandler {

		/**
		 * HashCode method.
		 */
		private Method m_hashCodeMethod;

		/**
		 * Equals method.
		 */
		private Method m_equalsMethod;

		/**
		 * toStirng method.
		 */
		private Method m_toStringMethod;

		/**
		 * Creates a DynamicProxyFactory.
		 */
		public DynamicProxyFactory() {
			try {
				m_hashCodeMethod = Object.class.getMethod("hashCode", null);
				m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				m_toStringMethod = Object.class.getMethod("toString", null);
			} catch (NoSuchMethodException e) {
				throw new NoSuchMethodError(e.getMessage());
			}
		}

		/**
		 * Creates a proxy object for the given specification. The proxy uses
		 * the given dependency to get the service object.
		 * 
		 * @param spec
		 *            the service specification (interface)
		 * @return the proxy object.
		 */
		public Object getProxy() {
			return Proxy.newProxyInstance(instanceManager.getClazz().getClassLoader(), new Class[] { dependency.getSpecification() }, this);
		}

		/**
		 * Invocation Handler delegating invocation on the service object.
		 * 
		 * @param proxy
		 *            the proxy object
		 * @param method
		 *            the method
		 * @param args
		 *            the arguments
		 * @return a proxy object.
		 * @throws Exception
		 *             if the invocation throws an exception
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
			if (dependency.getStatus() != DependencyStatus.UNRESOLVED) {
				Object svc = getService();
				Class<?> declaringClass = method.getDeclaringClass();
				if (declaringClass == Object.class) {
					if (method.equals(m_hashCodeMethod)) {
						return new Integer(this.hashCode());
					} else if (method.equals(m_equalsMethod)) {
						return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
					} else if (method.equals(m_toStringMethod)) {
						return this.toString();
					} else {
						throw new InternalError("Unexpected Object method dispatched: " + method);
					}
				}
				InvocationEvent ie = null;
				long requestTime = System.nanoTime(), responseTime;
				Object result = null;
				boolean success = true;
				try {
					result = method.invoke(svc, args);
				} catch(Exception exc) {
					success = false;
					throw exc;
				} finally {
					responseTime = System.nanoTime();
					ie = new InvocationEvent(dependency.getConsumer().getId(), serviceId, method.getName(), success, requestTime, responseTime);
				}
				return result;
			} else {
				throw new ComponentException("The dependency on " + dependency.getSpecificationName() + " interface is not valid!");
			}
		}
	}
	
	class ServiceListenerImpl implements ServiceListener {
		@Override
		public void onArrival(ServiceReference ref) {
			reference = ref;
			dependency.setServiceObject(proxyFactory.getProxy());
			dependency.computeDependencyState();
		}

		@Override
		public void onDeparture(ServiceReference reference) {
			release();
			context.ungetService(reference);
			dependency.computeDependencyState();
			broker.getBestService(context, dependency.getSpecificationName(), dependency.getConstraintList(),
					dependency.getBlackList(), this);
		}
		
	}
	
	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		@Override
		public Object addingService(ServiceReference reference) {
			broker = (Broker) context.getService(reference);
			synchronized (waiting) {
				broker.getBestService(context, dependency.getSpecificationName(), dependency.getConstraintList(),
						dependency.getBlackList(), listener);
			}
			return broker;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			broker = null;
		}	
	}
}
