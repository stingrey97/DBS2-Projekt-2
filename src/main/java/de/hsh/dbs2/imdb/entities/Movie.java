package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "joined_table_movie")
@Inheritance(strategy = InheritanceType.JOINED)
public class Movie {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "year")
    private int year;

    @Column(name = "type")
    private char type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "joined_table_has_genre",
            joinColumns = @JoinColumn(name = "movies_id"),
            inverseJoinColumns = @JoinColumn(name = "genres_id")
    )
    private final Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<MovieCharacter> movieCharacters = new HashSet<>();

    public Movie() {
    }

    public Movie(char type) {
        this.type = type;
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
        if (!genre.getMovies().contains(this)) {
            genre.addMovie(this);
        }
    }

    public void removeGenre(Genre genre) {
        this.genres.remove(genre);
        if (genre.getMovies().contains(this)) {
            genre.removeMovie(this);
        }
    }

    public void addMovieCharacter(MovieCharacter movieCharacter) {
        this.movieCharacters.add(movieCharacter);
        movieCharacter.setMovie(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public char getType() {
        return type;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public Set<MovieCharacter> getMovieCharacters() {
        return movieCharacters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
