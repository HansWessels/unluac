local id = x
if check() then
  local n = tonumber(string.sub(x, 6))
  id = player.episode[n].script or id
end