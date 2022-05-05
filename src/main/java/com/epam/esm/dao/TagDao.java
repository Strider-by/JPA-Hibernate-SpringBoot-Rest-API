package com.epam.esm.dao;

import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;

import java.util.List;

public interface TagDao {

    boolean create(TagCreateDto dto);

    Tag getByName(String name);

    List<Tag> getAll(long limit, long offset);

    boolean delete(String name);

}
