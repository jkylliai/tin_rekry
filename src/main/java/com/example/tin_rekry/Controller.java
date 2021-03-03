package com.example.tin_rekry;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InvalidNameException;
import java.util.List;

@RestController
public class Controller {
    private final DataService dataService;

    public Controller(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/create")
    public ResponseEntity<Thing> create(@RequestBody Thing thing) {
        try {
            dataService.addToDb(thing);
        } catch (InvalidNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(thing);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Thing>> search(@RequestBody Thing thing) {
        return ResponseEntity.ok(dataService.searchDb(thing.getName()));
    }

    @PostMapping("/update")
    public ResponseEntity<Thing> update(@RequestBody Thing thing) {
        try {
            return ResponseEntity.ok(dataService.updateThing(thing));
        } catch (InvalidNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (NoSuchFieldException e2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity delete(@RequestBody Thing thing) {
        try {
            dataService.deleteThing(thing.getId());
        } catch (NoSuchFieldException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok().body(null);
    }



}
