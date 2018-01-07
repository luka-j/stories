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
import rs.lukaj.stories.exceptions.InterpretationException;
import rs.lukaj.stories.parser.lines.*;
import rs.lukaj.stories.runtime.Chapter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static rs.lukaj.stories.Utils.Pair;
import static rs.lukaj.stories.parser.LineType.*;

public class Parser {

    private Line previousLine;
    private Line head;
    private boolean finished = false;
    private boolean insideStatementBlock;
    private int statementBlockIndent;
    private final Chapter chapter;
    private final List<Directive> directiveStack = new ArrayList<>();

    public Parser(Chapter chapter) {
        this.chapter = chapter;
    }

    /**
     * Parses all lines and returns head. Doesn't include comments or
     * statement block markers.
     * @param lines source file, separated by newline symbol
     * @return first line of the execution sequence
     * @throws InterpretationException if anything goes wrong (literally anything)
     */
    public Line parse(List<String> lines) throws InterpretationException {
        if(lines.isEmpty()) return new Nop(chapter, 0, 0);
        try {
            int lineNumber = 0;
            for (String line : lines) parse(line, lineNumber++);
            setJumps();
            return getHead();
        } catch (RuntimeException e) {
            throw new InterpretationException("Unknown interpretation exception", e);
        }
    }

    private String stripComments(String line) {
        int firstOccurence = line.indexOf("//");
        if(firstOccurence < 0) return line;
        if(line.charAt(firstOccurence-1) != '\\') return line.substring(0, firstOccurence).trim();

        int i = firstOccurence-1;
        StringBuilder res = new StringBuilder(line.length());
        for(int s=0; s<i; s++) res.append(line.charAt(s));
        for(;i<line.length()-1;i++) {
            if(i+2<line.length())
                if(line.charAt(i) == '\\' && line.charAt(i+1) == '/' && line.charAt(i+2) == '/')
                    continue;
            else if(line.charAt(i) == '/' && line.charAt(i+1) == '/' && line.charAt(i-1) != '\\')
                break;
            else
                res.append(line.charAt(i));
        }
        for(i = res.length()-1; i>=0 && Character.isWhitespace(i); i--)
            res.deleteCharAt(i);

        return res.toString();
    }

    /**
     * Parses a single line and appends it to the end, using this Parser's context
     * @param line String representing this line (including whitespace)
     * @param lineNumber line number of this line
     * @throws InterpretationException if type cannot be deduced or line cannot be parsed
     */
    //this is one mess of a method honestly
    public Line parse(String line, int lineNumber) throws InterpretationException {
        boolean escaped = false;
        int indent = Utils.countLeadingSpaces(line);
        line = line.trim(); // all lines are trimmed at the beginning, and indent is stored separately !!
        if(line.startsWith("\\")) escaped = true;
        if(escaped) line = line.substring(1);

        LineType type;
        if(insideStatementBlock && indent <= statementBlockIndent)
            insideStatementBlock = false;

        int commIndex = line.indexOf("//");
        if(commIndex > 0) line = stripComments(line);
        type = getType(line, chapter.getState(), escaped, insideStatementBlock);

        if(type == COMMENT) return COMMENT.parse(line, lineNumber, indent, chapter);
        if(type == STATEMENT_BLOCK_MARKER) {
            insideStatementBlock = true;
            statementBlockIndent = indent;
            return STATEMENT_BLOCK_MARKER.parse(line, lineNumber, indent, chapter);
        }

        Line current;
        current = type.parse(line, lineNumber, indent, chapter);

        if(head == null) head = current;
        if(previousLine != null) previousLine.setNextLine(current);
        if(current instanceof Directive) directiveStack.add((Directive) current);
        else {
            current.addDirectives(directiveStack);
            directiveStack.clear();
        }
        previousLine = current;
        return current;
    }

    public Line getHead() {
        if(!finished) System.err.println("warning: requested head before parsing is finished");
        return head;
    }

    private void setJumps() throws InterpretationException {
        Line curr = head;
        Deque<Pair<Integer, IfStatement>> indentStack = new LinkedList<>();
        while(curr != null) {
            Line next = curr.getNextLine();
            if(curr instanceof GotoStatement && !((GotoStatement)curr).hasSetJump()) {
                ((GotoStatement)curr).setJump(chapter);
            } //addressing only forward jumps here

            while(!indentStack.isEmpty() && curr.getIndent() <= indentStack.peekLast().a) {
                indentStack.pollLast().b.setNextIfFalse(curr);
            }
            if(curr instanceof IfStatement) {
                indentStack.push(new Pair<>(curr.getIndent(), (IfStatement) curr));
            }

            curr = next;
        }
        finished = true;
    }
}
