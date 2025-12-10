package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Импорт тестируемых классов
import lab4.model.Person;
import lab4.model.Division;
import java.time.LocalDate;

/**
 * Тесты для класса Person.
 */
class PersonTest {

    private Division testDivision;

    @BeforeEach
    void setUp() {
        testDivision = new Division("Test Division");
    }

    @Test
    void testPersonCreation() {
        Person person = new Person(
                1L,
                "John Doe",
                Person.Gender.MALE,
                LocalDate.of(1990, 1, 1),
                testDivision,
                50000.0
        );

        assertEquals(1L, person.getId());
        assertEquals("John Doe", person.getName());
        assertEquals(Person.Gender.MALE, person.getGender());
        assertEquals(LocalDate.of(1990, 1, 1), person.getBirthDate());
        assertEquals(testDivision, person.getDivision());
        assertEquals(50000.0, person.getSalary(), 0.001);
    }

    @Test
    void testPersonCreationWithInvalidParameters() {
        // Невалидный ID
        assertThrows(IllegalArgumentException.class, () ->
                new Person(null, "John", Person.Gender.MALE,
                        LocalDate.now(), testDivision, 50000.0));

        assertThrows(IllegalArgumentException.class, () ->
                new Person(0L, "John", Person.Gender.MALE,
                        LocalDate.now(), testDivision, 50000.0));

        // Невалидное имя
        assertThrows(IllegalArgumentException.class, () ->
                new Person(1L, "", Person.Gender.MALE,
                        LocalDate.now(), testDivision, 50000.0));

        // Невалидный пол
        assertThrows(IllegalArgumentException.class, () ->
                new Person(1L, "John", null,
                        LocalDate.now(), testDivision, 50000.0));

        // Невалидная дата
        assertThrows(IllegalArgumentException.class, () ->
                new Person(1L, "John", Person.Gender.MALE,
                        null, testDivision, 50000.0));

        // Невалидное подразделение
        assertThrows(IllegalArgumentException.class, () ->
                new Person(1L, "John", Person.Gender.MALE,
                        LocalDate.now(), null, 50000.0));

        // Невалидная зарплата
        assertThrows(IllegalArgumentException.class, () ->
                new Person(1L, "John", Person.Gender.MALE,
                        LocalDate.now(), testDivision, -1000.0));
    }

    @Test
    void testGenderFromString() {
        assertEquals(Person.Gender.MALE, Person.Gender.fromString("Male"));
        assertEquals(Person.Gender.MALE, Person.Gender.fromString("MALE"));
        assertEquals(Person.Gender.MALE, Person.Gender.fromString("Мужской"));
        assertEquals(Person.Gender.FEMALE, Person.Gender.fromString("Female"));
        assertEquals(Person.Gender.FEMALE, Person.Gender.fromString("FEMALE"));
        assertEquals(Person.Gender.FEMALE, Person.Gender.fromString("Женский"));

        assertThrows(IllegalArgumentException.class, () ->
                Person.Gender.fromString("Unknown"));
        assertThrows(IllegalArgumentException.class, () ->
                Person.Gender.fromString(""));
        assertThrows(IllegalArgumentException.class, () ->
                Person.Gender.fromString(null));
    }

    @Test
    void testEquality() {
        Person person1 = new Person(
                1L, "John", Person.Gender.MALE,
                LocalDate.of(1990, 1, 1), testDivision, 50000.0
        );

        Person person2 = new Person(
                1L, "John", Person.Gender.MALE,
                LocalDate.of(1990, 1, 1), testDivision, 50000.0
        );

        assertEquals(person1, person2);
        assertEquals(person1.hashCode(), person2.hashCode());
    }

    @Test
    void testToString() {
        Person person = new Person(
                1L, "John", Person.Gender.MALE,
                LocalDate.of(1990, 1, 1), testDivision, 50000.0
        );

        String toString = person.toString();
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("MALE"));
        assertTrue(toString.contains("50000"));
    }
}
