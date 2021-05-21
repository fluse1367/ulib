package eu.software4you.database.sql;

import lombok.Getter;

/**
 * Representation of all Sql data types.
 *
 * @see <a href="https://www.w3schools.com/mysql/mysql_datatypes.asp">https://www.w3schools.com/mysql/mysql_datatypes.asp</a>
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

    /**
     * A bit-value type. The number of bits per value is specified in size. The size parameter can hold a value from 1 to 64. The default value for size is 1.
     */
    BIT(1, 64L),

    /**
     * A very small integer. Signed range is from -128 to 127. Unsigned range is from 0 to 255. The size parameter specifies the maximum display width (which is 255).
     */
    TINYINT(255L),

    /**
     * A boolean (stored as number; zero is considered as false, nonzero values are considered as true).
     */
    BOOL(1L),

    /**
     * Equal to {@link #BOOL}
     */
    BOOLEAN(1L),

    /**
     * A small integer. Signed range is from -32,768 to 32,767. Unsigned range is from 0 to 65,535. The size parameter specifies the maximum display width (which is 255).
     */
    SMALLINT(255L),

    /**
     * A medium integer. Signed range is from -8,388,608 to 8,388,607. Unsigned range is from 0 to 16,777,215. The size parameter specifies the maximum display width (which is 255).
     */
    MEDIUMINT(255L),

    /**
     * A medium integer. Signed range is from -2,147,483,648 to 2,147,483,647. Unsigned range is from 0 to 4,294,967,295. The size parameter specifies the maximum display width (which is 255).
     */
    INT(255L),

    /**
     * Equal to {@link #INT}
     */
    INTEGER(255L),

    /**
     * A large integer. Signed range is from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807. Unsigned range is from 0 to 18,446,744,073,709,551,615. The size parameter specifies the maximum display width (which is 255).
     */
    BIGINT(255L),

    /**
     * A floating point number. MySQL uses the size to determine whether to use FLOAT or DOUBLE for the resulting data type. If the size is from 0 to 24, the data type becomes {@code FLOAT}. If it is from 25 to 53, the data type becomes {@code DOUBLE}
     */
    FLOAT(53L),

    /**
     * A normal-size floating point number. The total number of digits is specified in size.
     */
    DOUBLE(53L),

    /**
     * An exact fixed-point number. The total number of digits is specified in size. The maximum number for size is 65. The default value for size is 10.
     */
    DECIMAL(10, 65L),


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
