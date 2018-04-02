#include include.ch
#define is was

That is the other chapter

:char1
char1: Tell me something, what's your favourite food?
[food] Enter your favourite food //we're capturing answer in variable food here
#ifdef is
char1: That's nice, I like [food+ish] too
#endif

:>fun
#undef is
#!editor protected
Your result is [ordinal]

::fun:
   :string='25 //this is a actually a string with value of 25 (ignoring ')
   #ifdef is //is isn't defined here anymore
   :ordinal=string+3
   #endif
   #ifndef is
   :rand = randomInt(122)
   :str = 'rand
   :ordinal=string+str
   #endif
   Hey, I look like a procedure (who am I kidding?)
:>>

:a=asdf, b, c
:a=b?
    You shouldn't see this (asdf != null)
:b?
    You shouldn't see this (b is undeclared)
:b=c?
    You shouldn't see this (null != null)
:a=asdf?
    Bye bye!

#include include.ch