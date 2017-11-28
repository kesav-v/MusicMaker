import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;

public class OpenFile {

	public static Scanner openToRead(String path) {
		File f = new File(path);
		Scanner scan = null;
		try {
			scan = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Could not find file " + path);
			System.exit(1);
		}
		return scan;
	}

	public static PrintWriter openToWrite(String path) {
		File f = new File(path);
		PrintWriter out = null;
		try {
			out = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Could not find file " + path);
			System.exit(1);
		}
		return out;
	}
}