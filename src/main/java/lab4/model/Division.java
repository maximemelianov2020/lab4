package lab4.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Класс, представляющий подразделение компании.
 * ID генерируется автоматически при создании нового подразделения.
 */
public class Division {
    private static final AtomicLong ID_COUNTER = new AtomicLong(1);

    private final Long id;
    private final String name;

    /**
     * Конструктор для создания подразделения.
     *
     * @param name название подразделения
     * @throws IllegalArgumentException если название null или пустое
     */
    public Division(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название подразделения не может быть пустым");
        }
        this.id = ID_COUNTER.getAndIncrement();
        this.name = name.trim();
    }

    /**
     * Возвращает ID подразделения.
     *
     * @return ID подразделения
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает название подразделения.
     *
     * @return название подразделения
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return Objects.equals(id, division.id) && Objects.equals(name, division.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Division{id=" + id + ", name='" + name + "'}";
    }
}
