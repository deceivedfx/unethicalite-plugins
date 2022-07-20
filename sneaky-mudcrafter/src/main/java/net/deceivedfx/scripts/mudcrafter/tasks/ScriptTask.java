package net.deceivedfx.scripts.mudcrafter.tasks;

public interface ScriptTask
{
    boolean validate();

    int execute();

    default boolean blocking() {
        return true;
    }
}
