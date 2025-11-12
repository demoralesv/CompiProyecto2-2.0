declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  call void @putint(i32 3)
  call void @puteol()
  ret i32 0
}
