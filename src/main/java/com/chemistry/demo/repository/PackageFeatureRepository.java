package com.chemistry.demo.repository;

import com.chemistry.demo.entity.PackageFeature;
import com.chemistry.demo.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageFeatureRepository
        extends JpaRepository<PackageFeature, String> {

    List<PackageFeature> findByPackageEntity(Packages packageEntity);
}
