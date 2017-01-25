package br.ufpe.cin.dsoa.util;

public interface Constants {
	
	public static final String DSOA_PREFIX	= "DSOA";
	/**
	 * Tag Component
	 */
	public static final String COMPOSITION_ID_ATT        	= "id";
	public static final String COMPOSITION_NAME_ATT      	= "name";
	
	/** 
	 * Tag Requires
	 */
	public static final String REQUIRES_TAG_NAMESPACE	= "br.ufpe.cin.dsoa";
	public static final String REQUIRES_TAG    			= "requires";
	public static final String REQUIRES_ATT_FIELD		= "name";
	
	public static final String PROVIDES_TAG_NAMESPACE	= REQUIRES_TAG_NAMESPACE;
	public static final String PROVIDES_TAG    			= "provides";
	public static final String PROVIDES_ATT_FIELD		= "name";
	public static final String PROVIDES_ATT_CLASSNAME	= "classname";
	
	/** 
	 * Tag Constraint
	 */
	public static final String CONSTRAINT_TAG		    	= "constraint";
	public static final String CONSTRAINT_ATT_METRIC 		= "attribute";
	public static final String CONSTRAINT_ATT_OPERATION		= "operation";
	public static final String CONSTRAINT_ATT_EXPRESSION    = "expression";
	public static final String CONSTRAINT_ATT_THREASHOLD	= "threashold";
	public static final String CONSTRAINT_ATT_WEIGHT		= "weight";
	
	public static final String SERVICE_PROXY = "service.proxy";
	public static final String REMOTE_SERVICE = "service.imported"; 
	public static final String TOKEN = ".";
	public static final String UNDERLINE = "_";
	public static final String CONTEXT = "context";
	public static final String CONSTRAINT_ATT_WINDOW_SIZE = "windowSize";
	public static final String CONSTRAINT_ATT_WINDOW_TYPE = "windowType";
	
	public static final String NON_FUNCTIONAL_SPECIFICATION = "nonfunctional.specification";
	
	public static final String MANAGED_SERVICE = "(service.managed=*)";
	
	/**
	 * Event
	 */
	public static final String DSOA_EVENT 				= "dsoa.event";
	public static final String EVENT_TYPE				= "type";
	public static final String INVOCATION_EVENT			= "InvocationEvent";
	public static final String UNBIND_EVENT 			= "UnbindEvent";
	public static final String BIND_EVENT 				= "BindEvent";
	public static final String EVENT_METADATA 			= "metadata";
	public static final String EVENT_DATA 				= "data";
	public static final String INVOCATION_EVENT_TOPIC 	= "br/ufpe/cin/dsoa/invocationEvent";
	public static final String EVENT_SOURCE 			= "source";
	public static final String ATTRIBUTE_VALUE 			= "value";
	
	public static final String ADAPTER_ID	 			= "adapter-id";
	
	/**
	 *  Properties
	 */
	public static final String SERVICE_ID				= "serviceId";
	public static final String CONSUMER_ID				= "consumerId";
	public static final String SERVICE_INTERFACE		= "serviceInterface";
	public static final Object METADATA_TIMESTAMP 		= "timestamp";
	public static final Object METADATA_ID 				= "id";
	public static final String REMOTE					= "remote";
	
	public static final String CONTEXT_NAME = "PartitionedByEventSource";
	public static final String LOG_FOLDER = "logs/";
	public static final String LOG_EXTENSION = ".log";
}
