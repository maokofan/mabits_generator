package com.mybatisgen.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class Mybatis_Generator {
	private static String startPath;
	private static File configFile;

	public static void start() throws Exception {
		init();
		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(configFile);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
	}

	private static final String JAVACONECTORLOACATION = "location=";
	private static final String TARGETPACKAGE = "targetPackage=";

	public static void init() throws FileNotFoundException, IOException {
		startPath = System.getProperty("user.dir");
		configFile = new File(startPath, "/conf/mybatis-generator.xml");
		String conectorLoc = new File(startPath, "/lib/mysql-connector-java-5.1.46.jar").getAbsolutePath();
		List<String> lines = IOUtils.readLines(new FileInputStream(configFile), Charset.forName("utf-8"));
		boolean overriteFlush = false;
		if (lines != null && !lines.isEmpty()) {
			StringBuilder mybatisConfsb = new StringBuilder();
			for (String content : lines) {
				String trimLine = content.trim();
				if (trimLine.startsWith(JAVACONECTORLOACATION) && !content.contains(conectorLoc)) {
					overriteFlush = true;
					mybatisConfsb.append("location=\"" + conectorLoc + "\" />");
				} else if (trimLine.contains(TARGETPACKAGE)) {
					trimLine = trimLine.replace(TARGETPACKAGE, "");
					int indexStart = 1;
					int indexEnd = trimLine.indexOf(">");
					if (indexEnd == -1)
						indexEnd = trimLine.length() - 1;
					String packageStr = trimLine.substring(indexStart, indexEnd);
					String packagePath = "";
					File packageFile = null;
					if (packageStr.contains(".")) {
						packagePath = packageStr.replace(".", File.separator);
						packageFile = new File(System.getProperty("user.dir") + "/src/main/java", packagePath);
					} else {
						packageFile = new File(System.getProperty("user.dir") + "/src/main/resources", packagePath);
					}
					if (!packageFile.exists())
						packageFile.mkdirs();
				} else {
					mybatisConfsb.append(content);
				}
				mybatisConfsb.append(System.lineSeparator());

			}

			if (overriteFlush) {
				IOUtils.write(mybatisConfsb, new FileOutputStream(configFile), Charset.forName("utf-8"));
			}
		}

	}
}
