package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Kit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface KitRepository extends JpaRepository<Kit, String> {

    Optional<Kit> findByCode(String code);

    boolean existsByCode(String code);

    Page<Kit> findByActiveTrue(Pageable pageable);
}
