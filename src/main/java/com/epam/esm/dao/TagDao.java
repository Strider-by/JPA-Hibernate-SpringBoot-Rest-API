package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagDownstreamDto;

import java.util.List;

public interface TagDao {

    boolean create(TagDownstreamDto dto);

    Tag getByName(String name);

    List<Tag> getAll(long limit, long offset);

    boolean delete(String name);

}
