public class RunLLVM {
    public static void main(String[] args) {
        String source = "ide-triangle-v1.1.src/test/prueba.tri";
        if (args.length > 0) source = args[0];
        Triangle.IDECompiler comp = new Triangle.IDECompiler();
        String out = comp.compileToLLVM(source);
        if (out != null) {
            System.out.println("LLVM file generated: " + out);
        } else {
            System.out.println("LLVM generation failed. Check console output for errors.");
        }
    }
}
