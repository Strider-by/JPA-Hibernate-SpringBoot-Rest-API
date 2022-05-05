//package com.epam.esm.service.impl.v1;
//
//import com.epam.esm.dao.TagDao;
//import com.epam.esm.model.Tag;
//import com.epam.esm.model.dto.TagDownstreamDto;
//import com.epam.esm.service.TagsService;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class TagsServiceImpl implements TagsService {
//
//    private final TagDao tagDao;
//
//    public TagsServiceImpl(TagDao tagDao) {
//        this.tagDao = tagDao;
//    }
//
//    @Override
//    public boolean createTag(TagDownstreamDto dto) {
//        return tagDao.create(dto);
//    }
//
//    @Override
//    public boolean deleteTag(String tagName) {
//        return tagDao.delete(tagName);
//    }
//
//    @Override
//    public List<Tag> getAllTags(long limit, long offset) {
//        return tagDao.getAll(limit, offset);
//    }
//
//    @Override
//    public Tag getTag(String tagName) {
//        return tagDao.getByName(tagName);
//    }
//
//}
