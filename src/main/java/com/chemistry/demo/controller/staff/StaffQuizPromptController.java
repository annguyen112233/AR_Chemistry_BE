package com.chemistry.demo.controller.staff;

import com.chemistry.demo.dto.response.quiz.staff.QuizPromptResponse;
import com.chemistry.demo.services.quiz.QuizPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/reactions")
@RequiredArgsConstructor
public class StaffQuizPromptController {

    private final QuizPromptService
            quizPromptService;

    @GetMapping("/{reactionCode}/quiz-prompt")
    public ResponseEntity<QuizPromptResponse>
    getQuizPrompt(
            @PathVariable String reactionCode
    ) {
        return ResponseEntity.ok(
                quizPromptService.generatePrompt(
                        reactionCode
                )
        );
    }
}