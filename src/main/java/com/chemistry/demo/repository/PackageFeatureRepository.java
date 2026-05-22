package com.chemistry.demo.repository;

import com.chemistry.demo.entity.PackageFeature;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.enums.FeatureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageFeatureRepository extends JpaRepository<PackageFeature, String> {
    boolean existsByPackagesAndFeatures_Code(
            Packages packageEntity,
            FeatureCode code
    );

    List<PackageFeature> findByPackages(Packages packages);
}
