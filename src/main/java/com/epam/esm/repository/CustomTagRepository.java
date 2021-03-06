package com.epam.esm.repository;

import com.epam.esm.model.Tag;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomTagRepository {

    List<Tag> persistTags(List<Tag> tags);

    Tag findByName(String name);

    void deleteByName(String tagName);

    Tag createTag(Tag tag);

}
