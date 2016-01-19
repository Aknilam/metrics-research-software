package pl.einformatyka.mmse.connectors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnectResult {
	HashMap<Key, Integer> data;
	private final ArrayList<String> names;

	public ConnectResult(ArrayList<String> names) {
		this.names = names;
		data = new HashMap<Key, Integer>();
	}

	public HashMap<Key, Integer> getData() {
		return data;
	}

	public void addOrIncrement(Key newKey) {
		Integer count = data.get(newKey);
		if (count != null) {
			count++;
		} else {
			count = 1;
		}
		data.put(newKey, count);
	}

	public String toString() {
		String result = "";
		for (Map.Entry<Key, Integer> pair : data.entrySet()) {
			Key k = (Key) pair.getKey();
			result += k + ": " + pair.getValue() + "\n";
		}
		return result;
	}

	public void toTXT(String name) {

		String output = "";
		for (Map.Entry<Key, Integer> pair : data.entrySet()) {
			Key k = (Key) pair.getKey();

			output += k + ", " + pair.getValue() + "\n";
		}

		try (PrintStream out = new PrintStream(new FileOutputStream(name + ".txt"))) {
			out.println(output);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
	}

	public String toARFF(String name) {
		String output = "";
		for (Map.Entry<Key, Integer> pair : data.entrySet()) {
			Key k = (Key) pair.getKey();

			for (int i = 0; i < (Integer) pair.getValue(); i++) {
				output += k + "\n";
			}
		}
		String outputNames = "", fileName = "";
		for (String n : names) {
			outputNames += "@attribute " + n + " numeric\n";
			fileName += "_" + n;
		}
		output = "@relation output\n\n" + outputNames + "\n" + "@data\n" + output;
		fileName = name + fileName + ".arff";
		try (PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
			out.println(output);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		return fileName;
	}
}
