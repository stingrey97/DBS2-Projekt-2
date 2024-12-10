package de.hsh.dbs2.imdb.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            TypedQuery<Movie> q = em.createQuery("SELECT m FROM Movie m WHERE m.title LIKE :search", Movie.class);
            q.setParameter("search", "%" + search + "%");
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

            Movie movie = null;

            if (movieDTO.getId() == null) {
                movie = new Movie();
            } else {
                movie = em.find(Movie.class, movieDTO.getId());
            }

            movie.setTitle(movieDTO.getTitle());
            movie.setYear(movieDTO.getYear());
            movie.setType(movieDTO.getType().charAt(0));

            // Update Genres
            movie.getGenres().clear(); // Clear existing genres
            for (String genreName : movieDTO.getGenres()) {
                Genre genre = em.createQuery("SELECT g FROM Genre g WHERE g.genre = :genre", Genre.class)
                        .setParameter("genre", genreName)
                        .getResultStream()
                        .findFirst()
                        .orElse(new Genre(genreName, movie)); // Create new Genre if not exists
                movie.getGenres().add(genre);
            }

            // Update Characters
            movie.getMovieCharacters().clear(); // Clear existing characters
            for (CharacterDTO characterDTO : movieDTO.getCharacters()) {
                MovieCharacter character = new MovieCharacter();
                character.setCharacter(characterDTO.getCharacter());
                character.setAlias(characterDTO.getAlias());

                // Fetch or create Person
                Person person = em.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class)
                        .setParameter("name", characterDTO.getPlayer())
                        .getResultStream()
                        .findFirst()
                        .orElse(new Person(characterDTO.getPlayer()));
                character.setPerson(person);

                character.setMovie(movie);
                movie.getMovieCharacters().add(character);

                em.persist(movie);
            }

            // Persist or merge the Movie entity
            if (movie.getId() == null) {
                em.persist(movie);
            } else {
                em.merge(movie);
            }
            em.getTransaction().commit();

        } finally {
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

            Movie movie = em.find(Movie.class, movieId);
            if (movie != null) em.remove(movie);

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

            Set<String> genreStrings = new HashSet<>();
            for (Genre genre : movie.getGenres()) {
                genreStrings.add(genre.getGenre());
            }

            List<CharacterDTO> characterDTOs = new ArrayList<>();
            for (MovieCharacter character : movie.getMovieCharacters()) {
                characterDTOs.add(new CharacterDTO(character.getCharacter(), character.getAlias(), character.getPerson().getName()));
            }

            movieDTO = new MovieDTO(movie.getId(), movie.getTitle(), "" + movie.getType(), movie.getYear(), genreStrings, characterDTOs);
        } finally {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            HibernateConnection.close();
        }
        return movieDTO;
    }
}
