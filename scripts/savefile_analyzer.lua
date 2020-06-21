#!/usr/bin/env luajit

local file = io.open(..., "rb")

-- This script handles the outer framing and encryption of a savefile.
local isHeader = true
local function hex(a)
	io.write(string.format(" %02x", a))
end
while true do
	local entryHead = file:read(5)
	if not entryHead then return end
	if isHeader then
		print("header entry")
	else
		print("data entry")
	end
	io.write(" unk:")
	hex(entryHead:byte(5))
	print()
	-- do we really need to check all 32-bits for an examination tool? yes? *fine*
	local entryLen = entryHead:byte(1) + (entryHead:byte(2) * 0x100) + (entryHead:byte(3) * 0x10000) + (entryHead:byte(4) * 0x1000000)

	local entryData = ""
	io.write(" raw:")
	for i = 1, entryLen do
		local bt = file:read(1)
		entryData = entryData .. bt
		hex(bt:byte())
	end
	print()

	if not isHeader then
		-- data entry, try to decode
		local key = 0x53
		io.write("  key: ")
		for i = 1, entryData:byte(9) do
			io.write(string.char(bit.bxor(entryData:byte(9 + i), key)))
			key = (key + 9) % 256
		end
		print()
		local dataBase = 14 + entryData:byte(9)
		io.write("  dat:")
		local other = ""
		local key = 0x3A
		for i = dataBase, #entryData do
			local val = bit.bxor(entryData:byte(i), key)
			if val >= 32 and val ~= 127 then
				other = other .. string.char(val) .. "  "
			else
				other = other .. ".  "
			end
			hex(val)
			key = (key + 9) % 256
		end
		print()
		print("  dac: " .. other)
	end
	isHeader = false
	print()
end

