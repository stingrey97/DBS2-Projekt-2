package de.hsh.dbs2.imdb.main;

import de.hsh.dbs2.imdb.entities.CinemaMovie;
import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.entities.MovieCharacter;
import de.hsh.dbs2.imdb.entities.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

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
            // Unser Code ist perfekt und wirft keine Fehler
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            em.close();
        }
    }

    private static void testProgram() {
        em.getTransaction().begin();
        CinemaMovie movie = getLatrix();
        CinemaMovie testMovie = getTestMovie();

        for (Genre genre : movie.getGenres()) {
            System.out.println("Persistiere von Latrix: " + genre);
            em.persist(genre);
        }

        for (Genre genre : testMovie.getGenres()) {
            System.out.println("Persistiere von TestMovie: " + genre);
            em.persist(genre);
        }

        em.persist(movie);
        em.persist(testMovie);
        em.getTransaction().commit();
    }

    private static CinemaMovie getLatrix() {
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

    private static CinemaMovie getTestMovie() {
        Genre genre = new Genre();
        genre.setGenre("Action");

        Genre genre1 = new Genre();
        genre1.setGenre("Komödie");

        Genre genre2 = new Genre();
        genre2.setGenre("Dokumentation");

        Genre genre3 = new Genre();
        genre3.setGenre("Thriller (Michael Jackson)");

        // Persons
        Person person = new Person();
        person.setName("Beverly Hermann");
        person.setSex('w');

        Person person1 = new Person();
        person1.setName("Nils Obitz");
        person1.setSex('m');

        Person person2 = new Person();
        person2.setName("Vincent Terzenbach");
        person2.setSex('m');

        Person person3 = new Person();
        person3.setName("Lennart Vermehr");
        person3.setSex('m');

        // MovieCharacters
        MovieCharacter character = new MovieCharacter();
        character.setCharacter("Keine Ahnung");
        character.setAlias("Weiß ich nicht");
        character.setPosition(0);
        character.setPerson(person);

        MovieCharacter character1 = new MovieCharacter();
        character1.setCharacter("Helmut Schmidt");
        character1.setAlias("Kippe");
        character1.setPosition(1);
        character1.setPerson(person1);

        MovieCharacter character2 = new MovieCharacter();
        character2.setCharacter("König Vincent der Rekursivste");
        character2.setAlias("DRF");
        character2.setPosition(97);
        character2.setPerson(person2);

        MovieCharacter character3 = new MovieCharacter();
        character3.setCharacter("Bodybuilder bei Wish bestellt");
        character3.setAlias("Le Nerd");
        character3.setPosition(420);
        character3.setPerson(person3);

        // Movies
        CinemaMovie movie = new CinemaMovie();
        movie.setTitle("Das Universelle TestMovie Objekt"); // BDSM-Matrix
        movie.setYear(2024);
        movie.addGenre(genre);
        movie.addGenre(genre1);
        movie.addGenre(genre2);
        movie.addGenre(genre3);
        movie.addMovieCharacter(character);
        movie.addMovieCharacter(character1);
        movie.addMovieCharacter(character2);
        movie.addMovieCharacter(character3);
        movie.setTicketsSold(9000);

        return movie;
    }
}
