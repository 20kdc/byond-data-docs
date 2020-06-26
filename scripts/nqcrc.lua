#!/usr/bin/env luajit

-- given an input file, outputs the NQCRC in hex

local nqcrcTable = {}
for i = 0, 255 do
	local value = bit.lshift(i, 0x18)
	for j = 0, 7 do
		local mod = 0
		if value < 0 then
			mod = 0xAF
		end
		value = bit.bxor(bit.lshift(value, 1), mod)
	end
	nqcrcTable[i] = value
end

local f = io.open(..., "rb")

local hash = 0
while true do
	local bt = f:read(1)
	if not bt then break end
	hash = bit.bxor(bit.lshift(hash, 8), nqcrcTable[bit.band(bit.bxor(bit.rshift(hash, 24), bt:byte()), 0xFF)])
end

if hash < 0 then
	hash = hash + 0x100000000
end
print(string.format("%08x", hash))

