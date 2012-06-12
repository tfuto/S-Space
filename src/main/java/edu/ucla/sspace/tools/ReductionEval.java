package edu.ucla.sspace.tools;

import edu.ucla.sspace.common.ArgOptions;

import edu.ucla.sspace.matrix.Matrices;
import edu.ucla.sspace.matrix.Matrix;
import edu.ucla.sspace.matrix.MatrixFactorization;
import edu.ucla.sspace.matrix.MatrixFile;
import edu.ucla.sspace.matrix.MatrixIO;
import edu.ucla.sspace.matrix.MatrixIO.Format;
import edu.ucla.sspace.matrix.SVD;
import edu.ucla.sspace.matrix.factorization.AbstractSvd;
import edu.ucla.sspace.matrix.factorization.NonNegativeMatrixFactorizationMultiplicative;
import edu.ucla.sspace.matrix.factorization.SingularValueDecompositionLibC;
import edu.ucla.sspace.matrix.factorization.SingularValueDecompositionLibJ;
import edu.ucla.sspace.matrix.factorization.SingularValueDecompositionMatlab;

import edu.ucla.sspace.util.LoggerUtil;

import java.io.File;
import java.io.PrintWriter;
import java.util.logging.Level;


/**
 * @author Keith Stevens
 */
public class ReductionEval {

    public static void main(String[] args) throws Exception {
        ArgOptions options = new ArgOptions();
        options.addOption('w', "wordSpace",
                          "The name of the file to which the reduced " +
                          "word space should be saved",
                          true, "FILE", "Required");
        options.addOption('d', "docSpace",
                          "The name of the file to which the reduced " +
                          "document space should be saved",
                          true, "FILE", "Required");
        options.addOption('r', "dimensions",
                          "The number of reduced dimensions.",
                          true, "INTEGER", "Required");
        options.addOption('a', "reductionAlgorithm",
                          "The reduction algorithm to use, either NMF or SVD",
                          true, "NMF|SVD", "Required");
        options.parseOptions(args);

        LoggerUtil.setLevel(Level.FINE);

        int dimensions = options.getIntOption('r');
        //MatrixFactorization reducer = null;
        AbstractSvd reducer = null;
        Format format = null;
        if (options.getStringOption('a').equals("NMF")) {
            /*
            reducer = new NonNegativeMatrixFactorizationMultiplicative();
            format = Format.MATLAB_SPARSE;
            */
        } else if (options.getStringOption('a').equals("SVD")) {
            reducer = new SingularValueDecompositionLibC();
            format = Format.SVDLIBC_SPARSE_BINARY;
        } else
            System.exit(1);


        MatrixFile mFile = new MatrixFile(new File(options.getPositionalArg(0)),
                                          format);

        reducer.factorize(mFile, dimensions);

        File wordSpaceFile = new File(options.getStringOption('w'));
        MatrixIO.writeMatrix(reducer.dataClasses(), wordSpaceFile,
                             Format.DENSE_TEXT);

        File docSpaceFile = new File(options.getStringOption('d'));
        MatrixIO.writeMatrix(Matrices.transpose(reducer.classFeatures()), 
                             docSpaceFile,
                             Format.DENSE_TEXT);
    }
}