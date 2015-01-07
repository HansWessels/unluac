local upvalue1 = {}
local upvalue2 = {}
function f()
  local a = upvalue1.print
  local b = upvalue2.print
  local c = _ENV.print
  return print
end
function g()
  return _ENV.print
end
function f2(a)
  upvalue1.print = a
  upvalue2.print = a
  _ENV.print = a
  print = a
end
