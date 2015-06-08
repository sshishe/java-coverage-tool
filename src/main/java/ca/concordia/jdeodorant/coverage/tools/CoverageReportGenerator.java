package ca.concordia.jdeodorant.coverage.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiSourceFileLocator;
import org.jacoco.report.html.HTMLFormatter;

public class CoverageReportGenerator {

	private final File projectDirectory;
	private final String title;
	private final File executionDataFile;
	private final File classDirectory;
	private final List<File> sourceDirectories;
	private final File htmlreportDirectory;
	private final String csvReportFile;
	private static final String CSV_FILE_HEADER = "package_name,class_name,line,coverage_status";
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";

	private ExecFileLoader execFileLoader;

	public CoverageReportGenerator(final File projectDirectory,
			String[] sourceDirectories, String classDirectory,
			String reportOutputDirectory) {
		this.projectDirectory = projectDirectory;
		this.title = projectDirectory.getName();
		this.executionDataFile = new File(projectDirectory, "jacoco.exec");
		this.classDirectory = new File(projectDirectory, classDirectory);
		this.sourceDirectories = generateFilesForSourceDirectories(sourceDirectories);
		this.htmlreportDirectory = new File(reportOutputDirectory,
				"html.coverage");
		this.csvReportFile = reportOutputDirectory + "/coverage_report.csv";
	}

	private List<File> generateFilesForSourceDirectories(String[] sourceDirs) {
		List<File> sourceFiles = new ArrayList<File>();
		for (String sourceDir : sourceDirs) {
			sourceFiles.add(new File(projectDirectory, sourceDir));
		}
		return sourceFiles;
	}

	public void create() throws IOException {
		loadReportDataFromExecFile();
		IBundleCoverage bundleCoverage = analyzeStructure();
		createReport(bundleCoverage);
	}

	private void createReport(final IBundleCoverage bundleCoverage)
			throws IOException {
		final HTMLFormatter htmlFormatter = new HTMLFormatter();
		final IReportVisitor visitor = htmlFormatter
				.createVisitor(new FileMultiReportOutput(htmlreportDirectory));
		visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
				execFileLoader.getExecutionDataStore().getContents());
		MultiSourceFileLocator multiSourceFileLocator = new MultiSourceFileLocator(
				4);
		for (File file : sourceDirectories)
			multiSourceFileLocator.add(new DirectorySourceFileLocator(file,
					"utf-8", 4));
		visitor.visitBundle(bundleCoverage, multiSourceFileLocator);
		visitor.visitEnd();
	}

	private void loadReportDataFromExecFile() throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);
		analyzer.analyzeAll(classDirectory);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(csvReportFile);
			fileWriter.append(CSV_FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			for (final IClassCoverage classCoverage : coverageBuilder
					.getClasses()) {
				for (int index = classCoverage.getFirstLine(); index <= classCoverage
						.getLastLine(); index++) {
					String coverageStatus = getStatus(classCoverage.getLine(
							index).getStatus());
					if (coverageStatus == "")
						continue;
					fileWriter.append(classCoverage.getPackageName());
					fileWriter.append(COMMA_DELIMITER);
					String[] className = classCoverage.getName().split("/");
					fileWriter.append(className[className.length - 1]);
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(Integer.valueOf(index).toString());
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(coverageStatus);
					fileWriter.append(NEW_LINE_SEPARATOR);
				}
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out
						.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
		return coverageBuilder.getBundle(title);
	}

	private String getStatus(final int status) {
		switch (status) {
		case ICounter.NOT_COVERED:
			return "NOT_COVERED";
		case ICounter.PARTLY_COVERED:
			return "PARTLY_COVERED";
		case ICounter.FULLY_COVERED:
			return "FULLY_COVERED";
		}
		return "";
	}

	public static void generateReport(String file, String[] sourceDirectories,
			String classDirectory, String reportOutputDirectory) {
		File projectDirectory = new File(file);
		CoverageReportGenerator generator = new CoverageReportGenerator(
				projectDirectory, sourceDirectories, classDirectory,
				reportOutputDirectory);
		try {
			generator.create();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}