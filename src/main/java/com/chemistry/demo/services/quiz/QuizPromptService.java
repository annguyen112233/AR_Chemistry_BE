package com.chemistry.demo.services.quiz;

import com.chemistry.demo.dto.response.quiz.staff.QuizPromptResponse;

public interface QuizPromptService {

    QuizPromptResponse generatePrompt(
            String reactionCode
    );
}