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

package rs.lukaj.stories.parser;

import rs.lukaj.stories.Utils;
import rs.lukaj.stories.environment.FileProvider;
import rs.lukaj.stories.exceptions.PreprocessingException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Preprocessor {
    private static final String INCLUDE = "#include";
    private static final String DEFINE = "#define";
    private static final String UNDEFINE = "#undef";

    private List<String> lines;
    public Preprocessor(List<String> lines) {
        this.lines = lines;
    }

    private Map<String, String> defines = new HashMap<>();

    public List<String> process(FileProvider files, String path) {
        List<String> result = new ArrayList<>(lines.size());
        for(String line : lines) {
            String l = line.trim();
            if(!l.startsWith("#")) result.add(replaceDefines(line));
            else {
                if(l.startsWith(INCLUDE) && l.contains(" ")) {
                    File include = files.getSourceFile(path, l.split("\\s+", 2)[1]);
                    if(include != null && include.isFile()) {
                        try {
                            List<String> includeLines = Utils.readAllLines(include);
                            result.addAll(includeLines);
                        } catch (IOException e) {
                            throw new PreprocessingException("I/O exception while including file");
                        }
                    }
                } else if(l.startsWith(DEFINE)) {
                    String[] tokens = l.split("\\s+", 3);
                    if(tokens.length != 3) continue;
                    defines.put(tokens[1], tokens[2]);
                } else if(l.startsWith(UNDEFINE)) {
                    String[] tokens = l.split("\\s+", 2);
                    if(tokens.length != 2) defines.clear();
                    else defines.remove(tokens[1]);
                }
            }
        }

        return result;
    }

    private static Set<Character> tokenBreakChars = new HashSet<>();
    static {
        tokenBreakChars.add(' ');
        tokenBreakChars.add('(');
        tokenBreakChars.add(')');
        tokenBreakChars.add('[');
        tokenBreakChars.add(']');
        tokenBreakChars.add('?');
        tokenBreakChars.add(':');
        tokenBreakChars.add('>');
    }
    private String replaceDefines(String line) {
        StringBuilder res = new StringBuilder(line.length()), buff = new StringBuilder(32);
        for(int i=0; i<line.length(); i++) {
            char ch = line.charAt(i);
            if(tokenBreakChars.contains(ch)) {
                if(buff.length() > 0) {
                    String append = buff.toString();
                    if (defines.containsKey(append)) {
                        res.append(defines.get(append));
                    } else {
                        res.append(append);
                    }
                    buff.delete(0, buff.length());
                }
                res.append(ch);
            } else {
                buff.append(ch);
            }
        }

        String append = buff.toString();
        if (defines.containsKey(append)) {
            res.append(defines.get(append));
        } else {
            res.append(append);
        }

        return res.toString();
    }
}
