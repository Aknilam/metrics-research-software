package pl.einformatyka.mmse.boa;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

import com.google.common.base.Strings;

public class JobResultDictionary extends AbstractJobResultDictionary {
	Map<String, AbstractJobResultDictionary> values;
	int deepth;

	private JobResultDictionary(String key, ArrayList<String> keys, Double value, int deepth) {
		super(key, deepth);
		this.values = new TreeMap<String, AbstractJobResultDictionary>();
		addValue(keys, value, deepth);
	}

	public JobResultDictionary() {
		super("", 0);
		this.values = new TreeMap<String, AbstractJobResultDictionary>();
	}

	public JobResultDictionary(String key, ArrayList<String> keys, Double value) {
		this(key, keys, value, 0);
	}

	public void addValue(ArrayList<String> keys, Double value) {
		addValue(keys, value, 0);
	}

	private void addValue(ArrayList<String> keys, Double value, int deepth) {
		this.deepth = deepth;
		if (keys.size() >= 2) {
			String key0 = keys.get(0);
			keys.remove(0);
			if (this.values.get(key0) != null) {
				((JobResultDictionary) this.values.get(key0)).addValue(keys, value, deepth + 1);
			} else {
				this.values.put(key0, new JobResultDictionary(key0, keys, value, deepth + 1));
			}
		} else {
			this.values.put(keys.get(0), new JobResultDictionaryValue(keys.get(0), value, deepth + 1));
		}
	}
	
	public Map<String, AbstractJobResultDictionary> getValues() {
		return values;
	}

	public AbstractJobResultDictionary getValue(String key) {
		return values.get(key);
	}

	@Override
	public String toString() {
		String valus = "";
		for (Map.Entry<String, AbstractJobResultDictionary> pair : values.entrySet()) {
			valus += pair.getValue() + "\n";
		}
		if (key.equals("")) {
			return "{\n" + valus + "}";
		} else {
			return Strings.repeat(" ", deepth * 2) + key + ": {\n" + valus + Strings.repeat(" ", deepth * 2) + "}";
		}
	}

}
