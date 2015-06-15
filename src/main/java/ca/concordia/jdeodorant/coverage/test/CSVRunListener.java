package ca.concordia.jdeodorant.coverage.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVRunListener extends RunListener {
	Logger logger = LoggerFactory.getLogger(CSVRunListener.class);
	private static final String CSV_FILE_HEADER = "class_name,method_name,result";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String GLOBAL_CSV_FILE_NAME = "test_report.csv";
	private static final String AFTER_TEST_CSV_FILE_NAME = "test_report_offset.csv";
	private Map<Description, String> testFinishedList;
	private Map<Description, String> testFailuresList;
	private Map<Description, String> testIgnoredList;

	private String reportDirectory;
	private FileWriter fileWriter;

	public CSVRunListener(String reportDirectory) {
		this.reportDirectory = reportDirectory;
		testFinishedList = new LinkedHashMap<Description, String>();
		testFailuresList = new LinkedHashMap<Description, String>();
		testIgnoredList = new LinkedHashMap<Description, String>();
	}

	@Override
	public void testStarted(Description description) throws Exception {
		logger.info("Test Started: " + description.getClassName()
				+ " at method: " + description.getMethodName());
	}

	@Override
	public void testFinished(final Description description) throws Exception {
		logger.info("Test Finished: " + description.getClassName()
				+ " at method: " + description.getMethodName());
		testFinishedList.put(description, "finished");
	}

	@Override
	public void testFailure(Failure failure) throws IOException {
		logger.info("Test Failed: " + failure.getDescription().getClassName()
				+ " at method: " + failure.getDescription().getMethodName());
		testFailuresList.put(failure.getDescription(), "failed");
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		logger.info("Test Ignored: " + description.getClassName()
				+ " at method: " + description.getMethodName());
		testIgnoredList.put(description, "ignored");
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		logger.info("Test Run Started");

		String csvFileName = reportDirectory + "/" + GLOBAL_CSV_FILE_NAME;
		File f = new File(csvFileName);

		if (f.exists() && !f.isDirectory())
			csvFileName = reportDirectory + "/" + AFTER_TEST_CSV_FILE_NAME;

		logger.info(csvFileName);
		fileWriter = new FileWriter(csvFileName);
		fileWriter.append(CSV_FILE_HEADER.toString());
		fileWriter.append(NEW_LINE_SEPARATOR);
	}

	@Override
	public void testRunFinished(final Result result) throws Exception {
		logger.info("Test Run Finished");
		testFinishedList.keySet().removeAll(testFailuresList.keySet());
		testFinishedList.keySet().removeAll(testIgnoredList.keySet());
		for (Entry<Description, String> item : testFinishedList.entrySet())
			writeToFile(item.getKey(), item.getValue());
		for (Entry<Description, String> item : testFailuresList.entrySet())
			writeToFile(item.getKey(), item.getValue());
		for (Entry<Description, String> item : testIgnoredList.entrySet())
			writeToFile(item.getKey(), item.getValue());
		try {
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			logger.debug("Error while flushing/closing fileWriter !!!");
			e.printStackTrace();
		}
	}

	private void writeToFile(Description description, String status)
			throws IOException {
		fileWriter.append(description.getClassName());
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(description.getMethodName());
		fileWriter.append(COMMA_DELIMITER);
		fileWriter.append(status);
		fileWriter.append(NEW_LINE_SEPARATOR);
		fileWriter.flush();
	}

}
