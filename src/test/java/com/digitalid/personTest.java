package com.digitalid;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.*;

public class PersonTest {

    private static final String PERSONS_FILE = "persons.txt";
    private static final String IDS_FILE = "ids.txt";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(PERSONS_FILE));
        Files.deleteIfExists(Paths.get(IDS_FILE));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(PERSONS_FILE));
        Files.deleteIfExists(Paths.get(IDS_FILE));
    }

    // === ADD PERSON TESTS (5 TESTS) ===
    @Test
    @DisplayName("TC1: Valid person - returns true")
    void testAddPerson_ValidAll() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addPerson());
        assertTrue(Files.exists(Paths.get(PERSONS_FILE)));
    }

    @Test
    @DisplayName("TC2: Invalid ID length - returns false")
    void testAddPerson_InvalidIDLength() {
        Person p = new Person("shortID", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    @Test
    @DisplayName("TC3: Invalid state NSW - returns false")
    void testAddPerson_InvalidState() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|NSW|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    @Test
    @DisplayName("TC4: Invalid birthdate - returns false")
    void testAddPerson_InvalidDate() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "2023-13-45");
        assertFalse(p.addPerson());
    }

    @Test
    @DisplayName("TC5: No special chars in ID - returns false")
    void testAddPerson_NoSpecialChars() {
        Person p = new Person("56abcdefAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addPerson());
    }

    // === UPDATE PERSONAL DETAILS TESTS (5 TESTS) ===
    @Test
    @DisplayName("TC6: Under 18 address change - false")
    void testUpdatePersonalDetails_Under18Address() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "01-01-2010");
        p.addPerson();
        boolean result = p.updatePersonalDetails(
                "56s_d%&fAB", "John", "Doe",
                "100|New|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC7: Birthday + other change - false")
    void testUpdatePersonalDetails_BirthdayWithOthers() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        boolean result = p.updatePersonalDetails(
                "56s_d%&fAB", "Jane", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "16-11-1990");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC8: Even first digit ID change - false")
    void testUpdatePersonalDetails_EvenFirstDigit() {
        Person p = new Person("26s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        boolean result = p.updatePersonalDetails(
                "36s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);
    }

    @Test
    @DisplayName("TC9: Valid name change - true")
    void testUpdatePersonalDetails_ValidNameChange() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson();
        boolean result = p.updatePersonalDetails(
                "56s_d%&fAB", "Jane", "Smith",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(result);
    }

    @Test
    @DisplayName("TC10: Non-existent person - false")
    void testUpdatePersonalDetails_NonExistent() {
        Person p = new Person("99t_e#^hCD", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        boolean result = p.updatePersonalDetails(
                "99t_e#^hCD", "Jane", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(result);
    }

    // === ADD ID TESTS (5 TESTS) ===
    @Test
    @DisplayName("TC11: Valid passport - true")
    void testAddID_ValidPassport() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addID("passport", "AB123456"));
    }

    @Test
    @DisplayName("TC12: Valid licence - true")
    void testAddID_ValidLicence() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addID("licence", "AB12345678"));
    }

    @Test
    @DisplayName("TC13: Valid medicare - true")
    void testAddID_ValidMedicare() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addID("medicare", "123456789"));
    }

    @Test
    @DisplayName("TC14: Valid student card under 18 - true")
    void testAddID_ValidStudentUnder18() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "01-01-2010");
        assertTrue(p.addID("student", "123456789012"));
    }

    @Test
    @DisplayName("TC15: Invalid student card over 18 - false")
    void testAddID_InvalidStudentOver18() {
        Person p = new Person("56s_d%&fAB", "John", "Doe",
                "32|Highland|Melbourne|Victoria|Australia", "15-11-1990");
        assertFalse(p.addID("student", "123456789012"));
    }
}
