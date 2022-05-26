package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTagRepository extends JpaRepository<Tag, Long> {

}
