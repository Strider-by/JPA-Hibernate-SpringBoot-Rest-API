package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomTagRepository {

    List<Tag> persistTags(List<Tag> tags);

}
