package com.epam.esm.service;

import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagDownstreamDto;

import java.util.List;

public interface TagsService {

    boolean createTag(TagDownstreamDto dto);

    boolean deleteTag(String tagName);

    List<Tag> getAllTags(long limit, long offset);

    Tag getTag(String tagName);

}
