package de.woody64k.libs.vhl.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class VhlFileUtils {
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static URL findFileOnClasspath(String name) {
		URL url = ClassLoader.getSystemResource(name);
		if (url != null) {
			return url;
		} else {
			ClassLoader classLoader = VhlFileUtils.class.getClassLoader();
			return classLoader.getResource(name);
		}

	}

	public static VhlFileHolder readContentsOnClasspath(String name) {
		return readFileFromUrl(name, findFileOnClasspath(name));
	}

	public static String readContentsFromUrl(URL url) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static VhlFileHolder readFileOnClasspath(String name) {
		return readFileFromUrl(name, findFileOnClasspath(name));
	}

	public static VhlFileHolder readFileFromUrl(String name, URL url) {
		try (InputStream is = url.openStream()) {
			return new VhlFileHolder(name, IOUtils.toByteArray(is));
		} catch (IOException e) {
			throw new RuntimeException(String.format("File could not read from path:", url.getPath()));
		}
	}

	public static String buildPath(String... parts) {
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			if (i != 0) {
				path.append(VhlFileUtils.FILE_SEPARATOR);
			}
			path.append(parts[i]);
		}
		return path.toString();
	}

	public static void makeFileExists(File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException except) {
			throw new RuntimeException(except);
		}
	}

	public static void assertFileIsDirectory(final File tmpFolder) {
		if (!tmpFolder.exists()) {
			tmpFolder.mkdirs();
		} else {
			if (!tmpFolder.isDirectory()) {
				throw new RuntimeException(String.format("%s ist kein Ordner.", tmpFolder.getAbsolutePath()));
			}
		}
	}

}
