Positions (guidelines) are specified in % (number 0-1 or 1-100)

Margins and paddings are specified in dp (number of dp's)

Image sizes are specified in dp (number of dp's)

Text sizes are specified in sp (number of sp's)

Backgrounds are either #000000 (black) - #ffffff (white) for solid color, or an image name for an image

Alignment follows a custom format, roughly: {left|right|top|bot|bottom} {left|right|top|bot|bottom|guideline|none} {\[view]|guideline|none}; ...
       * Not combinations all are valid - e.g. it's nonsense to align left to top. Bot and bottom are synonyms.

\_background_ - background color in format #000000 - #ffffff or an image name

\_narrative.left_ - left guideline for the narrative (vertical), in %

\_narrative.top_ - top guideline for the narrative (horizontal), in %

\_narrative.bottom_ - bottom guideline for narrative (horizontal), in %

\_narrative.right_ - right guideline for narrative (vertical), in %

\_answers.left_ - left guideline for answers (vertical), in %

\_answers.top_ - top guideline for the topmost answer (horizontal), in %

\_answers.bottom_ - bottom guideline for the bottom-most answer (horizontal), in %

\_answers.right_ - right guideline for answers (vertical), in %

\_cname.left_ - left guideline for character name (vertical), in %

\_cname.right_ - right guideline for character name (vertical), in %

\_cname.bottom_ - bottom guideline for character name (horizontal), in %

\_countdown.left_ - left guideline for countdown box (vertical), in %

\_countdown.right_ - right guideline for countdown box (vertical), in %

\_countdown.bottom_ - bottom guideline for countdown box (horizontal), in %

\_avatar.bottom_ - bottom guideline for avatar (horizontal), in %

\_avatar.right_ - right guideline for avatar (vertical), in %

\_avatar.size_ - size for avatar, in dp (Android-specific unit, default is 70)

\_avatar.alignment_ - alignment for avatar, in format specified above

\_narrative.background_ - background color or image for narrative

\_narrative.padding.vertical_ - top and bottom padding for narrative box (i.e. the space between box border and text), in dp

\_narrative.padding.horizontal_ - left and right padding for narrative box, in dp

\_narrative.alignment_ - alignment for narrative, in format specified above

\_narrative.color_ - text color of the narrative

\_narrative.size_ - text size of the narrative, in sp (Android-specific unit, default is 16)

\_answer.background_ - background of a single answer, either a solid color or an image

\_answer.size_ - text size of an answer

\_answer.margins_ - margins for one answer (margin is the space between the bounding box and borders of the view), in dp

\_answer.text.color_ - text color of an answer

\_answer.padding.vertical_ - top and bottom padding of an answer, in dp

\_answer.padding.horizontal_ - left and right padding of an answer, in dp

\_answers.alignment_ - alignment for all answers, follows the format specified above

\_cname.background_ - background of the character name, either a solid color or an image

\_cname.padding.vertical_ - top and bottom padding of the character name, in dp

\_cname.padding.horizontal_ - left and right padding of the character name, in dp

\_cname.margins.vertical_ - top and bottom margins of the character name, in dp

\_cname.alignment_ - alignment for character name, follows the format specified above

\_cname.color_ - text color of the character name

\_cname.size_ - text size of the character name

\_countdown.background_ - background of the countdown box

\_countdown.margins.vertical_ - top and bottom margins of the countdown box

\_countdown.interval_ - interval in which to display updates of the countdown

\_countdown.format_ - format in which to display countdown, i.e. %.1f for 1 decimal

\_countdown.color_ - text color of the countdown

\_countdown.alignment_ - alignment for countdown

\_countdown.size_ - text size for the countdown