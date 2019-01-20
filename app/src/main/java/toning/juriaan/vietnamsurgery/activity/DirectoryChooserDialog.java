package toning.juriaan.vietnamsurgery.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import toning.juriaan.vietnamsurgery.R;

public class DirectoryChooserDialog {
    private String m_parentDir;
    private Context m_context;
    private TextView m_titleView;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private ChosenDirectoryListener m_chosenDirectoryListener;
    private ArrayAdapter<String> m_listAdapter = null;

    /**
     * Callback interface for selected directory
     */
    public interface ChosenDirectoryListener
    {
        void onChosenDir(String chosenDir);
    }

    public DirectoryChooserDialog(Context context, ChosenDirectoryListener chosenDirectoryListener)
    {
        m_context = context;
        m_parentDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_chosenDirectoryListener = chosenDirectoryListener;

    }

    /**
     * Load directory chooser dialog for initial default directory
     * @param cancelable boolean if cancel button must be showed
     */

    public void chooseDirectory(boolean cancelable)
    {
        // Initial directory is root directory
        chooseDirectory(m_parentDir, cancelable);
    }

    /**
     * Load directory chooser dialog for initial input directory
     * @param dir String with absolute path were directory chooser has to start
     * @param cancelable boolean if cancel button must be showed
     */
    public void chooseDirectory(String dir, boolean cancelable)
    {
        File dirFile = new File(dir);
        if (! dirFile.exists() || ! dirFile.isDirectory())
        {
            dir = m_parentDir;
        }

        try
        {
            dir = new File(dir).getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return;
        }

        m_dir = dir;
        m_subdirs = getDirectories(dir);

        class DirectoryOnClickListener implements DialogInterface.OnClickListener
        {
            public void onClick(DialogInterface dialog, int item)
            {
                // Navigate into the sub-directory
                m_dir += "/" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder =
                createDirectoryChooserDialog(dir, m_subdirs, new DirectoryOnClickListener());

        dialogBuilder.setPositiveButton(R.string.dialog_choose, ((DialogInterface dialog, int which) -> {
            if (m_chosenDirectoryListener != null)
            {
                // Call registered listener supplied with the chosen directory
                m_chosenDirectoryListener.onChosenDir(m_dir);
            }
        }));
        
        if(cancelable) {
            dialogBuilder.setNegativeButton(R.string.dialog_cancel, null);
        }


        final AlertDialog dirsDialog = dialogBuilder.create();

        dirsDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            /**
             * Method to check if the current directory is the last, if so, don't go up
             * Otherwise go back up to parentDir
             * @param dialog DialogInterface
             * @param keyCode int which key is pressed
             * @param event KeyEvent that hangs to the key that's pressed
             * @return boolean
             */
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    // Back button pressed
                    if ( m_dir.equals(m_parentDir) )
                    {
                        // The very top level directory, do nothing
                        return false;
                    }
                    else
                    {
                        // Navigate back to an upper directory
                        m_dir = new File(m_dir).getParent();
                        updateDirectory();
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        // Show directory chooser dialog
        dirsDialog.show();
    }

    /**
     * Method to get all the directories in current dir
     * @param dir String absolute path to current dir
     * @return List with strings of absolute paths
     */
    private List<String> getDirectories(String dir)
    {
        List<String> dirs = new ArrayList<>();

        File dirFile = new File(dir);
        if (! dirFile.exists() || ! dirFile.isDirectory())
        {
            return dirs;
        }

        for (File file : dirFile.listFiles())
        {
            if ( file.isDirectory() )
            {
                // Only show dirs that not start with a dot
                if(!file.getName().contains(".")) {
                    dirs.add( file.getName() );
                }
            }
        }

        Collections.sort(dirs, (String o1, String o2)->o1.compareTo(o2));

        return dirs;
    }

    /**
     * Builder to build the DirectoryChooserDialog
     * @param title String with the title
     * @param listItems List with directories that has to be shown
     * @param onClickListener onClickListener
     * @return AlertDialog
     */
    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

        // Create custom view for AlertDialog title
        // Current directory TextView allows long directory path to be wrapped to multiple lines.
        LinearLayout titleLayout = new LinearLayout(m_context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        m_titleView = new TextView(m_context);
        m_titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        m_titleView.setTextAppearance(m_context, android.R.style.TextAppearance_Medium);
        m_titleView.setPadding(0, 50, 0, 50);
        m_titleView.setTextColor( m_context.getResources().getColor(android.R.color.black) );
        m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        m_titleView.setText(m_context.getString(R.string.directory_chooser_text, title));
        m_titleView.setBackgroundResource(R.color.colorPrimary);

        titleLayout.addView(m_titleView);

        dialogBuilder.setCustomTitle(titleLayout);

        m_listAdapter = createListAdapter(listItems);

        dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);

        return dialogBuilder;
    }

    /**
     * Method to update the directory after a click on a directory
     */
    private void updateDirectory()
    {
        m_subdirs.clear();
        m_subdirs.addAll( getDirectories(m_dir) );
        m_titleView.setText(m_context.getString(R.string.directory_chooser_text, m_dir));

        m_listAdapter.notifyDataSetChanged();
    }

    /**
     * Adapter for the list of directories
     * @param items List with items
     * @return ArrayAdapter
     */
    private ArrayAdapter<String> createListAdapter(List<String> items)
    {
        return new ArrayAdapter<String>(m_context,
                android.R.layout.select_dialog_item, android.R.id.text1, items)
        {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);

                if (v instanceof TextView)
                {
                    // Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }
}
