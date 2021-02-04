package com.example.tin_rekry;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="things")
public class Thing {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private LocalDateTime creationTime;

    @JsonCreator
    public Thing(String name, int id) {
        this.name = name;
        this.id = id;
        creationTime = LocalDateTime.now();
    }

    public Thing() {};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public long getId() {
        return id;
    }
}
