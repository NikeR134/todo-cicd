package ru.netology.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void createsAndListsTasks() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Buy milk","description":"2 liters"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Buy milk")))
                .andExpect(jsonPath("$.completed", is(false)));
        mvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Buy milk")));
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"   "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatesExistingTask() throws Exception {
        long id = createTask("First");
        mvc.perform(put("/tasks/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated","description":"Done soon","completed":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated")))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingTask() throws Exception {
        mvc.perform(put("/tasks/99999").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Missing"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesExistingTask() throws Exception {
        long id = createTask("Remove");
        mvc.perform(delete("/tasks/" + id)).andExpect(status().isNoContent());
        mvc.perform(delete("/tasks/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenDeletingMissingTask() throws Exception {
        mvc.perform(delete("/tasks/99999")).andExpect(status().isNotFound());
    }

    private long createTask(String title) throws Exception {
        String response = mvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"%s"}
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
    }
}




