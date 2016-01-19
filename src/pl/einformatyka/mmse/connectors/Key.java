package pl.einformatyka.mmse.connectors;

import java.util.ArrayList;
import java.util.Collections;

public class Key {
	private ArrayList<Double> values;

	public String toString() {
		String result = ""; 
		for (Double v : values) {
			result += v + ", ";
		}
		return result.substring(0, result.length() - 2);
	}

	public Key() {
		values = new ArrayList<Double>();
	}
	
	public void addValue(Double v) {
		values.add(v);
	}
	
	public int size() {
		return values.size();
	}

	@Override
	public boolean equals(Object o) {     
		if (this == o)
			return true;
		if (!(o instanceof Key))
			return false;
		Key key = (Key) o;

	    if ((this.values.size() != key.values.size()) || (key.values == null && this.values != null) || (key.values != null && this.values == null)) {
	        return false;
	    }

	    if (key.values == null && this.values == null) return true;

	    // Sort and compare the two lists          
	    Collections.sort(key.values);
	    Collections.sort(this.values);      
	    return key.values.equals(this.values);
	}

	private static int[] primes = {149, 139, 137, 131, 127, 113, 109, 107, 103, 101, 97, 89, 83, 79, 73, 71, 67, 61, 59, 53, 47, 43, 41, 37, 31};
	
	@Override
	public int hashCode() {
		int result = 1;
		for (int i = 0; i < values.size(); i++) {
			result += primes[i % primes.length] * values.get(i);
		}
		return result;
	}

}
