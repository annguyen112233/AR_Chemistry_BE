package com.chemistry.demo.services.packages;

import com.chemistry.demo.dto.response.packages.PackageResponse;

import java.util.List;

public interface PackageService {
    List<PackageResponse> getAllPackages();
}
