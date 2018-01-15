# Stories
#### An interactive storytelling language

[![](https://jitpack.io/v/luq-0/stories.svg)](https://jitpack.io/#luq-0/stories)

_Looking for Android special variables? See [here](https://github.com/luq-0/stories/android-vars.md)_

For early and probably confusing sneak peek look at books/sample.
This project consists of a [exp4j](https://github.com/luq-0/exp4j) fork
as well, though it should probably belong in a separate module.
For purposes of this readme, current package (i.e. `.`) is assumed to
be rs.lukaj.stories.

Almost as pretty as php, but thankfully much smaller and less
general-purpose. Turing complete, though implementing any non-trivial
algorithm will probably be quite tedious.

## Idea

Provide an open-source language and interpreter for creating
choice-driven stories, presenting the user the opportunity to steer the
plot of the story by making their own choices. Most of the stories
should consist of dialogue, with some optional narrative. The user
should steer only the behaviour of one character by making choices in
their name, though this is in no way required.

Because the language is Turing complete, by nature it allows arbitrary
programs to be written, in form of conversational interfaces.

## Aim

This project is packaged as a Java library, not targeting any particular
environment. The aim of this project _isn't_ to provide a full implementation
for any environment, but rather to define the language and implement a
environment-agnostic interpreter. There is, however, a minimal debug
implementation targeting a specific Linux setup executed from a console
defined by `.runtime.DebugExecution`, `.environment.BasicTerminalDisplay`
and `.environment.LinuxDebugFiles` classes.
It can be used as a starting example, but is in no way thorough.

Secondary aim is compatibility with Android, at this moment at least to
Android 4.1. There _shall_ be at least one version per major revision
fully compatible with Android.

## Implementing the project

In order to implement the interpreter, you shall implement the interfaces
found in the `environment` package (i.e. `DisplayProvider` and `FileProvider`).
These define how the interpreter interacts with the environment.
FileProvider specifies where to find files interpreter is looking for,
files being either source code for stories or images. DisplayProvider
specifies how content is shown to the end user and all methods _must_ be
implemented in a blocking fashion. Asynchronous execution is a possibility
for the future, but not the priority at this moment.

Main point of entry for this library is the `.runtime.Runtime` class.

## Exceptions

There are four types of exceptions which can be raised, residing in the
`.exceptions` package.

LoadingException is thrown in case the book can't be loaded. This occurs
before interpretation phase, and is most likely an I/O error.

PreprocessingException and subtypes are raised during the preprocessing
phase. It be either a malformed # directive or directive which explicitly
raises the exception (#require or #error).

InterpretationException signals an error during the transforming the
source into sensible model and is most likely a syntax error.

ExecutionException is raised during the execution of the chapter, and
shall be inspected further in order to determine the cause.

## Hierarchy

#### Structure

Top-level object is a Book, which is broken up into chapters. The book
consists of a title and optional metadata and represents one story.
One or more chapters are where the actual content resides. Progress is
by default saved after each chapter. All chapters share the same state
and scope. Chapters consist of line-separated statements which
manipulate state or show output to the user.

#### Files

The book is represented by a directory in the filesystem, which is
obtained by a call to the `FileProvider#getRootDirectory(String)`
with the book name as the argument. Directory name _shall_ be the
book name. This, then, disqualifies certain characters such as file
separator (and a bunch of others on Windows) and reserved file names
(such as . and ..) as book names, so this _needn't_ be strictly
followed if implementation deems this requirement is too strict.

Current state is saved inside book root directory, in a file named
`.state`. It follows syntax described in the State section.

In case additional metadata for the book needs to be specified (e.g.
book title, if it isn't the same as folder name), it can be done in the
`.info` file which is in book root directory, following the state file
syntax. State loaded from the file is available using
`.runtime.Book#getBookInfo` method.

Each chapter is represented by a file named as `{no} {chapter_name}.ch`
inside source directory obtained by `FileProvider#getSourceDirectory(String)`
(argument being the book name; it can be the same as the root directory)
where `{no}` _shall_ be chapter number as a positive integer, starting
at one, and `{chapter name}` is treated as the name of the chapter.
These _must_ be separated by a space. Chapters are loaded in order of
their `{no}`s, so chapter numbers _needn't_ be consecutive, but only in
natural order.

## State and variables

#### Types

The interpreter differentiates between two fundamental types: `String`s
and `Double`s, as defined by the JLS. The language itself is untyped.
`String` evaluates as `NaN` when used in context where `Double` is
expected. `Double` is wrapped into a `String` when used in a context
where `String` is expected. There is a `Boolean` type which is a `Double`,
1 for true and 0 for false value. 0 and NaN evaluate as false, and any
other value as true. All Doubles but 0 are truthy values, while any
String but an empty one is truthy. `True` and `False` are predefined
constant variables which will raise an ExecutionException in case their
modification is attempted, initialized to 1.0 and 0.0 respectively.

There is a `Null` type, evaluating to either empty String or NaN (i.e.
it's definitely false) which is the default for all variables lacking
an initial value, including unanswered timed questions.

#### Evaluating expressions

If the expression contains only numeric (i.e. Double) variables or
consists of either &, |, <, >, *, /, ^ or - operator, it is a
numeric expression. Otherwise, it's a string expression.

In string expressions, + represents concatenation operator and = string
equality which is case-sensitive. All doubles are treated as strings in
this context. Result of concatenation is a string, and of equality
comparison a boolean. Expression in form !string can be used to check
for string emptiness (or nullity, as those are the same): it returns
1 (true) if string is empty, 0 (false) otherwise.

In numeric expressions, = represents equality, < and > less than and
greater than respectively, <= and >= greater than or equal and less than
or equal, + and - are plus and minus operators, & and | are and and or
operators (treating operands as booleans), ! is a not (treating operand
as a boolean), * and / are multiplication and division and ^ is
exponentiation. Certain functions are available as well, as defined
by the exp4j. Comparison operators and &, | and ! result in a boolean,
while others' result is a double.

Comparison operators have the lowest precedence, followed by not, then
or, then and. These are all lower than other operators, as defined by
exp4j.

#### Predefined constants and functions

Predefined constants are `True` which equals to 1.0 and `False` which
equals to 0.0.

Predefined functions are sin, cos, tan, cot, log, log2, log10, log1p,
abs, acos, asin, atan, cbrt, floor, sinh, sqrt, tanh, cosh, ceil, pow,
exp, expm1 and signum as defined in exp4j.

#### Variable names

Variable names _mustn't_ be empty strings.
Variable names _must_ start with either a letter or a underscore (_).
Variable names _mustn't_ include any operator character, a question
mark (?), a colon (:) or whitespace.
Additionally, variable name cannot be equal (in a case-sensitive way)
to any of the predefined constants or functions.

#### State and serialization

All values are stored in a `.runtime.State` instance as a Map. Any
duplicate variable definitions will result in an overwriting the old
value. A string can be assigned to the previously-numeric variable
and vice versa.

State is serialized as a line-separated string of
`{variable_name}:{type_mark}/{value}` where `{variable_name}` is the
variable name, `{type_mark}` is S for strings and N for any numeric
variable (Double, Boolean) and `{value}` is the value represented as a
string (for numeric variables, a base 10 representation, where decimal
part is delimited by a dot (.)). Each variable goes into its own line.
Variables are stored in no particular order.

Current state of the book can be obtained using `Book#getState` method.

#### Special variables

Special variables _shall_ start and end with at least one underscore.

Special variables used by this library start and end with two underscores.
Implementations are free to define their variables following the first
paragraph of this section. Developers writing books are not expected to
use variables starting and ending with an underscore.

Special variables present by default at this moment are the chapter
counter, \_\_chapter\_\_ and line counter \_\_line\_\_. Chapter counter
is 1-based index of the current chapter and is  incremented on each
chapter ending and fetched on each chapter beginning. If chapter counter
is larger than chapter count, book is regarded as finished. Line counter
uses line count which is part of Line object to keep track of last
executed line, and upon resume it loads first line whose line count is
less or equal to the value of \_\_line\_\_.

Special constant is \_\_LANG\_VERSION\_\_ which stores language version
as numeric value.

## Syntax and statements

### Source file structure

Source file consists of line-separated statements. If a line contains
a // sequence, it is split on the first // and only the first part
is treated as a statement; the rest is ignored (i.e. // designates
the beginning of a comment). Blank lines are ignored.

Each statement has an associated indent, i.e. number of spaces ( )
preceding it. Tabs (U+0009) are counted as four spaces, though their
usage is discouraged.

### Statement types

##### Comments
Lines starting with double slashes (//) are comments and are ignored
(i.e. they are thrown away while parsing).

##### Directives
Lines starting with a number sign (#) are directives. There are several
special directives which preprocessor recognizes:

#include {file} copies contents of {file} to current file. The file is
resolved relative to the source directory (as defined by FileProvider).
Maximum number of #include levels is 32.

#define {A} \[{B}] defines substitutions. If {B} isn't provided, the
directive is equal to #define {A} {A}. It is used to define substitutions
in code. It substitutes token A with string B (B can have spaces). Tokens
in this context refer to parts of source file split by space (' '),
parentheses ('(' and ')'), brackets ('\[' and ']'), question mark ('?'),
colon (':'), greater than sign ('>') or exclamation point ('!').

#undef {A} undefines substitution {A}, so it isn't performed anymore.

#ifdef {A} starts a conditional block which is preprocessed and compiled
only if {A} is a defined substitution.

#ifndef {A} starts a conditional block which is preprocessed and compiled
only if {A} isn't a defined substitution.

#if {A} starts a conditional block which is preprocessed and compiled
only if {A} is satisfied, where {A} is an expression which takes
variable from both book state and substitutions defined by #define. If
there is a both a variable and a substitution with the same name,
substitution has precedence.

#endif ends a conditional block

#error raises a TriggerPreprocessorError exception

#require {A} raises a RequireNotSatisfied exception if {A} isn't
satisfied. {A} is an expression defined the same way as {A} in #if
directive.

Non-preprocessor directives are attached to the following line and can
be obtained by Line#getDirectives, as well as be found in source code.
Directives can use any character, but preprocessor directives are
guaranteed to start with an alphabetic character.

##### Statement block marker
Lines which equal three colons (i.e. :::) are statement block markers.
All lines following them with larger indent are treated as procedural
statements (with leading :), except if they start with backslash in
which case they are treated as either Speech or Narrative. Comments are
ignored and not counted for determining block indent, meaning you can
have lesser indented comment inside statement block without it affecting
the 'larger indent' chain.

##### Procedural statements
Lines starting with a colon (:) are procedural statements and modify
either the state or the flow. There are several types:

Procedural statements ending with a question mark (?) are if-statements.
Their body is evaluated, and if the result is truthy, execution carries
on to the next line. If the result is falsy, execution jumps to the next
line with the same or less indent. Body of the if-statement (i.e.
statements which are executed only if the result is truthy) _must_ have
indent larger than that of the if-statement. If-statements can be found
inside questions, in which case the body must consist solely of
answers or other if-statements. In that case, if if-statement is
fulfilled, the answer is displayed to the user.

Procedural statements ending with a colon (:) are labels and mark a
certain place in code. They are no-ops.

Procedural statements starting and ending with a colon (:) are procedural
labels. When reached through normal execution (i.e. not by goto), they
jump to next return-statement. When reached by goto, they memorize
the goto from which the jump was made.

Procedural statements starting with a greater than sign (>, i.e. :>)
are goto-statements. Body of a goto-statement _must_ contain a label. When
execution reaches a goto-statement, it is redirected to the label and
continues from there on. In case there is no label with the name
designated by a goto-statement an InterpretationException is thrown.
Body of a goto-statement can optionally consist of a condition, followed
by a question mark (?), followed by a label name. In this case, the jump
is executed iff the condition is fulfilled.

Procedural statement equalling two greater then signs (i.e. :>>) is a
return-statement. They _must_ follow a procedural label. When reached,
they jump to the goto which associated procedural label memorized. These
can be used to simulate simple procedures, with an important cavaet
that recursion isn't allowed since there is no stack.

All other procedural statements are evaluated as assign-statements.
Assign-statements are a comma-separated list of assignments.
Assignments are split on the first equals (=) sign. First part
represents a variable name and the second an expression to be
evaluated. First part _must_ be a valid variable name, optionally with
a leading exclamation point ('!'), otherwise an InterpretationException
is thrown. In case there are no equals signs in the assign-statement
body, expression is set to empty string. At runtime, the expression is
evaluated, and it is stored into the appropriately named variable.
Assignments whose variables are preceded by an exclamation points are
undeclarations, i.e. after they are executed variable is removed from
the state. Undeclarations cannot contain an equals sign.

##### Halt
Lines which equal ;; are unconditional halts. They end the current
chapter.

##### Questions
Lines starting with a question mark (?) are questions to which the end
user should choose the desired answer. The syntax for question is as
follows: `?{(time)}{[variable_name]}{character}:{question_text}`.

`{(time)}` is an optional parameter, specifying time user has to make a
choice enclosed in parenthesis, optionally ending with a `s`, `ms` or
`min` suffix denoting time unit (by default seconds), as a positive
decimal number (by default unlimited).
`{[variable_name]}` is variable name to which the result of this
question should be stored, enclosed in square brackets and is required.
It _must_ be a valid variable name; otherwise, InterpretationException
is thrown.
`{character}` is the character presenting the question to the user and
is optional. `{question_text}` is the text of the question presented to
the user.

The result of user's choice is stored in a variable, whose value is a
string which is equal to the answers' value.

##### Answers
Lines starting with an asterisk (*) are answers, i.e. possible choices
to the previously posed question. These _must_ follow the question and
_shall_ have larger indent than the question. The syntax for an answer
is: `*{[value]}{answer_text}` where `{[value]}` is the answer value
(to which question variable is set if the answer is chosen) enclosed
in square brackets and `{answer_text}` is the text of the answer which
should be presented to the user. Answer value must be a valid variable
name, otherwise an InterpretationException is thrown. If the answer is
chosen, variable named as answer value is created and set to true;
otherwise, it's set to false.

If `{answer_text}` is an existing picture, the question is treated as a
picture question and given answer as a picture answer. Picture questions
_must_ have all picture answers; otherwise, an `InterpretationException`
will be thrown. It is therefore recommended to either prefix picture
names or use an extension when referring to the picture. Picture
question's answers are presented as pictures instead of textual answers
to the user, and are otherwise identical to the textual questions.

_Note: there are no implementations which support picture questions at
this moment._

##### Textual input
Lines starting with an opening square bracket (\[) and containing a
closing square bracket (\]) are signal for requesting textual input from
the user. Enclosed in the brackets is the variable to which the input
should be saved, and any additional text is passed as hint to the user.
As of this moment, there is no way to associate any character with the
textual input.

##### Speech
Lines containing a colon (:), but not starting with it, whose part
before the colon is a previously defined variable (i.e. through
assign-statement) is treated as character speech. Value of the variable,
if not an empty string or of Null type, represents the image of the
character's avatar which will be displayed to the user. Second part of
the statement (after the colon) is the line character speaks. As of this
moment, there is no limit to the speech length and implementation should
be able to handle all lengths.

##### Narrative
All other lines are treated as narratives and are displayed to the user
as such.

### Variable substitution

Inside questions, answers, speech and narrative, variable substitution
is performed. Variable substitution refers to a process in which every
variable name enclosed in square brackets is replaced with that
variable's value. In case variable with such name doesn't exist, the
square brackets are taken literally. So, when using state which contains
variable five set to 5, line `Explain it to me like I'm [five]` would
show as a narrative with text `Explain to to me like I'm 5`.



//todo whitespace rules inside statements (usually ignored), escaping

## License

**GNU LGPLv3**
