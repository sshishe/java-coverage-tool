package ca.concordia.jdeodorant.coverage.cli;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CLIParser {
	private CoverageCommandLineParams params;

	public enum ReportFormat {
		CSV, XML;
	};

	public class CoverageCommandLineParams {
		@Parameter
		private List<String> parameters = new ArrayList<String>();

		@Parameter(names = { "-p" }, description = "Project root directory")
		private String projectDir;

		@Parameter(names = "-c", description = "Directory that all .class files are located")
		private String classDirectory;

		@Parameter(names = "-r", description = "Report directory")
		private String reportDirectory;

		@Parameter(names = "-s", description = "Source directories, directories that all .java files are located")
		private String sourceDirectories;

		@Parameter(names = "-format", description = "Report format for unit test result which can be either XML or CSV")
		private ReportFormat reportFormat = ReportFormat.XML;
	}

	public CLIParser(String[] args) {
		params = new CoverageCommandLineParams();
		new JCommander(params, args);
	}

	public String getProjectDir() {
		return params.projectDir;
	}

	public String getClassDir() {
		return params.classDirectory;
	}

	public String[] getSourceDirs() {
		String values = params.sourceDirectories;
		if (values == null)
			return null;
		List<String> sourceDirs = new ArrayList<String>();
		for (String value : values.split(",")) {
			sourceDirs.add(value);
		}
		return sourceDirs.toArray(new String[] {});
	}

	public String getReportDir() {
		return params.reportDirectory;
	}

	public ReportFormat getReportFormat() {
		return params.reportFormat;
	}
}
