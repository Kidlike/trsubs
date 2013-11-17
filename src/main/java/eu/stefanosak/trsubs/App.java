package eu.stefanosak.trsubs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class App {

	public static void main(String[] args) throws IOException {
		/*
		 * vars
		 */
		String thisExec = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String execPath = thisExec.substring(0, thisExec.lastIndexOf("/"));
		String workingDir = Paths.get("").toAbsolutePath().toString();


		String inputFileStr = null;
		String outputFileStr = null;
		String inputLanguage = null;
		String outputLanguage = null;

		/*
		 * Parse Options
		 */
		CommandLineParser parser = new BasicParser();
		HelpFormatter hf = new HelpFormatter();

		Options options = new Options();
		options.addOption("h", "help", false, "prints help");
		options.addOption(OptionBuilder
			.withLongOpt("input-file")
			.withDescription("The input srt file")
			.hasArg()
			.withArgName("arg")
			.isRequired()
			.create("i"));
		options.addOption(OptionBuilder
			.withLongOpt("output-file")
			.withDescription("The output srt file")
			.hasArg()
			.withArgName("arg")
			.isRequired()
			.create("o"));
		options.addOption(OptionBuilder
			.withLongOpt("input-language")
			.withDescription("The input's language (can be omitted optionally)")
			.hasArg()
			.withArgName("arg")
			.create("s"));
		options.addOption(OptionBuilder
			.withLongOpt("output-language")
			.withDescription("the output's language")
			.hasArg()
			.withArgName("arg")
			.isRequired()
			.create("t"));

		try {
			CommandLine line = parser.parse(options, args);
			for (Option o : line.getOptions()) {
				switch (o.getOpt()) {
					case "i": {
						inputFileStr = o.getValue();
						break;
					}
					case "o": {
						outputFileStr = o.getValue();
						break;
					}
					case "s": {
						inputLanguage = o.getValue();
						break;
					}
					case "t": {
						outputLanguage = o.getValue();
						break;
					}
					case "h": {
						hf.printHelp(" ", options);
						System.exit(0);
						break;
					}
				}
			}
			if (line.getOptionValue("s") == null) {
				inputLanguage = "auto";
				System.out.println("WARN: Seting 'auto' as input language. Use the -s/--input-language option to specify.");
			}
		} catch (ParseException exp) {
			hf.printHelp(" ", options);
			System.out.println(exp.getMessage());
			System.exit(1);
		}

		System.out.println("input file: " + inputFileStr);
		System.out.println("output file: " + outputFileStr);
		System.out.println("input language: " + inputLanguage);
		System.out.println("output language: " + outputLanguage);

		/*
		 * fix permissions
		 */
		Runtime rt1 = Runtime.getRuntime();
		rt1.exec(new String[]{"chmod", "u+x", execPath + "/translate.bsh"});

		/*
		 * Translate
		 */
		ArrayList<String> outputFileLines = new ArrayList<>();
		List<String> srtFileLines = FileUtils.readLines(new File(inputFileStr), "UTF-8");
		float total = srtFileLines.size();
		float progress = 0;
		int progressIndicator = 0;

		System.out.print("Progress: 0%");
		for (String line : srtFileLines) {
			if (line.isEmpty()) {
				outputFileLines.add("");
			} else if (StringUtils.containsOnly(line, "1234567890") || line.contains("-->")) {
				outputFileLines.add(line);
			} else {
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec(new String[]{execPath + "/translate.bsh", line, inputLanguage, outputLanguage});
				List<String> execResults = IOUtils.readLines(pr.getInputStream());
				for (String str : execResults) {
					outputFileLines.add(str);
				}
				progressIndicator++;
			}
			BigDecimal bd = new BigDecimal(++progress / total * 100.0);
			String percentage = bd.round(new MathContext(5, RoundingMode.HALF_UP)).toPlainString();

			System.out.print("\r                                        "
				+ "\rProgress: ["
				+ ((progressIndicator % 4 == 0) ? "-"
				: (progressIndicator % 4 == 1) ? "\\"
				: (progressIndicator % 4 == 2) ? "|"
				: (progressIndicator % 4 == 3) ? "/" : "")
				+ "] " + percentage + "%");
		}
		System.out.println("");
		FileUtils.writeLines(new File(outputFileStr), "UTF-8", outputFileLines);
	}
}
