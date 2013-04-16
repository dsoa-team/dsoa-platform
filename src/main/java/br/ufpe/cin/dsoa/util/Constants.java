package br.ufpe.cin.dsoa.util;

public interface Constants {
	/** 
	 * Tag REQUIRES
	 */
	public static final String ID_ATT         		= "id";
	public static final String NAME_ATT         	= "name";
	public static final String FIELD_ATT          	= "field";
	public static final String MANAGER_ELE			= "manager";
	
    /**
	 * properties name
	 */
	public static final String PROVIDER_NAME          = "provider.name";
	public static final String PROVIDER_PID           = "service.pid";
	
	/**
	 * Tag handler
	 */
	public static final String NAMESPACE = "br.ufpe.cin.dsoa";
	public static final String NAME      = "requires";
	
	/** 
	 * Tag SLO
	 */
	public static final String SLO_ELEMENT              = "slo";
	public static final String SLO_ATTRIBUTE_ATTRIBUTE 	= "attribute";
	public static final String CATEGORY_ATT 			= "category";
	public static final String SLO_WINDOW_VALUE     	= "window.value";
	public static final String SLO_WINDOW_UNIT         	= "window.unit";
	
	public static final String SLO_EXPRESSION_ATTRIBUTE = "expression";
	public static final String SLO_VALUE_ATTRIBUTE      = "value";
	public static final String SLO_STATISTIC_ATTRIBUTE 	= "statistic";
	public static final String SLO_OPERATION_ATTRIBUTE  = "operation";
	public static final String SLO_WEIGHT_ATTRIBUTE     = "weight";
	
}
