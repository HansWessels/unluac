local i = get()
if i == "one" then
  if check1() then
    do1()
  end
elseif i == "two" then
  if check2() then
    do2()
  else
    -- do nothing
  end
else
  return
end