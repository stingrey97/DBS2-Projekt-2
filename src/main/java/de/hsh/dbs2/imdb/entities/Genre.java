package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "joined_table_genre")
public class Genre {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "genre")
    private String genre;

    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

    public void addMovie(Movie movie) {
        this.movies.add(movie);
        if (!movie.getGenres().contains(this)) {
            movie.addGenre(this);
        }
    }

    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
        if (movie.getGenres().contains(this)) {
            movie.removeGenre(this);
        }
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public Set<Movie> getMovies() {
        return movies;
    }
}
