package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.lesson.LessonResponse;
import com.chemistry.demo.entity.Lesson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonResponse toLessonResponse(Lesson lesson);
}
