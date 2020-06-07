#!/usr/bin/env luajit

-- given an input file and an expected substring,
--  shows the result of all XORs of the latter within the former
-- on any XOR algorithm, this will show the keystream
--  also shows the inter-byte jump, which is a great way of locating
--  XORJUMP9, as it will show "09 09 09 09..."
-- to make this easier, it will output " XORJUMP9" when this happens
-- for a known target string (such as one you might generate using DreamMaker),
--  this is much better than the brute-forcer

local fn, ss, other = ...

local alg = bit.bxor
if other == "add" then
 alg = function (a, b)
  return bit.band(a - b, 255)
 end
end

local f = io.open(fn, "rb")
local data = f:read("*a")

for i = 1, #data - (#ss - 1) do
 print(string.format("%x", i))
 local last
 local lasts = {}
 local isxj9 = true
 for j = 1, #ss do
  local cur = alg(data:byte(i + j - 1), ss:byte(j))
  if not last then
   io.write(string.format("%02x", cur))
  else
   io.write(string.format(" %02x", cur))
   if bit.band(cur - last, 0xFF) ~= 9 then
    isxj9 = false
   end
   table.insert(lasts, string.format("%02x", bit.band(cur - last, 0xFF)))
  end
  last = cur
 end
 print()
 print("   " .. table.concat(lasts, " "))
 if isxj9 then
  print("   XORJUMP9")
 end
end
