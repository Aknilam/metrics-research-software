package pl.einformatyka.mmse.boa;

import java.util.HashMap;
import java.util.Map;

public class JobResultMap extends AbstractJobResult {
	Map<String, Double> map;

	public JobResultMap(String name) {
		super(name);
		map = new HashMap<String, Double>();
	}

	public Map<String, Double> getData() {
		return map;
	}

	public void addValue(String key, Double value) {
		map.put(key, value);
	}

	public String toString() {
		String result = name + "\n";

		for (Map.Entry<String, Double> pair : map.entrySet()) {
			result += pair.getKey() + " = `" + pair.getValue() + "`";
			result += "\n";
		}
		return result;
	}
}
