package com.example.taskmanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${google.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<String> getTaskBreakdown(String taskTitle) {
        String prompt = "Break down the following task into 3-5 actionable subtasks. Return ONLY a valid JSON array of strings. Task: " + taskTitle;

        try {
            // Build the Gemini request payload
            String requestBody = objectMapper.writeValueAsString(
                Map.of(
                    "contents", List.of(
                        Map.of("parts", List.of(
                            Map.of("text", prompt)
                        ))
                    ),
                    "generationConfig", Map.of(
                        "temperature", 0.3 // Low temperature for consistent JSON formatting
                    )
                )
            );

            // Construct the HTTP POST request using the active gemini-2.5-flash model
            String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Execute the request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to communicate with Gemini API. Status: " + response.statusCode() + " Body: " + response.body());
            }

            // Parse the response from Gemini
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode candidates = rootNode.path("candidates");
            
            if (candidates.isMissingNode() || !candidates.isArray() || candidates.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid response structure from Gemini API");
            }

            // Navigate the Gemini response tree: candidates[0].content.parts[0].text
            String contentText = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Clean up potential markdown code blocks (e.g., ```json ... ```)
            contentText = contentText.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();

            // The 'contentText' should now be a clean JSON array string. Parse it into a List.
            JsonNode arrayNode = objectMapper.readTree(contentText);
            List<String> subtasks = new ArrayList<>();
            if (arrayNode.isArray()) {
                for (JsonNode node : arrayNode) {
                    subtasks.add(node.asText());
                }
            }
            return subtasks;

        } catch (ResponseStatusException e) {
            throw e; // Re-throw our explicit API errors
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating task breakdown from AI", e);
        }
    }
}