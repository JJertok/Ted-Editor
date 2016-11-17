package fr.xgouchet.texteditor.syntax;

import android.content.Context;
import android.util.Log;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTProblemDeclaration;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by 1 on 09.11.2016.
 */
public class ASTBuilder {
    public void Parse(String filePath)
            throws Exception {
        FileContent fileContent = FileContent.createForExternalFileLocation(filePath);
        Map definedSymbols = new HashMap();
        String[] includePaths = new String[0];
        IScannerInfo info = new ScannerInfo(definedSymbols, includePaths);
        IParserLogService log = new DefaultLogService();

        IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();

        int opts = 8;
        IASTTranslationUnit translationUnit = GPPLanguage.getDefault().getASTTranslationUnit(fileContent, info, emptyIncludes, null, opts, log);

        printTree(translationUnit, 1);

        ASTVisitor visitor = new ASTVisitor() {
            public int visit(IASTProblem name) {
                return 3;
            }

            public int visit(IASTAttribute attribute) {
                return 3;
            }
        };
        visitor.shouldVisitNames = true;
        visitor.shouldVisitDeclarations = false;
        visitor.shouldVisitDeclarators = true;
        visitor.shouldVisitAttributes = true;
        visitor.shouldVisitStatements = false;
        visitor.shouldVisitTypeIds = true;
        visitor.shouldVisitProblems = true;
        translationUnit.accept(visitor);

    }



    private static void printTree(IASTNode node, int index) {
//        if(node instanceof ASTProblem) {
//            System.out.println("Errors: " );
//            System.out.println(node);
//        }

        if(node instanceof CPPASTProblemDeclaration) {
            System.out.println(node);
            Log.d("TED", ((CPPASTProblemDeclaration) node).getOffset() + "");
//            System.out.println(((CPPASTProblemDeclaration) node).getOffset());
//            System.out.println(((CPPASTProblemDeclaration) node).getLength());
        }

        for (IASTNode chNode:
                node.getChildren()) {
            printTree(chNode, 0);
        }
    }


    public void Check(String text, Context ctx) {
        String filename = "checker.cpp";

        FileOutputStream outputStream;

        try {
            outputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Parse(ctx.getFilesDir() + "/" + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
