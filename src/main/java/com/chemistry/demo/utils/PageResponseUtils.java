package com.chemistry.demo.utils;

import com.chemistry.demo.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PageResponseUtils {

    public static <T, R> PageResponse<R> toPageResponse(
            Page<T> page,
            Function<T, R> mapper
    ) {

        List<R> items = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return PageResponse.<R>builder()
                .items(items)
                .page(page.getNumber())
                .size(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}