package hashcode;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class PizzaOrder {

	static Random random = new Random();

	static class PizzaInput {
		String fileName;
		int differentPizzaType;
		int maximumSlice;
		List<String> pizzaPerType;


	}
	// n type of pizza each one has its slices number
	// find the combination(list of pizza type) so that you have the maximum number or below

	public static PizzaInput readFile(String fileName) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader((new InputStreamReader(is)));

		PizzaInput pizzaInput = new PizzaInput();

		String[] firstLine = br.readLine().split(" ");
		pizzaInput.maximumSlice = Integer.valueOf(firstLine[0]);
		pizzaInput.differentPizzaType = Integer.valueOf(firstLine[1]);

		String secondLine = br.readLine();
		String[] b = secondLine.split(" ");
		pizzaInput.pizzaPerType = Arrays.asList(b);
		pizzaInput.fileName = fileName;
		return pizzaInput;

	}

	public static List<PizzaInput> readFiles() throws IOException {
		List<String> files = new ArrayList<>();
//		files.add("a_example.in");
		files.add("b_small.in");

		files.add("c_medium.in");
		files.add("d_quite_big.in");
		files.add("e_also_big.in");
		List<PizzaInput> pizzas = new ArrayList<>();
		for (String file : files) {
			pizzas.add(readFile(file));
		}
		return pizzas;

	}

	public static void writeToFile(String fileName, String value) throws IOException {
		String str = "World";
		BufferedWriter writer = new BufferedWriter(new FileWriter("result" + fileName, true));
		writer.append("\n");
		writer.append(value);

		writer.close();
	}

	public static void main(String[] args) throws Exception {
		List<PizzaInput> pizzaInputs = readFiles();

		pizzaInputs.stream().forEach(pizza -> {
			try {
				computeResult(pizza);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static BitSet generateRandomBitSet(int length) {
		BitSet a = new BitSet(length);
		for (int i = 0; i < length; i++) {
			a.set(i, random.nextBoolean());
		}
		return a;
	}

	public static void computeResult(PizzaInput pizza) throws IOException {
		writeToFile(pizza.fileName, pizza.fileName);
		System.out.println(pizza.fileName);
		// we have n pizzas so we need n bits
		// we may generate 7 bits not needed, to improve
		BitSet comb = generateRandomBitSet(pizza.differentPizzaType);
		BigInteger maxResult = null;
		Map<Integer, BigInteger> remainingPizzas = new HashMap<>();
		BigInteger maxSlices = BigInteger.valueOf(pizza.maximumSlice);
		for (int j = 0; j < pizza.differentPizzaType; j++) {
			remainingPizzas.put(j, BigInteger.valueOf(0));
			for (int k = j + 1; k < pizza.differentPizzaType; k++) {
				BigInteger bi = remainingPizzas.get(j);
				bi = bi.add(BigInteger.valueOf(Integer.valueOf(pizza.pizzaPerType.get(k))));
				remainingPizzas.put(j, bi);
			}
		}
		// we need a map with rest of values
		mainLoop:
		while (true) {
			BigInteger result = BigInteger.valueOf(0L);
			for (int i = 0; i < pizza.differentPizzaType; i++) {
				// if this bit present in this number
				if(comb.get(i)) {
					result = result.add(BigInteger.valueOf(Long.valueOf(pizza.pizzaPerType.get(i))));
					System.out.println("res:" + result);
					System.out.println("i:" + i);
				}
//				if(remainingPizzas.get(i).add(result).compareTo(maxSlices) == -1) {
//					break;
//				}
				// if sum of rest pizza type
				if(result.compareTo(BigInteger.valueOf(pizza.maximumSlice)) == 1) {
					break;
				}
			}
			// we could stop when reaching max
			if((maxResult == null || maxResult.compareTo(result) == -1) && (result.compareTo(BigInteger.valueOf(pizza.maximumSlice)) == -1 || result.compareTo(BigInteger.valueOf(pizza.maximumSlice)) == 0)) {
				maxResult = result;
				BitSet res = comb;

				writeToFile(pizza.fileName, "solution:" + res);
				writeToFile(pizza.fileName, "maxResult:" + maxResult.toString());

			}
			if(result.compareTo(BigInteger.valueOf(pizza.maximumSlice)) == 0) {
				break mainLoop;
			}
			comb = generateRandomBitSet(pizza.differentPizzaType);

		}

	}
}
