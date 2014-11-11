function report03()
  local x = init()
  while true do
    local y = f(x)
    if y == 0 then
      break
    end
    x = next_x(x, y)
    if x == 0 then
      break
    end
    for i = x, 1, -1 do
      if i > g(x) or i < 1 then
        break
      end
      local z = h(g(x), y)
      if not (z == 4) then
        break
      end
      if z > 0 then
        finish()
      end
    end
  end
end
