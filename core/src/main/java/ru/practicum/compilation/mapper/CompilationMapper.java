package ru.practicum.compilation.mapper;

import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(), compilation.getEvents(),
                compilation.isPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationDtoList.add(toCompilationDto(compilation));
        }
        return compilationDtoList;
    }
}