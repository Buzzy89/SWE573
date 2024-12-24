package com.yusuf.mysticalObject.repository;

import com.yusuf.mysticalObject.entity.WikiDataLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikiDataLabelRepository extends JpaRepository<WikiDataLabel, Long> {
    Optional<WikiDataLabel> findByWikidataId(String wikidataId);
    Optional<WikiDataLabel> findByQid(String qid);
} 