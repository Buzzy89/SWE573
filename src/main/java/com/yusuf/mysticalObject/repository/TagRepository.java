package com.yusuf.mysticalObject.repository;

import com.yusuf.mysticalObject.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
}
