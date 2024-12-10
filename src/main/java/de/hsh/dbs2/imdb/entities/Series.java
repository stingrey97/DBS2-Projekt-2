package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_table_movie_series")
public class Series extends Movie {

    @Column(name = "num_of_episodes")
    private int numOfEpisodes;

    public Series() {
        super('S');
    }

    public void setNumOfEpisodes(int numOfEpisodes) {
        this.numOfEpisodes = numOfEpisodes;
    }

    public int getNumOfEpisodes() {
        return numOfEpisodes;
    }
}
