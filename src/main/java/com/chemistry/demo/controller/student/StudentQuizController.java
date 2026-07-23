package com.chemistry.demo.controller.student;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.quiz.student.CompleteArRequest;
import com.chemistry.demo.dto.request.quiz.student.SaveQuizAnswerRequest;
import com.chemistry.demo.dto.response.quiz.student.*;
import com.chemistry.demo.enums.ReactionCategory;
import com.chemistry.demo.services.quiz.StudentQuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    /**
     * Lấy danh sách phản ứng theo:
     * - Lớp 8, 9, 10, 11, 12
     * - Nhóm METAL, ACID, BASE, SALT
     * - Từ khóa tìm kiếm
     *
     * Ví dụ:
     * GET /student/reactions
     *     ?grade=8
     *     &reactionCategory=METAL
     *     &keyword=Kẽm
     *     &page=0
     *     &size=10
     */
    @GetMapping("/reactions")
    public ApiResponse<PageResponse<StudentReactionResponse>> getReactions(
            @RequestParam Integer grade,

            @RequestParam
            ReactionCategory reactionCategory,

            @RequestParam(
                    required = false,
                    defaultValue = ""
            )
            String keyword,

            Pageable pageable
    ) {
        return ApiResponse
                .<PageResponse<StudentReactionResponse>>ok()
                .data(
                        studentQuizService.getReactions(
                                grade,
                                reactionCategory,
                                keyword,
                                pageable
                        )
                )
                .build();
    }

    /**
     * Lấy thông tin cơ bản của reaction trước khi bắt đầu:
     * - Tên phản ứng
     * - Phương trình
     * - Mô tả ngắn
     * - AR scene
     * - Thời gian quiz
     *
     * Chưa trả script chi tiết và câu hỏi.
     */
    @GetMapping("/reactions/{reactionId}")
    public ApiResponse<StudentReactionDetailResponse>
    getReactionDetail(
            @PathVariable String reactionId
    ) {
        return ApiResponse
                .<StudentReactionDetailResponse>ok()
                .data(
                        studentQuizService.getReactionDetail(
                                reactionId
                        )
                )
                .build();
    }

    /**
     * Lấy thông tin quiz đang publish của một reaction.
     */
    @GetMapping("/reactions/{reactionId}/quiz")
    public ApiResponse<StudentQuizSummaryResponse>
    getPublishedQuizByReaction(
            @PathVariable String reactionId
    ) {
        return ApiResponse
                .<StudentQuizSummaryResponse>ok()
                .data(
                        studentQuizService
                                .getPublishedQuizByReaction(
                                        reactionId
                                )
                )
                .build();
    }

    /**
     * Bắt đầu một lần làm bài.
     *
     * Nếu đang có attempt WAITING_AR hoặc RUNNING,
     * backend trả lại attempt hiện tại.
     *
     * Nếu không có attempt đang hoạt động,
     * backend tạo attempt mới với trạng thái WAITING_AR.
     */
    @PostMapping("/reactions/{reactionId}/attempts")
    public ApiResponse<StartQuizResponse> startQuiz(
            @PathVariable String reactionId
    ) {
        return ApiResponse
                .<StartQuizResponse>ok()
                .data(
                        studentQuizService.startQuiz(
                                reactionId
                        )
                )
                .build();
    }

    /**
     * Xác nhận user đã:
     * - Quét đúng hai flash card
     * - Thực hiện phản ứng AR thành công
     *
     * Chỉ từ thời điểm API này thành công,
     * timer 7 phút mới bắt đầu.
     */
    @PostMapping(
            "/quiz-attempts/{attemptCode}/complete-ar"
    )
    public ApiResponse<CompleteArResponse> completeAr(
            @PathVariable String attemptCode,

            @Valid
            @RequestBody
            CompleteArRequest request
    ) {
        return ApiResponse
                .<CompleteArResponse>ok()
                .data(
                        studentQuizService.completeAr(
                                attemptCode,
                                request
                        )
                )
                .build();
    }

    /**
     * Lấy trạng thái hiện tại của attempt.
     *
     * Frontend dùng API này để đồng bộ:
     * - AR đã hoàn thành chưa
     * - Timer còn bao nhiêu giây
     * - Attempt đã submit/timeout chưa
     */
    @GetMapping(
            "/quiz-attempts/{attemptCode}/state"
    )
    public ApiResponse<QuizAttemptStateResponse>
    getAttemptState(
            @PathVariable String attemptCode
    ) {
        return ApiResponse
                .<QuizAttemptStateResponse>ok()
                .data(
                        studentQuizService.getAttemptState(
                                attemptCode
                        )
                )
                .build();
    }

    /**
     * Lấy script và 5 câu hỏi.
     *
     * Chỉ truy cập được sau khi AR thành công
     * và attempt đang RUNNING.
     */
    @GetMapping(
            "/quiz-attempts/{attemptCode}/content"
    )
    public ApiResponse<StudentQuizContentResponse>
    getQuizContent(
            @PathVariable String attemptCode
    ) {
        return ApiResponse
                .<StudentQuizContentResponse>ok()
                .data(
                        studentQuizService.getQuizContent(
                                attemptCode
                        )
                )
                .build();
    }

    /**
     * Lưu hoặc cập nhật đáp án cho một câu hỏi.
     *
     * Frontend nên gọi API này mỗi lần user chọn đáp án.
     */
    @PutMapping(
            "/quiz-attempts/{attemptCode}"
                    + "/answers/{questionId}"
    )
    public ApiResponse<Void> saveAnswer(
            @PathVariable String attemptCode,

            @PathVariable String questionId,

            @Valid
            @RequestBody
            SaveQuizAnswerRequest request
    ) {
        studentQuizService.saveAnswer(
                attemptCode,
                questionId,
                request
        );

        return ApiResponse
                .<Void>ok()
                .build();
    }

    /**
     * User chủ động bấm nộp bài.
     *
     * Không cần gửi lại toàn bộ đáp án vì các đáp án
     * đã được lưu bằng API saveAnswer.
     */
    @PostMapping(
            "/quiz-attempts/{attemptCode}/submit"
    )
    public ApiResponse<SubmitQuizResponse> submitQuiz(
            @PathVariable String attemptCode
    ) {
        return ApiResponse
                .<SubmitQuizResponse>ok()
                .data(
                        studentQuizService.submitQuiz(
                                attemptCode
                        )
                )
                .build();
    }

    /**
     * Xem chi tiết kết quả của một attempt đã:
     * - SUBMITTED
     * - TIMEOUT
     */
    @GetMapping(
            "/quiz-attempts/{attemptCode}/result"
    )
    public ApiResponse<StudentQuizAttemptDetailResponse>
    getAttemptResult(
            @PathVariable String attemptCode
    ) {
        return ApiResponse
                .<StudentQuizAttemptDetailResponse>ok()
                .data(
                        studentQuizService
                                .getMyQuizAttemptDetail(
                                        attemptCode
                                )
                )
                .build();
    }

    /**
     * Xem lịch sử làm quiz của một reaction.
     *
     * Chỉ trả các attempt:
     * - SUBMITTED
     * - TIMEOUT
     *
     * Không trả:
     * - WAITING_AR
     * - RUNNING
     * - ABANDONED
     */
    @GetMapping(
            "/reactions/{reactionId}/attempt-history"
    )
    public ApiResponse<
            PageResponse<StudentQuizAttemptHistoryResponse>
            >
    getReactionAttemptHistory(
            @PathVariable String reactionId,
            Pageable pageable
    ) {
        return ApiResponse
                .<PageResponse<
                        StudentQuizAttemptHistoryResponse
                        >>ok()
                .data(
                        studentQuizService
                                .getReactionAttemptHistory(
                                        reactionId,
                                        pageable
                                )
                )
                .build();
    }

    /**
     * User thoát ra ngoài giữa chừng.
     *
     * Nếu chưa hết giờ:
     * trạng thái chuyển thành ABANDONED,
     * không xuất hiện trong lịch sử.
     *
     * Nếu đã hết giờ:
     * backend tự chấm và chuyển thành TIMEOUT.
     */
    @PostMapping(
            "/quiz-attempts/{attemptCode}/abandon"
    )
    public ApiResponse<Void> abandonAttempt(
            @PathVariable String attemptCode
    ) {
        studentQuizService.abandonAttempt(
                attemptCode
        );

        return ApiResponse
                .<Void>ok()
                .build();
    }
}