package com.epam.esm.service.impl;

import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private final TagRepository repository;

    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag createTag(TagCreateDto dto) {
        Tag tag = DtoConverter.toTag(dto);
        return repository.createTag(tag);
    }

    @Override
    public void deleteTag(String tagName) {
        repository.deleteByName(tagName);
    }

    @Override
    public Page<Tag> getAllTags(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Tag getTag(String tagName) {
        return repository.findByName(tagName);
    }

//    @Override
//    public Tag getTag(long id) {
//        return repository.findById(id).orElse(null);
//    }

}
