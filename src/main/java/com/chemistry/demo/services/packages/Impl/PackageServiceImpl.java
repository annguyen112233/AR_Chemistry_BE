package com.chemistry.demo.services.packages.Impl;

import com.chemistry.demo.dto.response.packages.PackageResponse;
import com.chemistry.demo.mapper.PackageMapper;
import com.chemistry.demo.repository.PackageRepository;
import com.chemistry.demo.services.packages.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    @Override
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public List<PackageResponse> getAllPackages() {
        return packageRepository.findAll()
                .stream()
                .map(packageMapper::toPackageResponse)
                .toList();
    }
}
