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

This allows ProcessingCompiler also to execute "jycessing" scripts using the `--launch`/`-l` flag.
Additionally, the `--bundle` or `-b` flag bundles the project files into a copy of ProcessingCompiler itself (see [bundler](https://github.com/Badbird5907/ProcessingCompiler/tree/master/src/main/java/dev/badbird/processing/bundler))

# Why?
The short answer:
Cause i can lol

<details>
<summary>The long answer</summary>
<br>
 Processing3 (jycessing) uses a bootleg version of python called "Jython", which doesn't allow you to import more modules easily.
 I discovered you can execute jycessing scripts directly from the cli via the jar file, which lead to the creation of <a href="https://gist.github.com/Badbird5907/3385ad2fcf0e0745eddc002530ea6df8#file-run_processing-ps1">this powershell script</a>.
 One day I decided that it would be *funny* if I made a "compiler" that combines modules into one file, and in the process of creating this compiler
 I also managed to get the processing3-py jar file to execute on java 17 without issue, by <a href="https://github.com/jdf/processing.py/blob/master/runtime/src/jycessing/LibraryImporter.java#L332">patching out a single call</a> from `ClassLoader.getSystemClassLoader()` to `getClass().getClassLoader()` using OW2 ASM.
</details>
