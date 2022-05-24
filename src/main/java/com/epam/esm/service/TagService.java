package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

    Tag createTag(TagCreateDto dto);

    void deleteTag(String tagName);

    Page<Tag> getAllTags(Pageable pageable);

    Tag getTag(String tagName);

//    Tag getTag(long id);

}
