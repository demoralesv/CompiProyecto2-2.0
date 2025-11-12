declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  %n = alloca i32
  store i32 3, i32* %n
  call void @puteol()
  ret i32 0
}
