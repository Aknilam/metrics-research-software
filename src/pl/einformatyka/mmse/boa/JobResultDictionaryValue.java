package pl.einformatyka.mmse.boa;

import com.google.common.base.Strings;

import pl.einformatyka.mmse.boa.AbstractJobResultDictionary;

public class JobResultDictionaryValue extends AbstractJobResultDictionary {
	private Double value;

	public JobResultDictionaryValue(String key, Double value, int deepth) {
		super(key, deepth);
		this.value = value;
	}
	
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Strings.repeat(" ", deepth * 2) + key + ": " + value;
	}

}
