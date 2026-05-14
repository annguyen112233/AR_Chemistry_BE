package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(PermissionName name);
}