package com.chemistry.demo.services.quiz.impl;

import com.chemistry.demo.dto.response.quiz.staff.QuizPromptResponse;
import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;
import com.chemistry.demo.enums.ReactionRole;
import com.chemistry.demo.repository.ReactionDefinitionRepository;
import com.chemistry.demo.repository.ReactionSubstanceRepository;
import com.chemistry.demo.services.quiz.QuizPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizPromptServiceImpl
        implements QuizPromptService {

    private final ReactionDefinitionRepository
            reactionDefinitionRepository;

    private final ReactionSubstanceRepository
            reactionSubstanceRepository;

    @Override
    @PreAuthorize(
            "hasAnyAuthority('ROLE_STAFF', 'ROLE_ADMIN')"
    )
    @Transactional(readOnly = true)
    public QuizPromptResponse generatePrompt(
            String reactionCode
    ) {
        String normalizedCode =
                normalizeReactionCode(reactionCode);

        ReactionDefinition reaction =
                reactionDefinitionRepository
                        .findByCode(normalizedCode)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reaction not found: "
                                                + normalizedCode
                                )
                        );

        List<ReactionSubstance> reactants =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.REACTANT
                        );

        List<ReactionSubstance> products =
                reactionSubstanceRepository
                        .findByReactionAndRoleOrderBySubstanceOrderAsc(
                                reaction,
                                ReactionRole.PRODUCT
                        );

        String prompt =
                buildPrompt(
                        reaction,
                        reactants,
                        products
                );

        return QuizPromptResponse.builder()
                .reactionCode(
                        reaction.getCode()
                )
                .reactionName(
                        reaction.getName()
                )
                .prompt(prompt)
                .build();
    }

    private String buildPrompt(
            ReactionDefinition reaction,
            List<ReactionSubstance> reactants,
            List<ReactionSubstance> products
    ) {
        String reactantText =
                formatSubstances(reactants);

        String productText =
                products == null
                        || products.isEmpty()
                        ? "Không có sản phẩm vì phản ứng không xảy ra"
                        : formatSubstances(products);

        String description =
                reaction.getDescription() != null
                        && !reaction.getDescription().isBlank()
                        ? reaction.getDescription().trim()
                        : "Không có mô tả bổ sung";

        String script =
                reaction.getScript() != null
                        && !reaction.getScript().isBlank()
                        ? reaction.getScript().trim()
                        : description;

        return """
                Hãy tạo một file CSV gồm đúng 5 câu hỏi trắc nghiệm
                dành cho học sinh học phản ứng hóa học bằng mô phỏng AR.

                THÔNG TIN PHẢN ỨNG

                - Mã phản ứng: %s
                - Tên phản ứng: %s
                - Lớp: %s
                - Nhóm phản ứng: %s
                - Loại phản ứng: %s
                - Phương trình hóa học: %s
                - Chất tham gia: %s
                - Sản phẩm: %s
                - Mô tả: %s
                - Nội dung quan sát AR: %s

                YÊU CẦU CÂU HỎI

                - Tạo đúng 5 câu hỏi.
                - Mỗi câu có đúng 4 lựa chọn A, B, C và D.
                - Mỗi câu chỉ có một đáp án đúng.
                - correct_answer chỉ được là A, B, C hoặc D.
                - question_order phải lần lượt là 1, 2, 3, 4, 5.
                - Tất cả các dòng phải có cùng một quiz_title.
                - Mỗi câu phải có explanation giải thích ngắn gọn.
                - Không tạo câu hỏi nằm ngoài dữ liệu phản ứng được cung cấp.
                - Không tạo đáp án mơ hồ hoặc có nhiều đáp án đúng.
                - Không lặp lại cùng một nội dung ở nhiều câu hỏi.

                ƯU TIÊN NỘI DUNG

                1. Chất hoặc flash card cần quét trong AR.
                2. Phương trình hóa học.
                3. Hiện tượng quan sát được.
                4. Sản phẩm tạo thành.
                5. Khí, kết tủa, màu sắc hoặc phản ứng có xảy ra hay không.

                ĐỊNH DẠNG CSV BẮT BUỘC

                Header phải chính xác như sau:

                quiz_title,question_order,question_text,option_a,option_b,option_c,option_d,correct_answer,explanation

                Ví dụ một dòng hợp lệ:

                "Quiz phản ứng Zn và HCl",1,"Hai chất tham gia phản ứng là gì?","Zn và HCl","Fe và HCl","Cu và HCl","Na và H2O","A","Phản ứng sử dụng Zn và HCl."

                QUY TẮC XUẤT KẾT QUẢ

                - Chỉ trả về nội dung CSV.
                - Không sử dụng Markdown.
                - Không đặt CSV trong khối code.
                - Không giải thích trước hoặc sau CSV.
                - Nội dung có dấu phẩy phải được đặt trong dấu ngoặc kép.
                - File phải sử dụng UTF-8.
                """.formatted(
                reaction.getCode(),
                reaction.getName(),
                reaction.getGrade(),
                reaction.getReactionCategory().name(),
                reaction.getReactionType().name(),
                reaction.getEquation(),
                reactantText,
                productText,
                description,
                script
        );
    }

    private String formatSubstances(
            List<ReactionSubstance> substances
    ) {
        if (substances == null
                || substances.isEmpty()) {
            return "Không có";
        }

        return substances.stream()
                .map(item -> {
                    String formula =
                            item.getSubstance()
                                    .getFormula();

                    Integer coefficient =
                            item.getCoefficient() != null
                                    ? item.getCoefficient()
                                    : 1;

                    if (coefficient == 1) {
                        return formula;
                    }

                    return coefficient + formula;
                })
                .collect(
                        Collectors.joining(", ")
                );
    }

    private String normalizeReactionCode(
            String reactionCode
    ) {
        if (reactionCode == null
                || reactionCode.isBlank()) {
            throw new IllegalArgumentException(
                    "reactionCode is required"
            );
        }

        return reactionCode
                .trim()
                .toUpperCase();
    }
}