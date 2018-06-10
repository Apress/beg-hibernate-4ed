package chapter06.primarykey;

import com.autumncode.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class BookTest {
    @Test
    public void bookTest() {
        try (Session session = SessionUtil.getSession()) {
            assertNotNull(session);
        }
    }
}
