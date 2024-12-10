package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "joined_table_person")
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private char sex;

    @OneToMany(mappedBy = "person", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<MovieCharacter> movieCharacters = new HashSet<>();

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public void addMovieCharacter(MovieCharacter movieCharacter) {
        this.movieCharacters.add(movieCharacter);
        movieCharacter.setPerson(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public char getSex() {
        return sex;
    }

    public Set<MovieCharacter> getMovieCharacters() {
        return movieCharacters;
    }
}
