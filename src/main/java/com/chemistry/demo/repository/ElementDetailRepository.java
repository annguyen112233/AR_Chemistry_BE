package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ElementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ElementDetailRepository extends JpaRepository<ElementDetail, String> {

    Optional<ElementDetail> findBySubstance_Id(String substanceId);



}
