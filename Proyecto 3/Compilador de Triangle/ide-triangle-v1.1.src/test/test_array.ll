declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  %a = alloca [3 x i32]
  %t0 = getelementptr inbounds [3 x i32], [3 x i32]* %a, i32 0, i32 0
  store i32 10, i32* %t0
  %t1 = getelementptr inbounds [3 x i32], [3 x i32]* %a, i32 0, i32 0
  %t2 = load i32, i32* %t1
  call void @putint(i32 %t2)
  call void @puteol()
  ret i32 0
}
