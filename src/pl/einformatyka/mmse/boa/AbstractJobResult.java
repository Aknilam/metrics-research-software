package pl.einformatyka.mmse.boa;

public abstract class AbstractJobResult {
	String name;

	public AbstractJobResult(String name) {
		this.name = name;
	}

	public abstract String toString();
}
