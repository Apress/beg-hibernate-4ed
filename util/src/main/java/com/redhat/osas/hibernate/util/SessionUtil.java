package com.redhat.osas.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.jboss.logging.Logger;

public class SessionUtil {
    private static final SessionUtil instance = new SessionUtil();
    private final SessionFactory factory;
    private static final String CONFIG_NAME = "/configuration.properties";

    Logger logger = Logger.getLogger(this.getClass());

    private SessionUtil() {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistryBuilder srBuilder = new ServiceRegistryBuilder();
        srBuilder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = srBuilder.buildServiceRegistry();
        factory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static Session getSession() {
        return getInstance().factory.openSession();
    }

    private static SessionUtil getInstance() {
        return instance;
    }
}
