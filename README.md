# Processing Compiler

Usage:
```
java -jar ProcessingCompiler.jar
 -b,--bundle <out>       Bundle the files into a executable jar
 -c,--config <file>      Config file path
 -dr,--disable-recurse   Disable recursive file discovery
 -h,--help               Show this help message
 -l,--launch             Launch the compiled file with jycessing
 -m,--main <file>        Main file path
 -o,--output <file>      Output file path
 -s,--strategy <graph/blind>     Compilation strategy
```


# What is this?
ProcessingCompiler "compiles" all imports (`from module import *`) in a python project (specifically for processing3) into one big file,
with variables and functions declared in proper order.
It does this using [Graphs](https://github.com/google/guava/wiki/GraphsExplained). (see [GraphCompilationStrategy](https://github.com/Badbird5907/ProcessingCompiler/blob/master/src/main/java/dev/badbird/processing/compiler/strategy/impl/graph/GraphCompilationStrategy.java))

It also patches the processing3-python jar file to be compatible with java versions 17 and above. (see [ClassLoaderCallTransformer](https://github.com/Badbird5907/ProcessingCompiler/blob/master/src/main/java/dev/badbird/processing/asm/ClassLoaderCallTransformer.java))

This allows ProcessingCompiler to also execute "jycessing" scripts using the `--launch`/`-l` flag.
Additionally, the `--bundle` or `-b` flag bundles the project files into a copy of ProcessingCompiler itself (see [bundler](https://github.com/Badbird5907/ProcessingCompiler/tree/master/src/main/java/dev/badbird/processing/bundler))

# Why?
Cause i can lol
