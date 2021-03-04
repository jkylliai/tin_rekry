package com.example.tin_rekry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.InvalidNameException;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DataService dataService;

    private ArgumentCaptor<Thing> argumentCaptor = ArgumentCaptor.forClass(Thing.class);

    @Test
    public void createTestReturn200() throws Exception {
        mvc.perform(post("/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"test\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is("test")))
            .andExpect(jsonPath("id", is(0)))
            .andExpect(jsonPath("creationTime",containsString(LocalDate.now().toString())));

        verify(dataService).addToDb(argumentCaptor.capture());
        assertEquals("test", argumentCaptor.getValue().getName());
    }

    @Test
    public void createTestReturn400() throws Exception {
        doThrow(new InvalidNameException()).when(dataService).addToDb(any(Thing.class));
        mvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"test\"}")).andExpect(status().isBadRequest());
    }

    @Test
    public void searchTest() throws Exception {
        Thing t1 = new Thing("test1",0);
        Thing t2 = new Thing("test2",1);
        List<Thing> things = new ArrayList<Thing>();
        things.add(t1);
        things.add(t2);

        given(dataService.searchDb(eq("test"))).willReturn(things);

        mvc.perform(post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("test1")))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].creationTime", containsString(LocalDate.now().toString())))
                .andExpect(jsonPath("$[1].name", is("test2")))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].creationTime", containsString(LocalDate.now().toString())));
    }

    @Test
    public void updateTestReturn200() throws Exception {
        Thing thing = new Thing("newName",1);
        given(dataService.updateThing(any(Thing.class))).willReturn(thing);

        mvc.perform(post("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"newName\",\"id\": \"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("newName")))
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("creationTime",containsString(thing.getCreationTime().toString().replaceFirst("\\.0*$|(\\.\\d*?)0+$", "")))); //regex removes trailing zeros

        verify(dataService).updateThing(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().getId());
        assertEquals("newName", argumentCaptor.getValue().getName());
    }

    @Test
    public void updateTestReturn400() throws Exception {
        doThrow(new InvalidNameException()).when(dataService).updateThing(any(Thing.class));

        mvc.perform(post("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"newName\",\"id\": \"1\"}"))
                .andExpect(status().isBadRequest());

        verify(dataService).updateThing(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().getId());
        assertEquals("newName", argumentCaptor.getValue().getName());
    }

    @Test
    public void updateTestReturn404() throws Exception {
        doThrow(new NoSuchFieldException()).when(dataService).updateThing(any(Thing.class));

        mvc.perform(post("/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"newName\",\"id\": \"1\"}"))
                .andExpect(status().isNotFound());

        verify(dataService).updateThing(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().getId());
        assertEquals("newName", argumentCaptor.getValue().getName());
    }

    @Test
    public void deleteReturn200() throws Exception {
        mvc.perform(delete("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"1\"}"))
                .andExpect(status().isOk());

        verify(dataService).deleteThing(1);
    }

    @Test
    public void deleteReturn404() throws Exception {
        doThrow(new NoSuchFieldException()).when(dataService).deleteThing(1);

        mvc.perform(delete("/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"1\"}"))
                .andExpect(status().isNotFound());

        verify(dataService).deleteThing(1);
    }
}
