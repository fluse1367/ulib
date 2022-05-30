package eu.software4you.ulib.core.database.sql;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Representation of all Sql data types.
 *
 * @see <a href="https://www.w3schools.com/mysql/mysql_datatypes.asp" target="_blank">https://www.w3schools.com/mysql/mysql_datatypes.asp</a>
 */
public enum DataType {
    /* Text Types */

    /**
     * A {@link String} with fixed length.
     * Can be from 0 to 255. Default is 1.
     */
    CHAR(String.class, 1, 255L),

    /**
     * A {@link String} with a maximum length.
     * Can be from 0 to 255. Default is 255.
     */
    VARCHAR(String.class, 255, 255L),

    /**
     * Equal to {@link #CHAR}, but stores binary byte strings.
     */
    BINARY(String.class, 1, 255L),

    /**
     * Equal to {@link #VARCHAR}, but stores binary byte strings.
     */
    VARBINARY(String.class, 255, 255L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 255 bytes.
     */
    TINYBLOB(byte[].class, 255L),

    /**
     * A {@link String} with a maximum capacity of 255 characters.
     */
    TINYTEXT(String.class, 255L),

    /**
     * A {@link String} with a maximum capacity of 65,535 characters.
     */
    TEXT(String.class, 65535L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 65,535 bytes.
     */
    BLOB(byte[].class, 65535L),

    /**
     * A {@link String} with a maximum capacity of 16,777,215 characters.
     */
    MEDIUMTEXT(String.class, 16777215L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 16,777,215 bytes.
     */
    MEDIUMBLOB(byte[].class, 777215L),

    /**
     * A {@link String} with a maximum capacity of 4,294,967,295 characters.
     */
    LONGTEXT(String.class, 4294967295L),

    /**
     * A <b>B</b>inary <b>L</b>arge <b>OB</b>ject with a maximum capacity of 4,294,967,295 bytes.
     */
    LONGBLOB(byte[].class, 4294967295L),

    /**
     * A {@link String} that can can only have a value chosen from a list.
     * The list can have up to 65,535 values. Default value list is empty.
     * Invalid values will be replaced by blank values while inserting. The sorting of the values will be as entered.
     */
    ENUM(String.class, 65535L),

    /**
     * A {@link String} that can can have 0 or more values, chosen from a list. The list can have up to 64 values.
     *
     * @see #ENUM
     */
    SET(String.class, 64L),

    /* Numeric Types */

    /**
     * A bit-value type. The number of bits per value is specified in size. The size parameter can hold a value from 1 to 64. The default value for size is 1.
     */
    BIT(byte.class, 1, 64L),

    /**
     * A very small integer. Signed range is from -128 to 127. Unsigned range is from 0 to 255. The size parameter specifies the maximum display width (which is 255).
     */
    TINYINT(byte.class, 255L),

    /**
     * A boolean (stored as number; zero is considered as false, nonzero values are considered as true).
     */
    BOOL(boolean.class, 1L),

    /**
     * Equal to {@link #BOOL}
     */
    BOOLEAN(boolean.class, 1L),

    /**
     * A small integer. Signed range is from -32,768 to 32,767. Unsigned range is from 0 to 65,535. The size parameter specifies the maximum display width (which is 255).
     */
    SMALLINT(short.class, 255L),

    /**
     * A medium integer. Signed range is from -8,388,608 to 8,388,607. Unsigned range is from 0 to 16,777,215. The size parameter specifies the maximum display width (which is 255).
     */
    MEDIUMINT(int.class, 255L),

    /**
     * A medium integer. Signed range is from -2,147,483,648 to 2,147,483,647. Unsigned range is from 0 to 4,294,967,295. The size parameter specifies the maximum display width (which is 255).
     */
    INT(int.class, 255L),

    /**
     * Equal to {@link #INT}
     */
    INTEGER(int.class, 255L),

    /**
     * A large integer. Signed range is from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807. Unsigned range is from 0 to 18,446,744,073,709,551,615. The size parameter specifies the maximum display width (which is 255).
     */
    BIGINT(long.class, 255L),

    /**
     * A floating point number. MySQL uses the size to determine whether to use FLOAT or DOUBLE for the resulting data type. If the size is from 0 to 24, the data type becomes {@code FLOAT}. If it is from 25 to 53, the data type becomes {@code DOUBLE}
     */
    FLOAT(double.class, 53L),

    /**
     * A normal-size floating point number. The total number of digits is specified in size.
     */
    DOUBLE(double.class, 53L),

    /**
     * An exact fixed-point number. The total number of digits is specified in size. The maximum number for size is 65. The default value for size is 10.
     */
    DECIMAL(String.class, 10, 65L),


    /* Date/Time Types */
    // TODO
    ;

    @Getter
    private final long defaultSize;
    @Getter
    private final long maximumSize;
    @Getter
    @NotNull
    private final Class<?> clazz;

    DataType(@NotNull Class<?> clazz, long maximumSize) {
        this(clazz, -1, maximumSize);
    }

    DataType(@NotNull Class<?> clazz, int defaultSize, long maximumSize) {
        this.clazz = clazz;
        this.defaultSize = defaultSize;
        this.maximumSize = maximumSize;
    }
}
