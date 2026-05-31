package com.elearning.service;

import com.elearning.dto.ChatPreviewDTO;
import com.elearning.enums.Department;
import com.elearning.model.ChatConversation;
import com.elearning.model.ChatConversation.ChatMessage;
import com.elearning.model.ChatConversation.Sender;
import com.elearning.repository.ChatConversationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatService implements ServiceInterface<ChatConversation> {

    private final ChatConversationRepository repo;
    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String openAiApiKey;

    public ChatService(ChatConversationRepository repo) {
        this.repo = repo;
        this.restClient = RestClient.create("https://api.openai.com");
    }

    // ── CRUD (ServiceInterface) ──────────────────────────────────

    @Override
    public List<ChatConversation> getAll() {
        return repo.findAll();
    }

    @Override
    public Optional<ChatConversation> getById(String id) {
        return repo.findById(id);
    }

    @Override
    public ChatConversation create(ChatConversation conversation) {
        return repo.save(conversation);
    }

    @Override
    public Optional<ChatConversation> update(String id, ChatConversation conversation) {
        return repo.findById(id).map(existing -> {
            if (conversation.getTitle() != null) existing.setTitle(conversation.getTitle());
            if (conversation.getSubject() != null) existing.setSubject(conversation.getSubject());
            if (conversation.getMessages() != null) existing.setMessages(conversation.getMessages());
            return repo.save(existing);
        });
    }

    @Override
    public Optional<ChatConversation> replace(String id, ChatConversation conversation) {
        if (repo.existsById(id)) {
            conversation.setConversationId(id);
            return Optional.of(repo.save(conversation));
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // ── Custom repository queries ────────────────────────────────

    public List<ChatConversation> getByPersonId(String personId) {
        return repo.findByPersonId(personId);
    }

    public List<ChatConversation> getByPersonIdAndSubject(String personId, Department subject) {
        return repo.findByPersonIdAndSubject(personId, subject);
    }

    public List<ChatPreviewDTO> getAllPreviewsByPersonId(String personId) {
        return repo.findAllPreviewsByPersonId(personId).stream()
                .map(ChatPreviewDTO::new)
                .toList();
    }

    public Optional<ChatConversation> rename(String id, String title) {
        long modified = repo.renameById(id, title);
        if (modified == 0) return Optional.empty();
        return repo.findById(id);
    }

    // ── Chat operations ──────────────────────────────────────────

    /**
     * Starts a new conversation.
     * 1. Calls OpenAI to generate a 2-6 word title summarizing the conversation.
     * 2. Calls OpenAI to get an agent response to the user's prompt.
     * 3. Saves and returns the initialized ChatConversation.
     */
    public ChatConversation start(String personId, Department subject, String systemMessage, String userMessage, LocalDateTime userTimestamp) {

        // Generate a short title
        String title = callOpenAi(
                "Generate a concise 2-6 word title that summarizes this conversation. " +
                "Return ONLY the title text, nothing else.",
                "System context: " + systemMessage + "\nUser message: " + userMessage
        );

        // Get the agent's actual response
        String agentReply = callOpenAi(systemMessage, userMessage);

        LocalDateTime agentTime = LocalDateTime.now();

        ArrayList<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(systemMessage, Sender.SYSTEM, userTimestamp));
        messages.add(new ChatMessage(userMessage, Sender.USER, userTimestamp));
        messages.add(new ChatMessage(agentReply, Sender.AGENT, agentTime));

        ChatConversation conversation = ChatConversation.builder()
                .personId(personId)
                .title(title)
                .subject(subject)
                .messages(messages)
                .build();

        return repo.save(conversation);
    }

    /**
     * Adds a user message to an existing conversation, calls OpenAI for a response,
     * appends both to the message list, and saves.
     */
    public Optional<ChatConversation> message(String conversationId, String userMessage, LocalDateTime userTimestamp) {
        return repo.findById(conversationId).map(conversation -> {
            conversation.getMessages().add(new ChatMessage(userMessage, Sender.USER, userTimestamp));

            // Build context from conversation history
            String context = buildContext(conversation);
            String agentReply = callOpenAi(context, userMessage);

            conversation.getMessages().add(new ChatMessage(agentReply, Sender.AGENT, LocalDateTime.now()));
            return repo.save(conversation);
        });
    }

    // ── OpenAI helpers ───────────────────────────────────────────

    private String callOpenAi(String systemPrompt, String userPrompt) {
        List<Map<String, String>> input = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "input", input
        );

        String json = restClient.post()
                .uri("/v1/responses")
                .header("Authorization", "Bearer " + openAiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return extractResponseText(json);
    }

    private String extractResponseText(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            return root.path("output")
                    .get(0)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText("");
        } catch (Exception e) {
            return "I'm sorry, I couldn't generate a response.";
        }
    }

    private String buildContext(ChatConversation conversation) {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage msg : conversation.getMessages()) {
            sb.append(msg.getFrom().name()).append(": ").append(msg.getMessage()).append("\n");
        }
        return sb.toString();
    }
}
