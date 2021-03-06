package br.ufpe.cin.dsoa.platform.registry.impl;
import java.util.Comparator;

import org.osgi.framework.ServiceReference;


public class RankComparator implements Comparator<ServiceReference>{
	
	public int compare(ServiceReference ref0, ServiceReference ref1) {

        Double grade0         = null;
        Double grade1         = null;
        
        grade0 = (Double) ref0.getProperty("constraint.operation.qos.AvgResponseTime.getCotation.LE");
        grade1 = (Double) ref1.getProperty("constraint.operation.qos.AvgResponseTime.getCotation.LE");

        if (grade0 != null && grade1 != null) {
            return grade0.compareTo(grade1); // Best grade first.
        } else {
            return 0; // Equals
        }
    }
	
}

