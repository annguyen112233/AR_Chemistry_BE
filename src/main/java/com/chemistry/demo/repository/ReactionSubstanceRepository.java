package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ReactionDefinition;
import com.chemistry.demo.entity.ReactionSubstance;
import com.chemistry.demo.enums.ReactionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReactionSubstanceRepository extends JpaRepository<ReactionSubstance, String> {

    List<ReactionSubstance> findByReactionId(String reactionId);

    List<ReactionSubstance> findByReactionIdAndRole(
            String reactionId,
            ReactionRole role
    );

    List<ReactionSubstance> findBySubstanceId(String substanceId);

    List<ReactionSubstance> findBySubstanceIdAndRole(
            String substanceId,
            ReactionRole role
    );

    @Query("""
        select distinct rs.reaction
        from ReactionSubstance rs
        where rs.role = 'REACTANT'
        and rs.substance.formula in :formulas
        and rs.reaction.active = true
    """)
    List<ReactionDefinition> findCandidateReactionsByReactantFormulas(
            List<String> formulas
    );
}
