package main;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

	public static Map<Integer, Map<String, String>> conversions = new HashMap<>();
	static {
		init();
		
		BiFunction<String, String, Map<String,String>> createMap = (curr1, curr2) -> {
			Map<String, String> map = new HashMap<>();
			map.put(curr1, curr2);
			
			return map;
		};
		
		conversions.put(1, createMap.apply("BRL", "USD"));
		conversions.put(2, createMap.apply("USD", "BRL"));
		conversions.put(3, createMap.apply("BRL", "ARS"));
		conversions.put(4, createMap.apply("ARS", "BRL"));
		conversions.put(5, createMap.apply("BRL", "EUR"));
		conversions.put(6, createMap.apply("EUR", "BRL"));
	}
	
	public static void init() {
		Dotenv
			.configure()
			.systemProperties()
			.load();
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		int option;
		while (true) {
			System.out.print(getMenuString());
			option = Integer.parseInt(sc.nextLine());
			
			if (option == conversions.entrySet().size() + 1) {
				break;
			}
			
			if (option > conversions.entrySet().size() + 1 || option < 0) {
				System.out.println("Opção inválida. Tente novamente");
				continue;
			}
			
			System.out.print("Digite o valor que deseja converter: ");
			Double valor = Double.parseDouble(sc.nextLine());
			System.out.println();
			
			Double conversionRate = ExchangerateApi.getConversionRate(option);
			Map.Entry<String, String> conversion = conversions.get(option).entrySet().iterator().next();
			DecimalFormat df = new DecimalFormat("#.##");
			
			System.out.println(
				"VALOR " 
				+ df.format(valor) 
				+ " (" + conversion.getKey() + ")" 
				+ " CORRESPONDE AO VALOR FINAL DE ===> " + (conversion.getKey().equals(ExchangerateApi.BASE_CURRENCY) ? df.format(valor * conversionRate) : df.format(valor / conversionRate))
				+ " (" + conversion.getValue() + ")"
			);
			System.out.println();
		}
	}
	
	public static String getMenuString() {
		StringBuilder menuString = new StringBuilder();
		for (Map.Entry<Integer, Map<String, String>> conversion : conversions.entrySet()) {
			Map.Entry<String, String> conversionPair = conversion.getValue().entrySet().iterator().next();
			menuString
				.append(conversion.getKey())
				.append(") ")
				.append(conversionPair.getKey())
				.append(" --> ")
				.append(conversionPair.getValue());
			
			menuString.append("\n");
		}
		menuString.append(conversions.entrySet().size() + 1)
		.append(") SAIR");
		menuString.append("\n");
		
		menuString.append("\nEscolha uma opção válida: ");
		
		return menuString.toString();
	}
}
