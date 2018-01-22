package application.accounting;

import gnu.getopt.LongOpt;
import gnu.getopt.Getopt;

/**
    Diese Klasse dient dem Parsen von Argumenten
*/

public class ArgParser {

    private String[] args = null;
    private boolean showHelp = false;
    private boolean showVersion = false;
    private String inputFilename = null;
    private String outputFilename = null;
    private String logFilename = null;
    private String nonOptions = null;

    public ArgParser(String[] args) {

        this.args = args;
        parseArgs();
        
    } // end of special constructor "ArgParser(String[])"

    /** Funktion zum Parsen von Argumenten */
    private void parseArgs() {
    
        StringBuffer sb = new StringBuffer();
        int c;
        String arg;
        LongOpt[] longopts = new LongOpt[6];
        
        longopts[0] = new LongOpt("input-file", LongOpt.REQUIRED_ARGUMENT, sb, 'i');
        longopts[1] = new LongOpt("output-file", LongOpt.REQUIRED_ARGUMENT, sb, 'o');
        longopts[2] = new LongOpt("log-file", LongOpt.OPTIONAL_ARGUMENT, sb, 'l');
        longopts[3] = new LongOpt("rate-of-interest", LongOpt.REQUIRED_ARGUMENT, null, 'r');
        longopts[4] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[5] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v');
        
        Getopt g = new Getopt("accounting", args, "i:o:l:r:hv", longopts);
        
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 0:
                    arg = g.getOptarg();
                    switch ((char)(new Integer(sb.toString())).intValue()) {
                        case 'i':
                            inputFilename = arg; break;
                        case 'o':
                            outputFilename = arg; break;
                        case 'l':
                            logFilename = arg; break;
                        case 'r':
                            nonOptions = arg; break;
                    }
                    break;
                
                case 1:
                    System.out.println("I see you have return in order set and that " +
                                        "a non-option argv element was just found " +
                                        "with the value '" + g.getOptarg() + "'");
                    break;
                
                case 2:
                    arg = g.getOptarg();
                    System.out.println("I know this, but pretend I didn't");
                    System.out.println("We picked option " +
                                        longopts[g.getLongind()].getName() +
                                    " with value " + 
                                    ((arg != null) ? arg : "null"));
                    break;
                
                case 'i':
                    arg = g.getOptarg();
                    inputFilename = arg;
                    break;
                
                case 'o':
                    arg = g.getOptarg();
                    outputFilename = arg;
                    break;
                
                case 'l':
                    arg = g.getOptarg();
                    logFilename = arg;
                    break;
                
                case 'r':
                    arg = g.getOptarg();
                    nonOptions = arg;
                    break;
                
                case 'v':
                    showVersion = true;
                    break;
                
                case 'h':
                    showHelp = true;
                    break;
            }
        }

    } // end of method "parseArgs()"

    /** hier wird die toString-Methode ueberschrieben */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < args.length; i++ ) {

            if ( args[i].equals("-h") || args[i].equals("--h") ||
                 args[i].equals("-v") || args[i].equals("--v") ) {

                sb.append(args[i]).append("\n");

            } // end of if ( args[i].equals("-h")  ...)
            else if ( args[i].equals("-i") || args[i].equals("--input-file") ||
                args[i].equals("-o") || args[i].equals("--output-file") ) {

                System.out.println("i: " + i + ", args.length: " + args.length);
                if ( i + 1 < args.length ) {
                    sb.append(args[i] + " " + args[++i]).append("\n");
                } // end of if ( i + 1 < args.length )
                else {
                    throw new IllegalArgumentException("missing filename");
                } // end of if ( i + 1 < args.length ) else

            } // end of else if ( args[i].equals("-i") ... )
            else if ( args[i].equals("-l") || args[i].equals("--log-file") ) {

                if ( i + 1 < args.length ) {

                    if ( args[i + 1].startsWith("-") ) {
                        sb.append(args[i]).append("\n");
                    } // end of if ( args[ i + 1].startsWith("-") )
                    else {
                        sb.append(args[i] + " " + args[++i]).append("\n");
                    } // end of if ( args[ i + 1].startsWith("-") ) else

                } // end of if ( i + 1 < args.length )

            } // end of else if ( args[i].equals("-i") ... )
            else {

                sb.append("non-option argument: " +
                          args[i]).append("\n");

            } // end of if ( args[i].equals("-h") || args[i].equals("--h") ) else

        } // end of for (int i = 0; i  < args.length; i++)

        return sb.toString();

    } // end of method "toString()"


    public boolean getShowHelp() {
        return showHelp;
    } // end of method "getShowHelp()"


    public boolean getShowVersion() {
        return showHelp;
    } // end of method "getShowVersion()"


    public String getInputFilename() {
        return inputFilename;
    } // end of method "getInputFilename()"


    public String getOutputFilename() {
        return outputFilename;
    } // end of method "getOutputFilename()"


    public String getLogFilename() {
        return logFilename;
    } // end of method "getLogFilename()"


    public String getNonOptions() {
        return nonOptions;
    } // end of method "getNonOptions()"


    public static final void main(final String[] args) {
        ArgParser argParser = new ArgParser(args);
        System.out.println(argParser);
    } // end of method "main(String[] args)"

} // end of class "ArgParser"
