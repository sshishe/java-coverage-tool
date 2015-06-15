package ca.concordia.jdeodorant.coverage.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.*;
import org.junit.runner.RunWith;
import org.junit.runner.JUnitCore;

import ca.concordia.jdeodorant.coverage.cli.CLIParser;
import ca.concordia.jdeodorant.coverage.test.AntXmlRunListener;
import ca.concordia.jdeodorant.coverage.test.CSVRunListener;
import static org.junit.extensions.cpsuite.SuiteType.*;

@RunWith(ClasspathSuite.class)
@SuiteTypes({ JUNIT38_TEST_CLASSES, TEST_CLASSES, RUN_WITH_CLASSES })
// @IncludeJars(true)
public class TestAndCoverageEngine {
	public static void run(String[] args) {
		CLIParser cliParser = new CLIParser(args);
		JUnitCore junit = new JUnitCore();
		switch (cliParser.getReportFormat()) {
		case XML:
			AntXmlRunListener runListener = new AntXmlRunListener();

			try {
				runListener.setOutputStream(new FileOutputStream(new File(
						cliParser.getReportDir() + "/" + "test_report.xml")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			junit.addListener(runListener);
			break;
		case CSV:
			junit.addListener(new CSVRunListener(cliParser.getReportDir()));
			break;
		default:
			break;
		}

		junit.run(TestAndCoverageEngine.class);
	}

	public static void generateReport(String[] args) {
		CLIParser cliParser = new CLIParser(args);
		CoverageReportGenerator.generateReport(cliParser.getProjectDir(),
				cliParser.getSourceDirs(), cliParser.getClassDir(),
				cliParser.getReportDir());
	}
}