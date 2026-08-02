package com.chemistry.demo.services.ai;

import com.chemistry.demo.services.ai.Impl.AiChatServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Các chuỗi trong test này là output LaTeX THẬT do google/gemma-4-*:free trả về
 * khi được hỏi "Giải thích cấu trúc phân tử nước H₂O" (test ngày 2026-08-02).
 * flutter_markdown ở client không render LaTeX nên phải chuẩn hoá tại server.
 */
class StripLatexTest {

    @Test
    void convertsRealGemmaOutput() {
        String raw = "Phân tử nước ($\\text{H}_2\\text{O}$) có cấu trúc hình chữ V (gấp khúc).";
        String out = AiChatServiceImpl.stripLatex(raw);

        assertEquals("Phân tử nước (H₂O) có cấu trúc hình chữ V (gấp khúc).", out);
        assertFalse(out.contains("$"), "còn sót ký tự $");
        assertFalse(out.contains("\\text"), "còn sót \\text");
    }

    @Test
    void convertsDegreeNotation() {
        assertEquals("Góc H-O-H xấp xỉ 104,5°.",
                AiChatServiceImpl.stripLatex("Góc H-O-H xấp xỉ $104,5^\\circ$."));
    }

    @Test
    void convertsBracedSubscripts() {
        assertEquals("H₂SO₄ và Ca(OH)₂",
                AiChatServiceImpl.stripLatex("$H_{2}SO_{4}$ và $Ca(OH)_{2}$"));
    }

    @Test
    void convertsSuperscriptsForIons() {
        assertEquals("Zn²⁺ và Cl⁻",
                AiChatServiceImpl.stripLatex("$Zn^{2+}$ và $Cl^{-}$"));
    }

    @Test
    void convertsArrowsAndOperators() {
        assertEquals("Zn + 2HCl → ZnCl₂ + H₂",
                AiChatServiceImpl.stripLatex("Zn + 2HCl \\rightarrow ZnCl_2 + H_2"));
    }

    @Test
    void leavesCleanMarkdownUntouched() {
        // Output của inclusionai/ling-3.0-flash — vốn đã sạch, không được đụng vào.
        String clean = "Nước H₂O có cấu trúc dạng chữ V.\n\n"
                + "- Góc liên kết H–O–H xấp xỉ 104,5 độ.\n"
                + "- **Phân tử cực** vì momen lưỡng cực không triệt tiêu.";
        assertEquals(clean, AiChatServiceImpl.stripLatex(clean));
    }

    @Test
    void doesNotMangleMarkdownEmphasisOrPrices() {
        String text = "Chi phí **quan trọng**: snake_case và a_b không phải chỉ số dưới.";
        String out = AiChatServiceImpl.stripLatex(text);
        assertTrue(out.contains("**quan trọng**"), "làm hỏng in đậm Markdown");
        assertTrue(out.contains("snake_case"), "đổi nhầm snake_case");
    }

    @Test
    void handlesNullAndBlank() {
        assertNull(AiChatServiceImpl.stripLatex(null));
        assertEquals("", AiChatServiceImpl.stripLatex(""));
    }
}
