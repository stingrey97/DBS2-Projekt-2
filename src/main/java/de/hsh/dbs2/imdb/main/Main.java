package de.hsh.dbs2.imdb.main;

import de.hsh.dbs2.imdb.entities.*;
import jakarta.persistence.*;

import java.util.*;

public class Main {

    private static EntityManager em;

    public static void main(String[] args) {

        // Add user login to environment parameters
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        // Add user login to persistence API
        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("jakarta.persistence.jdbc.user", user);
        configOverrides.put("jakarta.persistence.jdbc.password", password);


        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("movie_db", configOverrides)) {
            // Create EntityManager
            em = emf.createEntityManager();

            testProgram();

        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.close();
        }
    }

    private static void testProgram() {
        em.getTransaction().begin();
        CinemaMovie movie = getTestMovie();
        em.persist(movie);
        em.getTransaction().commit();
    }

    private static CinemaMovie getTestMovie() {
        Genre genreDrama = new Genre();
        genreDrama.setGenre("Drama");
        Genre genreSchmuddelfilm = new Genre();
        genreSchmuddelfilm.setGenre("Schmuddelfilm"); // Wg. Lack & Leder sowie den ganzen Schläuchen in den Körpern

        // Persons
        Person person = new Person();
        person.setName("Justin-Kevin Johnny van Wanrooij");
        person.setSex('?');

        // MovieCharacters
        MovieCharacter character = new MovieCharacter();
        character.setCharacter("Belgischer Hengst");
        character.setAlias("Toni Tortellini");
        character.setPosition(69);
        character.setPerson(person);

        // Movies
        CinemaMovie movie = new CinemaMovie();
        movie.setTitle("Latrix"); // BDSM-Matrix
        movie.setYear(1999);
        movie.addGenre(genreDrama);
        movie.addGenre(genreSchmuddelfilm);
        movie.addMovieCharacter(character);
        movie.setTicketsSold(420);

        return movie;
    }
}
