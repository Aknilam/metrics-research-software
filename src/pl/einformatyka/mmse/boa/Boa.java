package pl.einformatyka.mmse.boa;

import java.io.IOException;
import java.util.List;

import edu.iastate.cs.boa.BoaClient;
import edu.iastate.cs.boa.BoaException;
import edu.iastate.cs.boa.InputHandle;
import edu.iastate.cs.boa.JobHandle;
import edu.iastate.cs.boa.LoginException;
import edu.iastate.cs.boa.NotLoggedInException;
import pl.einformatyka.mmse.cache.ExecutingException;
import pl.einformatyka.mmse.cache.JobResultCache;
import pl.einformatyka.mmse.model.Metric;

public class Boa {
	private BoaClient client;

	private static Boa boa = null;
	private static JobResultCache cache;
	private String username;

	public static Boa initBoa(String username, String pass) throws LoginException {
		if (boa == null) {
			boa = new Boa(username, pass);
			cache = JobResultCache.getCache();
		}
		return boa;
	}

	public static Boa getBoa() {
		return boa;
	}

	private Boa(String username, String pass) throws LoginException {
		this.username = username;
		this.login(username, pass);
	}

	public void login(String username, String pass) throws LoginException {
		client = new BoaClient();
		client.login(username, pass);
	}

	public BoaClient getClient() {
		return client;
	}

	public JobThread runJob(Metric metric, int datasetId) throws IOException, NotLoggedInException, BoaException {
		int jobId = -1;
		try {
			jobId = cache.get(metric.getBoaCode(), username, datasetId);
		} catch (ExecutingException e) {
			jobId = e.getJobId();
		}
		JobHandle jh = null;
		if (jobId != -1) {
			try {
				jh = client.getJob(jobId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (jh == null) {
			jh = client.query(metric.getBoaCode(), readInputHandle(datasetId));
			cache.setExecuting(metric.getBoaCode(), username, datasetId, jh.getId());
		}
		JobThread job = new JobThread(jh, metric, username, datasetId);

		return job;
	}

	public InputHandle readInputHandle(int id) throws NotLoggedInException, BoaException {
		for (final InputHandle d : client.getDatasets()) {
			if (d.getId() == id) {
				return d;
			}
		}
		return null;
	}

	public InputHandle readInputHandle(String dataset) {
		try {
			Boa boa = Boa.getBoa();
			List<InputHandle> datasets = boa.getClient().getDatasets();

			for (InputHandle handle : datasets) {
				if (handle.getName().equals(dataset)) {
					return handle;
				}
			}
		} catch (BoaException e) {
			e.printStackTrace();
		}
		return null;
	}

}
