package fr.xgouchet.texteditor.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;

import fr.xgouchet.texteditor.ui.view.AdvancedEditText;

import static fr.xgouchet.texteditor.common.Constants.LINES_PER_PAGE;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
    Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    public int totalpages = 0;
    public int pageLines = 0;
    protected PageSystem mPageSystem;
    protected AdvancedEditText mEditor;
    protected int mtitleBaseLine = 35;
    protected int mleftMargin = 54;

    public MyPrintDocumentAdapter(Context context, PageSystem pageSystem,AdvancedEditText editor) {
        this.context = context;
        mEditor = editor;
        mPageSystem = pageSystem;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes,
                         PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback,
                         Bundle metadata) {

        myPdfDocument = new PrintedPdfDocument(context, newAttributes);


        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        pageLines = (pageHeight - 50)/20;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (mPageSystem.getAllText(mEditor.getText().toString()).length() > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("There is an empty page");
        }
    }

    @Override
    public void onWrite(final PageRange[] pageRanges,
                        final ParcelFileDescriptor destination,
                        final CancellationSignal cancellationSignal,
                        final WriteResultCallback callback) {

        drawPages(pageRanges,cancellationSignal,callback);

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPages(PageRange[] pageRanges,CancellationSignal cancellationSignal,WriteResultCallback callback) {

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        String a = mPageSystem.getAllText(mEditor.getText().toString());

        int strindex = 0;
        int pagenumber = 0;
        while(strindex<a.length()){
            if (pageInRange(pageRanges, pagenumber)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, pagenumber).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }

                Canvas canvas = page.getCanvas();
                int titleBaseLine = mtitleBaseLine;
                int line = 1;
                while(line<=pageLines){
                    if(a.indexOf('\n',strindex)== -1)
                    {
                        canvas.drawText(a.substring(strindex,a.length()), mleftMargin, titleBaseLine, paint);
                        strindex = a.length();
                        break;
                    }
                    else
                    {
                        canvas.drawText(a.substring(strindex,a.indexOf('\n',strindex)), mleftMargin, titleBaseLine, paint);
                        strindex = a.indexOf('\n',strindex)+1;
                    }
                    titleBaseLine = titleBaseLine+20;
                    line++;
                }
                myPdfDocument.finishPage(page);
            }
            pagenumber++;

        }
    }

}