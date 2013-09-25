package br.ufpe.cin.dsoa.util;

public interface Constants {
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
	public static final String REQUIRES_ATT_FIELD		= "field";
	
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
	public static final String EVENT_TYPE				= "type";
	public static final String INVOCATION_EVENT			= "InvocationEvent";
	public static final String EVENT_METADATA 			= "metadata";
	public static final String EVENT_DATA 				= "data";
	public static final String INVOCATION_EVENT_TOPIC 	= "br/ufpe/cin/dsoa/invocationEvent";
	public static final String EVENT_SOURCE = "source";
	public static final String ATTRIBUTE_VALUE = "value";
	
	public static final String CONTEXT_NAME = "PartitionedByEventSource";
}
