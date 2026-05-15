package com.chemistry.demo.services.admin.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.AdminUsersResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.admin.AdminUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserManagementServiceImpl implements AdminUserManagementService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional(readOnly = true)
    public PageResponse<AdminUsersResponse> getUsersForAdmin(Pageable pageable) {
        log.info("Fetching users for admin with pageable: {}", pageable);
        Page<User> users = userRepository.findAllWithRoles(pageable);

        List<AdminUsersResponse> items = users.getContent()
                .stream()
                .map(userMapper::toAdminUsersResponse)
                .toList();

        return PageResponse.<AdminUsersResponse>builder()
                .items(items)
                .page(users.getNumber())
                .size(users.getSize())
                .totalItems(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .first(users.isFirst())
                .last(users.isLast())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public String createUser() {
        return "Create user successfully";
    }
}
