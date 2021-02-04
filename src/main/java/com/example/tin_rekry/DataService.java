package com.example.tin_rekry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataService {
    @Autowired
    private ThingRepository repo;

    public void AddToDb(Thing thing) throws InvalidNameException {
        if (NameIsInvalid(thing.getName())) {
            throw new InvalidNameException();
        }
        thing.setName(thing.getName().toLowerCase());
        repo.save(thing);
    }

    private boolean NameIsInvalid(String name) {
        if (name == null) {
            return true;
        }
        return name.length() < 4 || name.length() > 64;
    }


    public void UpdateThing(Thing thing) throws NoSuchFieldException, InvalidNameException {
        if (NameIsInvalid(thing.getName())) {
            throw new InvalidNameException();
        }
        Optional<Thing> optional = repo.findById(thing.getId());
        if (!optional.isPresent()) {
            throw new NoSuchFieldException();
        }
        Thing thingInDB = optional.get();
        thingInDB.setName(thing.getName().toLowerCase());
        repo.save(thingInDB);
    }

    public void DeleteThing(long id) throws NoSuchFieldException {
        Optional<Thing> optional = repo.findById(id);
        if (!optional.isPresent()) {
            throw new NoSuchFieldException();
        }
        repo.deleteById(id);
    }

    public List<Thing> SearchDb(String name) {
        name = name.toLowerCase();
        Iterable<Thing> q = repo.findByName("%" + name + "%");
        List<Thing> things = new ArrayList<Thing>();

        for (Thing t : q) {
            things.add(t);
        }

        return things;

    }
}
