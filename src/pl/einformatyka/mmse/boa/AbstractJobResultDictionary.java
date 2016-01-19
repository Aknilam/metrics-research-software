package pl.einformatyka.mmse.boa;

public abstract class AbstractJobResultDictionary extends AbstractJobResult {
	protected String key;
	protected int deepth = 0;

	public AbstractJobResultDictionary(String key, int deepth) {
		super(key);
		this.key = key;
		this.deepth = deepth;
	}

	public abstract String toString();
}
