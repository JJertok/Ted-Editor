package fr.xgouchet.texteditor.common;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class PageSystem implements Constants {

    private List<String> pages;
    private ArrayList<Integer> startingLines;
    private int currentPage = 0;

    public PageSystem(String text) {

        final int linesPerPage = LINES_PER_PAGE;

        pages = new LinkedList<String>();

        int i = 0;
        final String[] lines = text.split("\n");
        StringBuilder builder = new StringBuilder();
        final int linesCount = lines.length;

        if (text.length() != 0) {
            while (i < linesCount) {
                builder.append(lines[i]);
                if ((i + 1) % linesPerPage == 0) {
                    pages.add(builder.toString());
                    builder.delete(0, builder.length());
                } else builder.append("\n");
                i++;
                if (i == linesCount) break;
            }
            if (builder.length() != 0) pages.add(builder.toString());
        } else pages.add("");


        startingLines = new ArrayList<Integer>();
        setStartingLines();
    }

    public void reInitPageSystem(String text){

        final int linesPerPage = LINES_PER_PAGE;

        pages = new LinkedList<String>();
        currentPage = 0;

        int i = 0;
        final String[] lines = text.split("\n");
        StringBuilder builder = new StringBuilder();
        final int linesCount = lines.length;

        if (text.length() != 0) {
            while (i < linesCount) {
                builder.append(lines[i]);
                if ((i + 1) % linesPerPage == 0) {
                    pages.add(builder.toString());
                    builder.delete(0, builder.length());
                } else builder.append("\n");
                i++;
                if (i == linesCount) break;
            }
            if (builder.length() != 0) pages.add(builder.toString());
        } else pages.add("");


        startingLines = new ArrayList<Integer>();
        setStartingLines();

    }

    public int getStartingLine() {
        return startingLines.get(currentPage);
    }

    public String getCurrentPageText() {
        return pages.get(currentPage);
    }

    public String getPageText(int page) {
        return pages.get(page);
    }

    public int getPageLength(int page) {
        return pages.get(page).length();
    }

    public int getPagesLength(int startPage, int endPage) {
        int result = 0;
        for (int i = startPage; i < endPage; i++)
            result += pages.get(i).length();
        return result;
    }

    public String getTextOfNextPages(boolean includeCurrent, int nOfPages) {
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = includeCurrent ? 0 : 1; i < nOfPages; i++) {
            if (pages.size() > (currentPage + i)) {
                stringBuilder.append(pages.get(currentPage + 1));
            }
        }

        return stringBuilder.toString();
    }

    public void savePage(String currentText) {
        pages.set(currentPage, currentText);
    }

    public void updatePage(int page, String text){pages.set(page, text);}

    public void addPage(String text) {
        pages.add(text);
        setStartingLines();
    }

    public void nextPage() {
        if (!canReadNextPage()) return;
        goToPage(currentPage + 1);
    }

    public void prevPage() {
        if (!canReadPrevPage()) return;
        goToPage(currentPage - 1);
    }

    public void goToPage(int page) {
        if (page >= pages.size()) page = pages.size() - 1;
        if (page < 0) page = 0;
        boolean shouldUpdateLines = page > currentPage && canReadNextPage();
        if (shouldUpdateLines) {
            String text = getCurrentPageText();
            int nOfNewLineNow = (text.length() - text.replace("\n", "").length()) + 1; // normally the last line is not counted so we have to add 1
            int nOfNewLineBefore = startingLines.get(currentPage + 1) - startingLines.get(currentPage);
            int difference = nOfNewLineNow - nOfNewLineBefore;
            updateStartingLines(currentPage + 1, difference);
        }
        currentPage = page;
    }

    public void goToPageByLine(int line) {
        int page = line / LINES_PER_PAGE;
        goToPage(page);
    }

    public void setStartingLines() {
        int i;
        int startingLine;
        int nOfNewLines;
        String text;
        startingLines.clear();
        startingLines.add(0);
        for (i = 1; i < pages.size(); i++) {
            text = pages.get(i - 1);
            nOfNewLines = text.length() - text.replace("\n", "").length() + 1;
            startingLine = startingLines.get(i - 1) + nOfNewLines;
            startingLines.add(startingLine);
        }
    }

    public void updateStartingLines(int fromPage, int difference) {
        if (difference == 0)
            return;
        int i;
        if (fromPage < 1) fromPage = 1;
        for (i = fromPage; i < pages.size(); i++) {
            startingLines.set(i, startingLines.get(i) + difference);
        }
    }

    public int getMaxPage() {
        return pages.size() - 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getAllText(String currentPageText) {
        pages.set(currentPage, currentPageText);
        int i;
        StringBuilder allText = new StringBuilder();
        for (i = 0; i < pages.size(); i++) {
            allText.append(pages.get(i));
        }
        return allText.toString();
    }

    public boolean canReadNextPage() {
        return currentPage < pages.size() - 1;
    }

    public boolean canReadPrevPage() {
        return currentPage >= 1;
    }
}
