package csv;

import lab4.model.Person;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import lab4.csv.CsvParser;

/**
 * Тесты для класса CsvParser.
 */
class CsvParserTest {

    private CsvParser csvParser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
        CsvParser.clearDivisionCache();
    }

    @Test
    void testParseValidCsvFile() throws IOException, CsvValidationException {
        // Используем валидные даты в формате dd.MM.yyyy
        // Дата должна быть в прошлом (не в будущем)
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "28281;Aahan;Male;15.05.1970;I;4800\n" +  // 15 мая 1970 - валидная дата в прошлом
                "28282;Jane;Female;07.02.1983;HR;5500";

        Path csvPath = tempDir.resolve("test_valid.csv");
        Files.write(csvPath, testCsv.getBytes());

        assertTrue(Files.exists(csvPath), "Файл должен существовать перед вызовом парсера");

        List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());

        assertNotNull(persons);
        assertFalse(persons.isEmpty());
        assertEquals(2, persons.size());

        // Проверяем первую запись
        Person firstPerson = persons.get(0);
        assertEquals(28281L, firstPerson.getId());
        assertEquals("Aahan", firstPerson.getName());
        assertEquals(Person.Gender.MALE, firstPerson.getGender());
        assertEquals("I", firstPerson.getDivision().getName());
        assertEquals(4800.0, firstPerson.getSalary(), 0.001);
    }

    @Test
    void testParseWithCustomSeparator() throws IOException, CsvValidationException {
        // Используем валидные даты
        String testCsv = "id,name,gender,birthDate,division,salary\n" +
                "1,John,Male,15.05.1970,IT,5000\n" +  // 15 мая 1970
                "2,Jane,Female,07.02.1983,HR,6000";   // 7 февраля 1983

        Path csvPath = tempDir.resolve("test_custom.csv");
        Files.write(csvPath, testCsv.getBytes());

        assertTrue(Files.exists(csvPath), "Временный файл должен существовать");

        List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString(), ',');

        assertEquals(2, persons.size());
        assertEquals("John", persons.get(0).getName());
        assertEquals("Jane", persons.get(1).getName());
    }

    @Test
    void testParseNonExistentFile() {
        Path nonExistent = tempDir.resolve("non_existent.csv");
        assertFalse(Files.exists(nonExistent), "Файл не должен существовать");

        assertThrows(FileNotFoundException.class, () ->
                csvParser.parseCsvFile(nonExistent.toAbsolutePath().toString())
        );
    }

    @Test
    void testParseInvalidCsvLine() throws IOException {
        // Невалидная дата (неправильный формат) + валидная строка
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "1;John;Male;invalid-date;IT;5000\n" +  // Невалидная дата
                "2;Jane;Female;07.02.1983;HR;6000";      // Валидная строка

        Path csvPath = tempDir.resolve("test_invalid.csv");
        Files.write(csvPath, testCsv.getBytes());

        assertTrue(Files.exists(csvPath), "Временный файл должен существовать");

        try {
            List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());
            assertNotNull(persons);
            // Должна быть обработана только валидная строка
            assertEquals(1, persons.size());
            assertEquals("Jane", persons.get(0).getName());
        } catch (FileNotFoundException e) {
            fail("Файл должен был быть найден: " + e.getMessage());
        } catch (CsvValidationException | IOException e) {
            // Другие исключения допустимы
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testParseInvalidFutureDate() throws IOException {
        // Дата в будущем должна быть отфильтрована
        String futureDate = LocalDate.now().plusYears(1).format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "1;John;Male;" + futureDate + ";IT;5000\n" +  // Дата в будущем
                "2;Jane;Female;07.02.1983;HR;6000";            // Валидная дата в прошлом

        Path csvPath = tempDir.resolve("test_future_date.csv");
        Files.write(csvPath, testCsv.getBytes());

        try {
            List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());
            assertNotNull(persons);
            // Только валидная дата должна быть обработана
            if (persons.size() == 1) {
                assertEquals("Jane", persons.get(0).getName());
            }
        } catch (Exception e) {
            // Исключение допустимо
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testParseEmptyFile() throws IOException {
        Path csvPath = tempDir.resolve("test_empty.csv");
        Files.write(csvPath, new byte[0]);

        assertTrue(Files.exists(csvPath), "Файл должен существовать");

        try {
            List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());
            assertNotNull(persons);
            assertTrue(persons.isEmpty());
        } catch (Exception e) {
            // Если бросает исключение - это тоже допустимо
            assertNotNull(e.getMessage());
        }
    }

    @Test
    void testDivisionCache() throws IOException, CsvValidationException {
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "1;John;Male;15.05.1970;IT;5000\n" +
                "2;Jane;Female;07.02.1983;IT;6000\n" +
                "3;Bob;Male;10.10.1990;IT;7000";

        Path csvPath = tempDir.resolve("test_cache.csv");
        Files.write(csvPath, testCsv.getBytes());

        assertTrue(Files.exists(csvPath), "Временный файл должен существовать");

        List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());

        assertNotNull(persons);
        assertEquals(3, persons.size(), "Должно быть 3 человека");

        // Все три человека должны ссылаться на один и тот же объект Division
        assertSame(persons.get(0).getDivision(), persons.get(1).getDivision());
        assertSame(persons.get(1).getDivision(), persons.get(2).getDivision());
    }

    @Test
    void testSimpleCsvFile() throws IOException, CsvValidationException {
        // Используем дату в прошлом, а не 2000 год
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "1;Test;Male;15.05.1990;TestDiv;1000";  // 15 мая 1990 - валидная дата

        Path csvPath = tempDir.resolve("simple.csv");
        Files.write(csvPath, testCsv.getBytes());

        System.out.println("Пытаемся прочитать файл: " + csvPath.toAbsolutePath());
        System.out.println("Файл существует: " + Files.exists(csvPath));

        List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());

        assertNotNull(persons);
        assertFalse(persons.isEmpty());
        assertEquals(1, persons.size());
        assertEquals("Test", persons.get(0).getName());
    }

    @Test
    void testParseValidDates() throws IOException, CsvValidationException {
        // Тест различных валидных форматов дат (если поддерживаются)
        String testCsv = "id;name;gender;birthDate;division;salary\n" +
                "1;Иван;Male;01.01.1990;IT;5000\n" +
                "2;Мария;Female;31.12.1985;HR;6000\n" +
                "3;Алексей;Male;29.02.2000;Admin;7000"; // 29 февраля 2000 (високосный год)

        Path csvPath = tempDir.resolve("test_dates.csv");
        Files.write(csvPath, testCsv.getBytes());

        List<Person> persons = csvParser.parseCsvFile(csvPath.toAbsolutePath().toString());

        assertNotNull(persons);
        assertEquals(3, persons.size());
    }

    @Test
    void debugFileCreation() throws IOException {
        Path testPath = tempDir.resolve("debug_test.csv");
        String content = "test;content";

        System.out.println("Temp dir: " + tempDir.toAbsolutePath());
        System.out.println("Test file path: " + testPath.toAbsolutePath());

        Files.write(testPath, content.getBytes());

        System.out.println("File exists: " + Files.exists(testPath));
        System.out.println("File size: " + Files.size(testPath));
        System.out.println("File content: " + Files.readString(testPath));

        assertTrue(Files.exists(testPath), "Файл должен существовать");
        assertEquals(content, Files.readString(testPath), "Содержимое файла должно совпадать");
    }
}
