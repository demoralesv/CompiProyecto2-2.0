@n = dso_local global i32 0, align 4

declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  store i32 3, i32* @n
  %t0 = load i32, i32* @n
  call void @putint(i32 %t0)
  call void @puteol()
  ret i32 0
}
