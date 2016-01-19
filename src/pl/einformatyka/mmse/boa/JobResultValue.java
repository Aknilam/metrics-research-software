package pl.einformatyka.mmse.boa;

public class JobResultValue extends AbstractJobResult {
	Double value;

	public JobResultValue(String name) {
		super(name);
	}

	public void set(Double value) {
		this.value = value;
	}

	public JobResultValue(String name, Double value) {
		super(name);
		this.value = value;
	}

	public String toString() {
		return name + ": `" + value + "`";
	}

}
