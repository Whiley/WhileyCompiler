package wyopcl.testing.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.AssertionFailedError;
import static org.junit.Assert.*;

public final class BaseTestUtil {
	private final String version = "v0.3.31";
	// user.dir is the current directory.
	private final String workspace_path = System.getProperty("user.dir")+ File.separator;
	private final String lib_path = workspace_path + "lib"+ File.separator;
	
	private final String classpath = lib_path + "wyjc-"	+ version + ".jar" + File.pathSeparator 
			+ lib_path + "wyopcl-" + version + ".jar" + File.pathSeparator 
			+ lib_path + "wyrl-" + version + ".jar" + File.pathSeparator
			+ lib_path + "wycs-" + version + ".jar"	+ File.pathSeparator
			+ lib_path + "wybs-"+ version + ".jar" + File.pathSeparator 
			+ lib_path + "wyil-" + version + ".jar" + File.pathSeparator
			+ lib_path + "wyc-" + version + ".jar" + File.pathSeparator;
	final String runtime = lib_path + "wyrt-" + version + ".jar";
	
	final String valid_path = workspace_path + "tests" + File.separator + "valid" + File.separator;
	final String invalid_path = workspace_path + "tests" + File.separator + "invalid" + File.separator;

	private ProcessBuilder pb;
	private Process p;

	public BaseTestUtil() {

	}

	public void exec(String file_name) {
		if (file_name.matches(".*_Invalid_*.")) {
			exec_invalid(file_name);
		} else {
			exec_valid(file_name);
		}

	}

	private void exec_valid(String file_name) {
		try {

			// Run the whiley program with interpreter.
			String path_whiley = file_name + ".whiley";
			// Set the working directory.
			pb = new ProcessBuilder("java", "-cp", classpath, "wyopcl.WyopclMain", "-bp", runtime, path_whiley);
			pb.directory(new File(valid_path));

			// System.out.println("" + pb.directory());
			p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(),
					Charset.forName("UTF-8")));

			// Load the output file (*.sysout).
			String path_sysout = valid_path + file_name + ".sysout";

			Iterator<String> iterator = Files.readAllLines(Paths.get(path_sysout), Charset.defaultCharset()).iterator();
			String output = null;
			while ((output = reader.readLine()) != null) {
				String expected = iterator.next();
				//System.out.println(output);
				assertEquals(expected, output);
			}

			// Ensure no records is left in the list.
			if (iterator.hasNext()) {
				throw new Exception("Test file: " + file_name);
			}

		} catch (Exception e) {
			terminate();
			throw new RuntimeException("Test file: " + file_name, e);
		}
	}

	private void exec_invalid(String file_name) {
		try {

			// Run the whiley program with interpreter.
			String path_whiley = file_name + ".whiley";
			// Set the working directory.
			pb = new ProcessBuilder("java", "-cp", classpath, "wyopcl.WyopclMain", "-bp", runtime, path_whiley, "-verify");
			pb.directory(new File(invalid_path));

			// System.out.println("" + pb.directory());
			p = pb.start();

			// Get the error stream because the result should be an error.
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream(),
					Charset.forName("UTF-8")));

			// Load the output file (*.sysout).
			String path_sysout = invalid_path + file_name + ".sysout";

			Iterator<String> iterator = Files.readAllLines(Paths.get(path_sysout), Charset.defaultCharset()).iterator();
			String output = null;
			while ((output = reader.readLine()) != null) {
				String expected = iterator.next();
				//System.out.println(output);
				// Check if the output matches the regular expression for file path, e.g.
				// .\List_Invalid_1.whiley:5: unknown variable
				if (output.matches(".*" + path_whiley + ":\\d+:.*.")) {
					String[] out_array = output.split(":");
					String[] expected_array = expected.split(":");
					assertEquals(expected_array[1], out_array[1]);
					//Split the second substring to get the cause of error, e.g.
					// internal failure, precondition not satisfied
					if(out_array[2].contains("internal failure,")){
						String res = out_array[2].split("internal failure,")[1]; 
						assertEquals(expected_array[2], res);
					}else{
						assertEquals(expected_array[2], out_array[2]);
					}
					
				} else {
					assertEquals(expected, output);
				}

			}

			// Ensure no records is left in the list.
			if (iterator.hasNext()) {
				throw new Exception("Test file: " + file_name);
			}

		} catch (Exception e) {
			terminate();
			throw new RuntimeException("Test file: " + file_name, e);
		}
	}

	public void terminate() {
		while (p != null) {
			p.destroy();
			p = null;
		}

		pb = null;
	}

}
