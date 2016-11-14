package chapter06.primarykey;

import com.redhat.osas.hibernate.util.SessionUtil;
import org.hibernate.Session;
import org.testng.annotations.Test;

public class BookTest {
    @Test
    public void BookTest() {
        Session session = SessionUtil.getSession();
        session.close();
    }
}
