package de.hsh.dbs2.imdb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class HibernateConnection {
    private static final EntityManagerFactory emf;
    private static EntityManager em;

    static {
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("jakarta.persistence.jdbc.user", user);
        configOverrides.put("jakarta.persistence.jdbc.password", password);

        emf = Persistence.createEntityManagerFactory("movie_db", configOverrides);
        em = emf.createEntityManager();
    }

    public static EntityManager get() {
        if (em == null || !em.isOpen()) em = emf.createEntityManager();
        return em;
    }

    public static void close() {
        em.close();
    }
}
