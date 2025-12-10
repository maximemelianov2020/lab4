package lab4;

import lab4.csv.CsvParser;
import lab4.model.Person;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

/**
 * Основной класс приложения для демонстрации работы парсера.
 */
public class Main {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        try {
            CsvParser parser = new CsvParser();

            // Парсим CSV файл
            List<Person> persons = parser.parseCsvFile("foreign_names.csv");

            // Выводим информацию о всех сотрудниках
            System.out.println("Найдено сотрудников: " + persons.size());
            System.out.println("\nПервые 10 сотрудников:");
            System.out.println("=".repeat(80));

            persons.stream()
                    .limit(10)
                    .forEach(System.out::println);

            // Дополнительная статистика
            System.out.println("\n" + "=".repeat(80));
            printStatistics(persons);

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        } catch (CsvValidationException e) {
            System.err.println("Ошибка валидации CSV файла: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Выводит статистику по списку сотрудников.
     *
     * @param persons список сотрудников
     */
    private static void printStatistics(List<Person> persons) {
        long maleCount = persons.stream()
                .filter(p -> p.getGender() == Person.Gender.MALE)
                .count();
        long femaleCount = persons.size() - maleCount;

        double avgSalary = persons.stream()
                .mapToDouble(Person::getSalary)
                .average()
                .orElse(0.0);

        double maxSalary = persons.stream()
                .mapToDouble(Person::getSalary)
                .max()
                .orElse(0.0);

        double minSalary = persons.stream()
                .mapToDouble(Person::getSalary)
                .min()
                .orElse(0.0);

        System.out.println("Статистика:");
        System.out.printf("- Всего сотрудников: %d%n", persons.size());
        System.out.printf("- Мужчин: %d (%.1f%%)%n", maleCount,
                (double) maleCount / persons.size() * 100);
        System.out.printf("- Женщин: %d (%.1f%%)%n", femaleCount,
                (double) femaleCount / persons.size() * 100);
        System.out.printf("- Средняя зарплата: %.2f%n", avgSalary);
        System.out.printf("- Максимальная зарплата: %.2f%n", maxSalary);
        System.out.printf("- Минимальная зарплата: %.2f%n", minSalary);
        System.out.printf("- Количество уникальных подразделений: %d%n",
                persons.stream().map(p -> p.getDivision().getName()).distinct().count());
    }
}
