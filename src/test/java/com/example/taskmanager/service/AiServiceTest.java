package com.example.taskmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AiServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        // Initialize the service with a real ObjectMapper but a mocked HttpClient
        aiService = new AiService(new ObjectMapper());
        ReflectionTestUtils.setField(aiService, "httpClient", mockHttpClient);
        ReflectionTestUtils.setField(aiService, "apiKey", "test-dummy-key");
    }

    @Test
    void getTaskBreakdown_ShouldParseGeminiResponse() throws Exception {
        // Construct a realistic mock response matching Gemini's payload structure
        String mockResponseBody = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "```json\\n[\\"Subtask 1\\", \\"Subtask 2\\", \\"Subtask 3\\"]\\n```"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Execute the service call
        List<String> subtasks = aiService.getTaskBreakdown("Test Task");

        // Assertions
        assertNotNull(subtasks);
        assertEquals(3, subtasks.size());
        assertEquals("Subtask 1", subtasks.get(0));
        assertEquals("Subtask 3", subtasks.get(2));
    }
}