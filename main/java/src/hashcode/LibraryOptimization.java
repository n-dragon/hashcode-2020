package hashcode;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LibraryOptimization {

	static Random random = new Random();

	static class Book {
		int id;
		int score;

		public Book(int id, int score) {
			this.id = id;
			this.score = score;
		}
	}

	static class Library {
		int id;
		List<Integer> ids = new ArrayList<>();
		int numberOfBooks;
		int signupProcess;
		int bookPerDay;
		double potential;
	}

	static class LibraryInput {
		int numberOfBooks;
		int numberOfLibraries;
		int numberOfDayToScan;
		String fileName;
		List<Book> bookScore = new ArrayList<>();
		List<Library> libraries = new ArrayList<>();


	}
	// n type of pizza each one has its slices number
	// find the combination(list of pizza type) so that you have the maximum number or below

	public static LibraryInput readFile(String fileName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader((new InputStreamReader(is)));

		LibraryInput libraryInput = new LibraryInput();

		String[] firstLine = br.readLine().split(" ");
		int bookNumbers = Integer.valueOf(firstLine[0]);
		libraryInput.numberOfBooks = bookNumbers;
		int librariesNumber = Integer.valueOf(firstLine[1]);
		libraryInput.numberOfLibraries = librariesNumber;
		int dayToScan = Integer.valueOf(firstLine[2]);
		libraryInput.numberOfDayToScan = dayToScan;
		libraryInput.fileName = fileName;

		String[] secondLine = br.readLine().split(" ");
		for (int j = 0; j < secondLine.length; j++) {
			// check ordre
			libraryInput.bookScore.add(new Book(j, Integer.valueOf(secondLine[j])));
		}
		String a;
		int index = 0;
		while ((a = br.readLine()) != null) {
			Library lib = new Library();
			lib.id = index;
			index++;
			String[] firstLibLine = a.split(" ");
			lib.numberOfBooks = Integer.valueOf(firstLibLine[0]);
			lib.signupProcess = Integer.valueOf(firstLibLine[1]);
			lib.bookPerDay = Integer.valueOf(firstLibLine[2]);

			a = br.readLine();
			String[] books = a.split(" ");
			for (int m = 0; m < books.length; m++) {
				lib.ids.add(Integer.valueOf(books[m]));
			}
			// sort at the end or as long we go ?
			List<Integer> sortedBookscores = lib.ids.stream().sorted((b1, b2) -> libraryInput.bookScore.get(b2).score - libraryInput.bookScore.get(b1).score).collect(Collectors.toList());
			lib.ids = sortedBookscores;
			libraryInput.libraries.add(lib);
		}

		return libraryInput;

	}

	public static List<LibraryInput> readFiles() throws IOException {
		List<String> files = new ArrayList<>();

//		files.add("a_example.txt");
		files.add("b_read_on.txt");

		files.add("c_incunabula.txt");
		files.add("d_tough_choices.txt");
		files.add("e_so_many_books.txt");
		files.add("f_libraries_of_the_world.txt");
		List<LibraryInput> libs = new ArrayList<>();
		for (String file : files) {
			libs.add(readFile(file));
		}
		return libs;

	}

	public static void writeToFile(String fileName, String value, boolean firstLine) throws IOException {
		String str = "World";
		BufferedWriter writer = new BufferedWriter(new FileWriter("result" + fileName, true));
		if(!firstLine) {
			writer.append("\n");
		}
		writer.append(value);

		writer.close();
	}

	public static void main(String[] args) throws Exception {
		List<LibraryInput> libraryInputs = readFiles();
		libraryInputs.stream().forEach(lib -> {
			try {
				computeResult(lib);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static double computeLibraryPotential(Library lib, List<Book> bookScores, int remainingDays) {
		// TODO could do it once
		List<Integer> sortedBookscores = lib.ids.stream().sorted((b1, b2) -> bookScores.get(b2).score - bookScores.get(b1).score).collect(Collectors.toList());

		int daysToScan = remainingDays - lib.signupProcess;
		int score = 0;
		mainLoop:
		while (daysToScan > 0) {
			for (int i = 0; i < lib.bookPerDay; i++) {
				// si plus assez de bouquin
				if(0 == sortedBookscores.size()) {
					break mainLoop;
				}
				int bookId = sortedBookscores.get(0);
				int scoreBook = bookScores.get(bookId).score;
				score += scoreBook;
				sortedBookscores.remove(0);
			}
			daysToScan--;
		}
		// score divided by time to sign up
		// => so that big signupprocess are disadvantaged to less point but using less main thread.
		return score / lib.signupProcess;

//		return score;
	}

	public LibraryOptimization() {
		super();
	}

	public static void printResult(List<Library> libs, String fileName) throws IOException {
		writeToFile(fileName, String.valueOf(libs.size()), true);


		for (int i = 0; i < libs.size(); i++) {
//			System.out.println(libs.get(i).potential);
			writeToFile(fileName, libs.get(i).id + " " + libs.get(i).ids.size(), false);

			writeToFile(fileName, libs.get(i).ids.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(" ")), false);
		}
	}

	public static void computeResult(LibraryInput libraryInput) throws IOException {
		List<Library> libs = libraryInput.libraries;
		List<Library> result = new ArrayList<>();
		Library lib = null;

		int remainingDays = libraryInput.numberOfDayToScan;
		int signupDay = 0;
		while (libs.size() != 0) {
			double maxScore = -1 * Double.MAX_VALUE;
//			System.out.println(libs.size());
			for (Library l : libs) {
				double libPotential = computeLibraryPotential(l, libraryInput.bookScore, remainingDays);
				l.potential = libPotential;
				if(l.potential > maxScore) {
					lib = l;
					maxScore = l.potential;
				}
			}
			libs.remove(lib);


			// we need to remove the same book from the other libraries
			Library finalLib = lib;
			libs.stream().forEach(l ->
					l.ids = l.ids.stream().filter(bookId -> !finalLib.ids.contains(bookId)).collect(Collectors.toList())
			);
			result.add(lib);
			remainingDays = remainingDays - lib.signupProcess;
			if(remainingDays <= 0) {
				break;

			}
			signupDay += lib.signupProcess;
			System.out.println(signupDay);
		}
		// TODO write as we go to ease memory
		printResult(result, libraryInput.fileName);
	}
}
