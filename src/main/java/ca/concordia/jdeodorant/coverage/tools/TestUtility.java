package ca.concordia.jdeodorant.coverage.tools;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class TestUtility {

	private static File CLASSES_DIR;
	private static String path;

	private TestUtility() {
	}

	public static Class<?>[] findClasses(String pathToBin) {
		TestUtility.path = pathToBin;
		CLASSES_DIR = findClassesDir();
		List<File> classFiles = new ArrayList<File>();
		findClasses(classFiles, CLASSES_DIR);
		List<Class<?>> classes = convertToClasses(classFiles, CLASSES_DIR);
		return classes.toArray(new Class[classes.size()]);
	}

	private static List<Class<?>> convertToClasses(final List<File> classFiles,
			final File classesDir) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : classFiles) {
			if (!file.getName().endsWith("Test.class")) {
				continue;
			}
			String name = file.getPath()
					.substring(classesDir.getPath().length() + 1)
					.replace('/', '.').replace('\\', '.');
			name = name.substring(0, name.length() - 6);
			Class<?> c;
			try {
				c = Class.forName(name);
				if (!Modifier.isAbstract(c.getModifiers())) {
					classes.add(c);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}

		// sort so we have the same order as Ant
		Collections.sort(classes, new Comparator<Class<?>>() {
			public int compare(final Class<?> c1, final Class<?> c2) {
				return c1.getName().compareTo(c2.getName());
			}
		});

		return classes;
	}

	private static void findClasses(final List<File> classFiles, final File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				findClasses(classFiles, file);
			} else if (file.getName().toLowerCase().endsWith(".class")) {
				classFiles.add(file);
			}
		}
	}

	private static File findClassesDir() {
		try {
			// String path =
			// "/Users/Shahriar/Documents/Workspace/jdeodorant-workspace/TestData/jruby/jruby-1.4.0/bin";
			// // AllTests.class.getProtectionDomain().getCodeSource()
			// .getLocation().getFile();
			// System.out.println("Path is: " + path);
			return new File(URLDecoder.decode(TestUtility.path, "UTF-8"));
		} catch (UnsupportedEncodingException impossible) {
			// using default encoding, has to exist
			throw new AssertionError(impossible);
		}
	}
}
