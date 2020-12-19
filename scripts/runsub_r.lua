#!/usr/bin/env luajit

-- ./runsub_r.lua FILE POS KEY SUM
-- this does not check checksum

local afile, apos, akey, asum = ...

afile = io.open(afile, "rb")
afile:seek("set", apos)
local input = afile:read("*a")
afile:close()

local key = tonumber(akey)
local checksum = 0

for i = 1, #input do
	local res = bit.band(input:byte(i) - (checksum + bit.rshift(key, checksum % 32)), 0xFF)
	io.write(string.char(res))
	checksum = bit.band(checksum + res, 0xFF)
end
io.flush()

