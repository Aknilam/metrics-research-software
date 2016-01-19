package pl.einformatyka.mmse.boa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobResult {
	HashMap<String, AbstractJobResult> results;
	private final static String TYPE_COLLECTION = "collection";
	private final static String TYPE_ARRAY = "array";
	private final static String TYPE_WEIGHTED = "weighted";
	private final static String TYPE_SINGLE = "single";

	public JobResult() {
		results = new HashMap<String, AbstractJobResult>();
	}

	public JobResult(String source, String output) {
		this();
		setResult(source, output);
	}

	public void setResult(String source, String output) {
		Map<String, String> outputs = countOutputs(source);
		setOutputs(outputs);
		setValues(outputs, output);
	}

	public Map<String, AbstractJobResult> getResult() {
		return results;
	}

	public Set<String> getNames() {
		return results.keySet();
	}

	public AbstractJobResult get(String name) throws Exception {
		AbstractJobResult result = results.get(name);
		if (result == null) {
			throw new Exception("name doesn't exist");
		}
		return result;
	}

	public String toString() {
		String result = "";
		for (Map.Entry<String, AbstractJobResult> pair : results.entrySet()) {
			if (result != "") {
				result += "\n";
			}
			result += "Name: `" + pair.getKey() + "`\n";
			result += pair.getValue().toString();
		}
		return result;
	}

	private void setOutputs(Map<String, String> map) {
		for (Map.Entry<String, String> pair : map.entrySet()) {
			String name = pair.getKey();
			String type = pair.getValue();
			if (type.equals(TYPE_SINGLE)) {
				results.put(name, new JobResultValue(name));
			} else if (type.equals(TYPE_COLLECTION) || type.equals(TYPE_ARRAY)) {
				results.put(name, new JobResultDictionary());
			} else {
				results.put(name, new JobResultMap(name));
			}
		}
	}

	private void setValues(Map<String, String> outputs, String output) {
		String[] values = output.split("\n");
		for (int v = 0; v < values.length; v++) {
			String[] splitted = values[v].split("\\[");
			String name = splitted[0];

			String value = "";

			for (int s = 1; s < splitted.length; s++) {
				value += splitted[s];
			}

			String type = outputs.get(name);
			AbstractJobResult jr = results.get(name);
			if (type.equals(TYPE_SINGLE)) {
				JobResultValue jv = (JobResultValue) jr;

				jv.set(Double.parseDouble(value.replace("] = ", "")));
			} else if (type.equals(TYPE_ARRAY) || type.equals(TYPE_COLLECTION)) {
				JobResultDictionary jm = (JobResultDictionary) jr;
				String[] kv = value.split("\\] = ");
				ArrayList<String> keys = new ArrayList<String>();
				Collections.addAll(keys, kv[0].split("\\]"));
				jm.addValue(keys, Double.parseDouble(kv[1]));
			} else if (type.equals(TYPE_WEIGHTED)) {
				JobResultMap jm = (JobResultMap) jr;
				String vv = value.split("\\] = ")[1];
				String[] kv = vv.split(", ");
				jm.addValue(kv[0].replace("]", "_"), Double.parseDouble(kv[1]));
			}
		}
	}

	private Map<String, String> countOutputs(String source) {
		Map<String, String> result = new HashMap<String, String>();

		String[] list = source.split("(;[ ]*[^#\n])|\n");

		for (int i = 0; i < list.length; i++) {
			String name = getOutputName(list[i]);
			if (name != null) {
				String type = list[i].split(":[ ]*output[ ]*")[1];
				if (type.indexOf("collection") > -1) {
					type = TYPE_COLLECTION;
				} else if (type.indexOf("[") > -1) {
					type = TYPE_ARRAY;
				} else if (type.indexOf("weight") > -1) {
					type = TYPE_WEIGHTED;
				} else {
					type = TYPE_SINGLE;
				}
				result.put(name, type);
			}
		}
		return result;
	}

	private boolean isOutput(String line) {
		Pattern pattern = Pattern.compile(":[ ]*output ");
		Matcher matcher = pattern.matcher(line);
		return matcher.find();
	}

	private String getOutputName(String line) {
		if (isOutput(line)) {
			return line.split(":[ ]*output[ ]*")[0].replaceAll(" ", "");
		} else {
			return null;
		}
	}
}
