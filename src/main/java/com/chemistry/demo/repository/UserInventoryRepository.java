package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventory, String> {


    Page<UserInventory> findByUserAndActiveTrue(
            User user,
            Pageable pageable
    );

    Optional<UserInventory> findByUserAndSubstance(
            User user,
            ChemicalSubstance substance
    );


    Optional<UserInventory> findByUserAndSubstance_Id(User user, String substanceId);
    long countDistinctByUserAndActiveTrueAndSubstance_ActiveTrueAndSubstance_IncludedInFullKitTrue(
            User user
    );

}
