package Triangle;

public class RunToLLVM {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java Triangle.RunToLLVM <source.tri>");
            System.exit(2);
        }
        String src = args[0];
        IDECompiler c = new IDECompiler();
        String out = c.compileToLLVM(src);
        if (out != null) System.out.println("Generated LLVM: " + out);
        else System.err.println("Compilation to LLVM failed.");
    }
}
