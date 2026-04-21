package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        taskRepository.deleteAll();
    }

    @Test
    void crudOperationsEndToEndTest() throws Exception {
        // 1. CREATE (POST)
        Task newTask = new Task();
        newTask.setTitle("Integration Test Task");
        newTask.setDescription("Testing End to End");

        String responseJson = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Task createdTask = objectMapper.readValue(responseJson, Task.class);
        Long taskId = createdTask.getId();

        // 2. READ ALL (GET)
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Integration Test Task")));

        // 3. READ SINGLE (GET)
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Integration Test Task")));

        // 4. UPDATE (PUT)
        createdTask.setTitle("Updated Integration Task");
        mockMvc.perform(put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Task")));

        // 5. DELETE
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // Verify Deletion
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }
}