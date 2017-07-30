package rs.luka.stories.runtime;

import net.objecthunter.exp4j.VariableProvider;
import net.objecthunter.exp4j.function.Functions;
import rs.luka.stories.Utils;
import rs.luka.stories.environment.FileProvider;
import rs.luka.stories.exceptions.InterpretationException;
import rs.luka.stories.parser.Type;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static rs.luka.stories.parser.Type.isTruthy;

/**
 * Created by luka on 3.6.17..
 */
public class State implements VariableProvider {

    private static class Value {

        private Type type;
        private Object value;

        private Value(Type type, Object value) {
            if(type.typeClass.isInstance(type)) throw new ClassCastException("Mismatched value type: expected " + type.typeClass.getName() + ", got " + value);
            this.type = type;
            this.value = value;
        }
    }

    private Map<String, Value> variables = new HashMap<>();

    private static final Pattern INVALID_NAMES = Pattern.compile("^.*[&|%+*<>=/\\\\\\-]+.*$|(.*([?:])$)");

    public static void checkName(String name) throws InterpretationException {
        if(!Character.isLetter(name.charAt(0)))
            throw new InterpretationException("Variable name doesn't start with a letter!");
        if(INVALID_NAMES.matcher(name).matches())
            throw new InterpretationException("Variable name is invalid (contains +, %, *, =, /, \\, -, or ? or : at the end)");
        if(Functions.getBuiltinFunction(name) != null)
            throw new InterpretationException("Variable name is a function name!");
    }

    public void setVariable(String name, String value) throws InterpretationException {
        checkName(name);
        variables.put(name, new Value(Type.STRING, value));
    }

    public void setVariable(String name, boolean value) throws InterpretationException {
        checkName(name);
        variables.put(name, new Value(Type.DOUBLE, value? 1. : 0.));
    }

    public void setVariable(String name, double value) throws InterpretationException {
        checkName(name);
        variables.put(name, new Value(Type.DOUBLE, value));
    }

    public void setFlag(String name) throws InterpretationException {
        setVariable(name, true);
    }

    public String getString(String name) {
        if(Utils.isDouble(name)) return name;
        return variables.get(name).value.toString();
    }

    public boolean getBool(String name) {
        return isTruthy(getDouble(name));
    }

    public File getImage(String name, FileProvider files) {
        return files.getImage(getString(name));
    }

    public Double getDouble(String name) {
        if(Utils.isDouble(name)) return Double.parseDouble(name);
        Value var = variables.get(name);
        if(var == null) return null;
        if(var.type == Type.DOUBLE) return (Double)var.value;
        else return Double.NaN;
    }

    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    public boolean isDouble(String name) {
        if(variables.get(name) == null) return false;
        return variables.get(name).type == Type.DOUBLE;
    }

    @Override
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}
