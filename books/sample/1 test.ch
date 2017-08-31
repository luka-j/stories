#this is a short test, meant for covering various ways of writing stuff here
 #not really representative

Hello World!
\??? //backslash is an escape character: everything starting with a backslash is either speech or narrative
This is \//not a comment //this is, though
:char2, character
//we need to declare characters before using them; colon denotes a procedural statement
character : Well hello there

::: //three colons denote the start of a statement block: everything following with larger indent,
    //except comments, is treated as a statement
    res=sqrt(10)+3
    5<res?
      \You can do math, congrats! //backslash, however, has precedence!

:question: //this is a label
?[q]character: Who's the best //a question, whose answer is stored in variable q
  *[ansMe] me
  *[ansYou] you
:q=ansMe?
  character: that's right
:q!=ansMe?
  char2: nuh-uh
:ansYou? //this is equivalent to q=ansYou
  :>question //aand the famous goto