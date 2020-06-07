#!/usr/bin/env luajit

-- This script brute-forces XOR-Jump-9 string encryption.

local pf = io.open(..., "rb")
local pfs = pf:read("*a")
pf:close()

local bit = bit or bit32

local function dec(x, key)
	local jump = 9
	local rt = {}
	for j = 1, #x do
		rt[j] = string.char(bit.bxor(key, x:byte(j)))
		key = (key + jump) % 256
	end
	return table.concat(rt)
end

local vr = {}
for i = 0, 255 do
	vr[i] = dec(pfs, i)
	io.stderr:write(i .. " computed\n")
end

local afail = {}
for i = 0, 8 do afail[i] = true end
for i = 11, 12 do afail[i] = true end
for i = 14, 31 do afail[i] = true end
for i = 127, 255 do afail[i] = true end

local function decstr(str, b)
	local i = 0
	while str:byte(i + b) and (not afail[str:byte(i + b)]) do
		i = i + 1
	end
	if i == 0 then return end
	return str:sub(b, i + (b - 1))
end

local st = 1
local delays = {}
while st < #pfs do
	if st % 16 == 0 then
		io.stderr:write((st / #pfs) .. "\n")
	end
	for key = 0, 255 do
		if delays[key] then
			delays[key] = delays[key] - 1
			if delays[key] == 0 then
				delays[key] = nil
			end
		else
			local str = decstr(vr[key], st)
			if str then
				io.write(string.format("%x/%x\n", st, key))
				io.write(str .. "\n")
				io.flush()
				delays[key] = #str
			end
		end
	end
	st = st + 1
end

