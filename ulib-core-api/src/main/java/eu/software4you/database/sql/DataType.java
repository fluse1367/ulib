package eu.software4you.database.sql;

import lombok.Getter;

/**
 * Representation of all Sql data types.
 */
public enum DataType {
    /* Text Types */

    /**
     * A {@link String} with fixed length.
     * Can be from 0 to 255. Default is 1.
     */
    CHAR(1, 255L),

    /**
     * A {@link String} with a maximum length.
     * Can be from 0 to 255. Default is 255.
     */
    VARCHAR(255, 255L),

    /**
     * Equal to {@link #CHAR}, but stores binary byte strings.
     */
    BINARY(1, 255L),

    /**
     * Equal to {@link #VARCHAR}, but stores binary byte strings.
     */
    VARBINARY(255, 255L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 255 bytes.
     */
    TINYBLOB(255L),

    /**
     * A {@link String} with a maximum capacity of 255 characters.
     */
    TINYTEXT(255L),

    /**
     * A {@link String} with a maximum capacity of 65,535 characters.
     */
    TEXT(65535L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 65,535 bytes.
     */
    BLOB(65535L),

    /**
     * A {@link String} with a maximum capacity of 16,777,215 characters.
     */
    MEDIUMTEXT(16777215L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 16,777,215 bytes.
     */
    MEDIUMBLOB(16, 777215L),

    /**
     * A {@link String} with a maximum capacity of 4,294,967,295 characters.
     */
    LONGTEXT(4294967295L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 4,294,967,295 bytes.
     */
    LONGBLOB(4294967295L),

    /**
     * A {@link String} that can can only have a value chosen from a list.
     * The list can have up to 65,535 values. Default value list is empty.
     * Invalid values will be replaced by blank values while inserting. The sorting of the values will be as entered.
     */
    ENUM(65535L),

    /**
     * A {@link String} that can can have 0 or more values, chosen from a list. The list can have up to 64 values.
     *
     * @see #ENUM
     */
    SET(64L),

    /* Numeric Types */



    /* Date/Time Types */;

    @Getter
    private final long defaultSize;
    @Getter
    private final long maximumSize;

    DataType(long maximumSize) {
        this(-1, maximumSize);
    }

    DataType(int defaultSize, long maximumSize) {
        this.defaultSize = defaultSize;
        this.maximumSize = maximumSize;
    }
}
