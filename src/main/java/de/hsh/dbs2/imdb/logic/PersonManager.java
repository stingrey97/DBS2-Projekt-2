package de.hsh.dbs2.imdb.logic;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.entities.Person;
import de.hsh.dbs2.imdb.util.HibernateConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class PersonManager {

	/**
	 * Liefert eine Liste aller Personen, deren Name den Suchstring enthaelt.
	 * @param text Suchstring
	 * @return Liste mit passenden Personennamen, die in der Datenbank eingetragen sind.
	 * @throws Exception
	 */
	public List<String> getPersonList(String text) throws Exception {
		EntityManager em = HibernateConnection.get();
		List<Person> persons;
		List<String> allPersons = new ArrayList<>();
		try {
			em.getTransaction().begin();
			TypedQuery<Person> q = em.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class);
			q.setParameter("name", text);
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
