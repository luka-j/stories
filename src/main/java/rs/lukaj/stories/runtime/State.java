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
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.exceptions.LoadingException;
import rs.lukaj.stories.parser.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by luka on 3.6.17..
 */
public class State implements VariableProvider {

    private static class Value {

        private static final String SEP = "/";

        private final Type type;
        private final Object value;

        private Value(Type type, Object value) {
            if(type.typeClass.isInstance(type)) throw new ClassCastException("Mismatched value type: expected " + type.typeClass.getName() + ", got " + value);
            this.type = type;
            this.value = value;
        }

        private Value(String fromString) {
            String[] fields = fromString.split(SEP, 2);
            type = Type.getByMark(fields[0]);
            if(type == null) throw new LoadingException("State file is corrupted: invalid type mark " + fields[0]);
            switch (type) {
                case STRING:
                    value = fields[1];
                    break;
                case DOUBLE:
                    value = Double.parseDouble(fields[1]);
                    break;
                default: value = null; //need to shut up the compiler
            }
        }

        @Override
        public String toString() {
            return type.mark + SEP + value.toString();
        }
    }


    private static final String SEP = ":";
    private static final Pattern INVALID_NAMES = Pattern.compile("^.*[&|%+*<>=/\\\\\\-]+.*$|(.*([?:])$)");

    public static void checkName(String name) throws InterpretationException {
        if(!Character.isLetter(name.charAt(0)) && name.charAt(0) != '_')
            throw new InterpretationException("Variable name doesn't start with a letter!");
        if(INVALID_NAMES.matcher(name).matches())
            throw new InterpretationException("Variable name is invalid (contains +, %, *, =, /, \\, -, or ? or : at the end)");
        if(Functions.getBuiltinFunction(name) != null)
            throw new InterpretationException("Variable name is a function name!");
    }

    protected State() {

    }

    protected State(File file) throws IOException {
        List<String> vars = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        for(String var : vars) {
            String[] fields = var.split(SEP, 2);
            Value val = new Value(fields[1]);
            variables.put(fields[0], val);
        }
    }

    protected void saveToFile(File file) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        for(Map.Entry<String, Value> e : variables.entrySet()) {
            out.write(e.getKey());
            out.write(SEP);
            out.write(e.getValue().toString());
            out.write("\n");
        }
        out.close();
    }

    private Map<String, Value> variables = new HashMap<>();

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
        if(!variables.containsKey(name)) return null;
        return variables.get(name).value.toString();
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
        if(var.type == Type.DOUBLE) return (Double)var.value;
        else return Double.NaN;
    }

    public Double getOrDefault(String name, double value) {
        Double val = getDouble(name);
        if(val==null || val == Double.NaN) return value;
        else return val;
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
