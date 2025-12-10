package lab4.csv;

import lab4.model.Division;
import lab4.model.Person;
import lab4.util.DateUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Класс для парсинга CSV файлов с данными о сотрудниках.
 */
public class CsvParser {
    private static final char DEFAULT_SEPARATOR = ';';
    private static final Map<String, Division> DIVISION_CACHE = new HashMap<>();

    /**
     * Читает CSV файл и преобразует его в список сотрудников.
     *
     * @param csvFilePath путь к CSV файлу
     * @return список сотрудников
     * @throws IOException если произошла ошибка чтения файла
     * @throws CsvValidationException если CSV файл невалиден
     */
    public List<Person> parseCsvFile(String csvFilePath) throws IOException, CsvValidationException {
        return parseCsvFile(csvFilePath, DEFAULT_SEPARATOR);
    }

    /**
     * Читает CSV файл и преобразует его в список сотрудников.
     *
     * @param csvFilePath путь к CSV файлу
     * @param separator разделитель полей
     * @return список сотрудников
     * @throws IOException если произошла ошибка чтения файла
     * @throws CsvValidationException если CSV файл невалиден
     */
    public List<Person> parseCsvFile(String csvFilePath, char separator)
            throws IOException, CsvValidationException {

        List<Person> persons = new ArrayList<>();

        // Сначала пробуем открыть как обычный файл
        InputStream in = null;

        try {
            File file = new File(csvFilePath);

            if (file.exists() && file.isFile()) {
                // Если файл существует на файловой системе
                in = new FileInputStream(file);
            } else {
                // Если файла нет на файловой системе, ищем в ресурсах
                in = getClass().getClassLoader().getResourceAsStream(csvFilePath);
                if (in == null) {
                    // Пробуем удалить префикс "src/test/resources/" если он есть
                    String resourcePath = csvFilePath.replace("src/test/resources/", "")
                            .replace("src/main/resources/", "");
                    in = getClass().getClassLoader().getResourceAsStream(resourcePath);

                    if (in == null) {
                        throw new FileNotFoundException("Файл не найден: " + csvFilePath);
                    }
                }
            }

            // Используем CSVReaderBuilder для создания читателя
            try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(in, "UTF-8"))
                    .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(separator).build())
                    .build()) {

                // Пропускаем заголовок
                reader.readNext();

                String[] nextLine;
                int lineNumber = 1; // Для отладки

                while ((nextLine = reader.readNext()) != null) {
                    lineNumber++;
                    try {
                        Person person = parsePerson(nextLine, lineNumber);
                        persons.add(person);
                    } catch (IllegalArgumentException e) {
                        System.err.printf("Ошибка в строке %d: %s%n", lineNumber, e.getMessage());
                        // Пропускаем невалидные строки, продолжаем обработку
                    }
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Игнорируем ошибку закрытия
                }
            }
        }

        return persons;
    }

    /**
     * Парсит одну строку CSV файла в объект Person.
     *
     * @param fields массив полей строки
     * @param lineNumber номер строки (для сообщений об ошибках)
     * @return объект Person
     * @throws IllegalArgumentException если данные невалидны
     */
    private Person parsePerson(String[] fields, int lineNumber) {
        if (fields.length < 6) {
            throw new IllegalArgumentException(
                    String.format("Строка %d: ожидается 6 полей, получено %d", lineNumber, fields.length)
            );
        }

        try {
            // Парсим ID
            Long id = parseId(fields[0], lineNumber);

            // Имя
            String name = fields[1].trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустым");
            }

            // Пол
            Person.Gender gender = Person.Gender.fromString(fields[2].trim());

            // Дата рождения
            LocalDate birthDate = DateUtils.parseDate(fields[3].trim());

            // Проверяем валидность даты
            if (!DateUtils.isValidDate(birthDate)) {
                throw new IllegalArgumentException("Дата рождения не может быть в будущем");
            }

            // Подразделение (используем кэш)
            String divisionName = fields[4].trim();
            Division division = getOrCreateDivision(divisionName);

            // Зарплата
            Double salary = parseSalary(fields[5].trim(), lineNumber);

            return new Person(id, name, gender, birthDate, division, salary);

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Строка %d: %s", lineNumber, e.getMessage()), e
            );
        }
    }

    /**
     * Парсит ID из строки.
     */
    private Long parseId(String idStr, int lineNumber) {
        try {
            Long id = Long.parseLong(idStr.trim());
            if (id <= 0) {
                throw new IllegalArgumentException("ID должен быть положительным числом");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат ID: " + idStr);
        }
    }

    /**
     * Парсит зарплату из строки.
     */
    private Double parseSalary(String salaryStr, int lineNumber) {
        try {
            Double salary = Double.parseDouble(salaryStr.trim());
            if (salary < 0) {
                throw new IllegalArgumentException("Зарплата не может быть отрицательной");
            }
            return salary;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат зарплаты: " + salaryStr);
        }
    }

    /**
     * Получает подразделение из кэша или создает новое.
     */
    private Division getOrCreateDivision(String divisionName) {
        if (divisionName == null || divisionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название подразделения не может быть пустым");
        }

        String normalizedName = divisionName.trim();
        return DIVISION_CACHE.computeIfAbsent(normalizedName, Division::new);
    }

    /**
     * Очищает кэш подразделений.
     * Полезно для тестирования.
     */
    public static void clearDivisionCache() {
        DIVISION_CACHE.clear();
    }
}
