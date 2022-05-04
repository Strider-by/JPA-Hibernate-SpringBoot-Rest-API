package com.epam.esm.model.util;

import com.epam.esm.controller.api.ControllerHelper;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpstreamDto;
import com.epam.esm.model.dto.TagDownstreamDto;
import com.epam.esm.model.dto.UserCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

    public static CertificateUpstreamDto toCertificateUpstreamDto(Certificate certificate) {

        if (certificate == null) {
            return null;
        }

        CertificateUpstreamDto dto = new CertificateUpstreamDto();
        dto.setId(certificate.getId());
        dto.setName(certificate.getName());
        dto.setDescription(
                certificate.getDescription().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()));
        dto.setPrice(certificate.getPrice());
        dto.setDuration(certificate.getDuration());
        dto.setCreated(DateConverter.toISO8601DateString(certificate.getCreateDate()));
        dto.setLastUpdate(DateConverter.toISO8601DateString(certificate.getLastUpdateDate()));

        return dto;
    }

    public static Certificate toCertificate(CertificateCreateDto dto) {
        Certificate certificate = new Certificate();
        certificate.setName(dto.getName());
        certificate.setPrice(dto.getPrice());
        certificate.setDuration(dto.getDuration());


        certificate.setDescription(
                toTags(dto.getDescription()));
        return certificate;
    }

    public static User toUser(UserCreateDto dto) {
        // empty fields
        return new User();
    }

//    public static Certificate toCertificate(long id, MultiValueMap<String, String> params) {
//        Certificate certificate = new Certificate();
//        certificate.setId(id);
//        certificate.setPrice(getAsInteger("price", params));
//        certificate.set
//    }

    public static Tag toTag(TagDownstreamDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        return tag;
    }

    public static Tag toTag(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        return tag;
    }

    public static List<Tag> toTags(List<TagDownstreamDto> dtos) {
        return dtos.stream()
                .map(DtoConverter::toTag)
                .collect(Collectors.toList());
    }

    public static <T> Page<T> createPageRepresentation(List<T> content, int pageNumber, int pageSize, long resultsTotal) {
        Pageable pageable = PageRequest.of(pageNumber - ControllerHelper.FIRST_PAGE_NUMBER, pageSize);
        Page<T> page = new PageImpl<>(content, pageable, resultsTotal);
        return page;
    }



}
