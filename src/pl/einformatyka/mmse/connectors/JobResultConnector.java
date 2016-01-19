package pl.einformatyka.mmse.connectors;

import com.sun.corba.se.impl.io.TypeMismatchException;
import pl.einformatyka.mmse.boa.*;

import java.util.*;

public class JobResultConnector {
	public static ConnectResult connectDictionariesByNames(JobResult output, HashMap<String, JobResult> jobs,
			String name) throws Exception {

		ArrayList<String> metricsNames = new ArrayList<String>();


		AbstractJobResult outputAbstract = output.get(name);
		for (Map.Entry<String, JobResult> pair : jobs.entrySet()) {
			AbstractJobResult jrAbstract = pair.getValue().get(name);
			if (outputAbstract.getClass() != jrAbstract.getClass()) {
				throw new TypeMismatchException();
			}
		}

		JobResultDictionary jrm = (JobResultDictionary) outputAbstract;

		List<JobResultDictionary> dicts = new ArrayList<JobResultDictionary>();

		for (Map.Entry<String, JobResult> pair : jobs.entrySet()) {
			AbstractJobResult jrAbstract = pair.getValue().get(name);

			JobResultDictionary jrMetric = (JobResultDictionary) jrAbstract;
			dicts.add(jrMetric);
			metricsNames.add(pair.getKey());
		}

		metricsNames.add("fixingRevisions");

		ConnectResult result = new ConnectResult(metricsNames);

		int count = jobs.size() + 1;

		// projects loop
		for (Map.Entry<String, AbstractJobResultDictionary> projectIdDictPair : jrm.getValues().entrySet()) {
			String projectId = projectIdDictPair.getKey();
			AbstractJobResultDictionary projectIdDict = jrm.getValue(projectId);
			JobResultDictionary classes = (JobResultDictionary) projectIdDict;
			// classes loop
			for (Map.Entry<String, AbstractJobResultDictionary> classDictPair : classes.getValues().entrySet()) {
				// last value
				AbstractJobResultDictionary revisionTimeDict = classDictPair.getValue();
				JobResultDictionaryValue finalValue;
				if (revisionTimeDict.getClass() == JobResultDictionary.class) {
					JobResultDictionary revisionTimeDictObj = (JobResultDictionary) revisionTimeDict;
					String revisionId = (String) revisionTimeDictObj.getValues().keySet().toArray()[0];
					finalValue = (JobResultDictionaryValue) (revisionTimeDictObj.getValue(revisionId));
				} else {
					finalValue = (JobResultDictionaryValue) revisionTimeDict;
				}
				Key newKey = new Key();

				ArrayList<String> lack = new ArrayList<String>();
				for (int i = 0; i < dicts.size(); i++) {
					JobResultDictionary jrMetric = dicts.get(i);
					JobResultDictionary projectIds = (JobResultDictionary) jrMetric
							.getValue(projectIdDictPair.getKey());
					Double outputValue = null;
					if (projectIds != null) {
						AbstractJobResultDictionary value = projectIds.getValue(classDictPair.getKey());
						if (value != null) {
							outputValue = ((JobResultDictionaryValue) value).getValue();
						}
					}
					if (outputValue != null) {
						newKey.addValue(outputValue);
					} else {
						lack.add(metricsNames.get(i));
					}
				}
				newKey.addValue(finalValue.getValue());

				if (newKey.size() == count) {
					result.addOrIncrement(newKey);
				} else {
					String lacks = "";
					for (String place : lack) {
						lacks += place + " ";
					}
					System.out.println("error [projectId][classId]: [" + projectIdDictPair.getKey() + "]["
							+ classDictPair.getKey() + "]: " + lacks.substring(0, lacks.length() - 1) + "; " + newKey);
				}
			}
		}
		return result;
	}

	public static ConnectResult connectByNames(JobResult jr1, String name1, JobResult jr2, String name2)
			throws Exception {

		ArrayList<String> names = new ArrayList<String>();
		names.add("metric");
		names.add("parametr");

		ConnectResult result = new ConnectResult(names);

		AbstractJobResult jra1 = jr1.get(name1);
		AbstractJobResult jra2 = jr2.get(name2);
		if (jra1.getClass() != jra2.getClass()) {
			throw new TypeMismatchException();
		}

		JobResultMap jrm1 = (JobResultMap) jra1;
		JobResultMap jrm2 = (JobResultMap) jra2;

		Iterator<String> iterator = jrm1.getData().keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Double value2 = jrm2.getData().get(key);
			if (value2 != null) {
				Key newKey = new Key();
				newKey.addValue(jrm1.getData().get(key));
				newKey.addValue(value2);
				result.addOrIncrement(newKey);
			}
		}
		return result;
	}
}
