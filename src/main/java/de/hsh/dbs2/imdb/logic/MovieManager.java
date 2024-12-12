package de.hsh.dbs2.imdb.logic;

import java.util.*;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.entities.Movie;
import de.hsh.dbs2.imdb.entities.MovieCharacter;
import de.hsh.dbs2.imdb.entities.Person;
import de.hsh.dbs2.imdb.logic.dto.*;
import de.hsh.dbs2.imdb.util.HibernateConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class MovieManager {

    /**
     * Ermittelt alle Filme, deren Filmtitel den Suchstring enthält.
     * Wenn der String leer ist, sollen alle Filme zurückgegeben werden.
     * Der Suchstring soll ohne Rücksicht auf Gross- und Kleinschreibung verarbeitet werden.
     *
     * @param search Suchstring.
     * @return Liste aller passenden Filme als MovieDTO
     */
    public List<MovieDTO> getMovieList(String search) {
        EntityManager em = HibernateConnection.get();
        List<Movie> movies;
        List<MovieDTO> movieDTOs = new ArrayList<>();
        try {
            em.getTransaction().begin();
            TypedQuery<Movie> q = em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE :search", Movie.class);
            q.setParameter("search", "%" + search.toLowerCase() + "%");
            movies = q.getResultList();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }

        for (Movie movie : movies) {
            Set<String> genreStrings = new HashSet<>();
            for (Genre genre : movie.getGenres()) {
                genreStrings.add(genre.getGenre());
            }

            List<CharacterDTO> characterDTOs = new ArrayList<>();
            for (MovieCharacter character : movie.getMovieCharacters()) {
                characterDTOs.add(new CharacterDTO(character.getCharacter(), character.getAlias(), character.getPerson().getName()));
            }

            movieDTOs.add(new MovieDTO(movie.getId(), movie.getTitle(), "" + movie.getType(), movie.getYear(), genreStrings, characterDTOs));
        }
        return movieDTOs;
    }

    /**
     * Speichert die übergebene Version des Films neu in der Datenbank oder aktualisiert den
     * existierenden Film.
     * Dazu werden die Daten des Films selbst (Titel, Jahr, Typ) berücksichtigt,
     * aber auch alle Genres, die dem Film zugeordnet sind und die Liste der Charaktere
     * auf den neuen Stand gebracht.
     *
     * @param movieDTO Film-Objekt mit Genres und Charakteren.
     */
    public void insertUpdateMovie(MovieDTO movieDTO) {
        EntityManager em = HibernateConnection.get();
        try {
            em.getTransaction().begin();

            Movie movie;
            // Movie existiert in DB
            if (movieDTO.getId() != null) {
                movie = em.find(Movie.class, movieDTO.getId());

                // Movie komplett leeren
                movie.getMovieCharacters().clear();
                for (Genre genre : movie.getGenres()) {
                    genre.getMovies().remove(movie);
                }
                movie.getGenres().clear();

                // Movie existiert noch nicht in DB
            } else {
                movie = new Movie();
            }

            // Einfache Informationen im Movie speichern
            movie.setTitle(movieDTO.getTitle());
            movie.setYear(movieDTO.getYear());
            movie.setType(movieDTO.getType().charAt(0));

            // Genres im Movie speichern:

            // Alle Genres holen
            List<Genre> allGenres = em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
            Map<String, Genre> knownGenres = new HashMap<>();
            for (Genre genre : allGenres) {
                knownGenres.put(genre.getGenre(), genre);
            }

            // Für alle Genres des DTO Objekts
            for (String dtoGenreString : movieDTO.getGenres()) {
                // Wenn Genre noch nicht in DB: In DB einfügen und Movie verknüpfen
                if (!knownGenres.containsKey(dtoGenreString)) {
                    Genre newGenre = new Genre();
                    newGenre.setGenre(dtoGenreString);
                    newGenre.addMovie(movie);
                    em.persist(newGenre);
                    // Zu bestehenden Genre das Movie hinzufügen
                } else {
                    knownGenres.get(dtoGenreString).addMovie(movie);
                }
            }

            // MovieCharacters im Movie speichern
            List<CharacterDTO> characterDTOs = movieDTO.getCharacters();
            // Für alle Characters des DTO Objekts
            for (CharacterDTO characterDTO : characterDTOs) {
                // Erzeuge neuen MovieCharacter, da diese nie vorher existieren (gelöscht)
                MovieCharacter movieCharacter = new MovieCharacter();
                movieCharacter.setMovie(movie);
                movieCharacter.setAlias(characterDTO.getAlias());
                movieCharacter.setCharacter(characterDTO.getCharacter());
                em.persist(movie);

                // Gibt es die Person / Player schon?
                TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class);
                personQuery.setParameter("name", characterDTO.getPlayer());
                Person person = personQuery.getSingleResult();
                // Wenn nein, neue Person anlegen
                if (person == null) {
                    person = new Person();
                    person.setName(characterDTO.getPlayer());
                    person.setSex('?');
                }
                // Und zum MovieCharacter adden
                person.addMovieCharacter(movieCharacter);
            }

            em.persist(movie);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }
    }

    /**
     * Löscht einen Film aus der Datenbank. Es werden auch alle abhängigen Objekte gelöscht,
     * d.h. alle Charaktere und alle Genre-Zuordnungen.
     *
     * @param movieId Die ID des entsprechenden Films.
     */
    public void deleteMovie(long movieId) {
        EntityManager em = HibernateConnection.get();
        try {
            em.getTransaction().begin();

            // Lade das Movie-Objekt
            Movie movie = em.find(Movie.class, movieId);

            if (movie != null) {
                // Entferne die Verknüpfungen zu Genres
                for (Genre genre : movie.getGenres()) {
                    genre.getMovies().remove(movie); // Entfernt Movie aus Genre
                }
                movie.getGenres().clear(); // Leert die Genres-Collection im Movie

                // Entferne das Movie-Objekt (orphanRemoval sorgt für Löschen der MovieCharacters)
                em.remove(movie);
            } else {
                System.out.println("Movie mit ID " + movieId + " existiert nicht.");
            }

            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }
    }

    /**
     * Liefert die Daten eines einzelnen Movies zurück
     *
     * @param movieId Die ID des gesuchten Films.
     * @return Das gesuchte MovieDTO Objekt
     */
    public MovieDTO getMovie(long movieId) {
        EntityManager em = HibernateConnection.get();
        MovieDTO movieDTO;
        try {
            em.getTransaction().begin();

            Movie movie = em.find(Movie.class, movieId);

            if (movie == null) return null;

            Set<String> genreStrings = new HashSet<>();

            for (Genre genre : movie.getGenres()) {
                genreStrings.add(genre.getGenre());
            }

            List<CharacterDTO> characterDTOs = new ArrayList<>();
            for (MovieCharacter character : movie.getMovieCharacters()) {
                characterDTOs.add(new CharacterDTO(character.getCharacter(), character.getAlias(), character.getPerson().getName()));
            }

            movieDTO = new MovieDTO(movie.getId(), movie.getTitle(), "" + movie.getType(), movie.getYear(), genreStrings, characterDTOs);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }
        return movieDTO;
    }
}
