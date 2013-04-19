package br.ufpe.cin.dsoa.handler.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.felix.ipojo.IPOJOServiceFactory;
import org.apache.felix.ipojo.InstanceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentException;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.impl.BrokerImpl;

public class DependencyManager implements ServiceListener {

	private BundleContext context;
	private InstanceManager instanceManager;

	private Broker broker;
	private DynamicProxyFactory proxyFactory;

	private Dependency dependency;
	private ServiceReference reference;
	private Object realService;

	public DependencyManager(Dependency dependency, InstanceManager instanceManager) {
		this.instanceManager = instanceManager;
		this.context = instanceManager.getContext();
		this.dependency = dependency;
		this.broker = new BrokerImpl(context, dependency.getSpecificationName(), dependency.getConstraintList(),
				dependency.getBlackList(), this);
		this.proxyFactory = new DynamicProxyFactory();
	}

	public Object getService() {
		if (this.realService == null) {
			this.realService = context.getService(reference);
			if (this.realService instanceof IPOJOServiceFactory) {
				this.realService = ((IPOJOServiceFactory) this.realService).getService(instanceManager);
			}
		}
		return this.realService;
	}
	
	public void resolve() {
		broker.getBestService();
	}

	public void release() {
		this.reference = null;
		this.dependency.setServiceObject(null);
	}

	@Override
	public void onArrival(ServiceReference reference) {
		this.reference = reference;
		this.dependency.setServiceObject(this.proxyFactory.getProxy());
		this.dependency.computeDependencyState();
	}

	@Override
	public void onDeparture(ServiceReference reference) {
		this.release();
		context.ungetService(reference);
		this.dependency.computeDependencyState();
		this.broker.getBestService();
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
				return method.invoke(svc, args);
			} else {
				throw new ComponentException("The dependency on " + dependency.getSpecificationName() + " interface is not valid!");
			}
		}

	}
}
