package com.chemistry.demo.controller.packages;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.response.packages.PackageResponse;
import com.chemistry.demo.services.packages.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/packages")
@RequiredArgsConstructor
public class PackageController {
    private final PackageService packageService;

    @GetMapping
    public ApiResponse<List<PackageResponse>> getAllPackages() {
        return ApiResponse.<List<PackageResponse>>ok()
                .data(packageService.getAllPackages())
                .build();
    }
}
