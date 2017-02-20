package chapter12.jpa;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.testng.annotations.AfterMethod;

public class MongoTest extends BaseJPAOGMTest {
    @AfterMethod
    public void clearDB() {
        try (MongoClient mongoClient = new MongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("chapter12");
            db.drop();
        }
    }

    @Override
    String getPersistenceUnitName() {
        return "chapter12-mongo";
    }
}
