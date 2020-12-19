#!/usr/bin/env luajit

-- ./cycsub_r.lua FILE

local afile = ...

local dat = io.open(afile, "rb"):read("*a")

local key = "\x3c\x7c\x33\x5e\x0a\x71\x0d\x5b"

local function cycsub_dec(buffer, key)
	local j = #buffer
	local out = ""
	local checksum = 0
	local keyIndex = 0
	while j >= 1 do
		local roundKey = bit.band(checksum + key:byte(keyIndex + 1), 0xFF)
		local bt = buffer:byte(j)
		bt = bit.band(bt - roundKey, 0xFF)
		out = string.char(bt) .. out
		checksum = bit.band(checksum + bt, 0xFF)
		keyIndex = (keyIndex + 1) % #key
		j = j - 1
	end
	return out
end

for i = 0, #dat - 1 do
	print("----")
	print(i)
	print("----")
	print(cycsub_dec(dat:sub(1, #dat - i), key))
end
io.flush()

