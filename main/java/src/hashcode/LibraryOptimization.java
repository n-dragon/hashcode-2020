package hashcode;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LibraryOptimization {

	static Random random = new Random();

	static Map<Integer, Integer> booksCount = new HashMap<>();

	static class Book {
		int id;
		int score;

		public Book(int id, int score) {
			this.id = id;
			this.score = score;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			Book book = (Book) o;

			if(id != book.id) return false;
			return score == book.score;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + score;
			return result;
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

	static class LibraryPotential {
		double potentialScore;
		List<Integer> bookIds;

		public LibraryPotential(double potentialScore, List<Integer> bookIds) {
			this.potentialScore = potentialScore;
			this.bookIds = bookIds;
		}
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
				int bookId = Integer.valueOf(books[m]);
				lib.ids.add(bookId);
				// adding count to hashMap
				booksCount.putIfAbsent(bookId, 0);
				int count = booksCount.get(bookId);
				booksCount.put(bookId, ++count);
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
//		files.add("b_read_on.txt");

//		files.add("c_incunabula.txt");
//		files.add("d_tough_choices.txt");
		files.add("e_so_many_books.txt");
//		files.add("f_libraries_of_the_world.txt");
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

	public static LibraryPotential computeLibraryPotential(Library lib, List<Book> bookScores, int remainingDays) {
		// TODO could do it once
		List<Integer> sortedBookscores = lib.ids.stream().sorted((b1, b2) -> bookScores.get(b2).score - bookScores.get(b1).score).collect(Collectors.toList());
		List<Integer> booksToRemove = new ArrayList<>();
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
				int bookRarity = booksCount.get(bookId);
//				score += (scoreBook );
				score += (scoreBook - 3*bookRarity);
				sortedBookscores.remove(0);
				booksToRemove.add(bookId);
			}
			daysToScan--;
		}
		// score divided by time to sign up
		// => so that big signupprocess are disadvantaged to less point but using less main thread.
		int libScore = score / lib.signupProcess;
		return new LibraryPotential(libScore, booksToRemove);
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
			LibraryPotential maxScore = new LibraryPotential(-1 * Double.MAX_VALUE, null);
			for (Library l : libs) {
				LibraryPotential libPotential = computeLibraryPotential(l, libraryInput.bookScore, remainingDays);
				l.potential = libPotential.potentialScore;
				if(l.potential > maxScore.potentialScore) {
					lib = l;
					maxScore = libPotential;
				}
			}
			libs.remove(lib);


			// we need to remove the same book from the other libraries
			maxScore.bookIds.forEach(bookId -> {
				int newCount = booksCount.get(bookId);
				booksCount.put(bookId, --newCount);
			});

			// removing all the books but all the books of the library may not passed ...
			LibraryPotential finalMaxScore = maxScore;
			libs.stream().forEach(l ->
					l.ids = l.ids.stream().filter(bookId -> !finalMaxScore.bookIds.contains(bookId)).collect(Collectors.toList())
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
