package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "class_table_movie_character")
public class MovieCharacter {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "character")
    private String character;

    @Column(name = "alias")
    private String alias;

    @Column(name = "position")
    private int position;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Movie movie;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Person person;

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (!movie.getMovieCharacters().contains(this)) {
            movie.addMovieCharacter(this);
        }
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public Long getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public Movie getMovie() {
        return movie;
    }

    public Person getPerson() {
        return person;
    }

    public String getAlias() {
        return alias;
    }

    public String getCharacter() {
        return character;
    }
}
