package pl.einformatyka.mmse.boa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iastate.cs.boa.BoaException;
import edu.iastate.cs.boa.CompileStatus;
import edu.iastate.cs.boa.ExecutionStatus;
import edu.iastate.cs.boa.JobHandle;
import pl.einformatyka.mmse.cache.JobResultCache;
import pl.einformatyka.mmse.model.Metric;

public class JobThread implements Runnable {
	private List<JobStatusListener> listeners = new ArrayList<JobStatusListener>();
	private JobHandle jh;
	private JobResult output;
	private Exception notified;
	private boolean notifiedSet = false;

	public final Metric metric;
	private String username;
	private Integer datasetId;
	
	public Thread t;

	public JobThread(JobHandle jh, Metric metric, String username, Integer datasetId) {
		this.jh = jh;
		this.metric = metric;
		this.username = username;
		this.datasetId = datasetId;

		t = new Thread(this);
		t.start();
	}

	public void run() {
		while (jh.getExecutionStatus() != ExecutionStatus.FINISHED) {
			if (jh.getCompilerStatus() == CompileStatus.ERROR) {
				try {
					notifyListeners(new Exception(String.join("\r\n", jh.getCompilerErrors())));
				} catch (Exception e) {
					notifyListeners(e);
				}
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				notifyListeners(e);
				return;
			}
			try {
				jh.refresh();
			} catch (BoaException e) {
				notifyListeners(e);
				return;
			}
		}

		try {
			output = new JobResult(jh.getSource(), jh.getOutput());
			JobResultCache cache = JobResultCache.getCache();
			// boaCode is essentially equal to jh.getSource(), but
			// Boa is removing whitespaces, so it cannot be used to read from
			// cache
			cache.changeToExecuted(metric.getBoaCode(), username, datasetId);
			notifyListeners(null);
		} catch (BoaException e) {
			notifyListeners(e);
		}
	}

	public JobResult getOutput() {
		return output;
	}

	public void registerJobStatusListener(JobStatusListener listener) {
		listeners.add(listener);
		if (notifiedSet == true) {
			if (notified == null) {
				listener.jobDone(this);
			} else {
				listener.jobError(this, notified);
			}
		}
	}

	private void notifyListeners(Exception e) {
		notified = e;
		notifiedSet = true;
		for (Iterator<JobStatusListener> iter = listeners.iterator(); iter.hasNext();) {
			JobStatusListener listener = (JobStatusListener) iter.next();
			if (e == null) {
				listener.jobDone(this);
			} else {
				listener.jobError(this, e);
			}
		}
	}
}