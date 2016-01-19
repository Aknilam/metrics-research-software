package pl.einformatyka.mmse;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.einformatyka.mmse.model.Metric;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Configuration {

	private static final String RESULT_FILE_PATH = "result_file_path";
	private static final String OUTPUT_PARAMETER = "output_parameter";
	private static final String DATASET = "dataset";
	private static final String METRIC = "metric";
	private static final String METRICS = "metrics";
	private static final String DATA_DIRECTORY = "/metrics";
	private static final String BOA_FILE_EXTENSION_NAME = "boa";

	private static final String VARIABLE_START_CHARACTERS = "${";
	private static final String VARIABLE_END_CHARACTERS = "}";

	private String outputParameter;
	private String path;
	private int datasetID;
	private List<Metric> metrics;
	private Metric output;

	public static Configuration readConfiguration(File configFile)
			throws ParserConfigurationException, SAXException, IOException, ValidationException {
		if (configFile == null)
			configFile = new File("res/input.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(configFile);

		NodeList metricsList = doc.getElementsByTagName(METRICS);
		NodeList metricList = doc.getElementsByTagName(METRIC);
		NodeList datasetList = doc.getElementsByTagName(DATASET);
		NodeList outputList = doc.getElementsByTagName(OUTPUT_PARAMETER);
		NodeList resultFile = doc.getElementsByTagName(RESULT_FILE_PATH);

		if (metricsList.getLength() != 1) {
			throw new ValidationException("Wrong amount of metrics nodes.");
		}

		if (datasetList.getLength() != 1) {
			throw new ValidationException("Wrong amount of dataset nodes.");
		}

		if (outputList.getLength() != 1) {
			throw new ValidationException("Wrong amount of output nodes.");
		}

		List<String> metricNames = new ArrayList<String>();
		for (int i = 0; i < metricList.getLength(); i++) {
			metricNames.add(metricList.item(i).getTextContent());
		}
		int dataset = Integer.parseInt(datasetList.item(0).getTextContent());
		String output = outputList.item(0).getTextContent();
		String resultFilePath = resultFile.item(0).getTextContent();
		Configuration config = new Configuration(metricNames, output, dataset,resultFilePath);
		return config;
	}

	public int getDatasetID() {
		return datasetID;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public String getOutputParameter() {
		return outputParameter;
	}

	public Metric getOutput() {
		return output;
	}

	public String getResultFilePath() { return path; }

	private Configuration(List<String> metrics, String outputParameter, int dataset,String resultFilePath) {
		this.outputParameter = outputParameter;
		this.datasetID = dataset;
		this.metrics = getMetrics(metrics);
		this.path = resultFilePath;
		ArrayList<String> a = new ArrayList<String>();
		a.add(outputParameter);
		this.output = getMetrics(a).get(0);
	}

	private ArrayList<Metric> getMetrics(List<String> metrics) {
		ArrayList<Metric> result = new ArrayList<Metric>();
		List<Metric> systemMetrics = getMetricsFromDataResourceDirectory(metrics);

		for (Metric metric : systemMetrics) {
			for (String metricName : metrics) {
				if (metric.getName().equals(metricName)) {
					result.add(metric);
				}
			}
		}
		return result;
	}

	public List<Metric> getMetricsFromDataResourceDirectory(List<String> metrics) {
		return metrics.stream().map(new Function<String, Metric>() {
			@Override
			public Metric apply(String metricName) {
				try {
					String boaLanguage = applyPropertyValues(getFileFromDataDirectory(metricName));
					return new Metric(metricName, boaLanguage);
				} catch (NoSuchElementException | IOException ex) {
					System.out.println("Cannot find " + metricName + ".boa file in " + System.getProperty("user.dir")
							+ DATA_DIRECTORY);
					System.exit(-1);
					return null;
				}
			}
		}).collect(Collectors.toList());
	}

	private String getFileFromDataDirectory(String metricName) throws NoSuchElementException, IOException {
		File dataDirectory = new File(System.getProperty("user.dir") + DATA_DIRECTORY);
		if (dataDirectory.exists()) {
			File boaFile = Arrays.asList(dataDirectory.listFiles()).stream()
					.filter(lambda -> Files.getFileExtension(lambda.getName()).equals(BOA_FILE_EXTENSION_NAME))
					.filter(lambda -> Files.getNameWithoutExtension(lambda.getName()).equals(metricName)).findAny()
					.get();
			return Files.toString(boaFile, Charsets.UTF_8);
		} else {
			System.out.println("There is no `" + DATA_DIRECTORY + "` directory!");
			System.exit(0);
			return null;
		}
	}

	public String applyPropertyValues(String boaLanguage) {
		for (Map.Entry<Object, Object> iterator : getPropertiesMap()) {
			boaLanguage = StringUtils.replace(boaLanguage, createVariable((String) iterator.getKey()),
					(String) iterator.getValue());
		}
		return boaLanguage;
	}

	private Set<Map.Entry<Object, Object>> getPropertiesMap() {
		Properties properties = new Properties();
		try (InputStream stream = new FileInputStream("res/config_attributes.properties")) {
			properties.load(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.entrySet();
	}

	private String createVariable(String variableName) {
		StringBuilder builder = new StringBuilder();
		builder.append(VARIABLE_START_CHARACTERS).append(variableName).append(VARIABLE_END_CHARACTERS);

		return builder.toString();
	}
}
