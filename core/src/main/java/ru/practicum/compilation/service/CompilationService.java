package ru.practicum.compilation.service;

import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.CompilationDtoRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CompilationDtoRequest compilationDto);

    void deleteCompilation(Integer compId);

    CompilationDto updateCompilation(Integer compId, CompilationDtoRequest compilationDto);

    List<CompilationDto> getAllCompilation(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Integer id);
}
