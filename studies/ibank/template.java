/**
 *
 * Template.java - Template file processing.
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 *
 */

package ibank;

import com.oroinc.text.regex.*;

import java.util.*;
import java.io.*;

/**
 * Template reads and process template data from a string or a file.
 * Template data represent for example a tempalte HTML file, which
 * contains variables of the form ${...}.  These variables are to be
 * replaced with dynamic values for interactive web applications.
 *
 * This class uses the regular expression library from ORO Inc.
 * A test main function is included at the end.
 */

public class Template {

  public static int debug = 0;  // 1 - turn on debugging

  /** Default value used when variable is not in the context.
   */
  private final String DEFAULT_VALUE = "<!-- undefined variable -->";

  /** Regular expression for variables ${...}. 
   */
  private String mRegEx = "\\$\\{([^\\}]+)\\}";

  /**
   * The template data string.
   */
  private String mString = null;

  /** Private handles */
  private PatternCompiler mCompiler = new Perl5Compiler();
  private Pattern mPattern;

  /**
   * Template data.  These are string separated by the variables.
   */
  private Vector mData = new Vector();

  /**
   * Template variables.  They appear in the template as ${varName}.
   * This vector contains the variable names.
   */
  private Vector mVars = new Vector();

  /**
   * Empty constuctor not allowed.
   */
  private Template() {
  }

  /**
   * Instantiate a template based on the specified string.
   *
   * @param s  Input string.
   * @param regex  Regular expression in place of the internal one.
   * @exception MalformedPatternException   If the pattern is not valid.
   * @exception NullPointerException   If the input file is null.
   */
  public Template(String s, String regex)
  throws MalformedPatternException, NullPointerException {
    if (s == null) 
      throw new NullPointerException("Tempalte: Null input string");
    mString = s;
    if (regex != null) mRegEx = regex;
    compile();
  }

  /**
   * Instantiate a template based on the specified file.
   * It reads the file and stores the content to mString.
   * The input regular expression is also compiled and stored.
   *
   * @param file   Input file.
   * @param regex  Regular expression.  If not null, it replaces the
   *               internal one. 
   * @exception NullPointerException   If the input file object is null.
   * @exception FileNotFoundException   If the input file is not found.
   * @exception IOException   
   * @exception MalformedPatternException   If the pattern is not valid.
   */
  public Template(File file, String regex)
  throws NullPointerException, FileNotFoundException,
         IOException, MalformedPatternException {
    if (file == null) 
      throw new NullPointerException("Tempalte: Null input file");

    BufferedReader b = null;
    mString = "";

    b = new BufferedReader(new FileReader(file));
    String line = b.readLine();
    while (line != null) {
      mString += line + "\n";
      line = b.readLine();
    }

    if (regex != null) mRegEx = regex;
    compile();
  }

  /** 
   * Return the regular expression. 
   */
  public String getRegEx() {
    return mRegEx;
  }

  /** 
   * Compile and set the pattern. 
   * 
   * @exception MalformedPatternException   If the pattern is not valid.
   */
  public void compile() throws MalformedPatternException {
    mCompiler = new Perl5Compiler();
    mPattern = mCompiler.compile(mRegEx);
  }

  /**
   * Return a string for the whole template. 
   * @return  A string representing the template.
   */
  public String toString() {
    return mString;
  }

