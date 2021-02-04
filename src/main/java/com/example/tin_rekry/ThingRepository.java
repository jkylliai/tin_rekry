package com.example.tin_rekry;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThingRepository extends CrudRepository<Thing, Long> {

    @Query(
            value = "SELECT * FROM THINGS WHERE NAME LIKE ?1",
            nativeQuery = true
    )
    Iterable<Thing> findByName(String name);
}
