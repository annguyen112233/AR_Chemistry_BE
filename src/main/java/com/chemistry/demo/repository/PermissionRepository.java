package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(PermissionName name);
}