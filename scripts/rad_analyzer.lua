#!/usr/bin/env luajit

local file = io.open(..., "rb")

-- This script just shows RAD block structure.
-- Might you be looking for the savefile analyzer?
local function hex(a)
	io.write(string.format(" %02x", a))
end
while true do
	local entryHead = file:read(5)
	if not entryHead then return end
	-- do we really need to check all 32-bits for an examination tool? yes? *fine*
	local entryLen = entryHead:byte(1) + (entryHead:byte(2) * 0x100) + (entryHead:byte(3) * 0x10000) + (entryHead:byte(4) * 0x1000000)
	print("chunk-alloc " .. entryHead:byte(5) .. " len: " .. entryLen)

	local entryData = ""
	io.write(" raw:")
	for i = 1, entryLen do
		local bt = file:read(1)
		entryData = entryData .. bt
		hex(bt:byte())
	end
	print()
	print()
end

