package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import lab4.model.Division;

/**
 * Тесты для класса Division.
 */
class DivisionTest {

    @Test
    void testDivisionCreation() {
        Division division = new Division("IT Department");

        assertNotNull(division.getId());
        assertEquals("IT Department", division.getName());
    }

    @Test
    void testDivisionCreationWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Division(""));
        assertThrows(IllegalArgumentException.class, () -> new Division(null));
        assertThrows(IllegalArgumentException.class, () -> new Division("   "));
    }

    @Test
    void testAutoIncrementId() {
        Division division1 = new Division("HR");
        Division division2 = new Division("Finance");
        Division division3 = new Division("Marketing");

        assertTrue(division2.getId() > division1.getId());
        assertTrue(division3.getId() > division2.getId());
    }

    @Test
    void testEquality() {
        Division division1 = new Division("IT");
        Division division2 = new Division("IT");

        assertNotEquals(division1, division2); // Разные ID
        assertNotEquals(division1.hashCode(), division2.hashCode());
    }

    @Test
    void testToString() {
        Division division = new Division("Sales");
        String toString = division.toString();

        assertTrue(toString.contains("Sales"));
        assertTrue(toString.contains("id="));
    }
}
