package dev.badbird.processing.compiler.strategy.impl.graph;

import com.google.common.graph.*;
import dev.badbird.processing.compiler.CompilerException;
import dev.badbird.processing.compiler.CompilerState;
import dev.badbird.processing.compiler.PythonFile;
import dev.badbird.processing.compiler.strategy.CompilationStrategy;

@SuppressWarnings("UnstableApiUsage")
public class GraphCompilationStrategy implements CompilationStrategy {
    @Override
    public String compile(CompilerState compilerState) {
        // we want to track which python files we want to put the contents of in the main file first
        // as python execution is top down, we need to copy the functions and classes declared in other files to the main file
        // in order to ensure that they are defined before they are used, as other files can also import them, so a simple "blind"
        // copy won't be enough. We should use a graph to track the dependencies between the files, and then copy the contents of the
        // files in the correct order to the main file by traversing the graph.
        Graph<String> graph = buildGraph(compilerState);
        System.out.println("Graph: " + graph);
        StringBuilder content = new StringBuilder("# GRAPH:\n# " + graph + "\n\n");

        compilerState.getPythonFiles().values().stream().filter(pythonFile -> pythonFile.getPriority() > 0)
                .sorted((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()))
                .forEach(pythonFile -> content.append(pythonFile.getContent()).append("\n"));

        Iterable<String> iterable = Traverser.forGraph(graph)
                .depthFirstPostOrder(compilerState.getMainPythonFile().getName());
        for (String s : iterable) {
            PythonFile file = compilerState.getPythonFiles().get(s);
            if (file == null) {
                throw new CompilerException("File " + s + " not found!");
            }
            if (file.getPriority() > 0) {
                continue; // skip the file if it has a priority
            }
            content.append(file.getContent()).append("\n");
        }
        // content.append(main.getContent());

        return content.toString();
    }

    public Graph<String> buildGraph(CompilerState state) {
        MutableGraph<String> graph = GraphBuilder.directed()
                .build();
        state.getPythonFiles().values().forEach((node) -> {
            System.out.println("Adding node " + node.getName() + " to graph");
            graph.addNode(node.getName());
        });
        state.getPythonFiles().values().forEach(pythonFile -> {
            String[] lines = pythonFile.getContent().split("\n");
            StringBuilder newContent = new StringBuilder("# COMPILER_BEGIN: " + pythonFile.getName() + "\n");
            for (String line : lines) {
                if (line.startsWith("from ")) { // check the import
                    String[] split = line.split(" ");
                    String fileName = split[1];
                    PythonFile file = state.getPythonFiles().get(fileName + ".py");
                    if (file == null) {
                        throw new CompilerException("File " + fileName + ".py not found!");
                    }
                    // check if the edge already exists
                    if (graph.hasEdgeConnecting(file.getName(), pythonFile.getName())) {
                        throw new CompilerException("Circular dependency detected between " + pythonFile.getName() + " and " + file.getName());
                    } else {
                        graph.putEdge(pythonFile.getName(), file.getName());
                    }
                } else {
                    newContent.append(line).append("\n");
                }
            }
            newContent.append("\n");
            newContent.append("# COMPILER_END: ").append(pythonFile.getName()).append("\n");
            pythonFile.setContent(newContent.toString());
        });
        return graph;
    }
}
