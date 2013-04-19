package br.ufpe.cin.dsoa.handler.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.apache.felix.ipojo.IPOJOServiceFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentException;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.impl.BrokerImpl;
import br.ufpe.cin.dsoa.contract.Constraint;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public class Dependency implements FieldInterceptor, ServiceListener {

	private DependencyHandler handler;
	private ServiceConsumer consumer;
	private String field;
	private Class<?> specification;
	private String filter;
	private List<Constraint> constraintList;
	private List<ServiceReference> blackList;
	private DependencyStatus status;
	private Broker broker;
	private BundleContext context;
	private ServiceReference reference;
	private DynamicProxyFactory proxyFactory;

	public Dependency(DependencyHandler dependencyHandler, ServiceConsumer serviceConsumer, String field,
			Class<?> specification, String filter, List<Constraint> constraintList) {
		super();
		this.handler = dependencyHandler;
		this.consumer = serviceConsumer;
		this.field = field;
		this.specification = specification;
		this.filter = filter;
		this.constraintList = constraintList;
		this.blackList = new ArrayList<ServiceReference>();
		this.status = DependencyStatus.UNRESOLVED;
		this.context = dependencyHandler.getInstanceManager().getContext();
		this.broker = new BrokerImpl(context, specification.getName(), constraintList, blackList, this);
		this.proxyFactory = new DynamicProxyFactory();
	}

	public DependencyHandler getHandler() {
		return handler;
	}

	public ServiceConsumer getConsumer() {
		return consumer;
	}

	public String getField() {
		return field;
	}

	public String getFilter() {
		return filter;
	}

	public List<Constraint> getConstraintList() {
		return constraintList;
	}

	public DependencyStatus getStatus() {
		return status;
	}

	public boolean isValid() {
		return this.status == DependencyStatus.RESOLVED;
	}

	public Class<?> getSpecification() {
		return this.specification;
	}

	public Object getService() {
		Object svc = context.getService(reference);
		if (svc instanceof IPOJOServiceFactory) {
			Object obj = ((IPOJOServiceFactory) svc).getService(handler.getInstanceManager());
			return obj;
		} else {
			return svc;
		}
	}

	public void start() {
		broker.getBestService();
	}

	public void stop() {
		
	}

	private void computeDependencyState() {
		boolean mustCallValidate = false;
		boolean mustCallInvalidate = false;
		synchronized (this) {
			if (reference != null) {
				if (status == DependencyStatus.UNRESOLVED) {
					status = DependencyStatus.RESOLVED;
					mustCallValidate = true;
				}
			} else {
				if (status == DependencyStatus.RESOLVED) {
					status = DependencyStatus.UNRESOLVED;
					mustCallInvalidate = true;
				}
			}
		}

		if (mustCallInvalidate) {
			invalidate();
		} else if (mustCallValidate) {
			validate();
		}

	}

	/**
	 * Calls the listener callback to notify the new state of the current
	 * dependency.
	 */
	private void invalidate() {
		handler.invalidate();
	}

	/**
	 * Calls the listener callback to notify the new state of the current
	 * dependency.
	 */
	private void validate() {
		handler.validate();
	}

	@Override
	public void onSet(Object pojo, String fieldName, Object value) {

	}

	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
		//return Proxy.newProxyInstance(getHandler().getInstanceManager().getClazz().getClassLoader(), new Class[] { specification }, new FakeInvoker());
		return proxyFactory.getProxy(getSpecification());
	}

	@Override
	public void onArrival(ServiceReference reference) {
		this.reference = reference;
		this.computeDependencyState();
	}

	@Override
	public void onDeparture(ServiceReference reference) {
		if (this.reference != null && this.reference.equals(reference)) {
			context.ungetService(reference);
			this.reference = null;
			this.computeDependencyState();
			this.broker.getBestService();
		}
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
		public Object getProxy(Class spec) {
			return Proxy.newProxyInstance(getHandler().getInstanceManager().getClazz().getClassLoader(), new Class[] { getSpecification() }, this);
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
			if (status != DependencyStatus.UNRESOLVED) {
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
				return method.invoke(svc, args);
			} else {
				throw new ComponentException("The dependency on " + specification + " interface is not valid!");
			}
		}

	}
	
}
