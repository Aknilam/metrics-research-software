package pl.einformatyka.mmse.cache;

public class ExecutingException extends Exception {
	private int jobId;
	public int getJobId() {
		return jobId;
	}
	public ExecutingException(int jobId) {
		super();
		this.jobId = jobId;
	}
}
