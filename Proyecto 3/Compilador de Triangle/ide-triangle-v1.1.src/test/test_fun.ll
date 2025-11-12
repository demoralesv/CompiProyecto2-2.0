declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  call void @puteol()
  ret i32 0
}
