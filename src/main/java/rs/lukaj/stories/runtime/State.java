/*
  Stories - an interactive storytelling language
  Copyright (C) 2017 Luka Jovičić

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
import java.util.*;
import java.util.regex.Pattern;

import static rs.lukaj.stories.parser.Type.*;
import static rs.lukaj.stories.parser.Type.P.CONST;
import static rs.lukaj.stories.parser.Type.P.NUMERIC;

/**
 * Created by luka on 3.6.17..
 */
public class State implements VariableProvider {

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

        @Override
        public String toString() {
            switch (type) {
                case STRING:
                    return (String)value;
                case DOUBLE:
                case CONSTANT_DOUBLE:
                    return value.toString();
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



    private Map<String, Value> variables = new HashMap<>();

    private void setPredefinedConstants() {
        variables.put("True", new Value<>(CONSTANT_DOUBLE, 1.));
        variables.put("False", new Value<>(CONSTANT_DOUBLE, 0.));
    }

    /**
     * Creates an empty state object, with only predefined constants present.
     */
    public State() {
        setPredefinedConstants();
    }

    protected State(File file) throws IOException {
        List<String> vars = Utils.readAllLines(file);
        for(String var : vars) {
            String[] fields = var.split(SEP, 2);
            Value val = new Value(fields[1]);
            variables.put(fields[0], val);
        }
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

    public void declareVariable(String name) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.NULL, null));
    }

    public void setVariable(String name, String value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.STRING, value));
    }

    public void setVariable(String name, boolean value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.DOUBLE, value? 1. : 0.));
    }

    public void setVariable(String name, double value) throws InterpretationException {
        checkName(name);
        checkCanModify(name);
        variables.put(name, new Value<>(Type.DOUBLE, value));
    }

    private Value<List<String>> getStringListImpl(String listName) {
        Value<List<String>> list = variables.get(listName);
        if(list == null) return null;
        if(list.type != STRING_LIST)
            throw new ExecutionException("Attempting to append to non-list variable");
        return list;
    }

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
     * @param index index of the desired member
     * @return listName[index]
     */
    public String getFromList(String listName, int index) {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null || list.value.size() >= index) return null;
        return list.value.get(index);
    }

    /**
     * Removes a member from {@link Type#STRING_LIST} if it exists.
     * @param listName name of the list
     * @param index index of the member to be removed
     */
    public void removeFromList(String listName, int index) {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null || list.value.size() >= index) return;
        else list.value.remove(index);
    }

    public void replaceInList(String listName, int index, String newValue) throws InterpretationException {
        Value<List<String>> list = getStringListImpl(listName);
        if(list == null) addToList(listName, newValue);
        else if(list.value.size() >= index) list.value.add(newValue);
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
        if(var.type == Type.DOUBLE) return ((Number)var.value).doubleValue();
        else return Double.NaN;
    }

    public Double getOrDefault(String name, double defValue) {
        Double val = getDouble(name);
        if(val==null || val == Double.NaN) return defValue;
        else return val;
    }

    public String getOrDefault(String name, String defValue) {
        String val = getString(name);
        if(val == null) return defValue;
        return val;
    }

    public Set<String> getVariableNames() {
        return variables.keySet();
    }

    public boolean isNumeric(String name) {
        if(variables.get(name) == null) return false;
        return variables.get(name).type.is(NUMERIC);
    }

    @Override
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}
