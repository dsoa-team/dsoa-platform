package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.util.Constants;

public class MonitoringAgent {

	private BundleContext ctx;
	private ServiceReference serviceReference;
	private boolean isMonitorable;
	
	private volatile boolean started;
	
	private volatile List<Method> methods;
	private volatile Map<String, Object[]> paramsMap;
	
	private volatile Object serviceObject;
	private volatile String serviceId;
	private volatile String itfName;
	private volatile EventTypeCatalog eventCatalog;
	private volatile EventProcessingService epService;

	public MonitoringAgent(EventTypeCatalog eventCatalog, EventProcessingService epService, BundleContext ctx, String serviceId, String itfName, ServiceReference reference,
			List<Method> methods, Map<String,Object[]> paramsMap) {
		this.eventCatalog = eventCatalog;
		this.epService = epService;
		this.ctx = ctx;
		this.serviceId = serviceId;
		this.itfName = itfName;
		this.serviceReference = reference;
		this.methods = methods;
		this.paramsMap = paramsMap;
		this.isMonitorable = (this.ctx != null && this.serviceReference != null);
	}

	private Thread monitoringThread;
	private MonitoringAgentRequestor monitoringRequestor;
	
	public void start() {
		if (isMonitorable && !started) {
			serviceObject = ctx.getService(serviceReference);
			monitoringRequestor = new MonitoringAgentRequestor();
			monitoringThread = new Thread(monitoringRequestor, serviceId + "-Monitor");
			monitoringThread.start();
			started = true;
		}
	}

	public void stop() {
		if (isMonitorable && started) {
			try {
				monitoringThread.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ctx.ungetService(serviceReference);
			started = false;
			monitoringThread = null;
		}
	}
	
	private class Summary {
		
		private long invCounter = 0;
		private long sucCounter = 0;
		private long excCounter = 0;
		private long respTime = 0;
		private String name;
		
		public Summary(String methodName) {
			this.name = methodName;
		}

		public void incInvCounter() {
			invCounter++;
		}
		
		public void incSucCounter() {
			sucCounter++;
		}
		
		public void incExcCounter() {
			excCounter++;
		}
		
		public void setRespTime(long respTime) {
			this.respTime = respTime;
		}

		public long getInvCounter() {
			return invCounter;
		}

		public long getSucCounter() {
			return sucCounter;
		}

		public long getExcCounter() {
			return excCounter;
		}

		public long getRespTime() {
			return respTime;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	private class MonitoringAgentRequestor implements Runnable {
		private int interval = 5000;
		private Map<String, Summary> summaries = new HashMap<String, Summary>();
		
		MonitoringAgentRequestor() {
			for(Method method : methods) {
				summaries.put(method.getName(), new Summary(method.getName()));
			}
		}
		
		public void run() {
			while (started) {
				long reqTime = System.currentTimeMillis();
				
				for (Method method : methods) {
					Summary s = summaries.get(method.getName());
					try {
						method.invoke(serviceObject, paramsMap.get(method.getName()));
						s.incSucCounter();
					} catch (Exception e) {
						s.incExcCounter();
						System.out.println("Method name: " + method.getName());
						e.printStackTrace();
					} finally {
						s.incInvCounter();
						s.setRespTime(System.currentTimeMillis() - reqTime);
					}
					notifyMonitoredResults(s);
				}
				
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void notifyMonitoredResults(Summary s) {
			Map<String, Object> metadata = new HashMap<String, Object>();
			metadata.put(Constants.SOURCE, serviceId + "." + s.getName());
			metadata.put(Constants.TIMESTAMP, System.currentTimeMillis());

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("serviceId", serviceId);
			data.put("serviceInterface", itfName);
			data.put("operationName", s.getName());
			data.put("totalInvocations", s.getInvCounter());
			data.put("successfulInvocations", s.getSucCounter());
			data.put("failedInvocations", s.getExcCounter());
			data.put("lastResponseTime", s.getRespTime());

			EventType eventType = eventCatalog.get(Constants.SERVICE_MONITORING_EVENT);
			Event dsoaEvent = eventType.createEvent(metadata, data);
			
			epService.publish(dsoaEvent);
		}
	}

}
