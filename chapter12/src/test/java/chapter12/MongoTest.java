package chapter12;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.testng.annotations.AfterMethod;

import java.net.UnknownHostException;

public class MongoTest extends BaseOGMTest {

    @AfterMethod
    public void clearDB() {
        try {
            Mongo mongo = new Mongo();
            DB db = mongo.getDB("chapter12");
            db.dropDatabase();
            mongo.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    String getPersistenceUnitName() {
        return "chapter12-mongo";
    }
}
