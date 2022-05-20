package com.epam.esm.model.util;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.TagCreateDto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DtoConverterTest {

    @Test
    void toCertificate() {
        String name = "Generic name";
        int price = 100;
        int duration = 2;
        List<TagCreateDto> createDescription = Arrays.asList("First tag", "Second tag", "Third tag").stream()
                .map(TagCreateDto::new)
                .collect(Collectors.toList());
        List<Tag> description = createDescription.stream()
                .map(tagCreateDto -> new Tag(null, tagCreateDto.getName()))
                .collect(Collectors.toList());

        CertificateCreateDto dto = new CertificateCreateDto();
        dto.setName(name);
        dto.setPrice(price);
        dto.setDuration(duration);
        dto.setDescription(createDescription);

        Certificate expected = new Certificate();
        expected.setName(name);
        expected.setPrice(price);
        expected.setDuration(duration);
        expected.setDescription(description);

        Certificate actual = DtoConverter.toCertificate(dto);
        assertEquals(expected, actual);
    }


    @Test
    void toTag_fromTagCreateDto() {
        String name = "generic name";
        TagCreateDto tagCreateDto = new TagCreateDto(name);
        Tag expected = new Tag(null, name);
        Tag actual = DtoConverter.toTag(tagCreateDto);
        assertEquals(expected, actual);
    }

    @Test
    void toTag_fromString() {
        String name = "generic name";
        Tag expected = new Tag(null, name);
        Tag actual = DtoConverter.toTag(name);
        assertEquals(expected, actual);
    }

    @Test
    void toTags() {
        List<TagCreateDto> tagCreateDtos = Stream.of("First tag", "Second tag", "Third tag")
                .map(TagCreateDto::new)
                .collect(Collectors.toList());

        List<Tag> expected = tagCreateDtos.stream()
                .map(tagCreateDto -> new Tag(null, tagCreateDto.getName()))
                .collect(Collectors.toList());

        List<Tag> actual = DtoConverter.toTags(tagCreateDtos);

        assertEquals(expected, actual);
    }

}