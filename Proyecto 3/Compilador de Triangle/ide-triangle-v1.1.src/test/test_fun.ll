define i32 @inc(i32 %p_x) {
  %x = alloca i32
  store i32 %p_x, i32* %x
  %t0 = load i32, i32* %x
  %t1 = add i32 %t0, 1
  ret i32 %t1
}


declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  %t2 = call i32 @inc(i32 10)
  call void @putint(i32 %t2)
  call void @puteol()
  ret i32 0
}
