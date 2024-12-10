package de.hsh.dbs2.imdb.logic;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.util.HibernateConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class GenreManager {

    /**
     * Ermittelt eine vollstaendige Liste aller in der Datenbank abgelegten Genres.
     * Die Genres werden alphabetisch sortiert zurueckgeliefert.
     *
     * @return Alle Genre-Namen als String-Liste
     */
    public List<String> getGenres() {
        EntityManager em = HibernateConnection.get();
        List<Genre> genres;
        List<String> allGenres = new ArrayList<>();
        try {
            em.getTransaction().begin();
            genres = em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }

        for (Genre genre : genres) {
            allGenres.add(genre.getGenre());
        }
        return allGenres;
    }
}
