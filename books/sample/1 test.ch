#this is a short test, meant for covering various ways of writing stuff here
 #not really representative

#require __LANG_VERSION__ > 0 //this directive is basically an assert - make sure condition is fulfilled

Hello World!
\??? //everything starting with a backslash is either speech or narrative
This is \//not a comment //this is, though, because backslash is usually an escape character all-round
:char2, character
//we need to declare characters before using them; colon denotes a procedural statement
character : Well hello there

::: //three colons denote the start of a statement block: everything following with larger indent,
    //except comments, is treated as a statement
    res=sqrt(10)+3
    5<res? //this is an if statement, following similar indent rules
      \You can do math, congrats! //backslash always has precedence!

:question: //this is a label
?[q]character: Who's the best //a question, whose answer is stored in variable q
  *[ansMe] me
  :3<5?
    *[ansYou] you
  :ansYou?
    *[ansGTH] I'm not a number, I'm a free man!
:q=ansMe?
  character: that's right
:q!=ansMe?
  char2: nuh-uh
  :ansYou? //this is equivalent to q=ansYou
    :>question //aand the famous goto

;; //this is a halt
Hey, wait for me! (they won't, they're already long gone)