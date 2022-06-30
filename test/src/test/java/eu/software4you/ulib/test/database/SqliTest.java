package eu.software4you.ulib.test.database;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.database.Database;
import eu.software4you.ulib.core.database.sql.ColumnBuilder;
import eu.software4you.ulib.core.database.sql.DataType;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class SqliTest {

    private final Path path = Path.of("testdb.sqlite");
    private final byte[] dummyData = {1, 2, 3, 4};

    private int insert_id = -1;

    @Test
    public void testDB() throws Exception {

        if (Files.exists(path))
            Files.delete(path);

        testNewDB();
        testExistingDB();
    }


    private void testNewDB() throws SQLException {
        var db = Database.connect(path);

        // #addTable will throw exception if table already exists
        var table = db.addTable("test",
                ColumnBuilder.of(DataType.INTEGER, "id").primary().autoIncrement(),
                ColumnBuilder.of(DataType.TINYBLOB, "data")
        );

        // table should not exist (yet)
        Assert.assertFalse(table.exists());

        // create table
        table.create().rethrow(SQLException.class);

        // table should now exist
        Assert.assertTrue(table.exists());

        // add data
        Assert.assertTrue(
                table.insert(new Pair<>("data", dummyData))
        );
        insert_id = db.fetchLastAutoincrementInsertionId();


        // fetch data
        byte[] data;
        try (var res = table.select("data")
                .where("id").isEqualToP(insert_id)
                .query()) {
            data = res.getBytes(1);
        }
        Assert.assertArrayEquals(data, dummyData);


        db.disconnect();

    }

    private void testExistingDB() throws SQLException {
        var db = Database.connect(path);

        var table = db.getTable("test").orElseThrow();

        byte[] data;
        try (var res = table.select("data")
                .where("id").isEqualToP(insert_id)
                .query()) {
            data = res.getBytes(1);
        }

        Assert.assertArrayEquals(data, dummyData);


        db.disconnect();
    }

}
