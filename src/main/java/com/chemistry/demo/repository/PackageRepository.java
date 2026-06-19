package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Packages, String> {
    Optional<Packages> findByGoogleProductId(String productId);
}
