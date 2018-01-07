package rs.lukaj.stories.runtime;

/**
 * Used to listen for state changes. It doesn't catch list modifications in state, as those are
 * internal data structures, nor constants as the only way language permits setting those is
 * programmatically. Instead, it allows implementors to get notice when a variable is about to be,
 * and after it is changed and act accordingly. All methods have default empty implementations.
 */
public interface OnStateChangeListener {
    /**
     * Invoked when change is imminent. Can be used to capture previous value of the variable.
     * @param state State which is about to be modified
     * @param variableName variable name which is about to get changed
     */
    default void beforeVariableSet(State state, String variableName) {}

    /**
     * Invoked after variable has a new String value assigned to it.
     * @param state State which was modified
     * @param variableName variable which was modified
     * @param newValue new value of a variable, equivalent to state.getString(variableName)
     */
    default void afterVariableSet(State state, String variableName, String newValue) {}

    /**
     * Invoked after variable has a new double (numeric) value assigned to it.
     * @param state State which was modified
     * @param variableName variable which was modified
     * @param newValue new value of a variable, equivalent to state.getDouble(variableName)
     */
    default void afterVariableSet(State state, String variableName, double newValue) {}

    /**
     * Invoked after variable has a new boolean (true-false, i.e. 1. or 0.) value assigned to it.
     * @param state State which was modified
     * @param variableName variable which was modified
     * @param newValue new value of a variable, equivalent to state.getBoolean(variableName)
     */
    default void afterVariableSet(State state, String variableName, boolean newValue) {}



    /**
     * Invoked after variable was declared and assigned the initial null value. This method
     * gets called only when variable was declared for the first time, and subsequent
     * state.declareVariable(variableName) calls will have no effect. Variables usually
     * get declared while parsing the source file.
     * @param state State which was modified
     * @param variableName newly declared variable
     */
    default void onVariableDeclared(State state, String variableName) {}

    /**
     * Invoked when a variable is about to be undeclared, i.e. removed from the state. You can
     * capture its value here. Variables are undeclared using ! prefix in AssignStatement
     * @param state State which is about to be modified
     * @param variableName variable which is about to be removed
     */
    default void beforeVariableUndeclared(State state, String variableName) {}

    /**
     * Invoked after a variable was undeclared, i.e. removed from the state.
     * @param state State which was modified
     * @param variableName variable which was removed
     */
    default void afterVariableUndeclared(State state, String variableName) {}
}
