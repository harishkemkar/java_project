import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // 1. Show System Date and Time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("\n[Current Time: " + now.format(formatter) + "]");

            // 2. Menu Options
            System.out.println("--- Multi-Tool App ---");
            System.out.println("1. Celsius to Fahrenheit");
            System.out.println("2. Fahrenheit to Celsius");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();

            if (choice == 3) {
                System.out.println("Exiting... Goodbye!");
                running = false;
                break;
            }

            System.out.print("Enter temperature value: ");
            double input = scanner.nextDouble();

            if (choice == 1) {
                double result = (input * 9/5) + 32;
                System.out.printf(">> Result: %.2f°C is %.2f°F%n", input, result);
            } else if (choice == 2) {
                double result = (input - 32) * 5/9;
                System.out.printf(">> Result: %.2f°F is %.2f°C%n", input, result);
            } else {
                System.out.println("!! Invalid option. Try again.");
            }
        }
        scanner.close();
    }
}