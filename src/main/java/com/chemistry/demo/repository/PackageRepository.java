package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.enums.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Packages, String> {

    Optional<Packages> findByPackageType(PackageType packageType);

}
