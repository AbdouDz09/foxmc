local player = ARGV[1]
local keys = redis.call("KEYS", "party:*:members")

local party

for _, key in ipairs(keys) do
  if redis.call("SISMEMBER", key, player) > 0 then
    party = string.gsub(key, "party:", "")
    party = string.gsub(party, ":members", "")
    break
  end
end

return party