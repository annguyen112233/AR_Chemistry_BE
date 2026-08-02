package com.chemistry.demo.services.ai.Impl;

import com.chemistry.demo.dto.ai.AiChatRequest;
import com.chemistry.demo.dto.ai.AiChatResponse;
import com.chemistry.demo.dto.ai.MemoryMatchResult;
import com.chemistry.demo.dto.response.ai.ConversationDetailResponse;
import com.chemistry.demo.dto.response.ai.ConversationResponse;
import com.chemistry.demo.entity.Conversation;
import com.chemistry.demo.entity.ConversationMessage;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeatureCode;
import com.chemistry.demo.enums.MessageRole;
import com.chemistry.demo.exception.AiErrorCode;
import com.chemistry.demo.exception.AppErrorCode;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.mapper.ConversationMapper;
import com.chemistry.demo.repository.ConversationMessageRepository;
import com.chemistry.demo.repository.ConversationRepository;
import com.chemistry.demo.dto.ai.RetrievedChunk;
import com.chemistry.demo.services.ai.AiChatService;
import com.chemistry.demo.services.ai.ConversationMemoryService;
import com.chemistry.demo.services.ai.EmbeddingService;
import com.chemistry.demo.services.ai.KnowledgeRetrievalService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;
    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final ConversationMapper conversationMapper;
    private final SecurityUtils securityUtils;
    private final ConversationMemoryService memoryService;
    private final EmbeddingService embeddingService;
    private final KnowledgeRetrievalService knowledgeRetrievalService;

    /**
     * Chuỗi model dự phòng, thử theo đúng thứ tự khai báo: các model Gemini trước,
     * rồi tới các model free khác. Khi một model hết quota / bị rate-limit / bị gỡ
     * khỏi OpenRouter thì tự động rơi xuống model kế tiếp.
     *
     * Mỗi phần tử phải là một model ID cụ thể. KHÔNG dùng alias auto-router
     * (ví dụ "openrouter/free") vì nó chọn ngẫu nhiên model mỗi request nên output
     * không ổn định và hay trộn chữ Hán/Hangul vào câu trả lời tiếng Việt.
     */
    @Value("${app.ai.chat-models}")
    private List<String> chatModels;

    // Cấu hình retrieval cho RAG
    private static final int RAG_TOP_K = 4;
    private static final double RAG_MIN_SCORE = 0.75;

    /**
     * Phần chung của mọi system prompt. Ràng buộc ngôn ngữ được đặt lên đầu và
     * nhắc lại ở cuối vì các model free hay code-switch sang tiếng Trung/Hàn khi
     * gặp thuật ngữ khoa học.
     */
    private static final String PROMPT_PREAMBLE = """
            Bạn là gia sư Hóa học của ứng dụng học tập AR Chemistry, hỗ trợ học sinh THPT Việt Nam.

            QUY TẮC NGÔN NGỮ (BẮT BUỘC — ưu tiên cao nhất):
            - Trả lời HOÀN TOÀN bằng tiếng Việt, dùng duy nhất bảng chữ cái Latinh có dấu tiếng Việt.
            - TUYỆT ĐỐI KHÔNG chèn chữ Hán, chữ Nhật, chữ Hàn, chữ Kirin hay bất kỳ hệ chữ nào khác.
            - KHÔNG chèn từ tiếng Anh khi tiếng Việt đã có từ tương đương.
              Ví dụ: viết "liên kết cộng hóa trị", KHÔNG viết "covalent bond".
            - Ngoại lệ duy nhất được phép giữ nguyên: ký hiệu nguyên tố và công thức
              hóa học (H₂O, NaCl, H₂SO₄), đơn vị đo (mol, gam, °C) và tên riêng khoa học.
            - Trước khi trả lời, hãy tự kiểm tra: nếu câu trả lời chứa ký tự ngoài
              bảng chữ cái tiếng Việt, hãy viết lại toàn bộ bằng tiếng Việt.

            CÁCH TRẢ LỜI:
            - Ngắn gọn, chính xác, đúng trình độ THPT. Tối đa khoảng 150 từ trừ khi được yêu cầu chi tiết hơn.
            - Dùng Markdown: in đậm cho thuật ngữ quan trọng, gạch đầu dòng khi liệt kê.
            - Viết công thức hóa học bằng ký tự chỉ số dưới Unicode: H₂O, CO₂, Ca(OH)₂, H₂SO₄.
              TUYỆT ĐỐI KHÔNG dùng LaTeX hay ký hiệu toán học: không viết $H_2O$, \\text{}, \\circ.
              Ứng dụng chỉ hiển thị Markdown thuần, mọi công thức LaTeX sẽ hiện ra dưới dạng
              mã nguồn thô và học sinh không đọc được.
            - Viết độ bằng ký tự °: "104,5°" chứ không phải "104.5^\\circ".
            - Chỉ xuất câu trả lời cuối cùng. KHÔNG viết ra quá trình suy luận,
              không mở đầu bằng "Tôi cần trả lời..." hay bất kỳ ghi chú nội bộ nào.
            - LUÔN kèm giải thích, đừng chỉ đưa mỗi công thức hay phương trình.
              Khi được hỏi về một phản ứng, phải nêu đủ: phương trình đã cân bằng,
              tên các chất tạo thành, và loại phản ứng. Tối thiểu 2–3 câu.
            - Nếu không chắc chắn hoặc câu hỏi vượt ngoài kiến thức Hóa học phổ thông,
              hãy nói rõ điều đó thay vì suy đoán.
            - Nếu câu hỏi không thuộc lĩnh vực Hóa học, lịch sự từ chối và gợi ý
              học sinh hỏi về Hóa học.
            """;

    private static final String LANGUAGE_REMINDER =
            "\nNhắc lại: toàn bộ câu trả lời phải bằng tiếng Việt, không chèn chữ Hán/Hàn/Nhật/Kirin.";

    private static final String BASE_SYSTEM_PROMPT =
            PROMPT_PREAMBLE
                    + "\nHãy trả lời dựa trên lịch sử hội thoại và kiến thức Hóa học phổ thông."
                    + LANGUAGE_REMINDER;

    /** Nhiệt độ thấp cho nội dung học thuật — ưu tiên chính xác hơn sáng tạo. */
    private static final double CHAT_TEMPERATURE = 0.2;

    /**
     * Ký tự thuộc các hệ chữ mà model free hay chèn nhầm (Hán, Kana, Hangul, Kirin).
     * Dùng để phát hiện câu trả lời hỏng trước khi hiển thị hoặc lưu vào memory.
     */
    private static final java.util.regex.Pattern FOREIGN_SCRIPT_PATTERN =
            java.util.regex.Pattern.compile(
                    "[\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\\p{IsHangul}\\p{IsCyrillic}]");

    @Override
    @Transactional
    public AiChatResponse chatWithAi(AiChatRequest request) {
        User currentUser = securityUtils.getCurrentUserCognitoSub();

        // ============================================================
        // STEP 0: Kiểm tra Memory - tìm câu hỏi tương tự đã hỏi trước đó
        // ============================================================
        try {
            Optional<MemoryMatchResult> memoryMatch = memoryService.findSimilarUserMessage(request.getMessage());
            if (memoryMatch.isPresent() && containsForeignScript(memoryMatch.get().getAnswer())) {
                // Câu trả lời hỏng đã lưu trước đây sẽ được phục vụ lại mãi mãi ở
                // similarity 100%. Bỏ qua cache và gọi lại model để tự chữa lành.
                log.warn("Cached answer for conversation {} contains foreign script, bypassing memory",
                        memoryMatch.get().getConversationId());
                memoryMatch = Optional.empty();
            }
            if (memoryMatch.isPresent()) {
                MemoryMatchResult match = memoryMatch.get();
                log.info("MEMORY REUSE: score={}, reusing answer from conversation={}",
                        String.format("%.4f", match.getSimilarityScore()), match.getConversationId());

                return AiChatResponse.builder()
                        .conversationId(match.getConversationId())
                        .answer(match.getAnswer())
                        .modelUsed("MEMORY_REUSE")
                        .success(true)
                        .timestamp(Instant.now())
                        .reusedMemory(true)
                        .similarityScore(match.getSimilarityScore())
                        .build();
            }
            log.info("No memory match found, proceeding to call AI model");
        } catch (Exception e) {
            log.warn("Memory search failed, falling back to AI model: {}", e.getMessage());
        }

        // ============================================================
        // STEP 1: Tạo hoặc lấy Conversation
        // ============================================================
        Conversation conversation = getOrCreateConversation(request, currentUser);

        // ============================================================
        // STEP 2: Lưu tin nhắn USER vào DB trước
        // ============================================================
        ConversationMessage userMessage = saveMessage(conversation, MessageRole.USER, request.getMessage(), null);

        // ============================================================
        // STEP 3: Gọi AI với lịch sử hội thoại
        // ============================================================
        String model = null;
        try {
            log.info("Calling AI for conversation: {}", conversation.getId());

            List<Message> messageContext = new ArrayList<>();
            List<ConversationMessage> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
            for (ConversationMessage msg : history) {
                if (msg.getRole() == MessageRole.USER) {
                    messageContext.add(new UserMessage(msg.getContent()));
                } else if (msg.getRole() == MessageRole.ASSISTANT) {
                    messageContext.add(new AssistantMessage(msg.getContent()));
                }
            }

            // ============================================================
            // STEP 3a: RAG - retrieve tri thức liên quan và grounding prompt
            // ============================================================
            String systemPrompt = buildGroundedSystemPrompt(request.getMessage());

            ModelAnswer generated = generateWithFallback(systemPrompt, messageContext);
            String answer = generated.answer();
            model = generated.model();

            // ============================================================
            // STEP 4: Lưu ASSISTANT message
            // ============================================================
            saveMessage(conversation, MessageRole.ASSISTANT, answer, model);

            // ============================================================
            // STEP 5: Sinh embedding cho USER message và lưu vào DB (async-safe)
            // ============================================================
            try {
                List<Double> embedding = embeddingService.embed(request.getMessage());
                if (!embedding.isEmpty()) {
                    String embeddingJson = memoryService.serializeEmbedding(embedding);
                    userMessage.setEmbeddingJson(embeddingJson);
                    messageRepository.save(userMessage);
                    log.info("Saved embedding for USER message: {}", userMessage.getId());
                }
            } catch (Exception e) {
                log.warn("Failed to save embedding, chat still succeeds: {}", e.getMessage());
            }

            // ============================================================
            // STEP 6: Cập nhật meta-data Conversation
            // ============================================================
            if (conversation.getTitle() == null || conversation.getTitle().isEmpty()) {
                conversation.setTitle(
                        request.getMessage().length() > 30
                                ? request.getMessage().substring(0, 30) + "..."
                                : request.getMessage()
                );
            }
            conversation.setModelUsed(model);
            conversation.setUpdatedAt(Instant.now());
            conversationRepository.save(conversation);

            return AiChatResponse.builder()
                    .conversationId(conversation.getId())
                    .answer(answer)
                    .modelUsed(model)
                    .success(true)
                    .timestamp(Instant.now())
                    .reusedMemory(false)
                    .similarityScore(null)
                    .build();

        } catch (Exception e) {
            log.error("AI call failed for conversation {}: {}", conversation.getId(), e.getMessage());
            throw new AppException(AiErrorCode.AI_SERVICE_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations() {
        User currentUser = securityUtils.getCurrentUserCognitoSub();
        List<Conversation> conversations = conversationRepository.findByUserCognitoSubOrderByUpdatedAtDesc(currentUser.getCognitoSub());
        return conversations.stream()
                .map(conversationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversationDetail(String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(AppErrorCode.UNCATEGORIZED_EXCEPTION));
        return conversationMapper.toDetailResponse(conversation);
    }

    @Override
    @Transactional
    public void deleteConversation(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new AppException(AppErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        conversationRepository.deleteById(conversationId);
    }

    /** Câu trả lời kèm model thực sự đã tạo ra nó. */
    private record ModelAnswer(String answer, String model) {}

    /**
     * Gọi lần lượt các model trong {@code app.ai.chat-models} cho tới khi có một
     * câu trả lời hợp lệ. Chuyển sang model kế tiếp khi model hiện tại hết quota,
     * bị rate-limit, bị gỡ khỏi OpenRouter, hoặc trả về câu trả lời lẫn chữ nước
     * ngoài mà không tự sửa được.
     */
    private ModelAnswer generateWithFallback(String systemPrompt, List<Message> messageContext) {
        if (chatModels == null || chatModels.isEmpty()) {
            log.error("app.ai.chat-models is empty — no model configured");
            throw new AppException(AiErrorCode.AI_SERVICE_ERROR);
        }

        for (int i = 0; i < chatModels.size(); i++) {
            String model = chatModels.get(i).trim();
            if (model.isEmpty()) continue;

            try {
                log.info("Trying model [{}/{}]: {}", i + 1, chatModels.size(), model);
                String answer = stripLatex(callModel(systemPrompt, messageContext, model));

                // Model free thỉnh thoảng vẫn trộn chữ Hán/Hàn/Kirin dù prompt đã cấm.
                // Thử lại một lần với chỉ thị sửa lỗi trước khi bỏ sang model khác.
                if (containsForeignScript(answer)) {
                    log.warn("Model {} returned foreign script, retrying once", model);
                    List<Message> retryContext = new ArrayList<>(messageContext);
                    retryContext.add(new UserMessage(
                            "Câu trả lời trước có lẫn ký tự không phải tiếng Việt. "
                                    + "Hãy viết lại toàn bộ câu trả lời bằng tiếng Việt thuần túy, "
                                    + "chỉ giữ nguyên công thức hóa học."));
                    answer = stripLatex(callModel(systemPrompt, retryContext, model));

                    if (containsForeignScript(answer)) {
                        log.warn("Model {} still returned foreign script, falling back to next model", model);
                        continue;
                    }
                }

                if (answer == null || answer.isBlank()) {
                    log.warn("Model {} returned empty answer, falling back to next model", model);
                    continue;
                }

                if (i > 0) {
                    log.info("Served by fallback model {} (primary models unavailable)", model);
                }
                return new ModelAnswer(answer, model);

            } catch (Exception e) {
                log.warn("Model {} failed ({}: {}), falling back to next model",
                        model, e.getClass().getSimpleName(), e.getMessage());
            }
        }

        log.error("All {} configured models failed", chatModels.size());
        throw new AppException(AiErrorCode.AI_SERVICE_ERROR);
    }

    private String callModel(String systemPrompt, List<Message> messageContext, String model) {
        return chatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(CHAT_TEMPERATURE)
                        .build())
                .system(systemPrompt)
                .messages(messageContext)
                .call()
                .content();
    }

    /** True nếu chuỗi chứa chữ Hán/Kana/Hangul/Kirin — dấu hiệu model bị code-switch. */
    private boolean containsForeignScript(String text) {
        return text != null && FOREIGN_SCRIPT_PATTERN.matcher(text).find();
    }

    /**
     * Gỡ cú pháp LaTeX khỏi câu trả lời. Các model dòng Gemma vẫn xuất
     * {@code $\text{H}_2\text{O}$} dù prompt đã cấm tường minh, mà flutter_markdown
     * ở client không render LaTeX nên học sinh sẽ thấy mã nguồn thô. Chuẩn hoá tại
     * server đáng tin hơn là trông chờ model tuân thủ prompt.
     */
    public static String stripLatex(String text) {
        if (text == null || text.isBlank()) return text;

        String out = text;

        // \text{H} / \mathrm{H} / \mathbf{H} -> H
        out = out.replaceAll("\\\\(?:text|mathrm|mathbf|mathit)\\s*\\{([^{}]*)\\}", "$1");

        // ^\circ hoặc \circ -> °
        out = out.replaceAll("\\^?\\\\circ", "°");

        // \times -> x ; \rightarrow / \to -> → ; \approx -> ≈
        out = out.replace("\\times", "×")
                 .replace("\\rightarrow", "→")
                 .replace("\\to", "→")
                 .replace("\\approx", "≈")
                 .replace("\\pm", "±")
                 .replace("\\cdot", "·");

        // Khoảng trắng LaTeX
        out = out.replaceAll("\\\\[,;:!]", " ");

        // Bỏ ký tự $ bao quanh biểu thức (cả $$...$$ và $...$)
        out = out.replaceAll("\\$\\$([^$]*)\\$\\$", "$1");
        out = out.replaceAll("\\$([^$\\n]*)\\$", "$1");

        // Chỉ số dưới/trên dạng _2 hoặc _{12} -> ký tự Unicode
        out = convertScripts(out, '_', "0123456789+-()", "₀₁₂₃₄₅₆₇₈₉₊₋₍₎");
        out = convertScripts(out, '^', "0123456789+-()", "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁽⁾");

        return out.trim();
    }

    /** Đổi {@code _2} / {@code _{12}} (hoặc {@code ^}) sang ký tự Unicode tương ứng. */
    private static String convertScripts(String text, char marker, String from, String to) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                java.util.regex.Pattern.quote(String.valueOf(marker)) + "(?:\\{([^{}]{1,6})\\}|([" + java.util.regex.Pattern.quote(from) + "]))");
        java.util.regex.Matcher m = p.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (m.find()) {
            String group = m.group(1) != null ? m.group(1) : m.group(2);
            StringBuilder converted = new StringBuilder();
            boolean convertible = true;
            for (char c : group.toCharArray()) {
                int idx = from.indexOf(c);
                if (idx < 0) { convertible = false; break; }
                converted.append(to.charAt(idx));
            }
            // Không đổi được thì giữ nguyên đoạn gốc để không làm hỏng nội dung.
            m.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(
                    convertible ? converted.toString() : m.group()));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Xây system prompt cho LLM theo kiểu RAG: retrieve các đoạn bài học liên
     * quan tới câu hỏi rồi chèn vào prompt để model trả lời có căn cứ (grounding).
     * Nếu không tìm được tài liệu liên quan thì dùng prompt mặc định.
     */
    private String buildGroundedSystemPrompt(String question) {
        try {
            List<RetrievedChunk> chunks =
                    knowledgeRetrievalService.retrieve(question, RAG_TOP_K, RAG_MIN_SCORE);
            String context = knowledgeRetrievalService.buildContextBlock(chunks);
            if (context.isBlank()) {
                log.info("RAG: no relevant lesson content, using base prompt");
                return BASE_SYSTEM_PROMPT;
            }
            log.info("RAG: grounding answer on {} lesson chunk(s)", chunks.size());
            return PROMPT_PREAMBLE + """

                    NGUỒN THAM CHIẾU:
                    Hãy trả lời CHỦ YẾU dựa trên ngữ cảnh tài liệu bài học dưới đây.
                    Nếu ngữ cảnh không chứa thông tin cần thiết, hãy nói rõ điều đó rồi
                    mới bổ sung bằng kiến thức Hóa học phổ thông.

                    === NGỮ CẢNH BÀI HỌC ===
                    %s
                    === HẾT NGỮ CẢNH ===
                    """.formatted(context) + LANGUAGE_REMINDER;
        } catch (Exception e) {
            log.warn("RAG grounding failed, using base prompt: {}", e.getMessage());
            return BASE_SYSTEM_PROMPT;
        }
    }

    private Conversation getOrCreateConversation(AiChatRequest request, User user) {
        if (request.getConversationId() != null && !request.getConversationId().isEmpty()) {
            return conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new AppException(AppErrorCode.UNCATEGORIZED_EXCEPTION));
        }
        Conversation conversation = Conversation.builder()
                .user(user)
                .build();
        return conversationRepository.save(conversation);
    }

    private ConversationMessage saveMessage(Conversation conversation, MessageRole role, String content, String model) {
        ConversationMessage message = ConversationMessage.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .modelUsed(model)
                .build();
        return messageRepository.save(message);
    }
}