 /**
  * Go through the template data to find variables.  Split the data
  * using the variable pattern as the separator.  Construct the data
  * vector and the variable vector.  
  *
  * The structure of the input data is like:
  *   data1 ${var1} data2 ${var2} .... dataN
  *
  * @exception FileNotFoundException
  * @exception IOException
  */
  public void analyze() throws FileNotFoundException, IOException {

    Perl5Matcher matcher = new Perl5Matcher();
    MatchResult r;

    // Create a PatternMatcherInput to keep track of 
    // the position where the last match finished, so that the next match 
    // search will start from there.  Always create such an instance
    // when you want to search a string for all of the matches it
    // contains, and not just the first one.
    PatternMatcherInput input = new PatternMatcherInput(mString);

    String data;
    String var;
    int i1 = 0;
    int i2;

    // Loop until there are no more matches left.
    while(matcher.contains(input, mPattern)) {
      // Since we're still in the loop, fetch match that was found.
      r = matcher.getMatch();  

      // The beginOffset() and endOffset() methods return the
      // offsets of a group relative to the beginning of the input.  The
      // begin() and end() methods return the offsets of a group relative
      // the to the beginning of a match.
      i2 = r.beginOffset(0);

      if (i2 < i1) data = null;
      else data = mString.substring(i1,i2);
      // Note: input.preMatch() gives substring from the beginning

      mData.add(data);  // add to vector
      if (debug >= 1) System.out.println("Data: '" + data + "'");

      var = r.toString().substring(r.begin(1), r.end(1));
      mVars.add(var);  // add to vector
      if (debug >= 1) System.out.println("Variable: '" + var + "'");

      i1 = r.endOffset(0);
    }

    // do the last data element
    if (i1 > mString.length()) data = null;
    else data = mString.substring(i1);

    mData.add(data);  // add to vector
    if (debug >= 1) System.out.println("Data: '" + data + "'");
  }

  /** Write the data using values in the input context.
   *  Be sure to call out.flush() to display the otuput afterwards.
   *
   * @param out  An output writer.
   * @param context  A hash table containing variable name and value
   *                 pairs.
   * @exception IOException if there is a problem writing to the Writer
   */
   public void write(Writer out, Hashtable context) throws IOException {
     String data, var, value;
     int i;

     for (i=0; i<mVars.size(); i++) {
       data = (String) mData.elementAt(i);
       if (data != null) out.write( data );
       var = (String) mVars.elementAt(i);
       value = (String) context.get(var);
       if (value == null) value = DEFAULT_VALUE;
       out.write( value );
     }

     // finish the last one
     data = (String) mData.elementAt(i);
     if (data != null) out.write( data );
   }

  /**
   * Test function. 
   */
   public static void main(String args[]) 
   {
     debug = 0;  // turn on debugging if == 1

     System.out.println("Usage: java Template [filename]\n");

     // set up context
     String s1 = "This is a ${test} of ${my message} block.";
     String s2 = "This is a ${test} of\\n ${new message}$M block.";
     String s3 = "${test}${my message}";
     String s4 = "This is another one.";
     String s5 = "This is a weird ${} one.";

     // test strings
     String [] s = { s1, s2, s3, s4, s5};

     Hashtable context = new Hashtable();

     context.put("my message", "Hello World!");
     context.put("test", "demo");

     // for solver.htm
     String ds = "\n";
     ds += "data.add( new DataSet('version 1', 1, -2, 1, 1, 1) );\n";
     ds += "data.add( new DataSet('version 2', 1, 2, 0, 0, -2) );\n";
     ds += "data.add( new DataSet('(Current)', 1, -3.4142, 2.8284, 1.4142, 2) );\n";

     context.put("set data sets", ds);
     context.put("firstEntry", "\n firstEntry = 0;");

     Writer w = null;

     // handle file argument
     if (args.length > 0) {

      try {
       w = new OutputStreamWriter(System.out);

       File f = new File(args[0]);
       Template t = new Template(f, null);

       System.out.println("...reading from " + f);
       t.analyze();
       System.out.println("...mutated data:");
       t.write(w, context);
       w.flush();

      } catch (Exception e) {
       e.printStackTrace();
      }

      return;
     }

     try {
       w = new OutputStreamWriter(System.out);

       Template t;

       for (int i=0; i<s.length; i++) {
         t = new Template(s[i], null);
         System.out.println(t);
         t.analyze();
         System.out.println("...Mutated data:");
         t.write(w, context);
         w.flush();
         System.out.println("\n");
       }

       // write the strings to a file
       String filename = "TestTemplate.txt";
       FileWriter fw = new FileWriter(filename);
       for (int i=0; i<s.length; i++) {
         fw.write(s[i] + "\n");
       }
       fw.flush();
       fw.close();
       System.out.println("Test strings written to " + filename);

       // create a template from the file
       File f = new File(filename);

       t = new Template(f, null);
       System.out.println("...From " + filename);
       System.out.print(t);
       t.analyze();
       System.out.println("...Mutated data:");
       t.write(w, context);
       w.flush();
       System.out.println("\n");

     } catch (Exception e) {
       e.printStackTrace();
     }

   }

}
