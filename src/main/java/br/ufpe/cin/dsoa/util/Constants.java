package br.ufpe.cin.dsoa.util;

public interface Constants {
	/**
	 * Tag Component
	 */
	public static final String COMPONENT_ID_ATT        	= "id";
	public static final String COMPONENT_NAME_ATT      	= "name";
	
	/** 
	 * Tag Requires
	 */
	public static final String REQUIRES_TAG_NAMESPACE	= "br.ufpe.cin.dsoa";
	public static final String REQUIRES_TAG    			= "requires";
	public static final String REQUIRES_ATT_FIELD		= "field";
	public static final String REQUIRES_ATT_FILTER		= "filter";
	
	/** 
	 * Tag Constraint
	 */
	public static final String CONSTRAINT_TAG		    	= "constraint";
	public static final String CONSTRAINT_ATT_METRIC 		= "metric";
	public static final String CONSTRAINT_ATT_OPERATION		= "operation";
	public static final String CONSTRAINT_ATT_EXPRESSION    = "expression";
	public static final String CONSTRAINT_ATT_THREASHOLD	= "threashold";
	public static final String CONSTRAINT_ATT_WEIGHT		= "weight";
	
	public static final String SERVICE_PROXY = "service.proxy";
	public static final String REMOTE_SERVICE = "service.imported"; 
	public static final String TOKEN = ".";
	public static final String CONTEXT = "context";
	public static final String CONSTRAINT_ATT_WINDOW_SIZE = "windowSize";
	public static final String CONSTRAINT_ATT_WINDOW_TYPE = "windowType";
}
