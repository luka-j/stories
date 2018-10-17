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

package rs.lukaj.stories.parser;

import rs.lukaj.stories.Utils;
import rs.lukaj.stories.environment.FileProvider;
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.exceptions.PreprocessingException;
import rs.lukaj.stories.exceptions.RequireNotSatisfiedException;
import rs.lukaj.stories.exceptions.TriggerPreprocessorError;
import rs.lukaj.stories.runtime.State;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Preprocessor {
    private static final int MAX_INCLUDE_LEVEL = 32;

    private static final String INCLUDE = "#include ";
    private static final String DEFINE = "#define ";
    private static final String UNDEFINE = "#undef ";

    private static final String IF = "#if ";
    private static final String ERROR = "#error";
    private static final String IFDEFINED = "#ifdef ";
    private static final String IFNOTDEFINED = "#ifndef ";
    private static final String ENDIF = "#endif";

    private static final String REQUIRE = "#require ";

    private List<String> lines;
    public Preprocessor(List<String> lines) {
        this.lines = lines;
    }

    private final Map<String, String> defines = new HashMap<>();
    private final Deque<Boolean> ifdefs = new ArrayDeque<>();

    public List<String> process(FileProvider files, String path, State state) {
        return process(files, path, state, 0);
    }

    private List<String> process(FileProvider files, String path, State state, int level) {
        if(level >= MAX_INCLUDE_LEVEL)
            throw new PreprocessingException("Too many #include levels! Check for circular includes");

        List<String> result = new ArrayList<>(lines.size());
        boolean ignore = false;
        for(String line : lines) {
            String l = line.trim();
            if(ignore && !l.startsWith(ENDIF) && !l.startsWith("#if")) continue;
            if(!l.startsWith("#") && !ignore) result.add(replaceDefines(line));
            else {
                if(l.startsWith(INCLUDE) && l.contains(" ")) {
                    File include = files.getSourceFile(path, l.split("\\s+", 2)[1]);
                    if(include != null && include.isFile()) {
                        try {
                            List<String> includeLines = Utils.readAllLines(include);
                            result.addAll(new Preprocessor(includeLines).process(files, path, state, level+1));
                        } catch (IOException e) {
                            throw new PreprocessingException("I/O exception while including file");
                        }
                    }
                } else if(l.startsWith(DEFINE)) {
                    String[] tokens = l.split("\\s+", 3);
                    String val = tokens.length < 3 ? tokens[1] : tokens[2];
                    defines.put(tokens[1], val);
                    try {
                        state.setVariable(tokens[1], val); //this is fine, as provided State is only a copy
                    } catch (InterpretationException e) {
                        ; //ignore if isn't a valid variable name
                    }
                } else if(l.startsWith(UNDEFINE)) {
                    String[] tokens = l.split("\\s+", 2);
                    //if(tokens.length < 2) defines.clear(); //won't really happen, as directives have trailing space
                    defines.remove(tokens[1]);
                } else if(l.startsWith(IFDEFINED) || l.startsWith(IFNOTDEFINED)) {
                    String[] tokens = l.split("\\s+", 2);
                    boolean sat = defines.containsKey(tokens[1]);
                    if(l.startsWith(IFNOTDEFINED)) sat = !sat;
                    ifdefs.add(sat);
                    if(!sat) ignore = true;
                } else if(l.startsWith(IF)) {
                    String[] tokens = l.split("\\s+", 2);
                    try {
                        boolean sat;
                        Expressions expr = new Expressions(tokens[1], state);
                        sat = Type.isTruthy(expr.eval());
                        ifdefs.add(sat);
                        if (!sat) ignore = true;
                    } catch (InterpretationException e) {
                        ;
                    }
                } else if(l.equals(ENDIF)) {
                    if(!ifdefs.removeLast() && !ifdefs.contains(false)) ignore = false;
                    //if removed element is true, and it doesn't contain any more `true`s
                } else if(l.equals(ERROR)) {
                    String[] tokens = l.split("\\s+", 2);
                    String msg = tokens.length == 2 ? tokens[1] : "";
                    throw new TriggerPreprocessorError(msg);
                } else if(l.startsWith(REQUIRE)) {
                    String[] tokens = l.split("\\s+", 2);
                    int comm = tokens[1].indexOf("//");
                    if(comm > 0)
                        tokens[1] = tokens[1].substring(0, comm);
                    try {
                        Expressions expr = new Expressions(tokens[1], state);
                        boolean sat = Type.isTruthy(expr.eval());
                        if(!sat) throw new RequireNotSatisfiedException(expr.literal);
                    } catch (InterpretationException e) {
                        throw new PreprocessingException("Interpretation exception while evaluating #require", e);
                    }
                } else {
                    result.add(line); //let's keep directives for now - without #define substitution
                }
            }
        }

        return result;
    }

    private static final Set<Character> tokenBreakChars = new HashSet<>();
    static {
        tokenBreakChars.add(' ');
        tokenBreakChars.add('(');
        tokenBreakChars.add(')');
        tokenBreakChars.add('[');
        tokenBreakChars.add(']');
        tokenBreakChars.add('?');
        tokenBreakChars.add(':');
        tokenBreakChars.add('>');
        tokenBreakChars.add('!');
    }
    private String replaceDefines(String line) {
        StringBuilder res = new StringBuilder(line.length()), buff = new StringBuilder(32);
        for(int i=0; i<line.length(); i++) {
            char ch = line.charAt(i);
            if(tokenBreakChars.contains(ch)) {
                if(buff.length() > 0) {
                    String append = buff.toString();
                    res.append(Utils.getOrDefault(defines, append, append));
                    buff.delete(0, buff.length());
                }
                res.append(ch);
            } else {
                buff.append(ch);
            }
        }

        String append = buff.toString();
        res.append(Utils.getOrDefault(defines, append, append));

        return res.toString();
    }
}
