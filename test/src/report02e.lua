function report02e(input)
  local x = 1
  if test(x) then
    do1(x)
  else
    error("asdf")
  end
  print("blah")
  local y = 4
  while true do
    do1()
    if not check() then
      return
    end
    local z = getz()
    if test(z) then
      local data1, data2 = getdata()
      if data1 == "valid" then
        local data3 = getdata(data2)
        if data3 then
          print("valid data")
        end
      end
      break
    end
  end
  if test3() then
    do3()
  else
    error("asdf")
  end
end