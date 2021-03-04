package com.example.tin_rekry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.InvalidNameException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DataServiceTest {

    private DataService dataService;

    @Autowired
    private ThingRepository repo;

    @Before
    public void init() {
        dataService = new DataService(repo);
    }

    @After
    public void dropAll() {
        repo.deleteAll();
    }

    @Test
    public void addToDbTest() {
        try {
            dataService.addToDb(new Thing("test1", 1));
            dataService.addToDb(new Thing("test2", 2));
            dataService.addToDb(new Thing("test3", 3));
            dataService.addToDb(new Thing("test4", 4));
        }
        catch (InvalidNameException e) {
            fail("Invalid name!");
        }

        int i = 0;
        for (Thing t : repo.findAll()) {
            i++;
            try {
                assertTrue(t.getName().equals("test"+i));
            }
            catch (AssertionError e) {
                System.out.println("Thing id was: "+ t.getId());
                System.out.println("i was: " + i);
                fail();
            }
        }
        assertTrue(i == 4);
    }

    @Test
    public void addToDbhNullName() {
        try {
            dataService.addToDb(new Thing(null, 1));
            fail("null name not caught!");
        }
        catch (InvalidNameException e) { }
    }

    @Test
    public void addToDbTooShortName() {
        try {
            dataService.addToDb(new Thing("moi", 1));
            fail("name with length < 3 not caught!");
        }
        catch (InvalidNameException e) { }
    }

    @Test
    public void addToDbTooLongName() {
        try {
            dataService.addToDb(new Thing("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 1));
            fail("name with length > 64 not caught!");
        }
        catch (InvalidNameException e) { }
    }

    @Test
    public void updateThingTest() {
        final String newName = "new name";

        Thing savedThing = repo.save(new Thing("old name",1));

        try {
            dataService.updateThing(new Thing(newName, (int) savedThing.getId()));
        } catch (NoSuchFieldException e) {
            fail("No item with that id in DB");
        } catch (InvalidNameException e) {
            fail("Invalid name");
        }

        Optional<Thing> optional = repo.findById(savedThing.getId());

        assertTrue(optional.get().getName().equals(newName));
    }

    @Test
    public void updateThingInvalidId() {
        final String newName = "new name";

        repo.save(new Thing("old name",1));

        try {
            dataService.updateThing(new Thing(newName,123));
            fail("Did not throw exception");
        } catch (NoSuchFieldException e) {
            //Pass
        } catch (InvalidNameException e) {
            fail("Invalid name");
        }
    }

    @Test
    public void updateThingInvalidName() {
        final String newName = "new";

        repo.save(new Thing("old name",1));

        try {
            dataService.updateThing(new Thing(newName,1));
            fail("Did not throw exception");
        } catch (NoSuchFieldException e) {
            fail("No item with that id in DB");
        } catch (InvalidNameException e) {

        }
    }

    @Test
    public void deleteThingTest() {
        Thing savedThing = repo.save(new Thing("test",1));
        assertTrue(repo.findById(savedThing.getId()).isPresent());
        try {
            dataService.deleteThing(savedThing.getId());
        } catch (NoSuchFieldException e) {
            fail("No item with that id in DB");
        }
        assertTrue(!repo.findById(savedThing.getId()).isPresent());
    }

    @Test
    public void deleteThingInvalidId() {
        Thing savedThing = repo.save(new Thing("test",1));
        try {
            dataService.deleteThing(savedThing.getId()+1);
            fail("Did not throw exception");
        } catch (NoSuchFieldException e) {

        }
    }

    @Test
    public void searchDbTest() {
        Thing[] savedThings = {
                repo.save(new Thing("hello", 1)),
                repo.save(new Thing("hellohello", 2)),
                repo.save(new Thing("hellohellohello", 3)),
                repo.save(new Thing("world", 4))
        };

        List<Thing> things = dataService.searchDb("hello");
        assertTrue(things.size() == 3);

        for (int i = 0; i < 3; i++) {
            assertTrue(things.get(i).getName().equals(savedThings[i].getName()));
            assertTrue(things.get(i).getId() == savedThings[i].getId());
        }

        things = dataService.searchDb("world");
        assertTrue(things.size() == 1
                && things.get(0).getId() == savedThings[3].getId()
                && things.get(0).getName().equals(savedThings[3].getName()));
    }
}
