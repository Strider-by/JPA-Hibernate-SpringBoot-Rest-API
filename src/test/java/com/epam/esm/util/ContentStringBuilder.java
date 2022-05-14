//package com.epam.esm.util;
//
//import com.epam.esm.model.dto.Pair;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ContentStringBuilder {
//    private List<Pair<String, Object>> chunks = new ArrayList<>();
//
//    public ContentStringBuilder add(String name, Object value) {
//        chunks.add(new Pair<>(name, value));
//        return this;
//    }
//
//    public String build() {
//        return chunks.stream()
//                .map(pair -> String.format("%s=%s", pair.getFirstValue(), pair.getSecondValue().toString()))
//                .reduce((part1, part2) -> String.join("&", part1, part2))
//                .orElse("");
//    }
//}
