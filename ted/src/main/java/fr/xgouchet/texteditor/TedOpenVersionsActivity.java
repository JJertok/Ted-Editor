package fr.xgouchet.texteditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import fr.xgouchet.texteditor.common.Constants;
import fr.xgouchet.texteditor.common.VersionsFiles;
import fr.xgouchet.texteditor.ui.adapter.PathListAdapter;


public class TedOpenVersionsActivity extends Activity implements Constants, View.OnClickListener,
        AdapterView.OnItemClickListener {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup content view
        setContentView(R.layout.layout_open);

        // buttons
        findViewById(R.id.buttonCancel).setOnClickListener(this);

        // widgets
        mFilesList = (ListView) findViewById(android.R.id.list);
        mFilesList.setOnItemClickListener(this);

        mFilePath = getIntent().getStringExtra("originalPath");
        mVersions = new VersionsFiles(getApplicationContext());
    }

    /**
     * @see android.app.Activity#onResume()
     */
    protected void onResume() {
        super.onResume();
        fillVersionsFilesView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path;

        path = mList.get(position);

        if (setOpenResult(new File(path))) {
            finish();
        }
    }


    protected void fillVersionsFilesView() {

        //TODO Use mFilePath hashcode
        mList = mVersions.loadVersions(String.valueOf(mFilePath.hashCode()));

        if (mList.size() == 0) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // create string list adapter
        mListAdapter = new PathListAdapter(this, mList);

        // set adpater
        mFilesList.setAdapter(mListAdapter);
    }

    /**
     * Set the result of this activity to open a file
     *
     * @param file
     *            the file to return
     * @return if the result was set correctly
     */
    protected boolean setOpenResult(File file) {
        Intent result;

        if ((file == null) || (!file.isFile()) || (!file.canRead())) {
            return false;
        }

        result = new Intent();
        result.putExtra("path", file.getAbsolutePath());

        setResult(RESULT_OK, result);
        return true;
    }

    protected String mContextPath;

    /** the dialog's list view */
    protected ListView mFilesList;
    /** The list adapter */
    protected ListAdapter mListAdapter;

    /** the list of versions */
    protected ArrayList<String> mList;

    protected String mFilePath;

    protected VersionsFiles mVersions;
}
