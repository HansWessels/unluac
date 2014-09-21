while true do
  for k,v in pairs(next_table()) do
    if k == "end" then
      return
    else
      break
    end
  end
end