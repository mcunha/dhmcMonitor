## dhmcMonitor

This plugin was developed to replace our reliance on other plugins that aren't working well enough.

**Ores**

This plugin will alert moderators when a player finds any ores (except coal and redstone currently). It will display the number of ores found and the light level.

Right now, the plugin requires access to Hawkeye to verify the ores have not been placed by a player.

**Flint and Steel**

Moderators will be notified of every use of flint and steel - if a player uses it too much within a short time, the moderators will be notified in combined messages instead of spamming them.

**Lava, TNT Placement**

Moderators are notified of placement of lava or TNT (which is disabled anyway).

**Command Use**

Many commands like op, deop, stop, reload, are disabled entirely. If a player attempts to use them, moderators are alerted.

**Profanity Filter**

The system has two sets of words - unmistakable curse words, and mistakable. If a curse word is often found in other, acceptable words, then we try to censor the word but allow the message through. Words that are clearly curse words are blocked entirely and moderators are alerted to the attempt.

The player is reminded that cursing AND trying to bypass the censor are both illegal. 

The filter will attempt to detect variations of curse words to make bypassing the filter harder. Non-standard characters are removed, we attempt to convert basic leet speak, etc.

The filter will improve with time.

**Caps**

If messages are longer than five characters or contain fewer than 20% caps, the message is allowed. If longer and has a higher ratio of caps, the message is converted to lower case.