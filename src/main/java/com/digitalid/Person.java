package com.digitalid;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Person class represents a citizen in the DigitalID platform.
 *
 * Responsibilities:
 * - Store personal information
 * - Validate input data using buisness rules
 * - Persist data using text files
 *
 * This class implements:
 * - addPerson(): Adds new person to the system
 * - updatePersonalDetails(): Updates an existing person's details
 * - addID(): Associates identification documents with a person
 *
 * It also handles:
 * - Validation logic (Regex, date format, ID rules)
 * - TXT file read/write operations
 */
public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;

    // File paths used for persistence
    private static final String PERSON_FILE = "persons.txt";
    private static final String ID_FILE = "ids.txt";

    /**
     * Constructor to initialize a Person object
     */
    public Person(String personID, String firstName, String lastName,
                  String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;
    }

    // =====================================================
    // 1. addPerson()
    // =====================================================

    /**
     * Adds a new person to persons.txt after validating:
     * - Person ID format
     * - Address format
     * - Birthdate format
     *
     * @return true if inserted successfully, otherwise false
     */
    public boolean addPerson() {
        // Validate all the required fields before writing to the file
        if (!isValidPersonID(personID)) return false;
        if (!isValidAddress(address)) return false;
        if (!isValidBirthDate(birthDate)) return false;
        //Append person data to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PERSON_FILE, true))) {

            writer.write(personID + "," + firstName + "," +
                    lastName + "," + address + "," + birthDate);
            writer.newLine();
            return true;

        } catch (IOException e) {
            // File write failure
            return false;
        }
    }

    // =====================================================
    // 2. updatePersonalDetails()
    // =====================================================

    /**
     * Updates an existing person's details in persons.txt
     *
     * Conditions:
     * 1. Under 18 → address cannot be changed
     * 2. If birthday changes → no other field can change
     * 3. If first digit of ID is even → ID cannot change
     */
    public boolean updatePersonalDetails(String newPersonID,
                                         String newFirstName,
                                         String newLastName,
                                         String newAddress,
                                         String newBirthDate) {
        // Validate new input values
        if (!isValidPersonID(newPersonID)) return false;
        if (!isValidAddress(newAddress)) return false;
        if (!isValidBirthDate(newBirthDate)) return false;

        // Condition 1: Under 18 cannot change adress
        if (getAge() < 18 && !this.address.equals(newAddress)) {
            return false;
        }

        // Condition 2: Birth date change must be the only modification
        if (!this.birthDate.equals(newBirthDate)) {

            if (!this.personID.equals(newPersonID) ||
                    !this.firstName.equals(newFirstName) ||
                    !this.lastName.equals(newLastName) ||
                    !this.address.equals(newAddress)) {

                return false;
            }
        }

        // Condition 3: Even first digit in ID prevents ID modification
        char firstDigit = this.personID.charAt(0);
        if (Character.isDigit(firstDigit) &&
                ((firstDigit - '0') % 2 == 0) &&
                !this.personID.equals(newPersonID)) {

            return false;
        }
        // Apply updates to object
        this.personID = newPersonID;
        this.firstName = newFirstName;
        this.lastName = newLastName;
        this.address = newAddress;
        this.birthDate = newBirthDate;
        // Update corresponding record in file
        try {
            File file = new File(PERSON_FILE);
            if (!file.exists()) return false;

            List<String> lines = Files.readAllLines(file.toPath());
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(this.personID)) {
                    updatedLines.add(personID + "," + firstName + "," +
                            lastName + "," + address + "," + birthDate);
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(file.toPath(), updatedLines);
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    // =====================================================
    // 3. addID()
    // =====================================================

    /**
     * Adds ID information into ids.txt
     *
     * Supported ID types:
     * - passport (2 uppercase + 6 digits)
     * - licence (2 uppercase + 8 digits)
     * - medicare (9 digits)
     * - student (12 digits, only if under 18)
     */
    public boolean addID(String idType, String idValue) {

        if (idType == null || idValue == null) return false;

        idType = idType.toLowerCase();
        boolean isValid = false;

        switch (idType) {

            case "passport":
                isValid = idValue.matches("[A-Z]{2}[0-9]{6}");
                break;

            case "licence":
                isValid = idValue.matches("[A-Z]{2}[0-9]{8}");
                break;

            case "medicare":
                isValid = idValue.matches("[0-9]{9}");
                break;

            case "student":
                // Student ID only allowed for minors
                if (getAge() >= 18) return false;
                isValid = idValue.matches("[0-9]{12}");
                break;

            default:
                return false;
        }

        if (!isValid) return false;
        // Save ID record to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ID_FILE, true))) {

            writer.write(this.personID + "," + idType + "," + idValue);
            writer.newLine();
            return true;

        } catch (IOException e) {
            return false;
        }
    }

    // =====================================================
    // Validation Methods
    // =====================================================
    /**
     * Validates person ID format according to system rules
     */
    private boolean isValidPersonID(String id) {

        if (id == null || id.length() != 10) return false;
        if (!id.substring(0, 2).matches("[2-9]{2}")) return false;
        if (!id.substring(8, 10).matches("[A-Z]{2}")) return false;

        String middle = id.substring(2, 8);
        int specialCount = 0;

        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }
        // Atleast 2 special characters required in the middle
        return specialCount >= 2;
    }
    /**
     * Validates adress format and ensures state is Victoris
     *
     */

    private boolean isValidAddress(String address) {

        if (address == null) return false;

        String[] parts = address.split("\\|");
        if (parts.length != 5) return false;

        return parts[3].equals("Victoria");
    }
    /**
     * Validates birth date format using dd- MM -yyyy pattern
     *
     */

    private boolean isValidBirthDate(String birthDate) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(birthDate, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    /**
     * Calculates age based on birth date and current date
     */

    private int getAge() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dob = LocalDate.parse(this.birthDate, formatter);
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
