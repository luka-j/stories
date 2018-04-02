/*
  Stories - an interactive storytelling language
  Copyright (C) 2017-2018 Luka Jovičić

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package rs.lukaj.stories.runtime;

import net.objecthunter.exp4j.VariableProvider;
import net.objecthunter.exp4j.function.Functions;
import rs.lukaj.stories.Utils;
import rs.lukaj.stories.environment.FileProvider;
import rs.lukaj.stories.exceptions.ExecutionException;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.exceptions.LoadingException;
import rs.lukaj.stories.parser.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

import static rs.lukaj.stories.parser.Type.*;
import static rs.lukaj.stories.parser.Type.P.*;

/**
 * Created by luka on 3.6.17..
 */
public class State implements VariableProvider {

    public static final String TRUE = "True";
    public static final String FALSE = "False";
    private static final String VERSION = "__LANG_VERSION__";

    //gawd I hate generics
    @SuppressWarnings("unchecked") //we do sorcery here
    private static class Value<T> {

        private static final char ARRAY_SEPARATOR_CHAR = '\u001c';
        private static final String ARRAY_SEPARATOR = ARRAY_SEPARATOR_CHAR + "";

        private static final String SEP = "/";

        private final Type type;
        private final T value;

        private Value(Type type, T value) {
            if(type != NULL && !type.typeClass.isInstance(value))
                throw new ClassCastException("Mismatched value type: expected " + type.typeClass.getName() + ", got " + value);
            this.type = type;
            this.value = value;
        }

        private Value(String fromString) {
            String[] fields = fromString.split(SEP, 2);
            type = getByMark(fields[0]);
            if(type == null) throw new LoadingException("State file is corrupted: invalid type mark " + fields[0]);
            switch (type) {
                case STRING:
                    value = (T)fields[1];
                    break;
                case DOUBLE:
                case CONSTANT_DOUBLE:
                    value = (T)(Double)Double.parseDouble(fields[1]);
                    break;
                case STRING_LIST:
                    value = (T) new ArrayList<>(Arrays.asList(fields[1].split(ARRAY_SEPARATOR)));
                    break;
                case NULL:
                    value = null;
                    break;
                default: value = null; //need to shut up the compiler
            }
        }

        private static DecimalFormat FORMAT = new DecimalFormat("0.##");
        @Override
        public String toString() {
            switch (type) {
                case STRING:
                    return (String)value;
                case DOUBLE:
                case CONSTANT_DOUBLE:
                    return FORMAT.format(value);
                case STRING_LIST:
                    return value.toString();
                case NULL:
                    return "";
                default:
                    return null;
            }
        }

        public String serialize() {
            if(type == STRING_LIST) {
                StringBuilder sb = new StringBuilder(64);
                sb.append(type.mark).append(SEP);
                List<String> list = (List<String>) value;
                for(String s : list)
                    sb.append(s).append(ARRAY_SEPARATOR_CHAR);
                return sb.toString();
            } else {
                return type.mark + SEP + String.valueOf(value);
            }
        }
    }

    private List<OnStateChangeListener> listeners = new ArrayList<>();
    private static final String SEP = ":";
    private static final Pattern INVALID_NAMES = Pattern.compile("^.*[&|%+*^<>=/\\\\\\-]+.*$|(.*([?:])$)");

    public static void checkName(String name) throws InterpretationException {
        if(name == null || name.isEmpty())
            throw new InterpretationException("Variable name is null or empty");
        if(!Character.isLetter(name.charAt(0)) && name.charAt(0) != '_')
            throw new InterpretationException("Variable name doesn't start with a letter!");
        if(INVALID_NAMES.matcher(name).matches())
            throw new InterpretationException("Variable name is invalid (contains +, %, *, =, /, \\, -, or ? or : at the end)");
        if(Functions.getBuiltinFunction(name) != null)
            throw new InterpretationException("Variable name is a function name!");
    }



    private final Map<String, Value> variables;

    private void setPredefinedConstants() {
        variables.put(TRUE, new Value<>(CONSTANT_DOUBLE, 1.));
        variables.put(FALSE, new Value<>(CONSTANT_DOUBLE, 0.));
        variables.put(VERSION, new Value<>(CONSTANT_DOUBLE, Runtime.VERSION));
    }

    /**
     * Creates an empty state object, with only predefined constants present.
     */
    public State() {
        variables = new HashMap<>();
        setPredefinedConstants();
    }

