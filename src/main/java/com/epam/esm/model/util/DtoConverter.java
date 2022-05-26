package com.epam.esm.model.util;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.CertificateCreateDto;
//import com.epam.esm.model.dto.CertificateUpstreamDto;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.model.dto.UserCreateDto;

import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

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
        // no actual fields we want to convert
        return new User();
    }

    public static Tag toTag(TagCreateDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        return tag;
    }

    public static Tag toTag(String tagName) {
        Tag tag = new Tag();
        tag.setName(tagName);
        return tag;
    }

    public static List<Tag> toTags(List<TagCreateDto> dtos) {
        return dtos.stream()
                .map(DtoConverter::toTag)
                .collect(Collectors.toList());
    }

}
