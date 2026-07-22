package com.minitextil.erp.components.core;

import java.util.function.Consumer;

public interface ProgramContext {
    void openProgram(ProgramId programId, ProgramParams params);
    void openProgram(ProgramId programId, ProgramParams params, Consumer<Object> onResult);
    void closeProgram();
    void closeProgram(Object result);
}