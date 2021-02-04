package com.example.tin_rekry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.InvalidNameException;
import java.util.List;

@RestController
public class Controller {
    @Autowired
    DataService dataService;

    @PostMapping("/create")
    public ResponseEntity<Thing> Create(@RequestBody Thing thing) {
        try {
            dataService.AddToDb(thing);
        } catch (InvalidNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(thing);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Thing>> Search(@RequestBody Thing thing) {
        return ResponseEntity.ok(dataService.SearchDb(thing.getName()));
    }

    @PostMapping("/update")
    public ResponseEntity<Thing> Update(@RequestBody Thing thing) {
        try {
            dataService.UpdateThing(thing);
        } catch (InvalidNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (NoSuchFieldException e2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(thing);
    }

    @DeleteMapping("/delete")
    public ResponseEntity Delete(@RequestBody Thing thing) {
        System.out.println(thing.getId());
        try {
            dataService.DeleteThing(thing.getId());
        } catch (NoSuchFieldException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok().body(null);
    }



}
