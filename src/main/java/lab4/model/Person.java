package lab4.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Класс, представляющий сотрудника компании.
 */
public class Person {
    private final Long id;
    private final String name;
    private final Gender gender;
    private final LocalDate birthDate;
    private final Division division;
    private final Double salary;

    /**
     * Перечисление для представления пола сотрудника.
     */
    public enum Gender {
        MALE, FEMALE;

        /**
         * Преобразует строку в enum Gender.
         *
         * @param genderStr строковое представление пола
         * @return соответствующий enum Gender
         * @throws IllegalArgumentException если строка не соответствует ни одному полу
         */
        public static Gender fromString(String genderStr) {
            if (genderStr == null || genderStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Пол не может быть пустым");
            }

            String normalized = genderStr.trim().toUpperCase();
            switch (normalized) {
                case "MALE":
                case "МУЖСКОЙ":
                case "М":
                    return MALE;
                case "FEMALE":
                case "ЖЕНСКИЙ":
                case "Ж":
                    return FEMALE;
                default:
                    throw new IllegalArgumentException("Неизвестный пол: " + genderStr);
            }
        }
    }

    /**
     * Конструктор для создания сотрудника.
     *
     * @param id ID сотрудника
     * @param name имя сотрудника
     * @param gender пол сотрудника
     * @param birthDate дата рождения
     * @param division подразделение
     * @param salary зарплата
     * @throws IllegalArgumentException если любой из параметров некорректен
     */
    public Person(Long id, String name, Gender gender, LocalDate birthDate,
                  Division division, Double salary) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (gender == null) {
            throw new IllegalArgumentException("Пол не может быть null");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Дата рождения не может быть null");
        }
        if (division == null) {
            throw new IllegalArgumentException("Подразделение не может быть null");
        }
        if (salary == null || salary < 0) {
            throw new IllegalArgumentException("Зарплата должна быть неотрицательной");
        }

        this.id = id;
        this.name = name.trim();
        this.gender = gender;
        this.birthDate = birthDate;
        this.division = division;
        this.salary = salary;
    }

    // Геттеры
    public Long getId() { return id; }
    public String getName() { return name; }
    public Gender getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public Division getDivision() { return division; }
    public Double getSalary() { return salary; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                gender == person.gender &&
                Objects.equals(birthDate, person.birthDate) &&
                Objects.equals(division, person.division) &&
                Objects.equals(salary, person.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, birthDate, division, salary);
    }

    @Override
    public String toString() {
        return String.format(
                "Person{id=%d, name='%s', gender=%s, birthDate=%s, division=%s, salary=%.2f}",
                id, name, gender, birthDate, division, salary
        );
    }
}
