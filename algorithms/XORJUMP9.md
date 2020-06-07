# XOR Jump 9

XOR Jump 9 is an obfuscation algorithm.

It isn't really worth calling it encryption.

BYOND is not the first or the last piece of software to feature this sort of thing. I had a rant here about this pattern, but removed it before publishing. Just know that it's stupid and it annoys me.

XOR Jump 9 is a symmetric algorithm.

It takes some input plaintext or ciphertext and an input key byte.

It is only different a constant XOR because it adds 9 to the key byte after each byte has been processed.

You might notice that this doesn't sound much better than a constant XOR.

```lua
local function xorjump9(data, key)
	local result = ""
	for i = 1, #data do
		result = result .. string.char(data:byte(i) ~ key)
		key = (key + 9) % 256
	end
	return result
end
```

