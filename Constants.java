import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Scanner;

public class Constants {

	public static final String PROTOCOL_VERSION = "STFMP/1.0";

	public static String encrypteString(String raw) {
		int key = 2;
		String encryptedString = null;
		if (key > 0) {
			String b64encoded = Base64.getEncoder().encodeToString(raw.getBytes());
			StringBuilder tmp = new StringBuilder();
			int OFFSET = key;
			for (int i = 0; i < b64encoded.length(); i++) {
				tmp.append((char) (b64encoded.charAt(i) + OFFSET));
			}
			encryptedString = tmp.toString();
		}
		return encryptedString + "\r\n";
	}

	public static String decrypteString(String encryptedString) {

		int key = 2;
		String raw = null;
		if (key > 0) {
			StringBuilder tmp = new StringBuilder();
			int OFFSET = key;
			for (int i = 0; i < encryptedString.length(); i++) {
				tmp.append((char) (encryptedString.charAt(i) - OFFSET));
			}
			String encrypted = tmp.toString();
			raw = new String(Base64.getDecoder().decode(encrypted));

		}
		return raw;
	}

	public static String generateKey() {
		int numKey = (int) (9 * Math.random()) + 1;
		return String.valueOf(numKey);
	}

	public static void storeKey(String key) {
		try {
			File file = new File("Key");
			PrintWriter writer = new PrintWriter(file);
			writer.print(key);
			writer.close();
		} catch (IOException e) {
			System.out.println("Fail to generate key: " + e.getMessage());

		}
	}

	public static String loadKey() {
		String key = "";
		try {
			File myObj = new File("Key");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				key += myReader.nextLine();
			}

		} catch (IOException e) {
			System.out.println("Key Not Found");
		}
		return key;
	}

}
