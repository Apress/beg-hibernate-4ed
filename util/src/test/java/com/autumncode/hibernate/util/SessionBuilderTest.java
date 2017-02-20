package com.autumncode.hibernate.util;

import org.hibernate.Session;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class SessionBuilderTest {
    @Test
    public void testSessionFactory() {
        try (Session session = SessionUtil.getSession()) {
            assertNotNull(session);
        }
    }
}
