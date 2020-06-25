#!/usr/bin/env luajit

-- useful for theorizing

-- 00000010  0f af 55 81 4f bc 11 b0  88 d6 1e 5e b9 be 06 51  |..U.O......^...Q|
-- 00000020  6b b7 73 6b 21 b7 69                              |k.sk!.i|

local inputX = "f9 5e 31 bb a0 b3 dd 93 0e ec fc a4 99 34 a2 f9 c5 4c b0 82 8c b1 64 5e fc eb 03 30 67 a3 e1 21 19 7e 51 db c0 d3 fd b3 2e 0c 1c c4 b9 54 c2 19 e5 6c d0 a2 ac d1 84 7e 1c 0b 23 50 87 c3 01 41 39 9e 71 fb e0 f3 1d d3 4e 2c 3c e4 d9 74 e2 39 05 8c f0 c2 cc f1 a4 9e 3c 2b 43 70 a7 e3 21 61 59 be 91 1b 00 13 3d f3 6e 4c 5c 04 f9 94 02 59 25 ac 10 e2 ec 11 c4 be 5c 4b 63 90 c7 03 41 81 79 de b1 3b 20 33 5d 13 8e 6c 7c 24 19 b4 22 79 45 cc 30 02 0c 31 e4 de 7c 6b 83 b0 e7 23 61 a1 99 fe d1 5b 40 53 7d 33 ae 8c 9c 44 39 d4 42 99 65 ec 50 22 2c 51 04 fe 9c 8b a3 d0 07 43 81 c1 b9 1e f1 7b 60 73 9d 53 ce ac bc 64 59 f4 62 b9 85 0c 70 42 4c 71 24 1e bc ab c3 f0 27 63 a1 e1 d9 3e 11 9b 80 93 bd 73 ee cc dc 84 79 14 82 d9 a5 2c 90 62 6c 91 44 3e dc cb e3 10 47 83 c1 01 c2 3b 0a"
local input = ""
for v in inputX:gmatch("[^ ]+") do
	input = input .. string.char(tonumber("0x" .. v))
end

local key = 0x2ea95866
local ver = 0x01140201
local add = 0x73b76b51
local minorVer = 0
key = bit.band(key + ver + add, 0xFFFFFFFF)

local checksum = 0

for i = 1, #input - 1 do
	local res = bit.band(input:byte(i) - (checksum + bit.rshift(key, checksum % 32)), 0xFF)
	io.write(string.char(res))
	checksum = bit.band(checksum + res, 0xFF)
end
io.flush()

if checksum == input:byte(#input) then
	io.stderr:write("\nOK\n")
else
	io.stderr:write("\nFAIL\n")
end
io.stderr:flush()

