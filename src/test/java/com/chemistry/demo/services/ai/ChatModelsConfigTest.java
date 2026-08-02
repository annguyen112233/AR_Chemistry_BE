package com.chemistry.demo.services.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * app.ai.chat-models chứa dấu ':' cả trong cú pháp placeholder lẫn trong model ID
 * (ví dụ "google/gemini-2.0-flash-exp:free"). Test này chốt rằng chuỗi vẫn được
 * resolve và tách thành danh sách model đúng như mong đợi.
 */
class ChatModelsConfigTest {

    private String rawChatModels() throws Exception {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load("application", new ClassPathResource("application.yml"));
        for (PropertySource<?> source : sources) {
            Object value = source.getProperty("app.ai.chat-models");
            if (value != null) return value.toString();
        }
        throw new AssertionError("app.ai.chat-models not found in application.yml");
    }

    @Test
    void defaultChainResolvesToOrderedModelList() throws Exception {
        MockEnvironment env = new MockEnvironment();
        String resolved = env.resolveRequiredPlaceholders(rawChatModels());

        @SuppressWarnings("unchecked")
        List<String> models = (List<String>) DefaultConversionService.getSharedInstance()
                .convert(resolved, List.class);

        assertNotNull(models);
        assertTrue(models.size() >= 2, "cần ít nhất 2 model để fallback có ý nghĩa");

        // Model đầu chuỗi phải là model nhanh & sạch nhất theo kết quả test thật
        // 2026-08-02 (2,4s, tiếng Việt chuẩn, không LaTeX).
        assertEquals("inclusionai/ling-3.0-flash:free", models.get(0),
                "model đầu chuỗi không còn là model tốt nhất đã kiểm chứng");

        // Các model đã test và bị loại — không được để lọt lại vào chuỗi.
        // Lý do loại: xem comment trong application.yml.
        List<String> rejected = List.of(
                "google/gemma-4-31b-it:free",               // HTTP 429 liên tục 6/6 lần
                "nvidia/nemotron-3-super-120b-a12b:free",   // lẫn chữ Hán
                "nvidia/nemotron-3-nano-30b-a3b:free",      // lộ chain-of-thought
                "nvidia/nemotron-nano-9b-v2:free",          // trả lời cụt
                "nvidia/nemotron-nano-12b-v2-vl:free",      // sai kiến thức hoá học
                "poolside/laguna-s-2.1:free",               // trả lời cụt
                "poolside/laguna-xs-2.1:free");             // trả lời cụt

        for (String model : models) {
            assertFalse(rejected.contains(model),
                    "model đã bị loại vì test hỏng nhưng vẫn còn trong chuỗi: " + model);
        }

        for (String model : models) {
            assertFalse(model.isBlank(), "model rỗng trong chuỗi");
            assertEquals(model, model.trim(), "model còn khoảng trắng thừa: '" + model + "'");
            // Hậu tố ':free' phải còn nguyên — nếu placeholder bị cắt sai thì mất phần này.
            assertTrue(model.contains("/"), "model ID thiếu vendor prefix: " + model);
            assertNotEquals("openrouter/free", model,
                    "không được dùng alias auto-router, phải ghim model cụ thể");
        }
    }

    @Test
    void environmentVariableOverridesDefaultChain() throws Exception {
        MockEnvironment env = new MockEnvironment()
                .withProperty("AI_CHAT_MODELS", "google/gemini-2.0-flash-exp:free,fallback/model-x:free");

        String resolved = env.resolveRequiredPlaceholders(rawChatModels());

        @SuppressWarnings("unchecked")
        List<String> models = (List<String>) DefaultConversionService.getSharedInstance()
                .convert(resolved, List.class);

        assertEquals(List.of("google/gemini-2.0-flash-exp:free", "fallback/model-x:free"), models);
    }
}
