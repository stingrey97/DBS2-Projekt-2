package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_table_movie_cinema")
public class CinemaMovie extends Movie {

    @Column(name = "tickets_sold")
    private int ticketsSold;

    public CinemaMovie() {
        super('C');
    }

    public void setTicketsSold(int ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }
}
