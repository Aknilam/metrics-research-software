package pl.einformatyka.mmse;

import edu.iastate.cs.boa.BoaException;
import edu.iastate.cs.boa.LoginException;
import edu.iastate.cs.boa.NotLoggedInException;
import org.xml.sax.SAXException;
import pl.einformatyka.mmse.boa.Boa;
import pl.einformatyka.mmse.boa.JobResult;
import pl.einformatyka.mmse.boa.JobThread;
import pl.einformatyka.mmse.connectors.ConnectResult;
import pl.einformatyka.mmse.connectors.JobResultConnector;
import pl.einformatyka.mmse.model.Metric;
import pl.einformatyka.mmse.prediction.PredictionModel;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Main {

	public static void main(String[] args) {
		executeScript();
	}


	public static void executeScript() {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("res/config.properties");
			prop.load(input);

			String username = prop.getProperty("user");
			String pass = prop.getProperty("pass");
			Boa.initBoa(username, pass);

			try {
				Configuration config = Configuration.readConfiguration(null);
				PredictionModel predictionModel = new PredictionModel(config);
				System.out.println("Dataset ID: " + config.getDatasetID());
				System.out.println("Output parameter: " + config.getOutputParameter());
				System.out.println("Metrics: " + config.getMetrics() + "\n");

				JobThread output;
				ArrayList<JobThread> metrics = new ArrayList<JobThread>();

				for (Metric metric : config.getMetrics()) {
					System.out.println(metric);
					JobThread jobThread = Boa.getBoa().runJob(metric, config.getDatasetID());
					metrics.add(jobThread);
				}
				output = Boa.getBoa().runJob(config.getOutput(), config.getDatasetID());

				HashMap<String, JobResult> results = new HashMap<String, JobResult>();

				output.t.join();
				for (JobThread job : metrics) {
					job.t.join();
					System.out.println(job.metric.getName() + " Output done");

					results.put(job.metric.getName(), job.getOutput());
				}

				try {
					ConnectResult result = JobResultConnector.connectDictionariesByNames(output.getOutput(), results,
							"m");
					System.out.println("Connected all");

					String fileName = result.toARFF("output_" + config.getDatasetID());

					predictionModel.learnPredictionModel(fileName);
					System.out.println("Saved all");
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println("finished");
			} catch (ValidationException | ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			} catch (NotLoggedInException e) {
				e.printStackTrace();
			} catch (BoaException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException | LoginException e) {
			e.printStackTrace();
		}


	}
}
