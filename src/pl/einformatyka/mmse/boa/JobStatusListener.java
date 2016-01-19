package pl.einformatyka.mmse.boa;

public interface JobStatusListener {
	public void jobDone(JobThread thread);

	public void jobError(JobThread thread, Exception e);
}