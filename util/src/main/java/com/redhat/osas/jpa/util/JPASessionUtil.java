package com.redhat.osas.jpa.util;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPASessionUtil {
    static Map<String, EntityManagerFactory> persistenceUnits = new HashMap<>();

    public static synchronized EntityManager getEntityManager(String persistenceUnitName) {
        if (persistenceUnits.get(persistenceUnitName) == null) {
            persistenceUnits.put(persistenceUnitName,
                    Persistence.createEntityManagerFactory(persistenceUnitName));
        }
        return persistenceUnits.get(persistenceUnitName)
                .createEntityManager();
    }

    public static Session getSession(String persistenceUnitName) {
        return getEntityManager(persistenceUnitName).unwrap(Session.class);
    }
}
