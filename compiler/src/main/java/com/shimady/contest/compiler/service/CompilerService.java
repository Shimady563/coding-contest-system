package com.shimady.contest.compiler.service;

import com.shimady.contest.compiler.model.Task;
import com.shimady.contest.compiler.model.dto.CompilationResult;

public interface CompilerService {
    CompilationResult compileAndRun(String code, Task task);
}
