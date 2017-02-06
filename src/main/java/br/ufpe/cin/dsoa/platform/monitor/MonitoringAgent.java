package br.ufpe.cin.dsoa.platform.monitor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
	private volatile boolean isMonitorable;

	private volatile boolean started;

	private volatile List<Method> methods;
	private volatile Map<String, Object[]> paramsMap;

	private volatile Object serviceObject;
	private String serviceId;
	private volatile String itfName;
	private volatile EventTypeCatalog eventCatalog;
	private volatile EventProcessingService epService;

	public MonitoringAgent(EventTypeCatalog eventCatalog,
			EventProcessingService epService, BundleContext ctx,
			String serviceId, String itfName, ServiceReference reference,
			List<Method> methods, Map<String, Object[]> paramsMap) {
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
	private volatile MonitoringAgentRequestor monitoringRequestor;

	public void start() {
		if (isMonitorable && !started) {
			serviceObject = ctx.getService(serviceReference);
			monitoringRequestor = new MonitoringAgentRequestor();
			monitoringThread = new Thread(monitoringRequestor, serviceId
					+ "-Monitor");
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
			monitoringRequestor = null;
			System.err
					.println("============= MONITOR SAIU ======================");
		}
	}

	private class Summary {

		@Override
		public String toString() {
			return "[serviceId=" + serviceId + ", invCounter=" + invCounter + ", sucCounter=" + sucCounter
					+ ", excCounter=" + excCounter + ", respTime=" + respTime
					+ ", previousFailureTime=" + previousFailureTime
					+ ", lastFailureTime=" + lastFailureTime
					+ ", success=" + success
					+ ", failure=" + failure + "]";
		}

		private long invCounter = 0;
		private long sucCounter = 0;
		private long excCounter = 0;
		private long respTime = 0;
		private long previousFailureTime = 0;
		private long lastFailureTime = Long.MAX_VALUE;
		private long startMonitoringTime = 0;
		private String operationName;
		private String serviceId;
		private int success;
		private int failure;

		public Summary(String serviceId, String methodName) {
			this.serviceId = serviceId;
			this.operationName = methodName;
		}

		public long getPreviousFailureTime() {
			return previousFailureTime;
		}

		public void setPreviousFailureTime(long previousFailureTime) {
			this.previousFailureTime = previousFailureTime;
		}

		public long getStartMonitoringTime() {
			return startMonitoringTime;
		}

		public void setStartMonitoringTime(long startMonitoringTime) {
			this.startMonitoringTime = startMonitoringTime;
		}

		public long getLastFailureTime() {
			return lastFailureTime;
		}

		public void setLastFailureTime(long lastFailureTime) {
			this.lastFailureTime = lastFailureTime;
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

		public String getOperationName() {
			return this.operationName;
		}

		public void setFailure(int i) {
			this.failure = i;
		}

		public void setSuccess(int i) {
			this.success = i;
		}
	}

	private class MonitoringAgentRequestor implements Runnable {
		private int interval = 1000;
		private Map<String, Summary> summaries = new HashMap<String, Summary>();
		private Logger logger;

		MonitoringAgentRequestor() {
			for (Method method : methods) {
				summaries.put(method.getName(), new Summary(serviceId, method.getName()));
			}
			java.util.logging.Formatter f = new java.util.logging.Formatter() {

				public String format(LogRecord record) {
					StringBuilder builder = new StringBuilder(1000);
					builder.append(formatMessage(record));
					return builder.toString();
				}
			};

			logger = Logger.getLogger("MonitoringAgent");
			try {
				FileHandler logHandler = new FileHandler(
						"logs/app/MonitoringAgent.log");
				logHandler.setFormatter(f);
				logger.setUseParentHandlers(false);
				logger.addHandler(logHandler);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (started) {
				long reqTimestamp = System.currentTimeMillis();
				String exceptionClass = null;
				String exceptionMessage = null;
				long respTimestamp;
				for (Method method : methods) {
					boolean success = true;
					Summary s = summaries.get(method.getName());
					if (s.getStartMonitoringTime() == 0) {
						s.setStartMonitoringTime(reqTimestamp);
					}
					try {
						method.invoke(serviceObject,
								paramsMap.get(method.getName()));
						s.incSucCounter();
						s.setFailure(0);
						s.setSuccess(1);						
					} catch (Exception e) {
						s.incExcCounter();
						if (s.getPreviousFailureTime() != 0) {
							s.setPreviousFailureTime(s.getLastFailureTime());
						}
						exceptionClass = e.getClass().getName();
						exceptionMessage = e.getMessage();
						success = false;
						System.err.println("Availability exception!");
						s.setLastFailureTime(System.currentTimeMillis());
						//e.printStackTrace();
						s.setFailure(1);
						s.setSuccess(0);
					} finally {
						s.incInvCounter();
						respTimestamp = System.currentTimeMillis();
						s.setRespTime(respTimestamp - reqTimestamp);
					}
					logger.info(s.toString()+"\n");
					notifyInvocation("MonitoringAgent",s.serviceId, s.getOperationName(), reqTimestamp, respTimestamp, success, exceptionClass,
							exceptionMessage,null,null,null, null );
				}

				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void notifyInvocation(String consumerId, String serviceId, String operationName,
				long requestTimestamp, long responseTimestamp, boolean success,
				String exceptionClass, String exceptionMessage, Map<String, String> parameterTypes,
				Map<String, Object> parameterValues, String returnType, Object returnValue) {

			String source = String.format("%s%s%s", serviceId, Constants.TOKEN, operationName);

			Map<String, Object> metadata = this.loadInvocationMetadata(source);
			Map<String, Object> data = this.loadInvocationData(consumerId, serviceId,
					operationName, requestTimestamp, responseTimestamp, success, exceptionClass,
					exceptionMessage, parameterTypes, parameterValues, returnType, returnValue);

			EventType eventType = eventCatalog.get(Constants.INVOCATION_EVENT);
			Event dsoaEvent = eventType.createEvent(metadata, data);

			epService.publish(dsoaEvent);
			
		}
		
		private Map<String, Object> loadInvocationMetadata(String source) {
			Map<String, Object> metadata = new HashMap<String, Object>();
			metadata.put("source", source);

			return metadata;
		}
		
		private Map<String, Object> loadInvocationData(String consumerId, String serviceId,
				String operationName, long requestTimestamp, long responseTimestamp,
				boolean success, String exceptionClass, String exceptionMessage,
				Map<String, String> parameterTypes, Map<String, Object> parameterValues,
				String returnType, Object returnValue) {

			Map<String, Object> data = new HashMap<String, Object>();
		
			data.put(Constants.CONSUMER_ID, consumerId);
			data.put(Constants.SERVICE_ID, serviceId);
			data.put(Constants.OPERATION_NAME, operationName);
			data.put(Constants.REQUEST_TIMESTAMP, requestTimestamp);
			data.put(Constants.RESPONSE_TIMESTAMP, responseTimestamp);
			data.put(Constants.SUCCESS, success);
			if (success) {
				data.put(Constants.SUCCESS_INCREMENT, 1);
				data.put(Constants.FAILURE_INCREMENT, 0);
			} else {
				data.put(Constants.SUCCESS_INCREMENT, 0);
				data.put(Constants.FAILURE_INCREMENT, 1);
			}
			data.put(Constants.RESPONSE_TIME, responseTimestamp - requestTimestamp);
			if (exceptionClass != null) {
				data.put(Constants.EXCEPTION_MESSAGE, exceptionMessage);
				data.put(Constants.EXCEPTION_CLASS, exceptionClass);
			}
			data.put(Constants.PARAMETER_TYPES, parameterTypes);
			data.put(Constants.PARAMETER_VALUES, parameterValues);
			data.put(Constants.RETURN_TYPE, returnType);
			data.put(Constants.RETURN_VALUE, returnValue);
			
			
			return data;
		}
		
		
		private void notifyMonitoredResults(Summary s) {
			long timestamp = System.currentTimeMillis();
			logger.info(timestamp + ":" + s.toString()+"\n");

			Map<String, Object> metadata = new HashMap<String, Object>();
			metadata.put(Constants.SOURCE, serviceId + "." + s.getOperationName());
			metadata.put(Constants.TIMESTAMP, timestamp);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("serviceId", serviceId);
			data.put("serviceInterface", itfName);
			data.put("operationName", s.getOperationName());
			data.put("totalInvocations", s.getInvCounter());
			data.put("successfulInvocations", s.getSucCounter());
			data.put("failedInvocations", s.getExcCounter());
			data.put("lastFailureTime", s.getLastFailureTime());
			data.put("previousFailureTime", s.getPreviousFailureTime());
			data.put("lastResponseTime", s.getRespTime());
			data.put("monitoringTime",
					System.currentTimeMillis() - s.getStartMonitoringTime());

			EventType eventType = eventCatalog
					.get(Constants.SERVICE_MONITORING_EVENT);
			Event dsoaEvent = eventType.createEvent(metadata, data);

			epService.publish(dsoaEvent);
			
		}
	}

}
