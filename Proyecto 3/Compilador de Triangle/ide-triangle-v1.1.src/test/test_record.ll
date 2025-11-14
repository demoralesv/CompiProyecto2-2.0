
%struct0 = type { i32, i32 }

declare void @putint(i32)

declare void @puteol()

define i32 @main() {
  %r = alloca %struct0
  %t1 = getelementptr inbounds %struct0, %struct0* %r, i32 0, i32 0
  store i32 7, i32* %t1
  %t2 = getelementptr inbounds %struct0, %struct0* %r, i32 0, i32 0
  %t3 = load i32, i32* %t2
  call void @putint(i32 %t3)
  call void @puteol()
  ret i32 0
}
