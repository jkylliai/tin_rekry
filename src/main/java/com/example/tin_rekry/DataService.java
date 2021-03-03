package com.example.tin_rekry;

import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataService {
    private final ThingRepository repo;

    public DataService(ThingRepository repo) {
        this.repo = repo;
    }

    public void addToDb(Thing thing) throws InvalidNameException {
        if (nameIsInvalid(thing.getName())) {
            throw new InvalidNameException();
        }
        thing.setName(thing.getName().toLowerCase());
        repo.save(thing);
    }

    private boolean nameIsInvalid(String name) {
        if (name == null) {
            return true;
        }
        return name.length() < 4 || name.length() > 64;
    }


    public Thing updateThing(Thing thing) throws NoSuchFieldException, InvalidNameException {
        if (nameIsInvalid(thing.getName())) {
            throw new InvalidNameException();
        }
        Optional<Thing> optional = repo.findById(thing.getId());
        if (!optional.isPresent()) {
            throw new NoSuchFieldException();
        }
        Thing thingInDB = optional.get();
        thingInDB.setName(thing.getName().toLowerCase());
        return repo.save(thingInDB);
    }

    public void deleteThing(long id) throws NoSuchFieldException {
        Optional<Thing> optional = repo.findById(id);
        if (!optional.isPresent()) {
            throw new NoSuchFieldException();
        }
        repo.deleteById(id);
    }

    public List<Thing> searchDb(String name) {
        name = name.toLowerCase();
        Iterable<Thing> q = repo.findByName("%" + name + "%");
        List<Thing> things = new ArrayList<Thing>();

        for (Thing t : q) {
            things.add(t);
        }

        return things;

    }
}
