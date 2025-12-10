package lab4.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Утилитарный класс для работы с датами.
 */
public class DateUtils {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитарного класса.
     */
    private DateUtils() {
        throw new UnsupportedOperationException("Это утилитарный класс, экземпляры создавать нельзя");
    }

    /**
     * Парсит строку с датой в формате "dd.MM.yyyy".
     *
     * @param dateStr строка с датой
     * @return LocalDate объект
     * @throws DateTimeParseException если строка не может быть распарсена
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new DateTimeParseException("Дата не может быть пустой", "", 0);
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    "Неверный формат даты. Ожидается формат: dd.MM.yyyy",
                    dateStr,
                    e.getErrorIndex()
            );
        }
    }

    /**
     * Проверяет, является ли дата валидной (не в будущем).
     *
     * @param date дата для проверки
     * @return true если дата валидна, false если в будущем
     */
    public static boolean isValidDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
}