    protected State(File file) throws IOException {
        List<String> vars = Utils.readAllLines(file);
        variables = new HashMap<>();
        for(String var : vars) {
            if(var.isEmpty()) continue;
            String[] fields = var.split(SEP, 2);
            if(fields.length < 2) continue;
            Value val = new Value(fields[1]);
            variables.put(fields[0], val);
        }
    }

    /**
     * Creates a new State object with the same variables as the provided one, but without listeners
     * associated to the original.
     * @param state original state which should be copied.
     */
    public State(State state) {
        variables = new HashMap<>(state.variables);
    }

    public void addOnStateChangeListener(OnStateChangeListener listener) {
        if(listener != null)
            listeners.add(listener);
    }

    private void checkCanModify(String name) {
        if(variables.containsKey(name) && variables.get(name).type.is(CONST))
            throw new ExecutionException("Attempting to modify a const value");
    }

    public void saveToFile(File file) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        for(Map.Entry<String, Value> e : variables.entrySet()) {
            out.write(e.getKey());
            out.write(SEP);
            out.write(e.getValue().serialize());
            out.write("\n");
        }
        out.close();
    }

    /**
     * Declares a new variable and sets it to null if it doesn't exist;
     * if the variable already exists, does nothing
     * @param name variable name
     * @throws InterpretationException in case name is invalid
     */
    public void declareVariable(String name) throws InterpretationException {
        if(variables.containsKey(name)) return;
        checkName(name);
        //checkCanModify(name); //not needed - we aren't modifying anything already there
        variables.put(name, new Value<>(Type.NULL, null));
        for(OnStateChangeListener listener : listeners) listener.onVariableDeclared(this, name);
    }

    /**
     * Removes a variable with this name from the state. If such variable doesn't
     * exist, this method does nothing.
     * @param name variable's name
     */
    public void undeclareVariable(String name) {
        if(name == null) return;
        checkCanModify(name);
        for(OnStateChangeListener listener : listeners) listener.beforeVariableUndeclared(this, name);
        variables.remove(name);
        for(OnStateChangeListener listener : listeners) listener.afterVariableUndeclared(this, name);
    }

    public void setVariable(String name, String value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        if(value == null) variables.put(name, new Value<>(Type.NULL, null));
        else {
            if (value.startsWith("'")) {
                value = value.substring(1);
                if (hasVariable(value)) value = String.valueOf(getDouble(value));
            }
            for(OnStateChangeListener listener : listeners) listener.beforeVariableSet(this, name);
            variables.put(name, new Value<>(Type.STRING, value));
            for(OnStateChangeListener listener : listeners) listener.afterVariableSet(this, name, value);
        }
    }

    public void setVariable(String name, boolean value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        for(OnStateChangeListener listener : listeners) listener.beforeVariableSet(this, name);
        variables.put(name, new Value<>(Type.DOUBLE, value? 1. : 0.));
        for(OnStateChangeListener listener : listeners) listener.afterVariableSet(this, name, value);
    }

    public void setVariable(String name, double value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        for(OnStateChangeListener listener : listeners) listener.beforeVariableSet(this, name);
        variables.put(name, new Value<>(Type.DOUBLE, value));
        for(OnStateChangeListener listener : listeners) listener.afterVariableSet(this, name, value);
    }

    //setConstant methods are designed to be used by implementations to provide their own constants, if necessary
    //once set, constants cannot be overriden, not even by these methods
    //also, these methods don't trigger listeners

    public void setConstant(String name, String value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.CONSTANT_STRING, value));
    }
    public void setConstant(String name, double value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.CONSTANT_DOUBLE, value));
    }

    /**
     * Sets decimal format used by {@link #getString(String)} when getting a variable whose internal type is double.
     * If supplied format is null, does nothing.
     * @param format format to use for formatting double variables as strings
     */
    public void setDecimalFormat(DecimalFormat format) {
        if(format != null) {
            Value.FORMAT = format;
        }
    }

    private Value<List<String>> getStringListImpl(String listName) {
        Value<List<?>> list = variables.get(listName);
        if(list == null) return null;
        if(list.type != STRING_LIST)
            throw new ExecutionException("Attempting to modify a non-(String-)list variable");
        return (Value<List<String>>)(Value<?>)list; //this wins the award for the most idiotic cast
        //also, did I mention I hate the lame excuse for generics in Java?
    }

    /**
     * Add an element (a string) to the list.
     * @param listName name of the list
     * @param value value to be added
     * @throws InterpretationException if list's name is invalid, or list is constant
     */
    public void addToList(String listName, String value) throws InterpretationException {
        checkName(listName);
        checkCanModify(listName);
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null) {
            List<String> newList = new ArrayList<>();
            newList.add(value);
            variables.put(listName, new Value<>(Type.STRING_LIST, newList));
        } else {
            list.value.add(value);
        }
    }

    /**
     * Inserts an element to a specific position in a list.
     * @param listName name of the list
     * @param index index to which to insert the element, 0-based
     * @param newValue value to be inserted
     * @throws InterpretationException if {@link #addToList(String, String)} throws
     */
    public void insertToList(String listName, int index, String newValue) throws InterpretationException {
        checkName(listName);
        checkCanModify(listName);
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null || index >= list.value.size()) addToList(listName, newValue);
        else list.value.add(index, newValue);
    }

    public void putList(String name, List<String> list) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        Value<List<String>> value = new Value<>(Type.STRING_LIST, list);
        variables.put(name, value);
    }
    /**
     * Retrieves a member from {@link Type#STRING_LIST}. If list doesn't exist,
     * or index is out of bounds, null is returned.
     * @param listName name of the list
     * @param index index of the desired member, 0-based
     * @return listName[index]
     */
    public String getFromList(String listName, int index) {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null || index >= list.value.size()) return null;
        return list.value.get(index);
    }

    /**
     * Removes a member from {@link Type#STRING_LIST} if it exists.
     * @param listName name of the list
     * @param index index of the member to be removed, 0-based
     */
    public void removeFromList(String listName, int index) {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null || index >= list.value.size()) return;
        else list.value.remove(index);
    }

    /**
     * Replaces specific element in list, if it exists. If it doesn't, adds it to the end.
     * @param listName name of the list
     * @param index index of the element to be replaced, 0-based
     * @param newValue new value for the element
     * @throws InterpretationException if {@link #addToList(String, String)} throws
     */
    public void replaceInList(String listName, int index, String newValue) throws InterpretationException {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null) addToList(listName, newValue);
        else if(index >= list.value.size()) list.value.add(newValue);
        else list.value.set(index, newValue);
    }

    /**
     * Returns a {@link Type#STRING_LIST} with this name. In case no such
     * list exists, an empty one is returned. Returned object is a copy,
     * i.e. it can be freely manipuleted without affecting the original list.
     * @param name list name
     * @return ArrayList representation of this variable
     */
    public ArrayList<String> getStringList(String name) {
        Value<List<String>> list = getStringListImpl(name);
        if(list == null) return new ArrayList<>();
        return new ArrayList<>(list.value);
    }

    public void setFlag(String name) throws InterpretationException {
        setVariable(name, true);
    }

    public String getString(String name) {
        if(Utils.isDouble(name)) return name;
        if(!variables.containsKey(name)) return null;
        return variables.get(name).toString();
    }

    /**
     * Returns value of a variable without type conversion. It can be (as of January 2018)
     * a String, a Double, a List of Strings or null. Modifying anything returned from this
     * method is safe, i.e. it won't affect the State itself. This method does not
     * differentiate between declared-but-yet-unassigned and undeclared variables, and
     * for both the return value will be null.
     * @param name variable name
     * @return raw value of the variable
     */
    @SuppressWarnings("unchecked")
    public Object getObject(String name) {
        if(Utils.isDouble(name)) return name;
        if(!variables.containsKey(name)) return null;
        else if(variables.get(name).type.is(LIST)) return new ArrayList<>((List<String>)variables.get(name));
        else return variables.get(name).value;
    }

    public boolean getBool(String name) {
        return Type.isTruthy(getDouble(name));
    }

    public File getImage(String name, FileProvider files) {
        return files.getImage(getString(name));
    }

    public Double getDouble(String name) {
        if(Utils.isDouble(name)) return Double.parseDouble(name);
        Value var = variables.get(name);
        if(var == null) return null;
        if(var.type.is(NUMERIC)) return ((Number)var.value).doubleValue();
        else return Double.NaN;
    }

    public Double getOrDefault(String name, double defValue) {
        if(isAssigned(name)) return getDouble(name);
        else return defValue;
    }

    public String getOrDefault(String name, String defValue) {
        if(isAssigned(name)) return getString(name);
        else return defValue;
    }

    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    public boolean isNumeric(String name) {
        return variables.get(name) != null && variables.get(name).type.is(NUMERIC);
    }

    public boolean isAssigned(String name) {
        return variables.containsKey(name) && variables.get(name).type != Type.NULL;
    }

    @Override
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public boolean hasAssignedVariable(String name) {
        return hasVariable(name) && variables.get(name).type != Type.NULL;
    }
}
