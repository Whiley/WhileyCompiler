package wycli.commands;

import wycc.lang.Path;
import wycli.cfg.Configuration;
import wycli.lang.Command;

import java.util.Arrays;
import java.util.List;

/**
 * The "root" command of the command tree.  This is the starting point for all command execution, and is really just a
 * dummy which is needed for the start of the chain.  However, it does include a few commands which can be passed
 * directly to the "wy" tool.
 */
public class Root implements Command {
    /**
     * Construct an appropriate root descriptor for a given set of top-level commands.
     *
     * @param commands
     * @return
     */
    public static Command.Descriptor DESCRIPTOR(final List<Command.Descriptor> commands) {
        // Done
        return new Command.Descriptor() {
            @Override
            public Configuration.Schema getConfigurationSchema() {
                throw new IllegalArgumentException();
            }

            @Override
            public List<Command.Option.Descriptor> getOptionDescriptors() {
                return Arrays.asList(
                        Command.OPTION_FLAG("verbose", "generate verbose information about the build", false),
                        Command.OPTION_POSITIVE_INTEGER("profile", "generate profiling information about the build", 0),
                        Command.OPTION_FLAG("brief", "generate brief output for syntax errors", false));
            }

            @Override
            public String getName() {
                return "wy";
            }

            @Override
            public String getDescription() {
                return "Command-line interface for the Whiley Compiler Collection";
            }

            @Override
            public List<Command.Descriptor> getCommands() {
                return commands;
            }

            @Override
            public Command initialise(Environment parent) {
                return new Root(this,parent);
            }
        };
    }

    private final Command.Descriptor descriptor;
    private final Command.Environment environment;

    public Root(Command.Descriptor descriptor, Command.Environment environment) {
        this.descriptor = descriptor;
        this.environment = environment;
    }

    @Override
    public Command.Descriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public void initialise() {
    }

    @Override
    public void finalise() {
    }

    @Override
    public boolean execute(Path path, Command.Template template) throws Exception {
        boolean verbose = template.getOptions().get("verbose", Boolean.class);
        //
        if (template.getChild() != null) {
            // Execute a subcommand
            template = template.getChild();
            // Access the descriptor
            Command.Descriptor descriptor = template.getCommandDescriptor();
            // Construct an instance of the command
            Command command = descriptor.initialise(environment);
            //
            return command.execute(path,template);
        } else {
            // Initialise command
            Command cmd = HelpSystem.DESCRIPTOR.initialise(environment);
            // Execute command
            return cmd.execute(path, template);
        }
    }


}
