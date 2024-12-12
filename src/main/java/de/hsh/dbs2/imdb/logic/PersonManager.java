package de.hsh.dbs2.imdb.logic;

import de.hsh.dbs2.imdb.entities.Person;
import de.hsh.dbs2.imdb.util.HibernateConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class PersonManager {

    /**
     * Liefert eine Liste aller Personen, deren Name den Suchstring enthaelt.
     *
     * @param text Suchstring
     * @return Liste mit passenden Personennamen, die in der Datenbank eingetragen sind.
     */
    public List<String> getPersonList(String text) {
        EntityManager em = HibernateConnection.get();
        List<Person> persons;
        List<String> allPersons = new ArrayList<>();
        try {
            em.getTransaction().begin();
            TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p WHERE LOWER(p.name) LIKE :name", Person.class);
            q.setParameter("name", "%" + text.toLowerCase() + "%");
            persons = q.getResultList();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }

        for (Person person : persons) {
            allPersons.add(person.getName());
        }
        return allPersons;
    }
}
