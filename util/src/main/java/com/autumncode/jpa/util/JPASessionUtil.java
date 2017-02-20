package com.autumncode.jpa.util;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPASessionUtil {
    private static Map<String, EntityManagerFactory> persistenceUnits = new HashMap<>();

    @SuppressWarnings("WeakerAccess")
    public static synchronized EntityManager getEntityManager(String persistenceUnitName) {
        persistenceUnits.putIfAbsent(persistenceUnitName,
                Persistence.createEntityManagerFactory(persistenceUnitName));
        return persistenceUnits.get(persistenceUnitName)
                .createEntityManager();
    }

    public static Session getSession(String persistenceUnitName) {
        return getEntityManager(persistenceUnitName).unwrap(Session.class);
    }
}
