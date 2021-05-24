/**
 * Sql specific database tools.
 * <p>
 * <b>Warning:</b> No query operations that require user input use quoted column names to make more advanced operations (such as use of functions) possible. Be aware of sql injections!
 * </p>
 * <p>
 * Note: Although the most methods of this package do not declare any Exceptions, they are still able to throw them!<br>
 * These include, but are not limited to:
 * <ul>
 *     <li>{@link java.sql.SQLException}</li>
 * </ul>
 * </p>
 *
 * @see <a href="https://projectlombok.org/features/SneakyThrows" target="_blank">https://projectlombok.org/features/SneakyThrows</a>
 */
package eu.software4you.database.sql;