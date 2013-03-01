package br.ufpe.cin.dsoa.management;

import br.ufpe.cin.dsoa.DsoaConstants;

public class MetricId {

	private String category;
	private String name;

	public MetricId(String category, String name) {
		this.category = category;
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricId other = (MetricId) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		return getCategory() + DsoaConstants.TOKEN + getName();
	}
}
